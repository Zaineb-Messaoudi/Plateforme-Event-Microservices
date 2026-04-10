package com.esprit.logistics.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

/**
 * DTO utilisé pour recevoir les événements du MS Evenement via RabbitMQ.
 * Ce DTO représente les données partagées — séparé de l'entité pour
 * garantir l'indépendance entre les microservices.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LogisticsEventDTO {
    private Long eventId;
    private String eventName;
    private String location;
    private LocalDate eventDate;
    private String status; // PLANNED, ONGOING, COMPLETED, CANCELLED
}
