package tn.esprit.microservice.event.services;

import tn.esprit.microservice.event.entities.Event;

import java.util.List;

public interface iEventService {

    List<Event> getAllEvents();

    Event getEventById(Long id);

    List<Event> searchEventByTitle(String title);

    Event addEvent(Event event);

    Event updateEvent(Event event);

    void deleteEvent(Long id);
}