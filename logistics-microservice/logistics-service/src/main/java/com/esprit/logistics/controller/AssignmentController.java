package com.esprit.logistics.controller;

import com.esprit.logistics.client.EventClient;
import com.esprit.logistics.dto.AssignmentDTO;
import com.esprit.logistics.dto.LogisticsEventDTO;
import com.esprit.logistics.entity.Assignment;
import com.esprit.logistics.service.AssignmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/assignments")
public class AssignmentController {

    @Autowired
    private AssignmentService assignmentService;

    @Autowired
    private EventClient eventClient;

    @GetMapping
    public List<Assignment> getAllAssignments() {
        return assignmentService.getAllAssignments();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Assignment> getAssignmentById(@PathVariable Long id) {
        return assignmentService.getAssignmentById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/event/{eventId}")
    public List<Assignment> getByEventId(@PathVariable Long eventId) {
        return assignmentService.getAssignmentsByEventId(eventId);
    }

    @PostMapping
    public ResponseEntity<Assignment> createAssignment(@RequestBody AssignmentDTO dto) {
        return ResponseEntity.ok(assignmentService.createAssignment(dto));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Assignment> updateStatus(@PathVariable Long id,
                                                    @RequestParam String status) {
        return ResponseEntity.ok(assignmentService.updateStatus(id, status));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAssignment(@PathVariable Long id) {
        assignmentService.deleteAssignment(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Endpoint Feign : récupère la liste des événements depuis le MS Evenement
     * via communication synchrone (Feign Client + Eureka).
     */
    @GetMapping("/events")
    public List<LogisticsEventDTO> getAllEventsFromEventService() {
        return eventClient.getAllEvents();
    }

    @GetMapping("/events/{eventId}")
    public LogisticsEventDTO getEventById(@PathVariable Long eventId) {
        return eventClient.getEventById(eventId);
    }
}
