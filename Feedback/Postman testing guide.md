# Feedback & Reclamation Microservice — Postman Testing Guide

**Project:** BuildDream — Plateforme de Gestion des Évènements
**Microservice:** Feedback & Reclamation
**Author:** Zaineb Messaoudi

---

## Base URLs

| Access | URL |
|--------|-----|
| Direct (Feedback service) | `http://localhost:8081` |
| Via API Gateway | `http://localhost:8087` |
| Eureka Dashboard | `http://localhost:8761` |

> All endpoints below work with **both base URLs**. Simply swap the base URL to test through the gateway.

---

## Registered Services (Eureka)

| Application | Port | Status |
|-------------|------|--------|
| EUREKA | 8761 | UP |
| FEEDBACK | 8081 | UP |
| GATEWAY | 8087 | UP |

---

## FEEDBACK Endpoints

### 1. Create Feedback
| Field | Value |
|-------|-------|
| Method | `POST` |
| URL | `http://localhost:8081/api/feedbacks` |
| Content-Type | `application/json` |

**Body:**
```json
{
    "userId": 1,
    "eventId": 1,
    "organizerId": 1,
    "rating": 4.5,
    "comment": "Great event, very well organized and enjoyable!"
}
```

**Validation rules:**
- `rating`: between `1.0` and `5.0` (half-stars allowed e.g. `3.5`)
- `comment`: minimum 10 characters, maximum 1000 characters
- One feedback per user per event (duplicate returns `409 Conflict`)

**Expected response:** `201 Created`
```json
{
    "id": 1,
    "userId": 1,
    "eventId": 1,
    "organizerId": 1,
    "rating": 4.5,
    "comment": "Great event, very well organized and enjoyable!",
    "status": "ACTIVE",
    "createdAt": "2026-02-25T21:00:00",
    "editDeadline": "2026-02-28T21:00:00",
    "editable": true
}
```

---

### 2. Get Feedback by ID
| Field | Value |
|-------|-------|
| Method | `GET` |
| URL | `http://localhost:8081/api/feedbacks/1` |

**Expected response:** `200 OK`

---

### 3. Update Feedback
| Field | Value |
|-------|-------|
| Method | `PUT` |
| URL | `http://localhost:8081/api/feedbacks/1` |
| Content-Type | `application/json` |

**Body:**
```json
{
    "rating": 3.5,
    "comment": "It was okay, could have been better organized."
}
```

> Both fields are optional — send only what you want to update.
> Returns `403 Forbidden` if the 72-hour edit window has expired.

**Expected response:** `200 OK`

---

### 4. Delete Feedback (Soft-Delete)
| Field | Value |
|-------|-------|
| Method | `DELETE` |
| URL | `http://localhost:8081/api/feedbacks/1` |

> This is a soft-delete — the record is kept in the database with status `DELETED`.

**Expected response:** `204 No Content`

---

### 5. Get Feedbacks by Event
| Field | Value |
|-------|-------|
| Method | `GET` |
| URL | `http://localhost:8081/api/feedbacks/event/1` |

**Expected response:** `200 OK` — array of feedbacks for event with ID 1

---

### 6. Get Feedbacks by User
| Field | Value |
|-------|-------|
| Method | `GET` |
| URL | `http://localhost:8081/api/feedbacks/user/1` |

**Expected response:** `200 OK` — array of feedbacks submitted by user with ID 1

---

### 7. Get Feedbacks by Organizer
| Field | Value |
|-------|-------|
| Method | `GET` |
| URL | `http://localhost:8081/api/feedbacks/organizer/1` |

**Expected response:** `200 OK` — array of feedbacks for all events of organizer with ID 1

---

### 8. Get Event Statistics
| Field | Value |
|-------|-------|
| Method | `GET` |
| URL | `http://localhost:8081/api/feedbacks/stats/event/1` |

**Expected response:** `200 OK`
```json
{
    "eventId": 1,
    "averageRating": 4.5,
    "totalFeedbacks": 1,
    "ratingDistribution": {
        "1": 0,
        "2": 0,
        "3": 0,
        "4": 1,
        "5": 0
    }
}
```

---

## RECLAMATION Endpoints

### 1. Create Reclamation
| Field | Value |
|-------|-------|
| Method | `POST` |
| URL | `http://localhost:8081/api/reclamations` |
| Content-Type | `application/json` |

**Body:**
```json
{
    "userId": 1,
    "eventId": 1,
    "organizerId": 1,
    "subject": "Issue with seating arrangement",
    "description": "The seats assigned to us were not as advertised. We paid for front row but were placed in the back."
}
```

**Validation rules:**
- `subject`: minimum 5 characters, maximum 500 characters
- `description`: minimum 20 characters, maximum 2000 characters

**Expected response:** `201 Created`
```json
{
    "id": 1,
    "userId": 1,
    "eventId": 1,
    "organizerId": 1,
    "subject": "Issue with seating arrangement",
    "description": "The seats assigned to us were not as advertised...",
    "status": "PENDING",
    "organizerResponse": null,
    "respondedAt": null,
    "createdAt": "2026-02-25T21:00:00"
}
```

---

### 2. Get Reclamation by ID
| Field | Value |
|-------|-------|
| Method | `GET` |
| URL | `http://localhost:8081/api/reclamations/1` |

**Expected response:** `200 OK`

---

### 3. Get Reclamations by User
| Field | Value |
|-------|-------|
| Method | `GET` |
| URL | `http://localhost:8081/api/reclamations/user/1` |

**Expected response:** `200 OK` — all reclamations submitted by user with ID 1

---

### 4. Get Reclamations by Organizer
| Field | Value |
|-------|-------|
| Method | `GET` |
| URL | `http://localhost:8081/api/reclamations/organizer/1` |

**Expected response:** `200 OK` — all reclamations directed to organizer with ID 1

---

### 5. Get Reclamations by Organizer + Status
| Field | Value |
|-------|-------|
| Method | `GET` |
| URL | `http://localhost:8081/api/reclamations/organizer/1/status/PENDING` |

**Available status values:**

| Status | Description |
|--------|-------------|
| `PENDING` | Submitted, awaiting organizer response |
| `IN_PROGRESS` | Organizer acknowledged |
| `RESOLVED` | Organizer resolved the issue |
| `REJECTED` | Organizer rejected the reclamation |
| `CLOSED` | Admin closed the reclamation |

**Expected response:** `200 OK`

---

### 6. Get Reclamations by Event
| Field | Value |
|-------|-------|
| Method | `GET` |
| URL | `http://localhost:8081/api/reclamations/event/1` |

**Expected response:** `200 OK` — all reclamations for event with ID 1

---

### 7. Get All Reclamations by Status (Admin)
| Field | Value |
|-------|-------|
| Method | `GET` |
| URL | `http://localhost:8081/api/reclamations/status/PENDING` |

**Expected response:** `200 OK` — all reclamations with the given status across all events

---

### 8. Organizer Responds to Reclamation
| Field | Value |
|-------|-------|
| Method | `PUT` |
| URL | `http://localhost:8081/api/reclamations/1/respond` |
| Content-Type | `application/json` |

**Body:**
```json
{
    "response": "We apologize for the inconvenience. We will process a partial refund within 3 business days.",
    "status": "RESOLVED"
}
```

> `status` must be one of: `RESOLVED`, `REJECTED`, `IN_PROGRESS`
> Returns `400 Bad Request` if reclamation is already `CLOSED`

**Expected response:** `200 OK`

---

### 9. Admin Closes Reclamation
| Field | Value |
|-------|-------|
| Method | `PATCH` |
| URL | `http://localhost:8081/api/reclamations/1/close` |

> No body required.

**Expected response:** `200 OK`

---

## Testing via API Gateway

Replace `http://localhost:8081` with `http://localhost:8087` for all endpoints above.

**Examples:**

```
GET  http://localhost:8087/api/feedbacks/1
GET  http://localhost:8087/api/feedbacks/event/1
GET  http://localhost:8087/api/feedbacks/user/1
GET  http://localhost:8087/api/feedbacks/organizer/1
GET  http://localhost:8087/api/feedbacks/stats/event/1

GET  http://localhost:8087/api/reclamations/1
GET  http://localhost:8087/api/reclamations/user/1
GET  http://localhost:8087/api/reclamations/organizer/1
GET  http://localhost:8087/api/reclamations/event/1
GET  http://localhost:8087/api/reclamations/status/PENDING
GET  http://localhost:8087/api/reclamations/organizer/1/status/PENDING
```

---

## Recommended Test Order

1. `POST /api/feedbacks` — create a feedback, note the returned `id`
2. `GET /api/feedbacks/1` — verify it was created
3. `PUT /api/feedbacks/1` — update rating/comment (within 72h window)
4. `GET /api/feedbacks/stats/event/1` — check rating distribution
5. `POST /api/reclamations` — create a reclamation
6. `GET /api/reclamations/status/PENDING` — verify it appears
7. `PUT /api/reclamations/1/respond` — organizer responds
8. `PATCH /api/reclamations/1/close` — admin closes it
9. `DELETE /api/feedbacks/1` — soft-delete the feedback
10. `GET /api/feedbacks/1` — should return `400` (soft-deleted)

---

## Error Responses

All errors follow this format:

```json
{
    "timestamp": "2026-02-25T21:00:00",
    "status": 400,
    "error": "Bad Request",
    "message": "Descriptive error message here"
}
```

| HTTP Status | Cause |
|-------------|-------|
| `400 Bad Request` | Validation failure or business rule violation |
| `403 Forbidden` | Trying to edit feedback after 72h window |
| `404 Not Found` | Resource does not exist |
| `409 Conflict` | Duplicate feedback (same user + event) |
| `500 Internal Server Error` | Unexpected server error |