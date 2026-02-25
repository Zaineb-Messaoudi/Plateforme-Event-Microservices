package tn.esprit.microservices.abonnement.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.microservices.abonnement.dto.response.PaymentResponse;
import tn.esprit.microservices.abonnement.service.PaymentService;

import java.util.List;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;


    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(paymentService.getPaymentById(id));
    }


    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PaymentResponse>> getUserPayments(
            @PathVariable Long userId) {
        return ResponseEntity.ok(paymentService.getUserPayments(userId));
    }


    @GetMapping("/subscription/{subscriptionId}")
    public ResponseEntity<List<PaymentResponse>> getBySubscription(
            @PathVariable Long subscriptionId) {
        return ResponseEntity.ok(paymentService.getPaymentsBySubscription(subscriptionId));
    }


    @PostMapping("/{id}/refund")
    public ResponseEntity<PaymentResponse> refund(@PathVariable Long id) {
        return ResponseEntity.ok(paymentService.refundPayment(id));
    }
}