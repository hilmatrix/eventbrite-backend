package com.nurmanhilman.eventbrite.repositories;

import com.nurmanhilman.eventbrite.entities.EventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends JpaRepository<EventEntity, Long> {
}

