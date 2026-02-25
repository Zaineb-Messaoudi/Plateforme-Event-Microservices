package tn.esprit.microservices.abonnement.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.microservices.abonnement.dto.request.CreateSubscriptionRequest;
import tn.esprit.microservices.abonnement.dto.response.SubscriptionResponse;
import tn.esprit.microservices.abonnement.entity.Subscription;
import tn.esprit.microservices.abonnement.entity.SubscriptionPlan;
import tn.esprit.microservices.abonnement.enums.PlanType;
import tn.esprit.microservices.abonnement.enums.SubscriptionStatus;
import tn.esprit.microservices.abonnement.exception.PaymentFailedException;
import tn.esprit.microservices.abonnement.exception.ResourceNotFoundException;
import tn.esprit.microservices.abonnement.repository.SubscriptionPlanRepository;
import tn.esprit.microservices.abonnement.repository.SubscriptionRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionPlanRepository planRepository;
    private final PaymentService paymentService;
    private final SubscriptionPlanService planService;

    // ══════════════════════════════════════════
    //  CREATE SUBSCRIPTION
    // ══════════════════════════════════════════
    @Transactional
    public SubscriptionResponse subscribe(CreateSubscriptionRequest request) {

        // 1. Check if user already has an active subscription
        if (subscriptionRepository.existsByUserIdAndStatus(
                request.getUserId(), SubscriptionStatus.ACTIVE)) {
            throw new IllegalStateException(
                    "User already has an active subscription. Cancel it first or change plan.");
        }

        // 2. Find the plan
        SubscriptionPlan plan = planRepository.findByPlanType(request.getPlanType())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Plan not found: " + request.getPlanType()));

        // 3. Build the subscription
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endDate = plan.getDurationInDays() > 0
                ? now.plusDays(plan.getDurationInDays())
                : null;  // FREE plan = no expiry

        Subscription subscription = Subscription.builder()
                .userId(request.getUserId())
                .plan(plan)
                .status(plan.getPlanType() == PlanType.FREE
                        ? SubscriptionStatus.ACTIVE
                        : SubscriptionStatus.PENDING_PAYMENT)
                .startDate(now)
                .endDate(endDate)
                .autoRenew(request.isAutoRenew())
                .build();

        Subscription saved = subscriptionRepository.save(subscription);

        // 4. If paid plan → process payment
        if (plan.getPlanType() != PlanType.FREE) {
            try {
                paymentService.processPayment(
                        saved.getId(),
                        request.getUserId(),
                        request.getPaymentMethodId(),
                        plan.getPrice(),
                        "Subscription to " + plan.getName() + " plan"
                );
                // Payment success → activate subscription
                saved.setStatus(SubscriptionStatus.ACTIVE);
                saved = subscriptionRepository.save(saved);
            } catch (Exception e) {
                // Payment failed → suspend subscription
                saved.setStatus(SubscriptionStatus.SUSPENDED);
                subscriptionRepository.save(saved);
                throw new PaymentFailedException(
                        "Subscription created but payment failed: " + e.getMessage());
            }
        }

        log.info("User {} subscribed to {} plan", request.getUserId(), plan.getName());
        return toResponse(saved);
    }

    // ══════════════════════════════════════════
    //  GET ACTIVE SUBSCRIPTION
    // ══════════════════════════════════════════
    public SubscriptionResponse getActiveSubscription(Long userId) {
        Subscription sub = subscriptionRepository
                .findByUserIdAndStatus(userId, SubscriptionStatus.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No active subscription found for user: " + userId));
        return toResponse(sub);
    }

    // ══════════════════════════════════════════
    //  GET SUBSCRIPTION BY ID
    // ══════════════════════════════════════════
    public SubscriptionResponse getSubscriptionById(Long id) {
        Subscription sub = subscriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Subscription not found: " + id));
        return toResponse(sub);
    }

    // ══════════════════════════════════════════
    //  GET ALL SUBSCRIPTIONS FOR A USER (HISTORY)
    // ══════════════════════════════════════════
    public List<SubscriptionResponse> getUserSubscriptions(Long userId) {
        return subscriptionRepository.findByUserId(userId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // ══════════════════════════════════════════
    //  CHECK IF USER HAS ACTIVE SUBSCRIPTION
    // ══════════════════════════════════════════
    public boolean isSubscriptionActive(Long userId) {
        return subscriptionRepository.existsByUserIdAndStatus(
                userId, SubscriptionStatus.ACTIVE);
    }

    // ══════════════════════════════════════════
    //  CANCEL SUBSCRIPTION
    // ══════════════════════════════════════════
    @Transactional
    public SubscriptionResponse cancelSubscription(Long subscriptionId) {
        Subscription sub = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Subscription not found: " + subscriptionId));

        if (sub.getStatus() == SubscriptionStatus.CANCELLED) {
            throw new IllegalStateException("Subscription is already cancelled");
        }

        sub.setStatus(SubscriptionStatus.CANCELLED);
        sub.setCancelledAt(LocalDateTime.now());
        sub.setAutoRenew(false);

        Subscription saved = subscriptionRepository.save(sub);
        log.info("Subscription {} cancelled for user {}", subscriptionId, sub.getUserId());
        return toResponse(saved);
    }

    // ══════════════════════════════════════════
    //  CHANGE PLAN (UPGRADE / DOWNGRADE)
    // ══════════════════════════════════════════
    @Transactional
    public SubscriptionResponse changePlan(Long userId, PlanType newPlanType, Long paymentMethodId) {

        // 1. Cancel current active subscription
        subscriptionRepository.findByUserIdAndStatus(userId, SubscriptionStatus.ACTIVE)
                .ifPresent(sub -> {
                    sub.setStatus(SubscriptionStatus.CANCELLED);
                    sub.setCancelledAt(LocalDateTime.now());
                    sub.setAutoRenew(false);
                    subscriptionRepository.save(sub);
                    log.info("Old subscription {} cancelled for plan change", sub.getId());
                });

        // 2. Create new subscription with the new plan
        CreateSubscriptionRequest request = CreateSubscriptionRequest.builder()
                .userId(userId)
                .planType(newPlanType)
                .paymentMethodId(paymentMethodId)
                .autoRenew(true)
                .build();

        return subscribe(request);
    }

    // ══════════════════════════════════════════
    //  CONVERT ENTITY → RESPONSE DTO
    // ══════════════════════════════════════════
    private SubscriptionResponse toResponse(Subscription sub) {
        return SubscriptionResponse.builder()
                .id(sub.getId())
                .userId(sub.getUserId())
                .plan(planService.toResponse(sub.getPlan()))
                .status(sub.getStatus())
                .startDate(sub.getStartDate())
                .endDate(sub.getEndDate())
                .autoRenew(sub.isAutoRenew())
                .createdAt(sub.getCreatedAt())
                .build();
    }
}