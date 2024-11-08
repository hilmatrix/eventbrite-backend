package com.nurmanhilman.eventbrite.entities;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.Instant;
import lombok.Data;

@Data
@Entity
@Table(name = "users")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    private String name;

    private String email;

    @JsonIgnore
    @Column(name = "password_hash")
    private String passwordHash;

    @JsonIgnore
    private String pin;

    @Column(name = "is_event_organizer")
    private boolean isEventOrganizer;

    @Column(name = "referral_code")
    private String referralCode;

    @Column(name = "profile_image_url")
    private String profileImageUrl;

    @Column(name = "is_onboarding_finished")
    private boolean isOnboardingFinished;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    // Getters and Setters
}
