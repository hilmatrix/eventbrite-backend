package com.nurmanhilman.eventbrite.controller;

import com.nurmanhilman.eventbrite.entities.EventEntity;
import com.nurmanhilman.eventbrite.exception.CustomResponseStatusException;
import com.nurmanhilman.eventbrite.repositories.EventRepository;
import com.nurmanhilman.eventbrite.repositories.TrxRepository;
import com.nurmanhilman.eventbrite.service.TrxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/available-seats")
public class AvailableSeatsController {

    @Autowired
    public EventRepository eventRepository;

    @Autowired
    public TrxService trxService;

    @GetMapping("/{eventId}")
    public ResponseEntity<?> getAvailableSeats(@PathVariable Long eventId) {
        EventEntity eventEntity = eventRepository.findById(eventId).get();
        int eventTransactions = trxService.getEventTransactions(eventId);

        int availableSeats = (eventEntity.getAvailableSeats() - (eventTransactions));

        Map<String, Object> result =  new HashMap<>();
        result.put("availableSeats", availableSeats);
        return ResponseEntity.status(200).body(result);
    }
}
