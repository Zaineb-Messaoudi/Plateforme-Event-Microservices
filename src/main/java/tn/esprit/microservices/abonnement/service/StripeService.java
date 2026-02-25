package tn.esprit.microservices.abonnement.service;

import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.PaymentIntent;
import com.stripe.model.PaymentMethod;
import com.stripe.model.Refund;
import com.stripe.param.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tn.esprit.microservices.abonnement.exception.PaymentFailedException;

import java.math.BigDecimal;

@Service
@Slf4j
public class StripeService {

    // ══════════════════════════════════════════
    //  CREATE STRIPE CUSTOMER
    // ══════════════════════════════════════════
    public Customer createCustomer(String email, String name) {
        try {
            CustomerCreateParams params = CustomerCreateParams.builder()
                    .setEmail(email)
                    .setName(name)
                    .build();

            Customer customer = Customer.create(params);
            log.info("Stripe customer created: {}", customer.getId());
            return customer;

        } catch (StripeException e) {
            log.error("Failed to create Stripe customer", e);
            throw new PaymentFailedException("Failed to create customer: " + e.getMessage());
        }
    }

    // ══════════════════════════════════════════
    //  GET EXISTING CUSTOMER
    // ══════════════════════════════════════════
    public Customer getCustomer(String customerId) {
        try {
            return Customer.retrieve(customerId);
        } catch (StripeException e) {
            log.error("Failed to retrieve Stripe customer", e);
            throw new PaymentFailedException("Failed to retrieve customer: " + e.getMessage());
        }
    }

    // ══════════════════════════════════════════
    //  ATTACH PAYMENT METHOD TO CUSTOMER
    // ══════════════════════════════════════════
    public PaymentMethod attachPaymentMethod(String paymentMethodId, String customerId) {
        try {
            PaymentMethod paymentMethod = PaymentMethod.retrieve(paymentMethodId);

            PaymentMethodAttachParams params = PaymentMethodAttachParams.builder()
                    .setCustomer(customerId)
                    .build();

            PaymentMethod attached = paymentMethod.attach(params);
            log.info("Payment method {} attached to customer {}", paymentMethodId, customerId);
            return attached;

        } catch (StripeException e) {
            log.error("Failed to attach payment method", e);
            throw new PaymentFailedException("Failed to attach payment method: " + e.getMessage());
        }
    }

    // ══════════════════════════════════════════
    //  DETACH PAYMENT METHOD FROM CUSTOMER
    // ══════════════════════════════════════════
    public void detachPaymentMethod(String paymentMethodId) {
        try {
            PaymentMethod paymentMethod = PaymentMethod.retrieve(paymentMethodId);
            paymentMethod.detach();
            log.info("Payment method {} detached", paymentMethodId);

        } catch (StripeException e) {
            log.error("Failed to detach payment method", e);
            throw new PaymentFailedException("Failed to detach payment method: " + e.getMessage());
        }
    }

    // ══════════════════════════════════════════
    //  RETRIEVE PAYMENT METHOD DETAILS
    // ══════════════════════════════════════════
    public PaymentMethod retrievePaymentMethod(String paymentMethodId) {
        try {
            return PaymentMethod.retrieve(paymentMethodId);
        } catch (StripeException e) {
            log.error("Failed to retrieve payment method", e);
            throw new PaymentFailedException("Failed to retrieve payment method: " + e.getMessage());
        }
    }

    // ══════════════════════════════════════════
    //  CREATE AND CONFIRM PAYMENT
    // ══════════════════════════════════════════
    public PaymentIntent createPayment(
            BigDecimal amount,
            String currency,
            String stripePaymentMethodId,
            String stripeCustomerId,
            String description
    ) {
        try {
            // Convert to cents (Stripe uses smallest currency unit)
            long amountInCents = amount.multiply(BigDecimal.valueOf(100)).longValue();

            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount(amountInCents)
                    .setCurrency(currency)
                    .setPaymentMethod(stripePaymentMethodId)
                    .setCustomer(stripeCustomerId)
                    .setDescription(description)
                    .setConfirm(true)                    // charge immediately
                    .setAutomaticPaymentMethods(
                            PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                    .setEnabled(true)
                                    .setAllowRedirects(
                                            PaymentIntentCreateParams.AutomaticPaymentMethods
                                                    .AllowRedirects.NEVER)
                                    .build()
                    )
                    .build();

            PaymentIntent intent = PaymentIntent.create(params);
            log.info("Payment created: {} | Status: {}", intent.getId(), intent.getStatus());
            return intent;

        } catch (StripeException e) {
            log.error("Stripe payment failed", e);
            throw new PaymentFailedException("Payment failed: " + e.getMessage());
        }
    }

    // ══════════════════════════════════════════
    //  REFUND PAYMENT
    // ══════════════════════════════════════════
    public Refund refundPayment(String paymentIntentId) {
        try {
            RefundCreateParams params = RefundCreateParams.builder()
                    .setPaymentIntent(paymentIntentId)
                    .build();

            Refund refund = Refund.create(params);
            log.info("Payment {} refunded. Refund ID: {}", paymentIntentId, refund.getId());
            return refund;

        } catch (StripeException e) {
            log.error("Stripe refund failed", e);
            throw new PaymentFailedException("Refund failed: " + e.getMessage());
        }
    }
}