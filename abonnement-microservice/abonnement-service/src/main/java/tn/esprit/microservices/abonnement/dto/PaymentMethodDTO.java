package tn.esprit.microservices.abonnement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentMethodDTO {
    private Long userId;
    private String stripeCustomerId;
    private String stripePaymentMethodId;
    private String cardLastFour;
    private String cardBrand;
    private String expiryMonth;
    private String expiryYear;
    private String holderName;
}
