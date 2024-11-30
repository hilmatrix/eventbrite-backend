package com.nurmanhilman.eventbrite.service;


import com.nurmanhilman.eventbrite.entities.PromotionEntity;
import com.nurmanhilman.eventbrite.repositories.PromotionRepository;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class PromotionService {

    private final PromotionRepository promotionRepository;

    @Autowired
    public PromotionService(PromotionRepository promotionRepository) {
        this.promotionRepository = promotionRepository;
    }

    public List<PromotionEntity> getAllPromotions() {
        return promotionRepository.findAll();
    }

    public Optional<PromotionEntity> getPromotionById(Long promoId) {
        return promotionRepository.findById(promoId);
    }

    public Optional<PromotionEntity> getPromotionByReferralCode(String referralCode) {
        return promotionRepository.findByReferralCode(referralCode);
    }

    public PromotionEntity createPromotion(PromotionEntity promotion) {
        return promotionRepository.save(promotion);
    }

    public PromotionEntity updatePromotion(Long promoId, PromotionEntity promotionDetails) {
        return promotionRepository.findById(promoId).map(promotion -> {
            promotion.setReferralCode(promotionDetails.getReferralCode());
            promotion.setPriceCut(promotionDetails.getPriceCut());
            promotion.setPromoStartedDate(promotionDetails.getPromoStartedDate());
            promotion.setPromoStartedTime(promotionDetails.getPromoStartedTime());
            promotion.setPromoEndedDate(promotionDetails.getPromoEndedDate());
            promotion.setPromoEndedTime(promotionDetails.getPromoEndedTime());
            promotion.setUpdatedAt(Instant.now());
            return promotionRepository.save(promotion);
        }).orElseThrow(() -> new RuntimeException("Promotion not found with id " + promoId));
    }

    public void deletePromotion(Long promoId) {
        promotionRepository.deleteById(promoId);
    }
}

