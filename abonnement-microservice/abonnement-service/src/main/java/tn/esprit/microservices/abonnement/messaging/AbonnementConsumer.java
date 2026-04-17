package tn.esprit.microservices.abonnement.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import tn.esprit.microservices.abonnement.config.RabbitMQConfig;
import tn.esprit.microservices.abonnement.dto.AbonnementEventDTO;
import tn.esprit.microservices.abonnement.service.SubscriptionService;

/**
 * Consommateur RabbitMQ — reçoit les demandes de vérification d'abonnement
 * depuis d'autres MS (ex: MS Evenement vérifie si un user a le droit de créer un événement).
 */
@Service
public class AbonnementConsumer {

    private final SubscriptionService subscriptionService;
    private static final Logger log = LoggerFactory.getLogger(AbonnementConsumer.class);

    public AbonnementConsumer(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @RabbitListener(queues = RabbitMQConfig.ABONNEMENT_CHECK_QUEUE,
                    containerFactory = "rabbitListenerContainerFactory")
    public void receiveAbonnementCheck(AbonnementEventDTO dto) {
        log.info("📨 Demande vérification abonnement reçue pour userId={}", dto.getUserId());
        subscriptionService.checkUserSubscription(dto.getUserId());
    }
}
