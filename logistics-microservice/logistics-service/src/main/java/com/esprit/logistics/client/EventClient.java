package com.esprit.logistics.client;

import com.esprit.logistics.dto.LogisticsEventDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * Feign Client pour communication synchrone avec le MS Evenement.
 * Le nom "event-service" doit correspondre au spring.application.name
 * du microservice Evenement de vos camarades.
 *
 * NB : Si le MS Evenement n'est pas encore disponible, les appels retourneront
 * une erreur — c'est normal en développement isolé.
 */
@FeignClient(name = "event-service", fallback = EventClientFallback.class)
public interface EventClient {

    @GetMapping("/events")
    List<LogisticsEventDTO> getAllEvents();

    @GetMapping("/events/{id}")
    LogisticsEventDTO getEventById(@PathVariable("id") Long id);
}
