package com.nurmanhilman.eventbrite.repositories;


import com.nurmanhilman.eventbrite.entities.PromotionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PromotionRepository extends JpaRepository<PromotionEntity, Long> {
    // Custom queries if needed
    Optional<PromotionEntity> findByPromoCode(String referralCode);

    @Query("SELECT p FROM PromotionEntity p " +
            "WHERE p.eventId IN (SELECT e.eventId FROM EventEntity e WHERE e.userId = :userId)")
    List<PromotionEntity> findAllPromosByUserId(@Param("userId") Long userId);
}

