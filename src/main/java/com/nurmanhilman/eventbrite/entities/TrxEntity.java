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

    @ManyToOne
    @JoinColumn(name = "event_id" , nullable= false)
    private EventEntity eventId;

    @ManyToOne
    @JoinColumn(name = "user_id" , nullable= false)
    private UserEntity user;

    @Column(name = "ticket_amount" ,nullable = false)
    private int ticketAmount;

    @Column(name = "total_price", nullable = false)
    private BigDecimal totalPrice;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "deleted_at")
    private Instant deletedAt;
}
