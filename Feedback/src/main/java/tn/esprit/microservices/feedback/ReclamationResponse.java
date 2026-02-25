package tn.esprit.microservices.feedback;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ReclamationResponse {
    private Long id;
    private Long userId;
    private Long eventId;
    private Long organizerId;
    private String subject;
    private String description;
    private ReclamationStatus status;
    private String organizerResponse;
    private LocalDateTime respondedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}