package tn.esprit.microservices.abonnement.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.microservices.abonnement.entity.SubscriptionPlan;
import tn.esprit.microservices.abonnement.enums.PlanType;
import tn.esprit.microservices.abonnement.repository.SubscriptionPlanRepository;

import java.util.List;
import java.util.Optional;

@Service
public class SubscriptionPlanService {

    @Autowired
    private SubscriptionPlanRepository planRepository;

    private static final Logger log = LoggerFactory.getLogger(SubscriptionPlanService.class);

    public List<SubscriptionPlan> getAllPlans() {
        return planRepository.findAll();
    }

    public List<SubscriptionPlan> getActivePlans() {
        return planRepository.findByActiveTrue();
    }

    public Optional<SubscriptionPlan> getPlanById(Long id) {
        return planRepository.findById(id);
    }

    public Optional<SubscriptionPlan> getPlanByType(PlanType planType) {
        return planRepository.findByPlanType(planType);
    }

    public SubscriptionPlan savePlan(SubscriptionPlan plan) {
        log.info("Création plan : {}", plan.getName());
        return planRepository.save(plan);
    }

    public SubscriptionPlan updatePlan(Long id, SubscriptionPlan updated) {
        return planRepository.findById(id).map(p -> {
            p.setName(updated.getName());
            p.setDescription(updated.getDescription());
            p.setPrice(updated.getPrice());
            p.setDurationInDays(updated.getDurationInDays());
            p.setMaxEvents(updated.getMaxEvents());
            p.setMaxParticipantsPerEvent(updated.getMaxParticipantsPerEvent());
            p.setFeatures(updated.getFeatures());
            p.setActive(updated.isActive());
            return planRepository.save(p);
        }).orElseThrow(() -> new RuntimeException("Plan non trouvé : " + id));
    }

    public void deletePlan(Long id) {
        planRepository.deleteById(id);
    }
}
