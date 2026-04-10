# 📦 Logistics Microservice — Gestion des Événements
**Projet : Applications Web Distribuées — ESPRIT 2025-2026**

---

## 🏗️ Architecture

```
┌─────────────────────────────────────────────────────────┐
│                    docker-compose                        │
│                                                         │
│  ┌──────────┐   ┌─────────────┐   ┌──────────────┐     │
│  │  Eureka  │   │Config Server│   │  API Gateway │     │
│  │ :8761    │   │   :8888     │   │    :9000     │     │
│  └────┬─────┘   └──────┬──────┘   └──────┬───────┘     │
│       │                │                  │             │
│       └────────────────┴──────────────────┘             │
│                         │                               │
│              ┌──────────┴──────────┐                    │
│              │  logistics-service  │                    │
│              │       :8085         │                    │
│              │  Equipment / Staff  │                    │
│              │  Assignment/Schedule│                    │
│              └──────────┬──────────┘                    │
│                         │                               │
│              ┌──────────┴──────────┐                    │
│              │     RabbitMQ        │                    │
│              │  :5672 / :15672     │                    │
│              └─────────────────────┘                    │
└─────────────────────────────────────────────────────────┘
```

---

## 🚀 Lancer le projet

### Option 1 — Sans Docker (développement)

Lancer dans cet ordre :
```bash
# 1. Eureka Server
cd eureka-server && mvn spring-boot:run

# 2. Config Server
cd config-server && mvn spring-boot:run

# 3. Logistics Service
cd logistics-service && mvn spring-boot:run

# 4. API Gateway
cd api-gateway && mvn spring-boot:run
```

> ⚠️ RabbitMQ doit être installé et démarré localement (port 5672)

### Option 2 — Avec Docker Compose

```bash
# Build + lancement de tous les services
docker-compose up --build

# En arrière-plan
docker-compose up --build -d

# Arrêter
docker-compose down
```

---

## 🌐 URLs d'accès

| Service           | URL                                      |
|-------------------|------------------------------------------|
| Eureka Dashboard  | http://localhost:8761                    |
| Config Server     | http://localhost:8888                    |
| API Gateway       | http://localhost:9000                    |
| Logistics Service | http://localhost:8085                    |
| RabbitMQ Admin    | http://localhost:15672 (guest/guest)     |
| H2 Console        | http://localhost:8085/h2-console         |

---

## 📡 Endpoints REST (via Gateway : localhost:9000)

### Equipment (Matériel)
| Méthode | URL                              | Description                    |
|---------|----------------------------------|--------------------------------|
| GET     | /equipments                      | Liste tout le matériel         |
| GET     | /equipments/{id}                 | Détail d'un équipement         |
| POST    | /equipments                      | Créer un équipement            |
| PUT     | /equipments/{id}                 | Mettre à jour                  |
| DELETE  | /equipments/{id}                 | Supprimer                      |
| GET     | /equipments/status/{status}      | Filtrer par statut             |
| GET     | /equipments/type/{type}          | Filtrer par type               |
| GET     | /equipments/welcome              | Message config-server          |

### Staff (Personnel)
| Méthode | URL                              | Description                    |
|---------|----------------------------------|--------------------------------|
| GET     | /staffs                          | Liste tout le personnel        |
| GET     | /staffs/{id}                     | Détail d'un membre             |
| POST    | /staffs                          | Créer un membre                |
| PUT     | /staffs/{id}                     | Mettre à jour                  |
| DELETE  | /staffs/{id}                     | Supprimer                      |
| GET     | /staffs/available                | Personnel disponible           |
| GET     | /staffs/role/{role}              | Filtrer par rôle               |

### Assignment (Affectation)
| Méthode | URL                              | Description                        |
|---------|----------------------------------|------------------------------------|
| GET     | /assignments                     | Liste toutes les affectations      |
| GET     | /assignments/{id}                | Détail d'une affectation           |
| GET     | /assignments/event/{eventId}     | Affectations pour un événement     |
| POST    | /assignments                     | Créer une affectation              |
| PATCH   | /assignments/{id}/status         | Changer le statut                  |
| DELETE  | /assignments/{id}                | Supprimer                          |
| GET     | /assignments/events              | Événements (via Feign → MS Event)  |
| GET     | /assignments/events/{id}         | Un événement (via Feign)           |

### Schedule (Planning)
| Méthode | URL                                    | Description              |
|---------|----------------------------------------|--------------------------|
| GET     | /schedules                             | Liste tous les plannings |
| GET     | /schedules/{id}                        | Détail d'un planning     |
| GET     | /schedules/assignment/{assignmentId}   | Plannings d'une affectat.|
| GET     | /schedules/priority/{priority}         | Filtrer par priorité     |
| POST    | /schedules/assignment/{assignmentId}   | Créer un planning        |
| PUT     | /schedules/{id}                        | Mettre à jour            |
| DELETE  | /schedules/{id}                        | Supprimer                |

---

## 🐇 RabbitMQ — Queues

| Queue                        | Rôle                                               |
|------------------------------|----------------------------------------------------|
| `event.logistics.queue`      | **Consommateur** — reçoit les événements du MS Event |
| `logistics.notification.queue` | **Producteur** — envoie les notifications d'affectation |

### Intégration avec le MS Evenement (vos camarades)

Le MS Evenement doit publier sur `event.logistics.queue` un message JSON :
```json
{
  "eventId": 1,
  "eventName": "Conférence Tech",
  "location": "Salle A",
  "eventDate": "2026-05-15",
  "status": "PLANNED"
}
```

---

## 🔗 Intégration Feign Client

Le logistics-service utilise Feign Client pour appeler le MS Evenement.

Dans `EventClient.java`, le nom du service est `event-service`.
Votre camarade doit configurer dans son `application.properties` :
```properties
spring.application.name=event-service
```

---

## 📝 Exemples de requêtes Postman

### Créer un équipement
```json
POST http://localhost:9000/equipments
{
  "name": "Microphone Shure",
  "type": "son",
  "quantity": 10,
  "status": "AVAILABLE",
  "location": "Salle B"
}
```

### Créer un membre du personnel
```json
POST http://localhost:9000/staffs
{
  "name": "Ali Ben Salem",
  "role": "technicien",
  "availability": true,
  "contact": "ali@esprit.tn"
}
```

### Créer une affectation
```json
POST http://localhost:9000/assignments
{
  "eventId": 1,
  "equipmentId": 1,
  "staffId": 1,
  "date": "2026-05-15",
  "status": "PENDING"
}
```

### Créer un planning
```json
POST http://localhost:9000/schedules/assignment/1
{
  "startTime": "2026-05-15T08:00:00",
  "endTime": "2026-05-15T18:00:00",
  "priority": "HIGH"
}
```

---

## 🔄 Rafraîchir la config sans redémarrer

```bash
POST http://localhost:8085/actuator/refresh
```

---

## 👥 Intégration avec les autres modules

Ce microservice est **autonome et extensible**. Pour l'intégrer :

1. **MS Evenement** → publier sur `event.logistics.queue` via RabbitMQ
2. **MS Notification** → écouter `logistics.notification.queue`
3. **Feign** → tous les MS s'enregistrent sur Eureka, la communication est automatique
