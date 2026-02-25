package tn.esprit.microservices.abonnement.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import tn.esprit.microservices.abonnement.enums.PlanType;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateSubscriptionRequest {

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Plan type is required")
    private PlanType planType;

    // REQUIRED for paid plans (BASIC, PREMIUM, ENTERPRISE)
    // null only for FREE plan
    private Long paymentMethodId;

    private boolean autoRenew = true;
}