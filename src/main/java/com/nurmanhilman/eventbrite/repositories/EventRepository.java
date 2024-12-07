package com.nurmanhilman.eventbrite.repositories;

import com.nurmanhilman.eventbrite.entities.EventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<EventEntity, Long>, JpaSpecificationExecutor<EventEntity> {
//    @Query("SELECT e FROM EventEntity e " +
//            "WHERE (:name IS NULL OR LOWER(CAST(e.name AS text)) LIKE LOWER(CONCAT('%', :name, '%'))) " +
//            "AND (:location IS NULL OR LOWER(CAST(e.location AS text)) LIKE LOWER(CONCAT('%', :location, '%'))) " +
//            "AND (:description IS NULL OR LOWER(CAST(e.description AS text)) LIKE LOWER(CONCAT('%', :description, '%'))) " +
//            "AND e.isActive = true")
//    List<EventEntity> filterAndSearchEvents(
//            @Param("name") String name,
//            @Param("location") String location,
//            @Param("description") String description);

}

