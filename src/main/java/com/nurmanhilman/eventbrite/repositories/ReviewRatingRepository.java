package com.nurmanhilman.eventbrite.repositories;

import com.nurmanhilman.eventbrite.entities.ReviewRatingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRatingRepository extends JpaRepository<ReviewRatingEntity, Long> {
    // Add custom query methods if needed
}
