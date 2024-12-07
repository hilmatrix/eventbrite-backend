package com.nurmanhilman.eventbrite.service;

import com.nurmanhilman.eventbrite.entities.EventEntity;
import com.nurmanhilman.eventbrite.repositories.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class EventService {

    private final EventRepository eventRepository;

    @Autowired
    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

//    public static List<EventEntity> findAll(String name, String location) {
//    }

//    public List<EventEntity> filterAndSearchEvents(String name, String location, String description) {
//        return eventRepository.filterAndSearchEvents(name, location, description);
//    }

    public EventEntity createEvent(EventEntity event) {
        return eventRepository.save(event);
    }

    public List<EventEntity> getAllEvents() {
        return eventRepository.findAll();
    }

    public EventEntity getEventById(Long id) {
        return eventRepository.findById(id).orElseThrow(() -> new RuntimeException("Event not found"));
    }

    public EventEntity updateEvent(Long id, EventEntity eventDetails) {
        EventEntity event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        // Update fields
        event.setName(eventDetails.getName());
        event.setPrice(eventDetails.getPrice());
        event.setDate(eventDetails.getDate());
        event.setTime(eventDetails.getTime());
        event.setLocation(eventDetails.getLocation());
        event.setDescription(eventDetails.getDescription());
        event.setAvailableSeats(eventDetails.getAvailableSeats());
        event.setTicketTypes(eventDetails.getTicketTypes());
        event.setIsPaidEvent(eventDetails.getIsPaidEvent());
        event.setIsActive(eventDetails.getIsActive());
        event.setUpdatedAt(Instant.now());

        return eventRepository.save(event);
    }

    public void deleteEvent(Long id) {
        EventEntity event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        eventRepository.delete(event);
    }
}

