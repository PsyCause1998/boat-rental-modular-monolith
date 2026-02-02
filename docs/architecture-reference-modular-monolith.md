# Architecture de Référence — Modular Monolith, Clean & Hexagonale

## Objectif du document

Ce document est une **référence théorique et pratique** pour comprendre et appliquer :

* le **modular monolith**
* l’architecture **hexagonale**
* l’architecture **clean**
* leurs variantes **CRUD**, **complexe**, et **hybride**

Il est volontairement **indépendant de tout projet concret**.

---

## Définitions clés des architectures

### Modular Monolith

Le **modular monolith** est une **application unique déployée en un seul bloc**, mais **organisée en modules fortement isolés**.

**Ce que ce n’est pas :**

* ❌ pas des microservices
* ❌ pas du code en vrac dans un seul package

**Ce que c’est :**

* ✅ un seul artefact (jar / application)
* ✅ des modules métiers indépendants
* ✅ des dépendances **contrôlées entre modules**
* ✅ une excellente base avant une éventuelle migration microservices

**Objectif principal :**

> Maîtriser la complexité sans payer le coût des microservices.

Chaque module :

* possède son **propre domaine métier**
* expose des **interfaces claires**
* ne dépend pas directement des autres modules (ou très peu)

👉 Le modular monolith est un **choix de déploiement**, pas une architecture métier.
Il peut contenir du **CRUD**, du **Clean**, de l’**Hexagonal** ou un mélange des trois.

---

### Clean Architecture

La **Clean Architecture** est une **architecture orientée règles métier**, centrée sur la **règle de dépendance**.

Principe fondamental :

> Les dépendances vont toujours vers le centre (le métier).

Elle impose une séparation stricte entre :

* **Domain** : règles métier pures
* **Application** : cas d’usage
* **Infrastructure** : frameworks, bases de données, HTTP, messaging

Caractéristiques :

* le domaine est **indépendant de toute technologie**
* la technique est **remplaçable**
* les règles métier sont **hautement testables**

👉 La Clean Architecture est une **discipline** :

* elle protège le métier
* elle demande plus de rigueur au départ
* elle évite les projets impossibles à maintenir

---

### Architecture Hexagonale (Ports & Adapters)

L’architecture **hexagonale** est une **mise en œuvre concrète** des principes de la Clean Architecture.

Idée centrale :

> Le métier ne parle jamais directement au monde extérieur.

Elle introduit explicitement :

* des **ports** (interfaces définies par le métier)
* des **adapters** (implémentations techniques)

Types de ports :

* **ports entrants** : ce que le métier permet de faire (use cases)
* **ports sortants** : ce dont le métier a besoin (DB, APIs, services externes)

Avantages clés :

* isolation maximale du métier
* frameworks considérés comme des détails
* tests métier simples, rapides et fiables

👉 Hexagonal ≠ Clean, mais :

* Hexagonal = **pattern d’implémentation**
* Clean = **philosophie globale**

---

### Lien entre les concepts

Ces trois notions sont **complémentaires** :

* **Modular Monolith** → organisation globale et stratégie de déploiement
* **Clean Architecture** → règles de dépendance et protection du métier
* **Hexagonal Architecture** → implémentation concrète via ports & adapters

👉 Cas idéal fréquent :

> Un **modular monolith** composé de **modules Clean / Hexagonaux**.

---

## 0. Principes fondamentaux

### Règle de dépendance

> Le métier ne dépend jamais de la technique.

Le **cœur métier** :

* ne connaît pas Spring
* ne connaît pas JPA
* ne connaît pas HTTP
* ne connaît pas JSON

Il ne manipule que :

* des objets métier
* des interfaces (ports)

---

## Ports & Adapters

### Port

* Interface
* Exprime un besoin du métier
* Défini côté application/domain

Exemples :

* UserRepository
* PaymentGateway
* NotificationSender

### Adapter

* Implémentation technique d’un port
* Dépend d’un framework
* Branche le monde extérieur au métier

Exemples :

* JpaUserRepository
* StripePaymentAdapter
* RestNotificationAdapter

---

## Controller ≠ Mapper

* Controller : parle HTTP (adapter entrant)
* Mapper : transforme DTO ↔ Domain

Un controller ne contient **aucune logique métier**.

---

## 1. Architecture CRUD Simple

### Quand l’utiliser

* Back-office
* Admin interne
* Métier simple
* Rapidité prioritaire

### Arborescence

```
simple-crud
└── user
    ├── User.java
    ├── UserRepository.java
    ├── UserService.java
    └── UserController.java
```

### Explications

* User : entité anémique
* Repository : accès DB direct
* Service : logique applicative simple
* Controller : exposition REST

Limites :

* couplage fort
* testabilité faible
* évolution coûteuse

---

## 2. Architecture Hexagonale / Clean (Complexe)

### Quand l’utiliser

* Métier critique
* Forte évolutivité
* Règles complexes

### Arborescence

```
complex-domain
└── order
    ├── domain
    ├── application
    │   ├── port
    │   └── service
    └── infrastructure
        ├── api
        └── persistence
```

### Domain

* Entités riches
* Value Objects
* Invariants métier
* Aucune dépendance framework

### Application

* Use cases
* Ports entrants et sortants
* Orchestration métier

### Infrastructure

* Controllers REST
* Repositories JPA
* Adaptateurs techniques

Avantages :

* testabilité maximale
* indépendance technique
* robustesse

---

## 3. Architecture Hybride (CRUD + Hexa)

### Quand l’utiliser

* Cas réel majoritaire
* CRUD majoritaire
* Quelques règles métier critiques

### Principe

* CRUD simples : repository direct
* Actions métier : use cases dédiés

### Arborescence

```
hybrid-domain
└── product
    ├── domain
    ├── application
    └── infrastructure
```

---

## Synthèse

| Architecture | Simplicité | Robustesse | Testabilité |
| ------------ | ---------- | ---------- | ----------- |
| CRUD simple  | ⭐⭐⭐⭐⭐      | ⭐⭐         | ⭐⭐          |
| Hexa clean   | ⭐⭐         | ⭐⭐⭐⭐⭐      | ⭐⭐⭐⭐⭐       |
| Hybride      | ⭐⭐⭐⭐       | ⭐⭐⭐⭐       | ⭐⭐⭐⭐        |

---

## Phrase clé

> Le métier dicte l’architecture, jamais l’inverse.
> Ports = besoins du métier.
> Adapters = détails techniques.
