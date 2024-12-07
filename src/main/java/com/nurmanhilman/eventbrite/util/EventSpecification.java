package com.nurmanhilman.eventbrite.util;

import com.nurmanhilman.eventbrite.entities.EventEntity;
import org.springframework.data.jpa.domain.Specification;

public class EventSpecification {
    private EventSpecification() {}

    public static Specification<EventEntity> name(String name) {
        
    }
}
