package tn.esprit.microservices.abonnement.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.microservices.abonnement.dto.request.PaymentMethodRequest;
import tn.esprit.microservices.abonnement.dto.response.PaymentMethodResponse;
import tn.esprit.microservices.abonnement.service.PaymentMethodService;

import java.util.List;

@RestController
@RequestMapping("/paymentMethods")
@RequiredArgsConstructor
public class PaymentMethodController {

    private final PaymentMethodService paymentMethodService;

    @PostMapping
    public ResponseEntity<PaymentMethodResponse> add(
            @Valid @RequestBody PaymentMethodRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(paymentMethodService.addPaymentMethod(request));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PaymentMethodResponse>> getUserMethods(
            @PathVariable Long userId) {
        return ResponseEntity.ok(paymentMethodService.getUserPaymentMethods(userId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        paymentMethodService.deletePaymentMethod(id);
        return ResponseEntity.noContent().build();
    }
}