package com.esprit.logistics.messaging;

import com.esprit.logistics.config.RabbitMQConfig;
import com.esprit.logistics.dto.LogisticsEventDTO;
import com.esprit.logistics.service.AssignmentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

/**
 * Consommateur RabbitMQ du logistics-service.
 * Ecoute la queue EVENT_LOGISTICS_QUEUE pour recevoir les notifications
 * du MS Evenement (ex: nouvel événement créé, événement mis à jour).
 */
@Service
public class EventConsumer {

    private final AssignmentService assignmentService;
    private static final Logger log = LoggerFactory.getLogger(EventConsumer.class);

    public EventConsumer(AssignmentService assignmentService) {
        this.assignmentService = assignmentService;
    }

    @RabbitListener(queues = RabbitMQConfig.EVENT_LOGISTICS_QUEUE,
                    containerFactory = "rabbitListenerContainerFactory")
    public void receiveEvent(LogisticsEventDTO eventDTO) {
        log.info("📨 Evenement reçu depuis RabbitMQ : eventId={}, nom={}",
                eventDTO.getEventId(), eventDTO.getEventName());

        // Traitement métier : préparer la logistique pour cet événement
        assignmentService.prepareLogisticsForEvent(eventDTO);
    }
}
