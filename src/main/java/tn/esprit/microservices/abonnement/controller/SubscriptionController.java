package tn.esprit.microservices.abonnement.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.microservices.abonnement.dto.request.ChangePlanRequest;
import tn.esprit.microservices.abonnement.dto.request.CreateSubscriptionRequest;
import tn.esprit.microservices.abonnement.dto.response.SubscriptionResponse;
import tn.esprit.microservices.abonnement.service.SubscriptionService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;


    @PostMapping
    public ResponseEntity<SubscriptionResponse> subscribe(
            @Valid @RequestBody CreateSubscriptionRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(subscriptionService.subscribe(request));
    }


    @GetMapping("/{id}")
    public ResponseEntity<SubscriptionResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(subscriptionService.getSubscriptionById(id));
    }


    @GetMapping("/user/{userId}/active")
    public ResponseEntity<SubscriptionResponse> getActiveSubscription(
            @PathVariable Long userId) {
        return ResponseEntity.ok(subscriptionService.getActiveSubscription(userId));
    }


    @GetMapping("/user/{userId}")
    public ResponseEntity<List<SubscriptionResponse>> getUserSubscriptions(
            @PathVariable Long userId) {
        return ResponseEntity.ok(subscriptionService.getUserSubscriptions(userId));
    }


    @GetMapping("/user/{userId}/status")
    public ResponseEntity<Map<String, Object>> checkStatus(@PathVariable Long userId) {
        boolean active = subscriptionService.isSubscriptionActive(userId);
        return ResponseEntity.ok(Map.of(
                "userId", userId,
                "active", active
        ));
    }


    @PutMapping("/{id}/cancel")
    public ResponseEntity<SubscriptionResponse> cancel(@PathVariable Long id) {
        return ResponseEntity.ok(subscriptionService.cancelSubscription(id));
    }


    @PutMapping("/user/{userId}/changePlan")
    public ResponseEntity<SubscriptionResponse> changePlan(
            @PathVariable Long userId,
            @Valid @RequestBody ChangePlanRequest request) {
        return ResponseEntity.ok(subscriptionService.changePlan(
                userId,
                request.getNewPlanType(),
                request.getPaymentMethodId()
        ));
    }
}