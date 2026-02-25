package tn.esprit.microservices.abonnement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.microservices.abonnement.entity.SubscriptionPlan;
import tn.esprit.microservices.abonnement.enums.PlanType;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionPlanRepository extends JpaRepository<SubscriptionPlan, Long> {

    Optional<SubscriptionPlan> findByPlanType(PlanType planType);

    List<SubscriptionPlan> findByActiveTrue();
}