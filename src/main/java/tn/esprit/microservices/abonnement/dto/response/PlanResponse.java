package tn.esprit.microservices.abonnement.dto.response;

import lombok.*;
import tn.esprit.microservices.abonnement.enums.PlanType;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlanResponse {

    private Long id;
    private String name;
    private PlanType planType;
    private String description;
    private BigDecimal price;
    private int durationInDays;
    private int maxEvents;
    private int maxParticipantsPerEvent;
    private List<String> features;
}