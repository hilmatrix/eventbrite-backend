package com.nurmanhilman.eventbrite.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import static com.nurmanhilman.eventbrite.util.DatabaseHelper.getNextId;
import static com.nurmanhilman.eventbrite.util.ReferralCodeGenerator.generateCode;

@Service
public class UserService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public boolean isEmailExist(String email) {
        String emailCheckSql = "SELECT COUNT(*) FROM users WHERE email = ?";
        Integer count = jdbcTemplate.queryForObject(emailCheckSql, new Object[]{email}, Integer.class);
        return (count != null && count > 0);
    }

    public boolean isReferralCodeExist(String code) {
        String referralCodeCheck = "SELECT COUNT(*) FROM users WHERE referral_code = ?";
        Integer count = jdbcTemplate.queryForObject(referralCodeCheck, new Object[]{code}, Integer.class);
        return (count != null && count > 0);
    }

    public Long getIdFromReferralCode(String code) {
        String referralCodeCheck = "SELECT user_id FROM users WHERE referral_code = ?";
        return jdbcTemplate.queryForObject(referralCodeCheck, new Object[]{code}, Long.class);
    }

    public String generateUniqueReferralCode(int length) {
        String code;
        do {
            code = generateCode(length);  // Generate a new referral code
        } while (isReferralCodeExist(code));  // Check if the code already exists
        return code;  // Return the unique referral code
    }

    public Long getNextIdUsers() {
        return getNextId(jdbcTemplate,"users", "user_id");
    }

    public int addRecord(Long nextUserId, String name, String email, String passwordHash, boolean isOrganizer, String referralCode) {

        String insertSql = "INSERT INTO users (user_id, name, email, password_hash, " +
                "is_event_organizer, referral_code) VALUES (?, ?, ?, ?, ?, ?)";

        return jdbcTemplate.update(insertSql, nextUserId, name, email, passwordHash,
                isOrganizer, referralCode);
    }
}
