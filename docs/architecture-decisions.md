# Décisions d’architecture & retours de projet

Ce document **ne décrit pas des concepts théoriques**.
Il documente **les décisions réellement prises durant le projet Boat Rental**,
leurs motivations, les alternatives envisagées et les compromis acceptés.

L’objectif est de capturer le **raisonnement architectural**, pas d’aligner des patterns.

---

## 1. Pourquoi un Modular Monolith

### Décision

Construire le projet comme un **monolithe modulaire**, et non comme un système de microservices.

### Raisonnement

* Le domaine ne justifie pas la complexité distribuée.
* Les microservices auraient introduit :

  * latence réseau
  * problèmes de cohérence des données
  * surcoût opérationnel
* Un monolithe modulaire permet :

  * des frontières fortes
  * des dépendances explicites
  * une itération rapide
  * un refactoring sûr

### Compromis

* Pas de déploiement indépendant par module
* Accepté car :

  * le projet est une **référence pédagogique**
  * les frontières sont imposées **au niveau du code**
  * une migration future vers des microservices reste possible

---

## 2. Domaine d’abord, frameworks ensuite

### Décision

La couche domaine a **zéro dépendance** à Spring, JPA, HTTP ou JSON.

### Raisonnement

* Le domaine exprime des règles métier, pas des détails techniques.
* Les frameworks sont des outils, pas des fondations.
* Cela permet :

  * des tests de domaine purs
  * le remplacement de frameworks
  * une maintenabilité long terme

### Conséquence

* Plus de code au départ (ports, interfaces)
* En échange :

  * responsabilités claires
  * couplages visibles
  * moins de magie implicite

---

## 3. Ports & Adapters en pratique

### Décision

Utiliser des packages explicites `port.in` et `port.out`.

### Raisonnement

* Rendre les dépendances visibles et intentionnelles.
* Forcer la réflexion en termes de :

  * **besoins métier**
  * et non d’implémentations techniques

### Exemple

* `StartRentalUseCase` représente une capacité métier
* `RentalRepository` représente un besoin du métier
* JPA n’est qu’un détail d’implémentation

---

## 4. Emplacement des frameworks

### Décision

Les frameworks (Spring, JPA, Web) sont **repoussés à la périphérie**.

### Règle

* Les modules métier **ne dépendent pas de Spring Boot**
* Spring est utilisé comme **outil d’assemblage** dans le module `application`

### Bénéfice

* Le métier reste lisible sans connaissance du framework
* Le wiring devient explicite et contrôlé

---

## 5. Types neutres aux frontières inter-modules

### Décision

Les ports inter-modules utilisent des **types neutres** (ex: `UUID`),
pas des `BoatId` ou `CustomerId` issus d’un autre module.

### Raisonnement

* Chaque module est propriétaire de son modèle
* Partager des Value Objects crée un couplage implicite
* Le port représente un **contrat**, pas un modèle interne

### Conséquence

* Conversion locale dans chaque module
* Frontières plus claires
* Couplage réduit

---

## 6. Même un module CRUD expose une interface

### Décision

Un module CRUD (`boats`, `customers`) expose une **interface publique**
(`BoatLookup`, `CustomerLookup`) lorsqu’il est consommé par un autre module.

### Raisonnement

* Sans interface, le module consommateur dépend :

  * de l’implémentation
  * du framework
* Une interface explicite permet :

  * un découplage réel
  * des dépendances visibles

### Ce que l’on gagne

* Dépendances explicites
* Pas de magie Spring
* Ports testables sans Spring
* Remplacement facile (fake, mock, autre implémentation)

### Ce que l’on perd

* La facilité du `@Service` injecté partout

👉 **C’est exactement le trade-off Clean / Hexagonal.**

---

## 7. Règle de wiring (assemblage)

### Décision

Le wiring est **centralisé et contrôlé**.

### Règle stricte

* Les modules métier (`rentals`, `boats`, `customers`) :

  * ❌ ne créent **aucun bean Spring** pour les ports
* Le module `application` :

  * ✅ est **le seul responsable** de l’assemblage

### Contrainte

* Un port = **un seul bean Spring actif**

### Bénéfice

* Configuration explicite
* Aucun câblage implicite
* Contrôle total des dépendances runtime

---

## 8. Contraintes base de données comme gardes métier

### Décision

Certaines règles métier critiques sont **garanties par la base de données**.

### Exemple

> « Un bateau ne peut avoir qu’une seule location ACTIVE à la fois »

### Raisonnement

* Les vérifications applicatives sont insuffisantes sous concurrence.
* Les race conditions sont inévitables sans garantie DB.
* La base est la **seule autorité cohérente** en écriture concurrente.

### Implémentation

```sql
CREATE UNIQUE INDEX uk_active_rental_per_boat
ON rentals (boat_id)
WHERE status = 'ACTIVE';
```

### Compromis

* Schéma un peu plus complexe
* Comportement déterministe sous charge

---

## 9. Index métier pour la performance

### Décision

Indexer explicitement les requêtes métier critiques.

### Exemple

Disponibilité d’un bateau :

```sql
CREATE INDEX IF NOT EXISTS idx_boat_id_status
ON boats (boat_id, status);
```

### Raisonnement

* La performance fait partie du comportement
* Un `existsByIdAndStatus` doit être O(log n)
* Les tests ont validé l’impact réel des index

---

## 10. Vérifications applicatives + garanties DB

### Décision

Conserver **les deux niveaux** de protection.

* Vérifications applicatives :

  * feedback rapide
  * lisibilité métier
* Contraintes DB :

  * intégrité finale

L’un sans l’autre est insuffisant.

---

## 11. Gestion des erreurs

### Décision

Utiliser une exception métier partagée avec :

* codes d’erreur explicites
* types d’erreur stables

### Raisonnement

* Les erreurs font partie du contrat
* Les codes restent stables entre :

  * REST
  * tests
  * futures architectures distribuées
* Le statut HTTP est un détail de présentation

---

## 12. Ce qui a été volontairement exclu

### Pas de CQRS

* Surcoût inutile pour la complexité actuelle

### Pas de bus d’événements

* Les événements restent internes

### Pas de locking optimiste

* Les contraintes DB couvrent le cas critique

---

## 13. L’architecture comme système de contraintes

L’architecture n’est pas là pour être flexible.
Elle est là pour **empêcher les mauvaises décisions**.

* Le domaine ne dépend pas de l’infrastructure
* Les cas d’usage ne peuvent pas contourner les règles
* La base garantit les invariants

C’est intentionnel.

---

## 14. Enseignement clé

La plupart des décisions répondent à une seule question :

> « Qu’est-ce qui restera cohérent quand le code sera 5× plus gros et sous charge ? »

Le projet optimise pour :

* la clarté
* la correction
* l’évolutivité

Pas pour la vitesse à court terme.
