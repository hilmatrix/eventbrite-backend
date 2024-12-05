package com.nurmanhilman.eventbrite.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import static com.nurmanhilman.eventbrite.util.DatabaseHelper.getNextId;

@Service
public class ReferralPointsService {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void updateExpiredPoints(Long ownerUserId) {
        String sql = """
            UPDATE referral_points
            SET deleted_at = ?
            WHERE created_at <= ? 
              AND deleted_at IS NULL
              AND owner_user_id = ?
        """;

        // Calculate the timestamp for 90 days ago
        LocalDateTime ninetyDaysAgo = LocalDateTime.now().minusDays(90);
        Timestamp currentTimestamp = Timestamp.valueOf(LocalDateTime.now());
        Timestamp ninetyDaysAgoTimestamp = Timestamp.valueOf(ninetyDaysAgo);

        // Execute the update query
        jdbcTemplate.update(sql, currentTimestamp, ninetyDaysAgoTimestamp, ownerUserId);
    }

    public int getPoints(Long ownerUserId) {
        String sql = """
            SELECT COALESCE(SUM(points_earned), 0) 
            FROM referral_points 
            WHERE created_at >= ? 
              AND deleted_at IS NULL
              AND owner_user_id = ?
        """;

        // Calculate the timestamp for 90 days ago
        LocalDateTime ninetyDaysAgo = LocalDateTime.now().minusDays(90);
        Timestamp ninetyDaysAgoTimestamp = Timestamp.valueOf(ninetyDaysAgo);

        // Execute the query and get the total points
        return jdbcTemplate.queryForObject(sql, Integer.class, ninetyDaysAgoTimestamp, ownerUserId);
    }

    public int addRecord(Long referralOwnerId, Long signupUserId, int points) {
        Long nextReferralPointId = getNextId(jdbcTemplate, "referral_points", "id");

        String insertReferralPoints = """
            INSERT INTO referral_points (id, owner_user_id, signup_user_id, points_earned) 
            VALUES (?, ?, ?, ?)
        """;

        return jdbcTemplate.update(insertReferralPoints, nextReferralPointId, referralOwnerId, signupUserId, points);
    }

    @Transactional
    public void usePoints10k(Long ownerUserId, int count) {
        String selectSql = """
            SELECT id 
            FROM referral_points
            WHERE deleted_at IS NULL
              AND owner_user_id = ?
            ORDER BY created_at ASC
            LIMIT ?
        """;

        String updateSql = """
            UPDATE referral_points
            SET deleted_at = ?
            WHERE id = ?
        """;

        // Fetch the oldest `count` records that are not deleted
        List<Integer> ids = jdbcTemplate.queryForList(selectSql, Integer.class, ownerUserId, count);

        // If there aren't enough points available, throw an exception
        if (ids.size() < count) {
            throw new IllegalArgumentException("Not enough points available to use.");
        }

        // Set the current timestamp
        Timestamp currentTimestamp = Timestamp.valueOf(LocalDateTime.now());

        // Mark each of the selected records as deleted
        for (Integer id : ids) {
            jdbcTemplate.update(updateSql, currentTimestamp, id);
        }
    }

    private Long getNextId(JdbcTemplate jdbcTemplate, String tableName, String columnName) {
        String sql = String.format("SELECT COALESCE(MAX(%s), 0) + 1 FROM %s", columnName, tableName);
        return jdbcTemplate.queryForObject(sql, Long.class);
    }
}
