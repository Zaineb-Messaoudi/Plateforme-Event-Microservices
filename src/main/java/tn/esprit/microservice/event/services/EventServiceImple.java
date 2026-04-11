package tn.esprit.microservice.event.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.microservice.event.Repository.EventRepository;
import tn.esprit.microservice.event.clients.CategoryClient;
import tn.esprit.microservice.event.entities.Event;
import tn.esprit.microservice.event.models.CategoryModel;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventServiceImple implements iEventService {

    private final EventRepository eventRepository;
    private final CategoryClient categoryClient;

    @Override
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    @Override
    public Event getEventById(Long id) {
        return eventRepository.findById(id).orElse(null);
    }

    @Override
    public List<Event> searchEventByTitle(String title) {
        return eventRepository.findByTitleContaining(title);
    }

    @Override
    public Event addEvent(Event event) {
        return eventRepository.save(event);
    }

    @Override
    public Event updateEvent(Event event) {
        return eventRepository.save(event);
    }

    @Override
    public void deleteEvent(Long id) {
        eventRepository.deleteById(id);
    }

    // Feign methods
    @Override
    public List<CategoryModel> getAllCategories() {
        return categoryClient.getAllCategories();
    }

    @Override
    public CategoryModel getCategoryByEventId(Long eventId) {
        Event event = eventRepository.findById(eventId).orElse(null);
        if (event == null || event.getCategoryId() == null) {
            return null;
        }
        return categoryClient.getCategoryById(event.getCategoryId());
    }

    @Override
    public List<CategoryModel> getActiveCategories() {
        return categoryClient.getActiveCategories();
    }
}