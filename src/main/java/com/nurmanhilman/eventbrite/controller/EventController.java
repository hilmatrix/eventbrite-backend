package com.nurmanhilman.eventbrite.controller;

import com.nurmanhilman.eventbrite.entities.EventEntity;
import com.nurmanhilman.eventbrite.entities.UserEntity;
import com.nurmanhilman.eventbrite.exception.CustomResponseStatusException;
import com.nurmanhilman.eventbrite.repositories.UserRepository;
import com.nurmanhilman.eventbrite.service.EventService;
import com.nurmanhilman.eventbrite.service.PromotionService;
import com.nurmanhilman.eventbrite.service.UserService;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/events")
public class EventController {

    private final EventService eventService;
    private final JwtDecoder jwtDecoder;
    private final UserRepository userRepository;
    private final UserService userService;
    private final PromotionService promotionService;

    @Autowired
    public EventController(EventService eventService, JwtDecoder jwtDecoder,
                           UserRepository userRepository, UserService userService, PromotionService promotionService) {
        this.eventService = eventService;
        this.jwtDecoder = jwtDecoder;
        this.userRepository = userRepository;
        this.userService = userService;
        this.promotionService = promotionService;
    }

    private boolean isOrganizer(String authorizationHeader) {
        try {
            // extract email from jwt
            String token = authorizationHeader.replace("Bearer", "");
            Jwt jwt = jwtDecoder.decode(token);
            String email = jwt.getSubject();

            // fetch user and check if they're an organizer
            Optional<UserEntity> userOptional = userRepository.findByEmail(email);
            return userOptional.map(UserEntity::isEventOrganizer).orElse(false);
        } catch (Exception e) {
            return false;
        }
    }

    // Create a new event
    @PostMapping
    public ResponseEntity<?> createEvent(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody EventEntity event
    ) {
        UserEntity userEntity = userService.getUserFromJwt(authorizationHeader);
        if (!userEntity.isEventOrganizer()) {
            return ResponseEntity.status(403).body("Access denied. Only organizers can create events.");
        }
        event.setCreatedAt(Instant.now());
        event.setUpdatedAt(Instant.now());
        event.setUserId(userEntity.getUserId());
        EventEntity savedEvent = eventService.createEvent(event);
        return ResponseEntity.ok(savedEvent);
    }

//    // Get a list of all events
    @GetMapping
    public ResponseEntity<List<EventEntity>> getAllEvents() {
        List<EventEntity> events = eventService.getAllEvents();
        return ResponseEntity.ok(events);
    }
//
//    // get a list of event by filter
    @GetMapping("/filter")
    public ResponseEntity<List<EventEntity>> filterAndSearchEvents(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "location", required = false) String location,
            @RequestParam(value = "description", required = false) String description) {

        if (name == null)
            name = "";
        if (location == null)
            location = "";
        if (description == null)
            description = "";
        List<EventEntity> events = eventService.filterAndSearchEvents(name, location, description);
        return ResponseEntity.ok(events);
    }

    @GetMapping("/latest/{resultPerPage}/{page}")
    public ResponseEntity<List<EventEntity>> findLatestEvents(@PathVariable int resultPerPage,
                                                              @PathVariable int page){

        List<EventEntity> events = eventService.findLatestEvents(resultPerPage, page);
        return ResponseEntity.ok(events);
    }

    // Get event by ID
    @GetMapping("/id/{id}")
    public ResponseEntity<EventEntity> getEventById(@PathVariable Long id) {
        EventEntity event = eventService.getEventById(id);
        return ResponseEntity.ok(event);
    }

    // Update an event
    @PutMapping("/id/{id}")
    public ResponseEntity<?> updateEvent(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable Long id,
            @RequestBody EventEntity event
    ) {
        if (!isOrganizer(authorizationHeader)) {
            return ResponseEntity.status(403).body("Access denied. Only organizers can update events.");
        }
        event.setUpdatedAt(Instant.now());
        EventEntity updatedEvent = eventService.updateEvent(id, event);
        return ResponseEntity.ok(updatedEvent);
    }

    // Delete an event
    @DeleteMapping("/id/{id}")
    public ResponseEntity<?> deleteEvent(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable Long id
    ) {
        UserEntity userEntity = userService.getUserFromJwt(authorizationHeader);
        EventEntity eventEntity = eventService.getEventById(id);
        if (!userEntity.isEventOrganizer()) {
            throw new CustomResponseStatusException(HttpStatus.FORBIDDEN, "Access denied. Only organizers can update events.");
        }
        if (userEntity.getUserId() != eventEntity.getUserId())
            throw new CustomResponseStatusException(HttpStatus.FORBIDDEN, "This user id is not owner of the event id ");

        promotionService.deleteAllByEventId(id);
        eventService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }
}

