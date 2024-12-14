package com.nurmanhilman.eventbrite.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;
import java.time.ZonedDateTime;
@Data
@Entity
@Table(name = "reviews_ratings")
public class ReviewRatingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_rating_id")
    private Long reviewRatingId;

    @Column(name = "trx_id", nullable = false)
    private Long transactionId;

    @Column(name = "review", nullable = false)
    private String review;

    @Column(name = "suggestion", nullable = false)
    private String suggestion;

    @Column(name = "rating", nullable = false)
    private int rating;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    // Getters and Setters
}

