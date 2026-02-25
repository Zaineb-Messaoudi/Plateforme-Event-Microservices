package tn.esprit.microservices.abonnement.service;

import com.stripe.model.Customer;
import com.stripe.model.PaymentMethod;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.microservices.abonnement.dto.request.PaymentMethodRequest;
import tn.esprit.microservices.abonnement.dto.response.PaymentMethodResponse;
import tn.esprit.microservices.abonnement.exception.ResourceNotFoundException;
import tn.esprit.microservices.abonnement.repository.PaymentMethodRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentMethodService {

    private final PaymentMethodRepository paymentMethodRepository;
    private final StripeService stripeService;

    // ══════════════════════════════════════════
    //  ADD PAYMENT METHOD (via Stripe)
    // ══════════════════════════════════════════
    @Transactional
    public PaymentMethodResponse addPaymentMethod(PaymentMethodRequest request) {

        // 1. Check if user already has a Stripe customer
        String stripeCustomerId;
        List<tn.esprit.microservices.abonnement.entity.PaymentMethod> existingMethods =
                paymentMethodRepository.findByUserIdAndActiveTrue(request.getUserId());

        if (!existingMethods.isEmpty()) {
            stripeCustomerId = existingMethods.get(0).getStripeCustomerId();
        } else {
            Customer customer = stripeService.createCustomer(
                    request.getEmail(),
                    request.getName() != null ? request.getName() : request.getEmail()
            );
            stripeCustomerId = customer.getId();
        }

        // 2. Attach payment method to Stripe customer
        PaymentMethod stripePaymentMethod = stripeService.attachPaymentMethod(
                request.getStripePaymentMethodId(),
                stripeCustomerId
        );

        // 3. Get card details from Stripe
        PaymentMethod.Card card = stripePaymentMethod.getCard();

        // 4. Save in our database
        tn.esprit.microservices.abonnement.entity.PaymentMethod pm =
                tn.esprit.microservices.abonnement.entity.PaymentMethod.builder()
                        .userId(request.getUserId())
                        .stripeCustomerId(stripeCustomerId)
                        .stripePaymentMethodId(stripePaymentMethod.getId())
                        .cardLastFour(card.getLast4())
                        .cardBrand(card.getBrand())
                        .expiryMonth(String.valueOf(card.getExpMonth()))
                        .expiryYear(String.valueOf(card.getExpYear()))
                        .holderName(stripePaymentMethod.getBillingDetails().getName())
                        .active(true)
                        .build();

        tn.esprit.microservices.abonnement.entity.PaymentMethod saved =
                paymentMethodRepository.save(pm);

        log.info("Payment method added for user {} via Stripe", request.getUserId());
        return toResponse(saved);
    }

    // ══════════════════════════════════════════
    //  GET USER'S PAYMENT METHODS
    // ══════════════════════════════════════════
    public List<PaymentMethodResponse> getUserPaymentMethods(Long userId) {
        return paymentMethodRepository.findByUserIdAndActiveTrue(userId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // ══════════════════════════════════════════
    //  DELETE (detach from Stripe + soft delete)
    // ══════════════════════════════════════════
    @Transactional
    public void deletePaymentMethod(Long paymentMethodId) {
        tn.esprit.microservices.abonnement.entity.PaymentMethod pm =
                paymentMethodRepository.findById(paymentMethodId)
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Payment method not found: " + paymentMethodId));

        stripeService.detachPaymentMethod(pm.getStripePaymentMethodId());

        pm.setActive(false);
        paymentMethodRepository.save(pm);
        log.info("Payment method {} deleted", paymentMethodId);
    }

    // ══════════════════════════════════════════
    //  CONVERT TO RESPONSE
    // ══════════════════════════════════════════
    private PaymentMethodResponse toResponse(
            tn.esprit.microservices.abonnement.entity.PaymentMethod pm) {
        return PaymentMethodResponse.builder()
                .id(pm.getId())
                .userId(pm.getUserId())
                .cardLastFour(pm.getCardLastFour())
                .cardBrand(pm.getCardBrand())
                .expiryMonth(pm.getExpiryMonth())
                .expiryYear(pm.getExpiryYear())
                .holderName(pm.getHolderName())
                .build();
    }
}