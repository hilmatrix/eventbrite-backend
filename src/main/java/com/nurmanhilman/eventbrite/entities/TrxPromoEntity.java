package com.nurmanhilman.eventbrite.entities;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "trx_promo")
public class TrxPromoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long trxPromoId;

    @Column(name = "trx_id", nullable = false)
    private Long trxId;

    @Column(name = "promo_id", nullable = false)
    private Long promoId;
    // Getters, Setters, Constructors
}

