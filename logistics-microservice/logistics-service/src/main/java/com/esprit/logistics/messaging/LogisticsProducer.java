package com.esprit.logistics.messaging;

import com.esprit.logistics.config.RabbitMQConfig;
import com.esprit.logistics.dto.AssignmentDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

/**
 * Producteur RabbitMQ du logistics-service.
 * Publie les notifications d'affectation vers LOGISTICS_NOTIFICATION_QUEUE
 * afin que d'autres MS (ex: MS Notification) puissent réagir.
 */
@Service
public class LogisticsProducer {

    private final RabbitTemplate rabbitTemplate;
    private static final Logger log = LoggerFactory.getLogger(LogisticsProducer.class);

    public LogisticsProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendAssignmentNotification(AssignmentDTO assignmentDTO) {
        try {
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.LOGISTICS_NOTIFICATION_QUEUE,
                    assignmentDTO
            );
            log.info("📤 Notification logistique envoyée pour eventId={}",
                    assignmentDTO.getEventId());
        } catch (AmqpException e) {
            log.error("Erreur lors de l'envoi de la notification logistique", e);
            throw e;
        }
    }
}
