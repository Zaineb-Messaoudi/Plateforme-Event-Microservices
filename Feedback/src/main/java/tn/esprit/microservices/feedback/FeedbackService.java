package tn.esprit.microservices.feedback;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;

    @Value("${feedback.edit-window-hours:72}")
    private int editWindowHours;

    @Transactional
    public FeedbackResponse create(FeedbackRequest request) {
        if (feedbackRepository.existsByUserIdAndEventId(request.getUserId(), request.getEventId())) {
            throw new RuntimeException(
                    "User " + request.getUserId() + " has already submitted feedback for event " + request.getEventId());
        }

        Feedback feedback = Feedback.builder()
                .userId(request.getUserId())
                .eventId(request.getEventId())
                .organizerId(request.getOrganizerId())
                .rating(request.getRating())
                .comment(request.getComment())
                .editDeadline(LocalDateTime.now().plusHours(editWindowHours))
                .build();

        return toResponse(feedbackRepository.save(feedback));
    }

    @Transactional
    public FeedbackResponse update(Long id, FeedbackUpdateRequest request) {
        Feedback feedback = findActiveById(id);

        if (LocalDateTime.now().isAfter(feedback.getEditDeadline())) {
            throw new RuntimeException(
                    "Edit window expired. Feedbacks can only be modified within " + editWindowHours + " hours of submission.");
        }

        if (request.getRating() != null) feedback.setRating(request.getRating());
        if (request.getComment() != null) feedback.setComment(request.getComment());

        return toResponse(feedbackRepository.save(feedback));
    }

    @Transactional
    public void delete(Long id) {
        Feedback feedback = findActiveById(id);
        feedback.setStatus(FeedbackStatus.DELETED);
        feedbackRepository.save(feedback);
    }

    @Transactional(readOnly = true)
    public FeedbackResponse findById(Long id) {
        return toResponse(findActiveById(id));
    }

    @Transactional(readOnly = true)
    public List<FeedbackResponse> findByEventId(Long eventId) {
        return feedbackRepository.findByEventIdAndStatus(eventId, FeedbackStatus.ACTIVE)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<FeedbackResponse> findByUserId(Long userId) {
        return feedbackRepository.findByUserIdAndStatus(userId, FeedbackStatus.ACTIVE)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<FeedbackResponse> findByOrganizerId(Long organizerId) {
        return feedbackRepository.findByOrganizerIdAndStatus(organizerId, FeedbackStatus.ACTIVE)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public EventStatsResponse getEventStats(Long eventId) {
        Double avg = feedbackRepository.findAverageRatingByEventId(eventId).orElse(0.0);
        Long total = feedbackRepository.countActiveByEventId(eventId);
        List<Object[]> raw = feedbackRepository.findRatingDistributionByEventId(eventId);

        Map<String, Long> distribution = new LinkedHashMap<>();
        for (int i = 1; i <= 5; i++) distribution.put(String.valueOf(i), 0L);
        for (Object[] row : raw) {
            String bucket = String.valueOf(((Number) row[0]).intValue());
            distribution.put(bucket, ((Number) row[1]).longValue());
        }

        return EventStatsResponse.builder()
                .eventId(eventId)
                .averageRating(avg != null ? Math.round(avg * 10.0) / 10.0 : 0.0)
                .totalFeedbacks(total)
                .ratingDistribution(distribution)
                .build();
    }

    private Feedback findActiveById(Long id) {
        return feedbackRepository.findById(id)
                .filter(f -> f.getStatus() == FeedbackStatus.ACTIVE)
                .orElseThrow(() -> new RuntimeException("Feedback not found with id: " + id));
    }

    private FeedbackResponse toResponse(Feedback f) {
        return FeedbackResponse.builder()
                .id(f.getId())
                .userId(f.getUserId())
                .eventId(f.getEventId())
                .organizerId(f.getOrganizerId())
                .rating(f.getRating())
                .comment(f.getComment())
                .status(f.getStatus())
                .createdAt(f.getCreatedAt())
                .updatedAt(f.getUpdatedAt())
                .editDeadline(f.getEditDeadline())
                .editable(f.getEditDeadline() != null && LocalDateTime.now().isBefore(f.getEditDeadline()))
                .build();
    }
}