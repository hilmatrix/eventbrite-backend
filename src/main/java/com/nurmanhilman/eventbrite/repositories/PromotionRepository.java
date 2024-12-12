package com.nurmanhilman.eventbrite.repositories;


import com.nurmanhilman.eventbrite.entities.PromotionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
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

    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM PromotionEntity p " +
            "JOIN EventEntity e ON p.eventId = e.eventId " +
            "WHERE p.promoId = :promoId AND e.userId = :userId")
    Boolean isUserOwnerOfPromotion(@Param("promoId") Long promoId, @Param("userId") Long userId);

    @Modifying
    @Query("DELETE FROM PromotionEntity p WHERE p.eventId = :eventId")
    void deleteAllByEventId(@Param("eventId") Long eventId);
}

