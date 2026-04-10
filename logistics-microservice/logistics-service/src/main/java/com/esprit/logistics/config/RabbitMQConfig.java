package com.esprit.logistics.config;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Configuration RabbitMQ côté CONSOMMATEUR (logistics-service).
 *
 * Le logistics-service déclare les queues dont il a besoin.
 * Le MS Evenement (producteur) publiera sur ces queues.
 *
 * Queues déclarées :
 * - EVENT_LOGISTICS_QUEUE : reçoit les notifications d'événements
 * - LOGISTICS_NOTIFICATION_QUEUE : envoie des notifications de logistique
 */
@Configuration
public class RabbitMQConfig {

    // Queue pour recevoir les événements du MS Evenement
    public static final String EVENT_LOGISTICS_QUEUE = "event.logistics.queue";

    // Queue pour publier les notifications logistiques vers d'autres MS
    public static final String LOGISTICS_NOTIFICATION_QUEUE = "logistics.notification.queue";

    @Bean
    public Queue eventLogisticsQueue() {
        return new Queue(EVENT_LOGISTICS_QUEUE, true); // durable = true
    }

    @Bean
    public Queue logisticsNotificationQueue() {
        return new Queue(LOGISTICS_NOTIFICATION_QUEUE, true);
    }

    // Converter JSON <-> POJO (Jackson2JsonMessageConverter)
    @Bean
    public MessageConverter messageConverter() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // ✨ LA LIGNE MAGIQUE
        return new Jackson2JsonMessageConverter(objectMapper);
    }

    // Factory utilisée par @RabbitListener avec le converter JSON
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory cf, MessageConverter converter) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(cf);
        factory.setMessageConverter(converter);
        factory.setConcurrentConsumers(1);
        factory.setMaxConcurrentConsumers(3);
        return factory;
    }
}
