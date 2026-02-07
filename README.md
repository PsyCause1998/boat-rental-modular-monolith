# Boat Rental – Modular Monolith Reference Project

Projet backend **Java / Spring Boot** servant de **projet de référence** pour comprendre,
concevoir et implémenter une **architecture Modular Monolith**
basée sur des principes de **Clean Architecture** et **Hexagonal Architecture**,
appliqués de manière **pragmatique et progressive**.

Ce dépôt a une vocation **pédagogique et technique** : il documente les choix réalisés,
leurs justifications, leurs implications, ainsi que les compromis assumés.

---

## 🎯 Objectifs du projet

Ce projet a pour but de :

* Construire un **monolithe modulaire** clair et strictement découpé
* Appliquer les principes de Clean Architecture et d’Hexagonal Architecture **lorsque la complexité le justifie**
* Isoler le **cœur métier critique** de tout framework (notamment dans le module `rentals`)
* Mettre en place une **stratégie de tests complète et réaliste**
* Servir de **base solide et durable** pour de futurs projets
* Préparer une éventuelle évolution vers les microservices **sans dette structurelle**

---

## 🧱 Choix techniques

* **Java 21 (LTS)**
* **Spring Boot 3.x**
* **Maven multi-modules**
* **JUnit 5 / AssertJ** pour les tests
* **H2** pour le développement local rapide
* **PostgreSQL (via Testcontainers)** pour les tests d’intégration

### Pourquoi un Modular Monolith ?

* Évite la complexité prématurée des microservices
* Favorise la cohérence métier
* Améliore la testabilité
* Permet une évolution incrémentale et maîtrisée
* Rend les dépendances visibles et contrôlables

---

## 🏗️ Architecture globale

Le projet est structuré comme un **monolithe modulaire** :

* Chaque module métier est **autonome**
* Les dépendances entre modules sont **explicites et contrôlées**
* Le métier est **prioritaire sur la technique**
* Spring est utilisé comme **outil d’assemblage**, pas comme fondation du métier

Le module `application` concentre l’**infrastructure technique** (Spring, JPA, Web)
et agit comme **composeur** des modules métier.

---

## 🧭 Périmètre & règles (architecture hybride assumée)

Ce dépôt est volontairement **hybride**.

### Modules

#### `rentals`

* Module de référence en **Clean / Hexagonal Architecture**
* Domaine métier **pur**, sans dépendance framework
* Ports explicites (entrants / sortants)
* Adapters branchés depuis le module `application`
* Modèle conçu pour la robustesse (règles métier, concurrence, invariants)

#### `boats`, `customers`

* Modules **CRUD pragmatiques**
* Architecture simple : controller / service / repository
* Dépendances directes à Spring et JPA autorisées
* Responsabilité locale claire
* Exposent uniquement des **contrats publics minimaux** (interfaces)

#### `application`

* Point d’entrée Spring Boot
* Infrastructure technique (Web, JPA, configuration)
* Wiring explicite des ports
* Tests d’intégration

#### `shared`

* Concepts transverses
* Exceptions communes
* Types simples et stables
* **Aucun framework**

---

### Règles de dépendances (strictes)

* `application` peut dépendre de tous les modules.
* Les modules métier peuvent dépendre de `shared`.
* **Les modules métier ne doivent jamais dépendre entre eux** :

  * boats ↛ customers ↛ rentals (et inversement)

Lorsqu’une communication inter-modules est nécessaire :

* Exposer une **interface de contrat stable** (ex: `BoatLookup`, `CustomerLookup`)
* L’implémentation reste **interne au module propriétaire**
* Les ports utilisent des **types simples (UUID)** pour éviter le couplage
* Le wiring est effectué dans le module `application`

👉 `shared` ne doit **jamais devenir un fourre-tout**.

---

## 📁 Arborescence globale

```
boat-rental
├── application
├── boats
├── customers
├── rentals
├── shared
└── docs
```

📘 Documentation détaillée :

* Architecture : `docs/architecture-reference-modular-monolith.md`
* Décisions : `docs/architecture-decisions.md`
* Tests : `docs/testing-strategy.md`

---

## 🔌 Ports & Adapters – principe clé

* Les **controllers REST** sont des *adapters entrants*
* Les **repositories JPA** sont des *adapters sortants*
* Le **domaine ne dépend de rien**
* Le **sens des dépendances pointe toujours vers le métier**

Un adapter peut être :

* une **classe dédiée**
* ou un **bean / lambda d’assemblage** dans `application`

> Un controller n’est **pas** un mapper.
> Il adapte un protocole (HTTP) vers un cas d’usage métier.

---

## 🧪 Stratégie de tests

La stratégie de tests est volontairement stricte :

* **Tests unitaires du domaine**

  * sans Spring
  * sans base de données
* **Tests des cas d’usage**
* **Tests d’intégration uniquement dans `application`**

  * PostgreSQL réel via Testcontainers
  * Validation de la concurrence
  * Validation des contraintes SQL
  * Validation des **choix d’indexation**

Certaines règles critiques sont volontairement **renforcées en base**
(ex: une seule location ACTIVE par bateau)
afin de garantir la cohérence même en cas de concurrence ou d’erreur applicative.

Objectif : tester le **comportement métier**, pas l’implémentation.

---

## ▶️ Lancer le projet

```bash
mvn clean install
mvn -pl application spring-boot:run
```

L’application démarre sur `http://localhost:8080`.

---

## 🚧 Statut

Projet en évolution continue.

Ce dépôt sert de **socle de référence** pour :

* expérimenter des patterns
* comparer des architectures
* former
* préparer des projets plus complexes

---

## 📚 Références conceptuelles

* Clean Architecture – Robert C. Martin
* Hexagonal Architecture – Alistair Cockburn
* Domain-Driven Design
* Modular Monoliths – ThoughtWorks / Spring

---

## 🧠 Philosophie

> Le code doit expliquer **le métier**,
> l’architecture doit **empêcher les erreurs**,
> et les frameworks doivent rester **des détails**.
