package tn.esprit.microservices.abonnement.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Feign Client vers le MS Evenement.
 * Permet de vérifier les droits d'un utilisateur selon son abonnement.
 * Le nom "event-service" doit correspondre au spring.application.name du MS Evenement.
 */
@FeignClient(name = "event-service", fallback = EventClientFallback.class)
public interface EventClient {

    @GetMapping("/events/count/{userId}")
    int getEventCountByUser(@PathVariable("userId") Long userId);
}
