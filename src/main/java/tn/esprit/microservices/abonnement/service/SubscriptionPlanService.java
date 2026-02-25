package tn.esprit.microservices.abonnement.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.microservices.abonnement.dto.response.PlanResponse;
import tn.esprit.microservices.abonnement.entity.SubscriptionPlan;
import tn.esprit.microservices.abonnement.enums.PlanType;
import tn.esprit.microservices.abonnement.exception.ResourceNotFoundException;
import tn.esprit.microservices.abonnement.repository.SubscriptionPlanRepository;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SubscriptionPlanService {

    private final SubscriptionPlanRepository planRepository;

    // ── Get all active plans ──
    public List<PlanResponse> getAllActivePlans() {
        return planRepository.findByActiveTrue()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // ── Get plan by type ──
    public PlanResponse getPlanByType(PlanType planType) {
        SubscriptionPlan plan = planRepository.findByPlanType(planType)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Plan not found: " + planType));
        return toResponse(plan);
    }

    // ── Get plan by ID ──
    public PlanResponse getPlanById(Long id) {
        SubscriptionPlan plan = planRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Plan not found with id: " + id));
        return toResponse(plan);
    }

    // ── Convert Entity → Response DTO ──
    public PlanResponse toResponse(SubscriptionPlan plan) {
        return PlanResponse.builder()
                .id(plan.getId())
                .name(plan.getName())
                .planType(plan.getPlanType())
                .description(plan.getDescription())
                .price(plan.getPrice())
                .durationInDays(plan.getDurationInDays())
                .maxEvents(plan.getMaxEvents())
                .maxParticipantsPerEvent(plan.getMaxParticipantsPerEvent())
                .features(plan.getFeatures() != null
                        ? Arrays.asList(plan.getFeatures().split(","))
                        : List.of())
                .build();
    }
}