package tn.esprit.microservices.abonnement.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.microservices.abonnement.dto.AbonnementEventDTO;
import tn.esprit.microservices.abonnement.dto.SubscriptionDTO;
import tn.esprit.microservices.abonnement.entity.Subscription;
import tn.esprit.microservices.abonnement.entity.SubscriptionPlan;
import tn.esprit.microservices.abonnement.enums.SubscriptionStatus;
import tn.esprit.microservices.abonnement.messaging.AbonnementProducer;
import tn.esprit.microservices.abonnement.repository.SubscriptionPlanRepository;
import tn.esprit.microservices.abonnement.repository.SubscriptionRepository;

import java.util.List;
import java.util.Optional;

@Service
public class SubscriptionService {

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private SubscriptionPlanRepository planRepository;

    @Autowired
    private AbonnementProducer abonnementProducer;

    private static final Logger log = LoggerFactory.getLogger(SubscriptionService.class);

    public List<Subscription> getAllSubscriptions() {
        return subscriptionRepository.findAll();
    }

    public List<Subscription> getSubscriptionsByUser(Long userId) {
        return subscriptionRepository.findByUserId(userId);
    }

    public Optional<Subscription> getActiveSubscription(Long userId) {
        return subscriptionRepository.findByUserIdAndStatus(userId, SubscriptionStatus.ACTIVE);
    }

    public Optional<Subscription> getSubscriptionById(Long id) {
        return subscriptionRepository.findById(id);
    }

    @Transactional
    public Subscription createSubscription(SubscriptionDTO dto) {
        SubscriptionPlan plan = planRepository.findById(dto.getPlanId())
                .orElseThrow(() -> new RuntimeException("Plan non trouvé : " + dto.getPlanId()));

        Subscription subscription = Subscription.builder()
                .userId(dto.getUserId())
                .plan(plan)
                .status(SubscriptionStatus.ACTIVE)
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .autoRenew(dto.isAutoRenew())
                .build();

        Subscription saved = subscriptionRepository.save(subscription);
        log.info("Abonnement créé pour userId={}, plan={}", dto.getUserId(), plan.getPlanType());

        // Publier notification RabbitMQ vers les autres MS
        AbonnementEventDTO eventDTO = new AbonnementEventDTO(
                saved.getUserId(),
                saved.getId(),
                plan.getPlanType().name(),
                saved.getStatus().name(),
                plan.getMaxEvents(),
                plan.getMaxParticipantsPerEvent()
        );
        abonnementProducer.sendAbonnementNotification(eventDTO);

        return saved;
    }

    @Transactional
    public Subscription cancelSubscription(Long id) {
        return subscriptionRepository.findById(id).map(s -> {
            s.setStatus(SubscriptionStatus.CANCELLED);
            Subscription saved = subscriptionRepository.save(s);
            log.info("Abonnement annulé : id={}", id);

            // Notifier les autres MS
            AbonnementEventDTO eventDTO = new AbonnementEventDTO(
                    saved.getUserId(), saved.getId(),
                    saved.getPlan().getPlanType().name(),
                    SubscriptionStatus.CANCELLED.name(),
                    0, 0
            );
            abonnementProducer.sendAbonnementNotification(eventDTO);
            return saved;
        }).orElseThrow(() -> new RuntimeException("Abonnement non trouvé : " + id));
    }

    public void deleteSubscription(Long id) {
        subscriptionRepository.deleteById(id);
    }

    /**
     * Vérifie si un utilisateur a un abonnement actif.
     * Appelé par le consommateur RabbitMQ.
     */
    public boolean checkUserSubscription(Long userId) {
        Optional<Subscription> active = getActiveSubscription(userId);
        log.info("Vérification abonnement userId={} → actif={}", userId, active.isPresent());
        return active.isPresent();
    }
}
