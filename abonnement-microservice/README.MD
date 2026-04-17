# 💳 Abonnement Microservice — Gestion des Événements
**Projet : Applications Web Distribuées — ESPRIT 2025-2026**

---

## 🏗️ Architecture

```
┌──────────────────────────────────────────────────┐
│               docker-compose                      │
│                                                  │
│  ┌──────────┐  ┌─────────────┐  ┌─────────────┐  │
│  │  Eureka  │  │Config Server│  │ API Gateway │  │
│  │  :8761   │  │   :8888     │  │   :9000     │  │
│  └──────────┘  └─────────────┘  └─────────────┘  │
│                                                  │
│         ┌────────────────────────┐               │
│         │   abonnement-service   │               │
│         │         :8086          │               │
│         │  SubscriptionPlan      │               │
│         │  Subscription          │               │
│         │  PaymentMethod         │               │
│         │  Payment               │               │
│         └───────────┬────────────┘               │
│                     │                            │
│         ┌───────────┴────────────┐               │
│         │       RabbitMQ         │               │
│         │  :5672 / :15672        │               │
│         └────────────────────────┘               │
└──────────────────────────────────────────────────┘
```

---

## 🚀 Lancer le projet

### Sans Docker (IntelliJ) — ordre obligatoire :
```
1. eureka-server       → EurekaServerApplication.java
2. config-server       → ConfigServerApplication.java
3. abonnement-service  → AbonnementServiceApplication.java
4. api-gateway         → ApiGatewayApplication.java
```

### Avec Docker Compose :
```bash
docker-compose up --build
```

---

## 🌐 URLs

| Service            | URL                                  |
|--------------------|--------------------------------------|
| Eureka             | http://localhost:8761                |
| Config Server      | http://localhost:8888                |
| API Gateway        | http://localhost:9000                |
| Abonnement Service | http://localhost:8086                |
| RabbitMQ Admin     | http://localhost:15672 (guest/guest) |
| MySQL DB           | localhost:3306                        |

> Frontend base URL: `http://localhost:9000`
> 
> The API Gateway is the entry point for all frontend requests, and CORS is enabled globally on the gateway.

---

## 📡 Endpoints REST (via Gateway : localhost:9000)

### Plans d'abonnement
| Méthode | URL                    | Description              |
|---------|------------------------|--------------------------|
| GET     | /plans                 | Tous les plans           |
| GET     | /plans/active          | Plans actifs             |
| GET     | /plans/{id}            | Un plan par ID           |
| GET     | /plans/type/{planType} | Par type (FREE/BASIC...) |
| POST    | /plans                 | Créer un plan            |
| PUT     | /plans/{id}            | Modifier un plan         |
| DELETE  | /plans/{id}            | Supprimer                |
| GET     | /plans/welcome         | Message config-server    |

### Abonnements
| Méthode | URL                             | Description               |
|---------|---------------------------------|---------------------------|
| GET     | /subscriptions                  | Tous les abonnements      |
| GET     | /subscriptions/{id}             | Par ID                    |
| GET     | /subscriptions/user/{userId}    | Par utilisateur           |
| GET     | /subscriptions/user/{userId}/active | Abonnement actif      |
| GET     | /subscriptions/user/{userId}/check  | Vérifier si actif     |
| POST    | /subscriptions                  | Créer un abonnement       |
| PATCH   | /subscriptions/{id}/cancel      | Annuler                   |
| DELETE  | /subscriptions/{id}             | Supprimer                 |

### Méthodes de paiement
| Méthode | URL                                  | Description          |
|---------|--------------------------------------|----------------------|
| GET     | /payment-methods                     | Toutes              |
| GET     | /payment-methods/{id}                | Par ID              |
| GET     | /payment-methods/user/{userId}       | Par utilisateur     |
| GET     | /payment-methods/user/{userId}/active| Actives seulement   |
| POST    | /payment-methods                     | Ajouter             |
| PATCH   | /payment-methods/{id}/deactivate     | Désactiver          |
| DELETE  | /payment-methods/{id}                | Supprimer           |

### Paiements
| Méthode | URL                                    | Description           |
|---------|----------------------------------------|-----------------------|
| GET     | /payments                              | Tous les paiements    |
| GET     | /payments/{id}                         | Par ID                |
| GET     | /payments/user/{userId}                | Par utilisateur       |
| GET     | /payments/subscription/{subscriptionId}| Par abonnement        |
| GET     | /payments/status/{status}              | Par statut            |
| POST    | /payments                              | Créer un paiement     |
| POST    | /payments/create-intent                | Créer un PaymentIntent Stripe |
| PATCH   | /payments/{id}/confirm                 | Confirmer             |
| PATCH   | /payments/{id}/confirm-intent          | Confirmer Stripe Intent |
| PATCH   | /payments/{id}/fail                    | Marquer échoué        |
| PATCH   | /payments/{id}/refund                  | Rembourser            |
| DELETE  | /payments/{id}                         | Supprimer             |

---

## 🐇 RabbitMQ — Queues

| Queue                          | Rôle                                      |
|--------------------------------|-------------------------------------------|
| `abonnement.notification.queue`| Notifie les autres MS des changements     |
| `payment.notification.queue`   | Notifie les confirmations de paiement     |
| `abonnement.check.queue`       | Reçoit les demandes de vérification       |

---

## 📝 Exemples Postman

### Créer un plan
```json
POST http://localhost:9000/plans
{
  "name": "Plan Premium",
  "planType": "PREMIUM",
  "description": "Accès illimité aux événements",
  "price": 49.99,
  "durationInDays": 30,
  "maxEvents": 100,
  "maxParticipantsPerEvent": 500,
  "features": "Streaming HD, Support prioritaire",
  "active": true
}
```

### Créer un abonnement
```json
POST http://localhost:9000/subscriptions
{
  "userId": 1,
  "planId": 1,
  "startDate": "2026-04-13T00:00:00",
  "endDate": "2026-05-13T00:00:00",
  "autoRenew": true
}
```

### Ajouter une méthode de paiement
```json
POST http://localhost:9000/payment-methods
{
  "userId": 1,
  "stripeCustomerId": "cus_ABC123",
  "stripePaymentMethodId": "pm_XYZ789",
  "cardLastFour": "4242",
  "cardBrand": "Visa",
  "expiryMonth": "12",
  "expiryYear": "2028",
  "holderName": "Ali Ben Salem"
}
```

### Créer un paiement
```json
POST http://localhost:9000/payments
{
  "userId": 1,
  "subscriptionId": 1,
  "paymentMethodId": 1,
  "amount": 49.99,
  "currency": "TND",
  "stripePaymentIntentId": "pi_ABC123",
  "description": "Paiement abonnement Premium"
}
```
