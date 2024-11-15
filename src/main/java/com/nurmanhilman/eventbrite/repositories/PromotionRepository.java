package com.nurmanhilman.eventbrite.repositories;


import com.nurmanhilman.eventbrite.entities.PromotionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PromotionRepository extends JpaRepository<PromotionEntity, Long> {
    // Custom queries if needed
}

