package tn.esprit.microservices.abonnement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO utilisé pour la communication RabbitMQ avec les autres MS.
 * Envoyé quand un abonnement est créé ou mis à jour.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AbonnementEventDTO {
    private Long userId;
    private Long subscriptionId;
    private String planType;       // FREE, BASIC, PREMIUM, ENTERPRISE
    private String status;         // ACTIVE, EXPIRED, CANCELLED...
    private int maxEvents;
    private int maxParticipantsPerEvent;
}
