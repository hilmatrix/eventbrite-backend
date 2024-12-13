package com.nurmanhilman.eventbrite.controller;

import com.nurmanhilman.eventbrite.entities.EventEntity;
import com.nurmanhilman.eventbrite.entities.PromotionEntity;
import com.nurmanhilman.eventbrite.entities.TicketEntity;
import com.nurmanhilman.eventbrite.entities.UserEntity;
import com.nurmanhilman.eventbrite.repositories.EventRepository;
import com.nurmanhilman.eventbrite.repositories.PromotionRepository;
import com.nurmanhilman.eventbrite.repositories.TicketRepository;
import com.nurmanhilman.eventbrite.service.EventService;
import com.nurmanhilman.eventbrite.service.TrxService;
import com.nurmanhilman.eventbrite.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/organizer")
public class OrganizerController {
    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EventService eventService;

    @Autowired
    private UserService userService;

    @Autowired
    private PromotionRepository promotionRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private TrxService trxService;

    @GetMapping("/events")
    public ResponseEntity<?> getAllEvents(@RequestHeader("Authorization") String authorizationHeader) {
        UserEntity userEntity = userService.getUserFromJwt(authorizationHeader);
        List<EventEntity> events = eventRepository.findByUserId(userEntity.getUserId());
        return ResponseEntity.ok(events);
    }

    @GetMapping("/events/{id}") public ResponseEntity<EventEntity> getEventDetailsById(@PathVariable Long id) {
        EventEntity event = eventService.getEventById(id);
        return ResponseEntity.ok(event);
    }

    @GetMapping("/promotions")
    public ResponseEntity<?> getAllPromotions(@RequestHeader("Authorization") String authorizationHeader) {
        UserEntity userEntity = userService.getUserFromJwt(authorizationHeader);
        List<PromotionEntity> promotions = promotionRepository.findAllPromosByUserId(userEntity.getUserId());
        return ResponseEntity.ok(promotions);
    }

    @GetMapping("/tickets-sold")
    public ResponseEntity<?> getAllTicketsSold(@RequestHeader("Authorization") String authorizationHeader) {
        UserEntity userEntity = userService.getUserFromJwt(authorizationHeader);
        List<TicketEntity> tickets = ticketRepository.findAllTicketsByOrganizerId(userEntity.getUserId());
        Map<String, Object> result = new HashMap<>();
        result.put("ticketsSold", tickets.size());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/revenue")
    public ResponseEntity<?> getAllRevenue(@RequestHeader("Authorization") String authorizationHeader) {
        UserEntity userEntity = userService.getUserFromJwt(authorizationHeader);
        BigDecimal revenue = trxService.getTotalTransactionPriceByOrganizerId(userEntity.getUserId());
        Map<String, Object> result = new HashMap<>();
        result.put("revenue", revenue);
        return ResponseEntity.ok(result);
    }


}
