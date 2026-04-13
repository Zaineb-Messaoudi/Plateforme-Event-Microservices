# Plateforme de Gestion des Évènements
## Architecture Microservices

## Présentation
Ce projet est une **plateforme de gestion des évènements** conçue selon une **architecture microservices**, visant à résoudre les limitations des architectures monolithiques en matière de scalabilité, de modularité et d’évolutivité.

Le projet a été réalisé dans le cadre du cours **Applications Web Distribuées**.

La plateforme couvre l’ensemble du cycle de vie des évènements : création, réservation, abonnements, logistique, feedbacks et gestion de contenu, avec une authentification centralisée et sécurisée.

## Architecture Globale
Chaque microservice est indépendant, déployable séparément et communique avec les autres via des API sécurisées.

### Caractéristiques principales
- Architecture découplée
- Scalabilité horizontale
- Authentification centralisée
- Haute disponibilité
- Facilité de maintenance et d’évolution

## Acteurs
- **Organisateurs** : création et gestion des évènements, suivi des réservations, gestion logistique et accès aux statistiques
- **Utilisateurs** : consultation des évènements, réservation de tickets, abonnements, feedbacks et interactions sociales
- **Administrateurs** : supervision globale du système, modération et analyse

## Microservices
- User Service
- Event Service
- Reservation Service
- Subscription Service
- Logistics Service
- Feedback & Complaint Service
- Blog Service

## Technologies
### Backend
- Spring Boot (Java 17)
- NestJS (TypeScript)
- JWT pour l’authentification
- APIs REST

### Bases de données
- MongoDB
- MySQL
- H2 (base embarquée)

### Frontend
- React

## Exigences Non Fonctionnelles
- Haute performance et faible latence
- Sécurité des données et transactions
- Scalabilité
- Haute disponibilité
- Maintenabilité et évolutivité

## Contexte Académique
Projet réalisé par l’équipe **BuildDream** dans le cadre du cours  
**Applications Web Distribuées**.
