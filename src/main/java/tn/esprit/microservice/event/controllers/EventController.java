package tn.esprit.microservice.event.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.microservice.event.entities.Event;
import tn.esprit.microservice.event.models.CategoryModel;
import tn.esprit.microservice.event.services.iEventService;

import javax.sql.DataSource;
import java.util.List;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final iEventService eventService;

    @Autowired
    DataSource dataSource;

    // DB INFO
    @GetMapping("/db-info")
    public String getDbInfo() throws Exception {
        return dataSource.getConnection().getMetaData().getDatabaseProductName();
    }

    // GET ALL EVENTS
    @GetMapping
    public ResponseEntity<List<Event>> getAllEvents() {
        List<Event> events = eventService.getAllEvents();
        if (events.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(events);
    }

    // GET EVENT BY ID
    @GetMapping("/{id}")
    public ResponseEntity<Event> getEventById(@PathVariable Long id) {
        Event event = eventService.getEventById(id);
        if (event == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(event);
    }

    // SEARCH EVENT BY TITLE
    @GetMapping("/search")
    public ResponseEntity<List<Event>> searchEvent(
            @RequestParam(required = false) String title) {
        List<Event> events = eventService.searchEventByTitle(title);
        if (events.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(events);
    }

    // ADD EVENT
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Event> addEvent(@RequestBody Event event) {
        return new ResponseEntity<>(eventService.addEvent(event), HttpStatus.CREATED);
    }

    // UPDATE EVENT
    @PutMapping("/{id}")
    public ResponseEntity<Event> updateEvent(
            @PathVariable Long id, @RequestBody Event event) {
        Event existing = eventService.getEventById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }
        existing.setTitle(event.getTitle());
        existing.setDescription(event.getDescription());
        existing.setCategory(event.getCategory());
        existing.setLocation(event.getLocation());
        existing.setStartDate(event.getStartDate());
        existing.setEndDate(event.getEndDate());
        existing.setCapacity(event.getCapacity());
        existing.setPrice(event.getPrice());
        existing.setPublished(event.isPublished());
        existing.setCategoryId(event.getCategoryId());
        return ResponseEntity.ok(eventService.updateEvent(existing));
    }

    // DELETE EVENT
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteEvent(@PathVariable Long id) {
        Event existing = eventService.getEventById(id);
        if (existing == null) {
            return ResponseEntity.ok("Event not found");
        }
        eventService.deleteEvent(id);
        return ResponseEntity.ok("Event deleted successfully");
    }

    // ---- FEIGN ENDPOINTS ----

    // GET ALL CATEGORIES FROM CATEGORY MICROSERVICE
    @GetMapping("/categories")
    public ResponseEntity<List<CategoryModel>> getAllCategories() {
        List<CategoryModel> categories = eventService.getAllCategories();
        if (categories.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(categories);
    }

    // GET CATEGORY OF A SPECIFIC EVENT
    @GetMapping("/{id}/get-category")
    public ResponseEntity<CategoryModel> getCategoryByEventId(@PathVariable Long id) {
        CategoryModel category = eventService.getCategoryByEventId(id);
        if (category == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(category);
    }

    // GET ACTIVE CATEGORIES FROM CATEGORY MICROSERVICE
    @GetMapping("/categories/active")
    public ResponseEntity<List<CategoryModel>> getActiveCategories() {
        List<CategoryModel> categories = eventService.getActiveCategories();
        if (categories.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(categories);
    }
}