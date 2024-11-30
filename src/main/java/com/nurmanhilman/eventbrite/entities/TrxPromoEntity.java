package com.nurmanhilman.eventbrite.entities;

import jakarta.persistence.*;


@Entity
@Table(name = "trx_promo")
public class TrxPromoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "trx_id", nullable = false)
    private TrxEntity transaction;

    @ManyToOne
    @JoinColumn(name = "promo_id", nullable = false)
    private PromotionEntity promotion;
    // Getters, Setters, Constructors
}

