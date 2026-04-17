package tn.esprit.microservices.abonnement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import tn.esprit.microservices.abonnement.enums.PlanType;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionPlanDTO {
    private Long id;
    private String name;
    private PlanType planType;
    private String description;
    private BigDecimal price;
    private int durationInDays;
    private int maxEvents;
    private int maxParticipantsPerEvent;
    private String features;
    private boolean active;
}
