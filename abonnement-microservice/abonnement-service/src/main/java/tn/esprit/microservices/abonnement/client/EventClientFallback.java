package tn.esprit.microservices.abonnement.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class EventClientFallback implements EventClient {

    private static final Logger log = LoggerFactory.getLogger(EventClientFallback.class);

    @Override
    public int getEventCountByUser(Long userId) {
        log.warn("MS Evenement indisponible — retour 0 pour userId={} (fallback)", userId);
        return 0;
    }
}
