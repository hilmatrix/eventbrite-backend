package com.nurmanhilman.eventbrite.repositories;

import com.nurmanhilman.eventbrite.entities.PromotionEntity;
import com.nurmanhilman.eventbrite.entities.TrxEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface TrxRepository extends JpaRepository<TrxEntity, Long> {
    List<TrxEntity> findByUserId(Long userId);  // Returns a List instead of Optional

    @Query("SELECT COUNT(t) > 0 FROM TrxEntity t WHERE t.userId = :userId AND t.eventId = :eventId AND t.deletedAt IS NULL")
    boolean existsByUserIdAndEventId(@Param("userId") Long userId, @Param("eventId") Long eventId);

    @Query("SELECT t.trxId FROM TrxEntity t WHERE t.userId = :userId AND t.eventId = :eventId AND t.deletedAt IS NULL")
    Optional<Long> findTrxIdByUserIdAndEventId(@Param("userId") Long userId, @Param("eventId") Long eventId);
}
