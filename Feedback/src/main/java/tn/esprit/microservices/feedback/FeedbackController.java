package tn.esprit.microservices.feedback;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/feedbacks")
@RequiredArgsConstructor
public class FeedbackController {

    private final FeedbackService feedbackService;

    // POST /api/feedbacks
    @PostMapping
    public ResponseEntity<FeedbackResponse> create(@Valid @RequestBody FeedbackRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(feedbackService.create(request));
    }

    // GET /api/feedbacks/{id}
    @GetMapping("/{id}")
    public ResponseEntity<FeedbackResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(feedbackService.findById(id));
    }

    // PUT /api/feedbacks/{id}
    @PutMapping("/{id}")
    public ResponseEntity<FeedbackResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody FeedbackUpdateRequest request) {
        return ResponseEntity.ok(feedbackService.update(id, request));
    }

    // DELETE /api/feedbacks/{id}  (soft-delete)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        feedbackService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // GET /api/feedbacks/event/{eventId}
    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<FeedbackResponse>> findByEvent(@PathVariable Long eventId) {
        return ResponseEntity.ok(feedbackService.findByEventId(eventId));
    }

    // GET /api/feedbacks/user/{userId}
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<FeedbackResponse>> findByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(feedbackService.findByUserId(userId));
    }

    // GET /api/feedbacks/organizer/{organizerId}
    @GetMapping("/organizer/{organizerId}")
    public ResponseEntity<List<FeedbackResponse>> findByOrganizer(@PathVariable Long organizerId) {
        return ResponseEntity.ok(feedbackService.findByOrganizerId(organizerId));
    }

    // GET /api/feedbacks/stats/event/{eventId}
    @GetMapping("/stats/event/{eventId}")
    public ResponseEntity<EventStatsResponse> getEventStats(@PathVariable Long eventId) {
        return ResponseEntity.ok(feedbackService.getEventStats(eventId));
    }
}