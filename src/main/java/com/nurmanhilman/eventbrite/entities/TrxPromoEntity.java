package com.nurmanhilman.eventbrite.entities;

import jakarta.persistence.*;
import jakarta.transaction.Transaction;

@Entity
@Table(name = "trx_promo")
public class TrxPromoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "trx_id", nullable = false)
    private Transaction transaction;

    @ManyToOne
    @JoinColumn(name = "promo_id", nullable = false)
    private PromotionEntity promotion;
    // Getters, Setters, Constructors
}

