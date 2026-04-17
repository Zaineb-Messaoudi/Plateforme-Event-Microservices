package tn.esprit.microservices.abonnement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.microservices.abonnement.dto.PaymentMethodDTO;
import tn.esprit.microservices.abonnement.entity.PaymentMethod;
import tn.esprit.microservices.abonnement.service.PaymentMethodService;

import java.util.List;

@RestController
@RequestMapping("/payment-methods")
public class PaymentMethodController {

    @Autowired
    private PaymentMethodService paymentMethodService;

    @GetMapping
    public List<PaymentMethod> getAllPaymentMethods() {
        return paymentMethodService.getAllPaymentMethods();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentMethod> getById(@PathVariable Long id) {
        return paymentMethodService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public List<PaymentMethod> getByUserId(@PathVariable Long userId) {
        return paymentMethodService.getByUserId(userId);
    }

    @GetMapping("/user/{userId}/active")
    public List<PaymentMethod> getActiveByUserId(@PathVariable Long userId) {
        return paymentMethodService.getActiveByUserId(userId);
    }

    @PostMapping
    public ResponseEntity<PaymentMethod> addPaymentMethod(@RequestBody PaymentMethodDTO dto) {
        return ResponseEntity.ok(paymentMethodService.savePaymentMethod(dto));
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<PaymentMethod> deactivate(@PathVariable Long id) {
        return ResponseEntity.ok(paymentMethodService.deactivate(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePaymentMethod(@PathVariable Long id) {
        paymentMethodService.deletePaymentMethod(id);
        return ResponseEntity.noContent().build();
    }
}
