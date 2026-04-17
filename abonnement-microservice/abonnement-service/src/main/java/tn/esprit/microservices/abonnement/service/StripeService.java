package tn.esprit.microservices.abonnement.service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class StripeService {

    @Value("${stripe.api.secret-key}")
    private String stripeSecretKey;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeSecretKey;
    }

    public PaymentIntent createPaymentIntent(BigDecimal amount, String currency, String paymentMethodId) throws StripeException {
        long amountInCents = amount.multiply(BigDecimal.valueOf(100)).longValueExact();

        PaymentIntentCreateParams.Builder paramsBuilder = PaymentIntentCreateParams.builder()
                .setCurrency(currency.toLowerCase())
                .setAmount(amountInCents)
                .setAutomaticPaymentMethods(
                        PaymentIntentCreateParams.AutomaticPaymentMethods.builder().setEnabled(true).build()
                );

        if (paymentMethodId != null && !paymentMethodId.isBlank()) {
            paramsBuilder.setPaymentMethod(paymentMethodId);
            paramsBuilder.setConfirm(true);
        }

        return PaymentIntent.create(paramsBuilder.build());
    }

    public PaymentIntent retrievePaymentIntent(String paymentIntentId) throws StripeException {
        return PaymentIntent.retrieve(paymentIntentId);
    }
}
