# Boat Rental – Modular Monolith Reference Project

Projet backend **Java / Spring Boot** servant de **projet de référence** pour comprendre,
concevoir et implémenter une **architecture Modular Monolith**
basée sur les principes de **Clean Architecture** et **Hexagonal Architecture**.

Ce dépôt a une vocation **pédagogique et technique** : il documente les choix,
leurs justifications et leurs implications à long terme.

---

## 🎯 Objectifs du projet

Ce projet a pour but de :

- Construire un **monolithe modulaire** clair et strictement découpé
- Appliquer les principes :
    - Clean Architecture
    - Hexagonal Architecture (Ports & Adapters)
- Isoler le **cœur métier** de tout framework
- Mettre en place une **stratégie de tests complète**
- Servir de **base solide et durable** pour de futurs projets
- Préparer une éventuelle évolution vers les microservices **sans dette structurelle**

---

## 🧱 Choix techniques

- **Java 21 (LTS)**
- **Spring Boot 3.x**
- **Maven multi-modules**
- **JUnit 5 / AssertJ** pour les tests
- **H2** pour le développement (remplaçable facilement)

### Pourquoi un Modular Monolith ?

- Évite la complexité prématurée des microservices
- Favorise la cohérence métier
- Améliore la testabilité
- Permet une évolution incrémentale et maîtrisée

---

## 🏗️ Architecture globale

Le projet est structuré comme un **monolithe modulaire** :

- Chaque module métier est **autonome**
- Les dépendances entre modules sont **explicites**
- Le domaine métier est **indépendant des frameworks**
- Spring est utilisé comme **outil d’assemblage**, pas comme fondation du métier

### Arborescence globale

```
boat-rental
├── application
├── boats
├── customers
├── rentals
├── shared
└── docs
```

👉 Une documentation d’architecture détaillée est disponible ici :  
📘 **[Architecture Reference](docs/architecture-reference-modular-monolith.md)**

---

## 📦 Description des modules

### `application`
- Point d’entrée Spring Boot
- Configuration technique (Spring, JPA, Web)
- Démarrage de l’application
- Assemblage des modules métier

### `boats`, `customers`, `rentals`
Modules métier indépendants, organisés selon Clean Architecture :
- `domain` : cœur métier (entités, value objects, règles)
- `application` : cas d’usage
- `infrastructure` : persistance, adaptateurs techniques
- `api` : contrôleurs REST (adaptateurs entrants)

### `shared`
- Concepts transverses
- Value Objects partagés
- Exceptions communes
- Utilitaires métier (sans dépendance Spring)

---

## 🔌 Ports & Adapters – principe clé

- Les **controllers REST** sont des *adapters entrants*
- Les **repositories JPA** sont des *adapters sortants*
- Le **domaine ne dépend de rien**
- Le **sens des dépendances pointe toujours vers le domaine**

> Un controller n’est **pas** un mapper.  
> Il adapte un protocole (HTTP) vers un cas d’usage métier.

---

## 🧪 Stratégie de tests

Le projet adopte une stratégie de tests stricte :

- **Tests unitaires du domaine**
    - sans Spring
    - sans base de données
- **Tests des cas d’usage**
- **Tests d’intégration**
    - uniquement au niveau `application`
- Objectif : tester le **comportement métier**, pas l’implémentation

---

## ▶️ Lancer le projet

```bash
mvn clean install
mvn -pl application spring-boot:run
```

L’application démarre par défaut sur `http://localhost:8080`.

---

## 🚧 Statut

Projet en évolution continue.  
Ce dépôt sert de **socle de référence** pour :

- expérimenter des patterns
- former
- comparer des approches architecturales
- préparer des projets plus complexes

---

## 📚 Références conceptuelles

- Clean Architecture – Robert C. Martin
- Hexagonal Architecture – Alistair Cockburn
- Domain-Driven Design
- Modular Monoliths – Spring / ThoughtWorks

---

## 🧠 Philosophie

> Le code doit expliquer **le métier**,  
> l’architecture doit **empêcher les erreurs**,  
> et les frameworks doivent rester **des détails**.
