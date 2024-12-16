package com.nurmanhilman.eventbrite.repositories;

import com.nurmanhilman.eventbrite.entities.ReviewRatingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRatingRepository extends JpaRepository<ReviewRatingEntity, Long> {
    // Add custom query methods if needed
    @Query("SELECT COUNT(rr) > 0 FROM ReviewRatingEntity rr " +
            " JOIN TrxEntity t ON rr.transactionId = t.trxId " +
            " WHERE t.userId = :userId AND t.eventId = :eventId AND rr.deletedAt IS NULL ")
    boolean existsByUserIdAndEventId(@Param("userId") Long userId, @Param("eventId") Long eventId);

    @Query("SELECT rr FROM ReviewRatingEntity rr " +
            "JOIN TrxEntity t ON rr.transactionId = t.trxId " +
            "WHERE t.userId = :userId AND rr.deletedAt IS NULL")
    List<ReviewRatingEntity> findByUserId(@Param("userId") Long userId);

    @Query("SELECT rr FROM ReviewRatingEntity rr " +
            "JOIN TrxEntity t ON rr.transactionId = t.trxId " +
            "WHERE t.eventId = :eventId AND rr.deletedAt IS NULL")
    List<ReviewRatingEntity> findByEventId(@Param("eventId") Long eventId);
}
