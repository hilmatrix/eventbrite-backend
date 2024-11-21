package com.nurmanhilman.eventbrite.controller;


import com.nurmanhilman.eventbrite.entities.PromotionEntity;
import com.nurmanhilman.eventbrite.service.PromotionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/promotions")
public class PromotionController {

    private final PromotionService promotionService;

    @Autowired
    public PromotionController(PromotionService promotionService) {
        this.promotionService = promotionService;
    }

    @GetMapping
    public List<PromotionEntity> getAllPromotions() {
        return promotionService.getAllPromotions();
    }

    @GetMapping("/{promoId}")
    public ResponseEntity<PromotionEntity> getPromotionById(@PathVariable Long promoId) {
        return promotionService.getPromotionById(promoId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public PromotionEntity createPromotion(@RequestBody PromotionEntity promotion) {
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
    public ResponseEntity<Void> deletePromotion(@PathVariable Long promoId) {
        promotionService.deletePromotion(promoId);
        return ResponseEntity.noContent().build();
    }
}

