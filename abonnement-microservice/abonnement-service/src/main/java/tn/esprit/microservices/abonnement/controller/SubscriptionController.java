package tn.esprit.microservices.abonnement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.microservices.abonnement.dto.SubscriptionDTO;
import tn.esprit.microservices.abonnement.entity.Subscription;
import tn.esprit.microservices.abonnement.service.SubscriptionService;

import java.util.List;

@RestController
@RequestMapping("/subscriptions")
public class SubscriptionController {

    @Autowired
    private SubscriptionService subscriptionService;

    @GetMapping
    public List<Subscription> getAllSubscriptions() {
        return subscriptionService.getAllSubscriptions();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Subscription> getSubscriptionById(@PathVariable Long id) {
        return subscriptionService.getSubscriptionById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public List<Subscription> getByUserId(@PathVariable Long userId) {
        return subscriptionService.getSubscriptionsByUser(userId);
    }

    @GetMapping("/user/{userId}/active")
    public ResponseEntity<Subscription> getActiveSubscription(@PathVariable Long userId) {
        return subscriptionService.getActiveSubscription(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}/check")
    public ResponseEntity<Boolean> checkSubscription(@PathVariable Long userId) {
        return ResponseEntity.ok(subscriptionService.checkUserSubscription(userId));
    }

    @PostMapping
    public ResponseEntity<Subscription> createSubscription(@RequestBody SubscriptionDTO dto) {
        return ResponseEntity.ok(subscriptionService.createSubscription(dto));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<Subscription> cancelSubscription(@PathVariable Long id) {
        return ResponseEntity.ok(subscriptionService.cancelSubscription(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSubscription(@PathVariable Long id) {
        subscriptionService.deleteSubscription(id);
        return ResponseEntity.noContent().build();
    }
}
