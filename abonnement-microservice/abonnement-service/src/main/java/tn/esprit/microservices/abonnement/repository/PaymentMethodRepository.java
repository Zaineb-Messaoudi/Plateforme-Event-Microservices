package tn.esprit.microservices.abonnement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.microservices.abonnement.entity.PaymentMethod;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, Long> {
    List<PaymentMethod> findByUserId(Long userId);
    List<PaymentMethod> findByUserIdAndActiveTrue(Long userId);
    Optional<PaymentMethod> findByStripePaymentMethodId(String stripePaymentMethodId);
}
