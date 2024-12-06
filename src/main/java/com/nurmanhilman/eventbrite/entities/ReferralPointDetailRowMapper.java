package com.nurmanhilman.eventbrite.entities;
import org.springframework.jdbc.core.RowMapper;

import java.time.Duration;
import java.time.LocalDateTime;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class ReferralPointDetailRowMapper implements RowMapper<ReferralPointDetailEntity> {

    @Override
    public ReferralPointDetailEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        ReferralPointDetailEntity entity = new ReferralPointDetailEntity();

        // Points earned
        entity.setPointsEarned(rs.getInt("points_earned"));

        // Created at timestamp
        LocalDateTime createdAt = rs.getTimestamp("created_at").toLocalDateTime();
        entity.setCreatedAt(createdAt);

        // Calculate expiration time (90 days after created_at)
        LocalDateTime expirationDate = createdAt.plusDays(90);
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime deletedAt = rs.getTimestamp("deleted_at") != null ? rs.getTimestamp("deleted_at").toLocalDateTime() : null;

        if (now.isBefore(expirationDate)) {
            // If not expired, calculate remaining days and minutes
            Duration duration = Duration.between(now, expirationDate);
            entity.setExpirationDaysLeft((int) duration.toDays());
            entity.setExpirationMinutesLeft((int) duration.toMinutes());
        } else {
            // If expired, set remaining days and minutes to zero
            entity.setExpirationDaysLeft(0);
            entity.setExpirationMinutesLeft(0);
        }

        // Set whether it's used or expired
        entity.setUsedOrExpired(deletedAt != null); // Default is false; you handle `deleted_at` elsewhere.

        return entity;
    }
}