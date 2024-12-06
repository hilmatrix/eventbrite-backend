package com.nurmanhilman.eventbrite.entities;

import java.time.LocalDateTime;

public class ReferralPointDetailEntity {
    private int pointsEarned;
    private LocalDateTime createdAt;
    private int expirationDaysLeft;
    private int expirationMinutesLeft;
    private boolean isUsedOrExpired;

    // Getters and setters
    public int getPointsEarned() {
        return pointsEarned;
    }

    public void setPointsEarned(int pointsEarned) {
        this.pointsEarned = pointsEarned;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public int getExpirationDaysLeft() {
        return expirationDaysLeft;
    }

    public void setExpirationDaysLeft(int expirationDaysLeft) {
        this.expirationDaysLeft = expirationDaysLeft;
    }

    public int getExpirationMinutesLeft() {
        return expirationMinutesLeft;
    }

    public void setExpirationMinutesLeft(int expirationMinutesLeft) {
        this.expirationMinutesLeft = expirationMinutesLeft;
    }

    public boolean isUsedOrExpired() {
        return isUsedOrExpired;
    }

    public void setUsedOrExpired(boolean usedOrExpired) {
        isUsedOrExpired = usedOrExpired;
    }
}
