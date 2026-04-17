package tn.esprit.microservices.abonnement.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration RabbitMQ pour abonnement-service.
 *
 * Queues :
 * - ABONNEMENT_NOTIFICATION_QUEUE : publie les changements d'abonnement
 *   vers les autres MS (ex: MS Evenement pour vérifier les droits)
 * - PAYMENT_NOTIFICATION_QUEUE : publie les confirmations de paiement
 */
@Configuration
public class RabbitMQConfig {

    // Queue de notification abonnement (producteur)
    public static final String ABONNEMENT_NOTIFICATION_QUEUE = "abonnement.notification.queue";

    // Queue de notification paiement (producteur)
    public static final String PAYMENT_NOTIFICATION_QUEUE = "payment.notification.queue";

    // Queue pour recevoir les demandes de vérification d'abonnement (consommateur)
    public static final String ABONNEMENT_CHECK_QUEUE = "abonnement.check.queue";

    @Bean
    public Queue abonnementNotificationQueue() {
        return new Queue(ABONNEMENT_NOTIFICATION_QUEUE, true);
    }

    @Bean
    public Queue paymentNotificationQueue() {
        return new Queue(PAYMENT_NOTIFICATION_QUEUE, true);
    }

    @Bean
    public Queue abonnementCheckQueue() {
        return new Queue(ABONNEMENT_CHECK_QUEUE, true);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                          MessageConverter converter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(converter);
        return template;
    }

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
