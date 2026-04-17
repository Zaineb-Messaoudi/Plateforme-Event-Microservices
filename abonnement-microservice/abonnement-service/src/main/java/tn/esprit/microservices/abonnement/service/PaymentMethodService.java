package tn.esprit.microservices.abonnement.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.microservices.abonnement.dto.PaymentMethodDTO;
import tn.esprit.microservices.abonnement.entity.PaymentMethod;
import tn.esprit.microservices.abonnement.repository.PaymentMethodRepository;

import java.util.List;
import java.util.Optional;

@Service
public class PaymentMethodService {

    @Autowired
    private PaymentMethodRepository paymentMethodRepository;

    private static final Logger log = LoggerFactory.getLogger(PaymentMethodService.class);

    public List<PaymentMethod> getAllPaymentMethods() {
        return paymentMethodRepository.findAll();
    }

    public List<PaymentMethod> getByUserId(Long userId) {
        return paymentMethodRepository.findByUserId(userId);
    }

    public List<PaymentMethod> getActiveByUserId(Long userId) {
        return paymentMethodRepository.findByUserIdAndActiveTrue(userId);
    }

    public Optional<PaymentMethod> getById(Long id) {
        return paymentMethodRepository.findById(id);
    }

    public PaymentMethod savePaymentMethod(PaymentMethodDTO dto) {
        PaymentMethod pm = PaymentMethod.builder()
                .userId(dto.getUserId())
                .stripeCustomerId(dto.getStripeCustomerId())
                .stripePaymentMethodId(dto.getStripePaymentMethodId())
                .cardLastFour(dto.getCardLastFour())
                .cardBrand(dto.getCardBrand())
                .expiryMonth(dto.getExpiryMonth())
                .expiryYear(dto.getExpiryYear())
                .holderName(dto.getHolderName())
                .active(true)
                .build();
        log.info("Méthode de paiement ajoutée pour userId={}", dto.getUserId());
        return paymentMethodRepository.save(pm);
    }

    public PaymentMethod deactivate(Long id) {
        return paymentMethodRepository.findById(id).map(pm -> {
            pm.setActive(false);
            log.info("Méthode de paiement désactivée : id={}", id);
            return paymentMethodRepository.save(pm);
        }).orElseThrow(() -> new RuntimeException("Méthode de paiement non trouvée : " + id));
    }

    public void deletePaymentMethod(Long id) {
        paymentMethodRepository.deleteById(id);
    }
}
