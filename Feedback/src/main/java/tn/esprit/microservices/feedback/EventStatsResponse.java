package tn.esprit.microservices.feedback;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class EventStatsResponse {
    private Long eventId;
    private Double averageRating;
    private Long totalFeedbacks;
    /**
     * Distribution: key = rating (1-5 as String), value = count
     * e.g. {"1": 2, "2": 5, "3": 10, "4": 30, "5": 53}
     */
    private Map<String, Long> ratingDistribution;
}
