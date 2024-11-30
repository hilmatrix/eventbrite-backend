package com.nurmanhilman.eventbrite.entities;

import com.nurmanhilman.eventbrite.entities.EventEntity;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;

@Data
@Entity
@Table(name = "promotions")
public class PromotionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "promo_id")
    private Long promoId;

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private EventEntity eventId;

    @Column(name = "referral_code", nullable = false)
    private String referralCode;

    @Column(name = "price_cut", nullable = false)
    private BigDecimal priceCut;

    @Column(name = "promo_started_date", nullable = false)
    private LocalDate promoStartedDate;

    @Column(name = "promo_started_time", nullable = false)
    private LocalTime promoStartedTime;

    @Column(name = "promo_ended_date", nullable = false)
    private LocalDate promoEndedDate;

    @Column(name = "promo_ended_time", nullable = false)
    private LocalTime promoEndedTime;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "deleted_at")
    private Instant deletedAt;
    // Getters and Setters
}
