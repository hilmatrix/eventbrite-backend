package com.nurmanhilman.eventbrite.controller;

import com.nurmanhilman.eventbrite.entities.EventEntity;
import com.nurmanhilman.eventbrite.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/v1/events")
public class EventController {

    private final EventService eventService;

    @Autowired
    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    // Create a new event
    @PostMapping
    public ResponseEntity<EventEntity> createEvent(@RequestBody EventEntity event) {
        event.setCreatedAt(Instant.now());
        event.setUpdatedAt(Instant.now());
        EventEntity savedEvent = eventService.createEvent(event);
        return ResponseEntity.ok(savedEvent);
    }

    // Get a list of all events
    @GetMapping
    public ResponseEntity<List<EventEntity>> getAllEvents() {
        List<EventEntity> events = eventService.getAllEvents();
        return ResponseEntity.ok(events);
    }

    // Get event by ID
    @GetMapping("/{id}")
    public ResponseEntity<EventEntity> getEventById(@PathVariable Long id) {
        EventEntity event = eventService.getEventById(id);
        return ResponseEntity.ok(event);
    }

    // Update an event
    @PutMapping("/{id}")
    public ResponseEntity<EventEntity> updateEvent(@PathVariable Long id, @RequestBody EventEntity event) {
        event.setUpdatedAt(Instant.now());
        EventEntity updatedEvent = eventService.updateEvent(id, event);
        return ResponseEntity.ok(updatedEvent);
    }

    // Delete an event
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }
}

