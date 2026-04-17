package tn.esprit.microservices.abonnement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.microservices.abonnement.dto.PaymentDTO;
import tn.esprit.microservices.abonnement.entity.Payment;
import tn.esprit.microservices.abonnement.enums.PaymentStatus;
import tn.esprit.microservices.abonnement.service.PaymentService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @GetMapping
    public List<Payment> getAllPayments() {
        return paymentService.getAllPayments();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Payment> getPaymentById(@PathVariable Long id) {
        return paymentService.getPaymentById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public List<Payment> getByUserId(@PathVariable Long userId) {
        return paymentService.getPaymentsByUser(userId);
    }

    @GetMapping("/subscription/{subscriptionId}")
    public List<Payment> getBySubscription(@PathVariable Long subscriptionId) {
        return paymentService.getPaymentsBySubscription(subscriptionId);
    }

    @GetMapping("/status/{status}")
    public List<Payment> getByStatus(@PathVariable PaymentStatus status) {
        return paymentService.getByStatus(status);
    }

    @PostMapping
    public ResponseEntity<Payment> createPayment(@RequestBody PaymentDTO dto) {
        return ResponseEntity.ok(paymentService.createPayment(dto));
    }

    @PostMapping("/create-intent")
    public ResponseEntity<Map<String, String>> createStripePaymentIntent(@RequestBody PaymentDTO dto) {
        return ResponseEntity.ok(paymentService.createStripePaymentIntent(dto));
    }

    @PatchMapping("/{id}/confirm-intent")
    public ResponseEntity<Payment> confirmStripePaymentIntent(@PathVariable Long id,
                                                              @RequestParam String paymentIntentId) {
        return ResponseEntity.ok(paymentService.confirmStripePaymentIntent(id, paymentIntentId));
    }

    @PatchMapping("/{id}/confirm")
    public ResponseEntity<Payment> confirmPayment(@PathVariable Long id,
                                                   @RequestParam String stripeChargeId) {
        return ResponseEntity.ok(paymentService.confirmPayment(id, stripeChargeId));
    }

    @PatchMapping("/{id}/fail")
    public ResponseEntity<Payment> failPayment(@PathVariable Long id,
                                                @RequestParam String reason) {
        return ResponseEntity.ok(paymentService.failPayment(id, reason));
    }

    @PatchMapping("/{id}/refund")
    public ResponseEntity<Payment> refundPayment(@PathVariable Long id) {
        return ResponseEntity.ok(paymentService.refundPayment(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePayment(@PathVariable Long id) {
        paymentService.deletePayment(id);
        return ResponseEntity.noContent().build();
    }
}
