package tn.esprit.microservices.abonnement.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentMethodRequest {

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotBlank(message = "Stripe payment method ID is required")
    private String stripePaymentMethodId;

    @NotBlank(message = "Email is required")
    private String email;

    private String name;
}