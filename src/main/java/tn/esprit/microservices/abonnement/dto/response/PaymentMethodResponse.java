package tn.esprit.microservices.abonnement.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentMethodResponse {

    private Long id;
    private Long userId;
    private String cardLastFour;
    private String cardBrand;
    private String expiryMonth;
    private String expiryYear;
    private String holderName;
}