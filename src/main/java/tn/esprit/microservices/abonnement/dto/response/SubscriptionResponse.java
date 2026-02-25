package tn.esprit.microservices.abonnement.dto.response;

import lombok.*;
import tn.esprit.microservices.abonnement.enums.SubscriptionStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionResponse {

    private Long id;
    private Long userId;
    private PlanResponse plan;
    private SubscriptionStatus status;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private boolean autoRenew;
    private LocalDateTime createdAt;
}