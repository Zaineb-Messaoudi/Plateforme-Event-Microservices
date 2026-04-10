package com.esprit.logistics.client;

import com.esprit.logistics.dto.LogisticsEventDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * Fallback activé quand le MS Evenement est indisponible.
 * Permet au logistics-service de continuer à fonctionner même sans le MS Evenement.
 */
@Component
public class EventClientFallback implements EventClient {

    private static final Logger log = LoggerFactory.getLogger(EventClientFallback.class);

    @Override
    public List<LogisticsEventDTO> getAllEvents() {
        log.warn("MS Evenement indisponible — retour liste vide (fallback)");
        return Collections.emptyList();
    }

    @Override
    public LogisticsEventDTO getEventById(Long id) {
        log.warn("MS Evenement indisponible — retour null pour eventId={} (fallback)", id);
        LogisticsEventDTO fallback = new LogisticsEventDTO();
        fallback.setEventId(id);
        fallback.setEventName("Evenement indisponible");
        fallback.setStatus("UNKNOWN");
        return fallback;
    }
}
