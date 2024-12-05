package com.nurmanhilman.eventbrite.repositories;

import com.nurmanhilman.eventbrite.entities.PromotionEntity;
import com.nurmanhilman.eventbrite.entities.TrxEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface TrxRepository extends JpaRepository<TrxEntity, Long> {
    List<TrxEntity> findByUserId(Long userId);  // Returns a List instead of Optional
}
