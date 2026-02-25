package tn.esprit.microservices.abonnement.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "payment_method")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentMethod {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    // ── Stripe references ──
    @Column(nullable = false)
    private String stripeCustomerId;

    @Column(nullable = false)
    private String stripePaymentMethodId;

    // ── Card info (from Stripe, read-only) ──
    private String cardLastFour;
    private String cardBrand;
    private String expiryMonth;
    private String expiryYear;
    private String holderName;

    @Column(nullable = false)
    private boolean active = true;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}