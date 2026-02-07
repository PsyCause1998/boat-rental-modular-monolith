# Stratégie de tests & enseignements

Ce document n’explique pas *comment écrire des tests*.
Il explique **pourquoi certains tests ont été écrits**,
**quels types de tests ont été choisis**,
et surtout **ce que ces tests ont révélé sur l’architecture du projet**.

Il s’agit d’un retour d’expérience, pas d’un tutoriel.

---

## 1. Pourquoi les tests ont été introduits tardivement

### Constat

Les tests ont été ajoutés **après** l’implémentation des fonctionnalités cœur.

### Bénéfice

* Concentration initiale sur la modélisation du métier
* Pas d’abstraction prématurée
* Le domaine a émergé naturellement

### Coût

* Refactoring nécessaire a posteriori
* Mais ces refactorings ont mis en évidence :

    * des responsabilités floues
    * des dépendances implicites

👉 Les tests ont servi de **révélateur de design**, pas de garde-fou tardif.

---

## 2. Types de tests utilisés

### 2.1 Tests du domaine

Caractéristiques :

* Tests unitaires purs
* Aucun Spring
* Aucune base de données

Objectifs :

* Valider les règles métier
* Valider les transitions d’état
* Garantir les invariants

Valeur apportée :

* Très rapides
* Déterministes
* Servent de documentation vivante du métier

---

### 2.2 Tests d’intégration avec Testcontainers

Utilisés pour valider :

* les contraintes base de données
* les comportements en concurrence
* l’exécution réelle du SQL

Pourquoi Testcontainers :

* Même comportement qu’en production
* Aucun setup local spécifique
* Échecs reproductibles

💡 Enseignement clé :

> « Si ça ne fonctionne qu’avec des mocks, alors ça ne fonctionne pas vraiment. »

---

### 2.3 Tests de concurrence

Objectif :

* Vérifier la cohérence métier sous conditions de course

Scénarios testés :

* Plusieurs appels concurrents à `startRental`
* Sur le même bateau

Résultat :

* Les vérifications applicatives ont échoué
* La contrainte base de données a tenu

Conclusion :

> Les bugs de concurrence sont des problèmes d’architecture,
> pas de logique métier.

---

## 3. Tests de performance des index

### Objectif

Mesurer l’impact réel des index, pas le supposer.

### Setup

* 1 000 000 de lignes
* PostgreSQL
* Requêtes strictement identiques

### Résultats

| Scénario   | Temps moyen |
| ---------- | ----------- |
| Sans index | ~18–20 ms   |
| Avec index | ~1 ms       |

### Enseignement

Les index ne sont pas une optimisation théorique.
Ils apportent des **ordres de grandeur** de gain.

---

## 4. Pourquoi mesurer plutôt que supposer

Erreur fréquente :

> « La base de données gérera ça. »

Réalité :

* Une base exécute exactement ce qu’on lui demande
* Un mauvais schéma produit de mauvaises performances

Les tests ont imposé :

* des chiffres concrets
* une compréhension réelle des coûts

---

## 5. Les tests comme feedback de design

Plusieurs décisions architecturales ont été ajustées grâce aux tests :

* Les erreurs sont devenues explicites
* Des contraintes base de données ont été ajoutées
* La logique applicative a été simplifiée

Les tests n’ont pas seulement validé le code.
Ils ont **modifié l’architecture**.

---

## 6. Ce que les tests ne font volontairement pas

* Pas de tests end-to-end HTTP
* Pas de tests UI

Raison :

* Le focus est mis sur la **justesse métier** et l’architecture
* Pas sur les pipelines de livraison

---

## 7. Quand les tests valent vraiment le coût

Les tests sont indispensables lorsque :

* une règle ne doit jamais être violée
* la concurrence est impliquée
* la performance est critique

Ils sont moins utiles lorsque :

* la logique est triviale
* le comportement est évident

---

## 8. Conclusion

Les tests ne servent pas à augmenter la couverture.
Ils servent à augmenter la **confiance**.

Ce projet a montré que :

* de bons tests révèlent les failles architecturales
* une mauvaise architecture résiste aux tests

👉 Si quelque chose est difficile à tester,
👉 c’est généralement qu’il est mal conçu.
