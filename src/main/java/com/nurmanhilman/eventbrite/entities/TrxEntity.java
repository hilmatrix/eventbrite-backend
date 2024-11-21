package com.nurmanhilman.eventbrite.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Entity
@Table(name = "trx")
public class TrxEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "trx_id")
    private Long trxId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id" , nullable= false)
    private EventEntity event;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id" , nullable= false)
    private UserEntity user;

    @Column(name = "ticket_amount" ,nullable = false)
    private int ticketAmount;

    @Column(name = "total_price", nullable = false)
    private BigDecimal totalPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "promo_id")
    private PromotionEntity promotion;

    @Column(name = "referral_code_used")
    private String referralCodeUsed;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "deleted_at")
    private Instant deletedAt;
}
