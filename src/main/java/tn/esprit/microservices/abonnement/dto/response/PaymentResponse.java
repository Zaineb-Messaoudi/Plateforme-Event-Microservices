package tn.esprit.microservices.abonnement.dto.response;

import lombok.*;
import tn.esprit.microservices.abonnement.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponse {

    private Long id;
    private Long userId;
    private Long subscriptionId;
    private BigDecimal amount;
    private String currency;
    private PaymentStatus status;
    private String stripePaymentIntentId;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime paidAt;
}