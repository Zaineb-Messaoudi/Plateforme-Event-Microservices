package tn.esprit.microservices.feedback;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reclamations")
@RequiredArgsConstructor
public class ReclamationController {

    private final ReclamationService reclamationService;

    // POST /api/reclamations
    @PostMapping
    public ResponseEntity<ReclamationResponse> create(@Valid @RequestBody ReclamationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reclamationService.create(request));
    }

    // GET /api/reclamations/{id}
    @GetMapping("/{id}")
    public ResponseEntity<ReclamationResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(reclamationService.findById(id));
    }

    // GET /api/reclamations/user/{userId}
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ReclamationResponse>> findByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(reclamationService.findByUserId(userId));
    }

    // GET /api/reclamations/organizer/{organizerId}
    @GetMapping("/organizer/{organizerId}")
    public ResponseEntity<List<ReclamationResponse>> findByOrganizer(@PathVariable Long organizerId) {
        return ResponseEntity.ok(reclamationService.findByOrganizerId(organizerId));
    }

    // GET /api/reclamations/organizer/{organizerId}/status/{status}
    @GetMapping("/organizer/{organizerId}/status/{status}")
    public ResponseEntity<List<ReclamationResponse>> findByOrganizerAndStatus(
            @PathVariable Long organizerId,
            @PathVariable ReclamationStatus status) {
        return ResponseEntity.ok(reclamationService.findByOrganizerIdAndStatus(organizerId, status));
    }

    // GET /api/reclamations/event/{eventId}
    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<ReclamationResponse>> findByEvent(@PathVariable Long eventId) {
        return ResponseEntity.ok(reclamationService.findByEventId(eventId));
    }

    // GET /api/reclamations/status/{status}   (admin)
    @GetMapping("/status/{status}")
    public ResponseEntity<List<ReclamationResponse>> findByStatus(@PathVariable ReclamationStatus status) {
        return ResponseEntity.ok(reclamationService.findByStatus(status));
    }

    // PUT /api/reclamations/{id}/respond   (organizer)
    @PutMapping("/{id}/respond")
    public ResponseEntity<ReclamationResponse> respond(
            @PathVariable Long id,
            @Valid @RequestBody OrganizerResponseRequest request) {
        return ResponseEntity.ok(reclamationService.respondToReclamation(id, request));
    }

    // PATCH /api/reclamations/{id}/close   (admin)
    @PatchMapping("/{id}/close")
    public ResponseEntity<ReclamationResponse> close(@PathVariable Long id) {
        return ResponseEntity.ok(reclamationService.closeReclamation(id));
    }
}
