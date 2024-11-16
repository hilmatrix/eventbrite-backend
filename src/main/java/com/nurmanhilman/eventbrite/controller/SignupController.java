package com.nurmanhilman.eventbrite.controller;

import com.nurmanhilman.eventbrite.requests.SignupRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static com.nurmanhilman.eventbrite.util.ReferralCodeGenerator.generateCode;

@RestController
@RequestMapping("/api/v1/signup")
public class SignupController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

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

    public Long getNextId(String table, String idName) {
        String sql = "SELECT MAX(" + idName +") FROM " +table;
        Long maxId = jdbcTemplate.queryForObject(sql, Long.class);
        return (maxId != null) ? maxId + 1 : 1L;
    }

    public Long getNextIdUsers() {
        return getNextId("users", "user_id");
    }

    @PostMapping
    public ResponseEntity<?> signup(@RequestBody Map<String, Object> signupData) {
        try {

            SignupRequest signupRequest = new SignupRequest(signupData);
            if (!signupRequest.isValid) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(signupRequest.errorList);
            }

            if (isEmailExist(signupRequest.email)) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Email " + signupRequest.email + " already exists");
            }

            if (signupRequest.isReferralExist) {
                if (!isReferralCodeExist(signupRequest.referral)) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body("Referral " +signupRequest.referral+ " is not found");
                }
            }

            // Encode the password
            String passwordHash = passwordEncoder.encode(signupRequest.password);
            String referralCode = generateUniqueReferralCode(8);
            Long nextUserId = getNextIdUsers();

            // Insert the new user into the database
            String insertSql = "INSERT INTO users (user_id, name, email, password_hash, " +
                    "is_event_organizer, referral_code) VALUES (?, ?, ?, ?, ?, ?)";

            int rowsInserted = jdbcTemplate.update(insertSql, nextUserId, signupRequest.name, signupRequest.email, passwordHash,
                    signupRequest.isOrganizer, referralCode);

            if (signupRequest.isReferralExist) {
                Long referralOwnerId = getIdFromReferralCode(signupRequest.referral);

                Long nextReferralPointId = getNextId("referral_points", "id");
                String insertReferralPoints = "INSERT INTO referral_points (id, owner_user_id, signup_user_id, points_earned) " +
                        "VALUES (?,?,?,?)";

                jdbcTemplate.update(insertReferralPoints, nextReferralPointId, referralOwnerId, nextUserId, 10000);
            }

            if (rowsInserted > 0) {
                return ResponseEntity.status(HttpStatus.CREATED).body("User signed up successfully");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to sign up user");
            }

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error occurred while signing up user");
        }
    }
}
