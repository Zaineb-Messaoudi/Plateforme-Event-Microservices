package tn.esprit.microservices.feedback;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class FeedbackResponse {
    private Long id;
    private Long userId;
    private Long eventId;
    private Long organizerId;
    private Double rating;
    private String comment;
    private FeedbackStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime editDeadline;
    private boolean editable;          // true if still within edit window
}