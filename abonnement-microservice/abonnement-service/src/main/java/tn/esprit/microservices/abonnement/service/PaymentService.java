package tn.esprit.microservices.abonnement.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.microservices.abonnement.dto.PaymentDTO;
import tn.esprit.microservices.abonnement.entity.Payment;
import tn.esprit.microservices.abonnement.entity.PaymentMethod;
import tn.esprit.microservices.abonnement.entity.Subscription;
import tn.esprit.microservices.abonnement.enums.PaymentStatus;
import tn.esprit.microservices.abonnement.messaging.AbonnementProducer;
import tn.esprit.microservices.abonnement.repository.PaymentMethodRepository;
import tn.esprit.microservices.abonnement.repository.PaymentRepository;
import tn.esprit.microservices.abonnement.repository.SubscriptionRepository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private PaymentMethodRepository paymentMethodRepository;

    @Autowired
    private AbonnementProducer abonnementProducer;

    @Autowired
    private StripeService stripeService;

    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);

    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    public List<Payment> getPaymentsByUser(Long userId) {
        return paymentRepository.findByUserId(userId);
    }

    public List<Payment> getPaymentsBySubscription(Long subscriptionId) {
        return paymentRepository.findBySubscriptionId(subscriptionId);
    }

    public Optional<Payment> getPaymentById(Long id) {
        return paymentRepository.findById(id);
    }

    public List<Payment> getByStatus(PaymentStatus status) {
        return paymentRepository.findByStatus(status);
    }

    @Transactional
    public Payment createPayment(PaymentDTO dto) {
        Subscription subscription = subscriptionRepository.findById(dto.getSubscriptionId())
                .orElseThrow(() -> new RuntimeException("Abonnement non trouvé : " + dto.getSubscriptionId()));

        PaymentMethod paymentMethod = null;
        if (dto.getPaymentMethodId() != null) {
            paymentMethod = paymentMethodRepository.findById(dto.getPaymentMethodId())
                    .orElseThrow(() -> new RuntimeException("Méthode de paiement non trouvée : " + dto.getPaymentMethodId()));
        }

        Payment payment = Payment.builder()
                .userId(dto.getUserId())
                .subscription(subscription)
                .paymentMethod(paymentMethod)
                .amount(dto.getAmount())
                .currency(dto.getCurrency() != null ? dto.getCurrency() : "TND")
                .status(PaymentStatus.PENDING)
                .stripePaymentIntentId(dto.getStripePaymentIntentId())
                .description(dto.getDescription())
                .build();

        Payment saved = paymentRepository.save(payment);
        log.info("Paiement créé : id={}, userId={}, montant={}", saved.getId(), dto.getUserId(), dto.getAmount());

        // Notifier via RabbitMQ
        abonnementProducer.sendPaymentNotification(dto);

        return saved;
    }

    @Transactional
    public Map<String, String> createStripePaymentIntent(PaymentDTO dto) {
        try {
            Subscription subscription = subscriptionRepository.findById(dto.getSubscriptionId())
                    .orElseThrow(() -> new RuntimeException("Abonnement non trouvé : " + dto.getSubscriptionId()));

            PaymentMethod paymentMethod = null;
            if (dto.getPaymentMethodId() != null) {
                paymentMethod = paymentMethodRepository.findById(dto.getPaymentMethodId())
                        .orElseThrow(() -> new RuntimeException("Méthode de paiement non trouvée : " + dto.getPaymentMethodId()));
            }

            String currency = dto.getCurrency() != null ? dto.getCurrency() : "TND";
            PaymentIntent intent = stripeService.createPaymentIntent(dto.getAmount(), currency, paymentMethod != null ? paymentMethod.getStripePaymentMethodId() : null);

            Payment payment = Payment.builder()
                    .userId(dto.getUserId())
                    .subscription(subscription)
                    .paymentMethod(paymentMethod)
                    .amount(dto.getAmount())
                    .currency(currency)
                    .status(PaymentStatus.PENDING)
                    .stripePaymentIntentId(intent.getId())
                    .description(dto.getDescription())
                    .build();

            Payment saved = paymentRepository.save(payment);
            log.info("Stripe payment intent créé : id={}, paymentId={}", intent.getId(), saved.getId());

            Map<String, String> response = new HashMap<>();
            response.put("paymentId", String.valueOf(saved.getId()));
            response.put("paymentIntentId", intent.getId());
            response.put("clientSecret", intent.getClientSecret());
            return response;
        } catch (StripeException e) {
            throw new RuntimeException("Erreur Stripe lors de la création du PaymentIntent : " + e.getMessage(), e);
        }
    }

    @Transactional
    public Payment confirmStripePaymentIntent(Long id, String paymentIntentId) {
        try {
            Payment payment = paymentRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Paiement non trouvé : " + id));

            PaymentIntent intent = stripeService.retrievePaymentIntent(paymentIntentId);
            if (!"succeeded".equals(intent.getStatus())) {
                throw new RuntimeException("Stripe PaymentIntent n'est pas encore réussi : " + intent.getStatus());
            }

            payment.setStatus(PaymentStatus.COMPLETED);
            payment.setStripePaymentIntentId(intent.getId());
            if (intent.getCharges() != null && !intent.getCharges().getData().isEmpty()) {
                payment.setStripeChargeId(intent.getCharges().getData().get(0).getId());
            }
            payment.setPaidAt(LocalDateTime.now());
            log.info("Paiement confirmé via Stripe : id={}, paymentIntentId={}", id, paymentIntentId);

            PaymentDTO notifDTO = new PaymentDTO();
            notifDTO.setUserId(payment.getUserId());
            notifDTO.setSubscriptionId(payment.getSubscription().getId());
            notifDTO.setAmount(payment.getAmount());
            notifDTO.setStatus(PaymentStatus.COMPLETED);
            abonnementProducer.sendPaymentNotification(notifDTO);

            return paymentRepository.save(payment);
        } catch (StripeException e) {
            throw new RuntimeException("Erreur Stripe lors de la confirmation du PaymentIntent : " + e.getMessage(), e);
        }
    }

    @Transactional
    public Payment confirmPayment(Long id, String stripeChargeId) {
        return paymentRepository.findById(id).map(p -> {
            p.setStatus(PaymentStatus.COMPLETED);
            p.setStripeChargeId(stripeChargeId);
            p.setPaidAt(LocalDateTime.now());
            log.info("Paiement confirmé : id={}", id);

            PaymentDTO notifDTO = new PaymentDTO();
            notifDTO.setUserId(p.getUserId());
            notifDTO.setSubscriptionId(p.getSubscription().getId());
            notifDTO.setAmount(p.getAmount());
            notifDTO.setStatus(PaymentStatus.COMPLETED);
            abonnementProducer.sendPaymentNotification(notifDTO);

            return paymentRepository.save(p);
        }).orElseThrow(() -> new RuntimeException("Paiement non trouvé : " + id));
    }

    @Transactional
    public Payment failPayment(Long id, String reason) {
        return paymentRepository.findById(id).map(p -> {
            p.setStatus(PaymentStatus.FAILED);
            p.setFailureReason(reason);
            log.info("Paiement échoué : id={}, raison={}", id, reason);
            return paymentRepository.save(p);
        }).orElseThrow(() -> new RuntimeException("Paiement non trouvé : " + id));
    }

    @Transactional
    public Payment refundPayment(Long id) {
        return paymentRepository.findById(id).map(p -> {
            p.setStatus(PaymentStatus.REFUNDED);
            log.info("Paiement remboursé : id={}", id);
            return paymentRepository.save(p);
        }).orElseThrow(() -> new RuntimeException("Paiement non trouvé : " + id));
    }

    public void deletePayment(Long id) {
        paymentRepository.deleteById(id);
    }
}
