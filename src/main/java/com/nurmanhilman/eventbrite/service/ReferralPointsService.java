package com.nurmanhilman.eventbrite.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import static com.nurmanhilman.eventbrite.util.DatabaseHelper.getNextId;

@Service
public class ReferralPointsService {
    @Autowired
    private JdbcTemplate jdbcTemplate;


    public int addRecord(Long referralOwnerId, Long signupUserId, int points) {
        Long nextReferralPointId = getNextId(jdbcTemplate,"referral_points", "id");

        String insertReferralPoints = "INSERT INTO referral_points (id, owner_user_id, signup_user_id, points_earned) " +
                "VALUES (?,?,?,?)";

        return jdbcTemplate.update(insertReferralPoints, nextReferralPointId, referralOwnerId, signupUserId, points);
    }
}
