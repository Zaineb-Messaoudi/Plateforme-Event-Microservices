package tn.esprit.microservices.abonnement.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import tn.esprit.microservices.abonnement.config.RabbitMQConfig;
import tn.esprit.microservices.abonnement.dto.AbonnementEventDTO;
import tn.esprit.microservices.abonnement.dto.PaymentDTO;

/**
 * Producteur RabbitMQ — publie les notifications d'abonnement et paiement
 * vers les autres microservices (MS Evenement, MS Notification...).
 */
@Service
public class AbonnementProducer {

    private final RabbitTemplate rabbitTemplate;
    private static final Logger log = LoggerFactory.getLogger(AbonnementProducer.class);

    public AbonnementProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendAbonnementNotification(AbonnementEventDTO dto) {
        try {
            rabbitTemplate.convertAndSend(RabbitMQConfig.ABONNEMENT_NOTIFICATION_QUEUE, dto);
            log.info("📤 Notification abonnement envoyée pour userId={}, plan={}",
                    dto.getUserId(), dto.getPlanType());
        } catch (AmqpException e) {
            log.error("Erreur envoi notification abonnement", e);
            throw e;
        }
    }

    public void sendPaymentNotification(PaymentDTO dto) {
        try {
            rabbitTemplate.convertAndSend(RabbitMQConfig.PAYMENT_NOTIFICATION_QUEUE, dto);
            log.info("📤 Notification paiement envoyée pour userId={}, statut={}",
                    dto.getUserId(), dto.getStatus());
        } catch (AmqpException e) {
            log.error("Erreur envoi notification paiement", e);
            throw e;
        }
    }
}
