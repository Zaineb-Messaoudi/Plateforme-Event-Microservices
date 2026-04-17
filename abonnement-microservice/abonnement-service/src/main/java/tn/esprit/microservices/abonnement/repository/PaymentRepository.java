package tn.esprit.microservices.abonnement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.microservices.abonnement.entity.Payment;
import tn.esprit.microservices.abonnement.enums.PaymentStatus;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByUserId(Long userId);
    List<Payment> findBySubscriptionId(Long subscriptionId);
    List<Payment> findByStatus(PaymentStatus status);
    Optional<Payment> findByStripePaymentIntentId(String stripePaymentIntentId);
}
