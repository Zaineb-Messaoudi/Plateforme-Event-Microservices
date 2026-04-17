package tn.esprit.microservices.abonnement.entity;

import jakarta.persistence.*;
import lombok.*;
import tn.esprit.microservices.abonnement.enums.PlanType;

import java.math.BigDecimal;

@Entity
@Table(name = "subscription_plan")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private PlanType planType;

    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    private int durationInDays;

    private int maxEvents;

    private int maxParticipantsPerEvent;

    @Column(length = 1000)
    private String features;

    @Column(nullable = false)
    private boolean active = true;
}
