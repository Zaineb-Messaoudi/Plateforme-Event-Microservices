package tn.esprit.microservices.abonnement.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import tn.esprit.microservices.abonnement.enums.PlanType;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChangePlanRequest {

    @NotNull(message = "New plan type is required")
    private PlanType newPlanType;

    // null = use default payment method
    private Long paymentMethodId;
}