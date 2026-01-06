# Architecture de Référence — Modular Monolith, Clean & Hexagonale

## Objectif du document
Ce document est une **référence théorique et pratique** pour comprendre et appliquer :
- le **modular monolith**
- l’architecture **hexagonale**
- l’architecture **clean**
- leurs variantes **CRUD**, **complexe**, et **hybride**

Il est volontairement **indépendant de tout projet concret**.

---

## 0. Principes fondamentaux

### Règle de dépendance
> Le métier ne dépend jamais de la technique.

Le **cœur métier** :
- ne connaît pas Spring
- ne connaît pas JPA
- ne connaît pas HTTP
- ne connaît pas JSON

Il ne manipule que :
- des objets métier
- des interfaces (ports)

---

## Ports & Adapters

### Port
- Interface
- Exprime un besoin du métier
- Défini côté application/domain

Exemples :
- UserRepository
- PaymentGateway
- NotificationSender

### Adapter
- Implémentation technique d’un port
- Dépend d’un framework
- Branche le monde extérieur au métier

Exemples :
- JpaUserRepository
- StripePaymentAdapter
- RestNotificationAdapter

---

## Controller ≠ Mapper

- Controller : parle HTTP (adapter entrant)
- Mapper : transforme DTO ↔ Domain

Un controller ne contient **aucune logique métier**.

---

## 1. Architecture CRUD Simple

### Quand l’utiliser
- Back-office
- Admin interne
- Métier simple
- Rapidité prioritaire

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
- User : entité anémique
- Repository : accès DB direct
- Service : logique applicative simple
- Controller : exposition REST

Limites :
- couplage fort
- testabilité faible
- évolution coûteuse

---

## 2. Architecture Hexagonale / Clean (Complexe)

### Quand l’utiliser
- Métier critique
- Forte évolutivité
- Règles complexes

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
- Entités riches
- Value Objects
- Invariants métier
- Aucune dépendance framework

### Application
- Use cases
- Ports entrants et sortants
- Orchestration métier

### Infrastructure
- Controllers REST
- Repositories JPA
- Adaptateurs techniques

Avantages :
- testabilité maximale
- indépendance technique
- robustesse

---

## 3. Architecture Hybride (CRUD + Hexa)

### Quand l’utiliser
- Cas réel majoritaire
- CRUD majoritaire
- Quelques règles métier critiques

### Principe
- CRUD simples : repository direct
- Actions métier : use cases dédiés

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
|-------------|-----------|------------|-------------|
| CRUD simple | ⭐⭐⭐⭐⭐ | ⭐⭐ | ⭐⭐ |
| Hexa clean  | ⭐⭐ | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐⭐ |
| Hybride     | ⭐⭐⭐⭐ | ⭐⭐⭐⭐ | ⭐⭐⭐⭐ |

---

## Phrase clé

> Le métier dicte l’architecture, jamais l’inverse.
> Ports = besoins du métier.
> Adapters = détails techniques.
