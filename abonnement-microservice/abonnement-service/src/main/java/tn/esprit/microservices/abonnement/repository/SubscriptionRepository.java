package tn.esprit.microservices.abonnement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.microservices.abonnement.entity.Subscription;
import tn.esprit.microservices.abonnement.enums.SubscriptionStatus;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    List<Subscription> findByUserId(Long userId);
    Optional<Subscription> findByUserIdAndStatus(Long userId, SubscriptionStatus status);
    List<Subscription> findByStatus(SubscriptionStatus status);
}
