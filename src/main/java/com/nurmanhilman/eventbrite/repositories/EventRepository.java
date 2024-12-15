package com.nurmanhilman.eventbrite.repositories;

import com.nurmanhilman.eventbrite.entities.EventEntity;
import com.nurmanhilman.eventbrite.entities.TicketEntity;
import com.nurmanhilman.eventbrite.entities.TrxEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<EventEntity, Long>, JpaSpecificationExecutor<EventEntity> {
    @Query("SELECT e FROM EventEntity e " +
            "WHERE (:name IS NULL OR LOWER(CAST(e.name AS text)) LIKE LOWER(CONCAT('%', :name, '%'))) " +
            "AND (:location IS NULL OR LOWER(CAST(e.location AS text)) LIKE LOWER(CONCAT('%', :location, '%'))) " +
            "AND (:description IS NULL OR LOWER(CAST(e.description AS text)) LIKE LOWER(CONCAT('%', :description, '%'))) " +
            "AND e.isActive = true ORDER BY e.date DESC")
    List<EventEntity> filterAndSearchEvents(
            @Param("name") String name,
            @Param("location") String location,
            @Param("description") String description);

    List<EventEntity> findByUserId(Long userId);

    @Query("SELECT t FROM TicketEntity t " +
            "JOIN EventEntity e ON t.eventId = e.eventId " +
            "WHERE e.userId = :userId")
    List<TicketEntity> findAllTicketsByUserId(@Param("userId") Long userId);

    @Query("SELECT e FROM EventEntity e " +
            "WHERE e.isActive = true " +
            "ORDER BY e.date DESC")
    List<EventEntity> findLatestEvents(Pageable pageable);

    @Query("SELECT CASE WHEN (e.date < CURRENT_DATE OR (e.date = CURRENT_DATE AND e.time < CURRENT_TIME)) THEN true ELSE false END FROM EventEntity e WHERE e.eventId = :eventId")
    boolean isEventExpired(@Param("eventId") Long eventId);
}

