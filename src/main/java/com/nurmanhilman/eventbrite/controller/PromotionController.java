package com.nurmanhilman.eventbrite.controller;


import com.nurmanhilman.eventbrite.entities.EventEntity;
import com.nurmanhilman.eventbrite.entities.PromotionEntity;
import com.nurmanhilman.eventbrite.entities.UserEntity;
import com.nurmanhilman.eventbrite.exception.CustomResponseStatusException;
import com.nurmanhilman.eventbrite.repositories.EventRepository;
import com.nurmanhilman.eventbrite.service.PromotionService;
import com.nurmanhilman.eventbrite.service.UserService;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/promotions")
public class PromotionController {

    private final PromotionService promotionService;
    private final UserService userService;
    private final EventRepository eventRepository;

    @Autowired
    public PromotionController(PromotionService promotionService, UserService userService, EventRepository eventRepository) {
        this.userService = userService;
        this.promotionService = promotionService;
        this.eventRepository = eventRepository;
    }

    @GetMapping
    public ResponseEntity<List<PromotionEntity>> getAllPromotions() {
        List<PromotionEntity> promotions = promotionService.getAllPromotions();
        return ResponseEntity.ok(promotions);
    }

    @GetMapping("/{promoId}")
    public ResponseEntity<PromotionEntity> getPromotionById(@PathVariable Long promoId) {
        return promotionService.getPromotionById(promoId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public PromotionEntity createPromotion(@RequestHeader("Authorization") String authorizationHeader,
                                           @RequestBody PromotionEntity promotion) {
        UserEntity userEntity = userService.getUserFromJwt(authorizationHeader);
        System.out.println("Event id = "+ promotion.getEventId());
        Optional<EventEntity> eventEntity = eventRepository.findById(promotion.getEventId());
        if (eventEntity.isEmpty())
            throw new CustomResponseStatusException(HttpStatus.NOT_FOUND, "Event id " + promotion.getEventId() +" is not found");
        if (eventEntity.get().getUserId() != userEntity.getUserId())
            throw new CustomResponseStatusException(HttpStatus.FORBIDDEN, "This user id is not owner of the event id "+promotion.getEventId());
        promotion.setCreatedAt(Instant.now());
        promotion.setUpdatedAt(Instant.now());
        return promotionService.createPromotion(promotion);
    }

    @PutMapping("/{promoId}")
    public ResponseEntity<PromotionEntity> updatePromotion(@PathVariable Long promoId, @RequestBody PromotionEntity promotionDetails) {
        try {
            PromotionEntity updatedPromotion = promotionService.updatePromotion(promoId, promotionDetails);
            return ResponseEntity.ok(updatedPromotion);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{promoId}")
    public ResponseEntity<Void> deletePromotion(@RequestHeader("Authorization") String authorizationHeader,
                                                @PathVariable Long promoId) {
        UserEntity userEntity = userService.getUserFromJwt(authorizationHeader);
        if (!promotionService.isUserOwnerOfPromotion(promoId, userEntity.getUserId()))
            throw new CustomResponseStatusException(HttpStatus.FORBIDDEN, "This user id is not owner of the promo id "+promoId);
        promotionService.deletePromotion(promoId);
        return ResponseEntity.noContent().build();
    }
}

