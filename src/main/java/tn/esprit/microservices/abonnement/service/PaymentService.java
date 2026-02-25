package tn.esprit.microservices.abonnement.service;

import com.stripe.model.PaymentIntent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.microservices.abonnement.dto.response.PaymentResponse;
import tn.esprit.microservices.abonnement.entity.Payment;
import tn.esprit.microservices.abonnement.entity.PaymentMethod;
import tn.esprit.microservices.abonnement.entity.Subscription;
import tn.esprit.microservices.abonnement.enums.PaymentStatus;
import tn.esprit.microservices.abonnement.exception.PaymentFailedException;
import tn.esprit.microservices.abonnement.exception.ResourceNotFoundException;
import tn.esprit.microservices.abonnement.repository.PaymentMethodRepository;
import tn.esprit.microservices.abonnement.repository.PaymentRepository;
import tn.esprit.microservices.abonnement.repository.SubscriptionRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final StripeService stripeService;

    // ══════════════════════════════════════════
    //  PROCESS PAYMENT (via Stripe)
    // ══════════════════════════════════════════
    @Transactional
    public PaymentResponse processPayment(
            Long subscriptionId,
            Long userId,
            Long paymentMethodId,
            BigDecimal amount,
            String description
    ) {
        // 1. Find subscription
        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Subscription not found: " + subscriptionId));

        // 2. Find payment method (ALWAYS required now)
        if (paymentMethodId == null) {
            throw new PaymentFailedException(
                    "Payment method ID is required. Please select a card.");
        }

        PaymentMethod paymentMethod = paymentMethodRepository.findById(paymentMethodId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Payment method not found: " + paymentMethodId));

        // 3. Create payment record (PENDING)
        Payment payment = Payment.builder()
                .userId(userId)
                .subscription(subscription)
                .paymentMethod(paymentMethod)
                .amount(amount)
                .currency("usd")
                .status(PaymentStatus.PENDING)
                .description(description)
                .build();

        payment = paymentRepository.save(payment);

        // 4. Charge via Stripe
        try {
            PaymentIntent intent = stripeService.createPayment(
                    amount,
                    "usd",
                    paymentMethod.getStripePaymentMethodId(),
                    paymentMethod.getStripeCustomerId(),
                    description
            );

            if ("succeeded".equals(intent.getStatus())) {
                payment.setStatus(PaymentStatus.COMPLETED);
                payment.setPaidAt(LocalDateTime.now());
                payment.setStripePaymentIntentId(intent.getId());
                payment.setStripeChargeId(intent.getLatestCharge());
                log.info("Payment {} completed via Stripe", payment.getId());
            } else {
                payment.setStatus(PaymentStatus.FAILED);
                payment.setFailureReason("Stripe status: " + intent.getStatus());
                paymentRepository.save(payment);
                throw new PaymentFailedException(
                        "Payment not completed. Status: " + intent.getStatus());
            }

        } catch (PaymentFailedException e) {
            payment.setStatus(PaymentStatus.FAILED);
            payment.setFailureReason(e.getMessage());
            paymentRepository.save(payment);
            throw e;
        }

        return toResponse(paymentRepository.save(payment));
    }

    // ══════════════════════════════════════════
    //  GET USER PAYMENTS
    // ══════════════════════════════════════════
    public List<PaymentResponse> getUserPayments(Long userId) {
        return paymentRepository.findByUserId(userId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // ══════════════════════════════════════════
    //  GET PAYMENT BY ID
    // ══════════════════════════════════════════
    public PaymentResponse getPaymentById(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Payment not found: " + paymentId));
        return toResponse(payment);
    }

    // ══════════════════════════════════════════
    //  GET PAYMENTS BY SUBSCRIPTION
    // ══════════════════════════════════════════
    public List<PaymentResponse> getPaymentsBySubscription(Long subscriptionId) {
        return paymentRepository.findBySubscriptionId(subscriptionId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // ══════════════════════════════════════════
    //  REFUND (via Stripe)
    // ══════════════════════════════════════════
    @Transactional
    public PaymentResponse refundPayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Payment not found: " + paymentId));

        if (payment.getStatus() != PaymentStatus.COMPLETED) {
            throw new PaymentFailedException(
                    "Can only refund completed payments. Current: " + payment.getStatus());
        }

        stripeService.refundPayment(payment.getStripePaymentIntentId());

        payment.setStatus(PaymentStatus.REFUNDED);
        Payment saved = paymentRepository.save(payment);
        log.info("Payment {} refunded via Stripe", paymentId);
        return toResponse(saved);
    }

    // ══════════════════════════════════════════
    //  CONVERT TO RESPONSE
    // ══════════════════════════════════════════
    private PaymentResponse toResponse(Payment p) {
        return PaymentResponse.builder()
                .id(p.getId())
                .userId(p.getUserId())
                .subscriptionId(p.getSubscription().getId())
                .amount(p.getAmount())
                .currency(p.getCurrency())
                .status(p.getStatus())
                .stripePaymentIntentId(p.getStripePaymentIntentId())
                .description(p.getDescription())
                .createdAt(p.getCreatedAt())
                .paidAt(p.getPaidAt())
                .build();
    }
}