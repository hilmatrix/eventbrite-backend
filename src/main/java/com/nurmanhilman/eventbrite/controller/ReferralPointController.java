package com.nurmanhilman.eventbrite.controller;

import com.nurmanhilman.eventbrite.service.ReferralPointsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/referral-points")
public class ReferralPointController {
    @Autowired
    private final JwtDecoder jwtDecoder;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ReferralPointsService referralPointsService;

    public ReferralPointController(JwtDecoder jwtDecoder, JdbcTemplate jdbcTemplate) {
        this.jwtDecoder = jwtDecoder;
        this.jdbcTemplate = jdbcTemplate;
    }

    public String getEmailFromJwt(String authorizationHeader) {
        String token = authorizationHeader.replace("Bearer ", "");
        Jwt jwt = jwtDecoder.decode(token);
        return jwt.getSubject();
    }

    public Long getIdFromEmail(String email) {
        String referralCodeCheck = "SELECT user_id FROM users WHERE email = ?";
        return jdbcTemplate.queryForObject(referralCodeCheck, new Object[]{email}, Long.class);
    }

    public Long getReferralPointsFromDb(String email) {
        String referralCodeCheck = "SELECT SUM(points_earned) FROM referral_points WHERE owner_user_id = ?";

        Long id = getIdFromEmail(email);

        return (long) referralPointsService.getPoints(id);
    }

    @GetMapping
    public ResponseEntity<?> getReferralPoints(@RequestHeader("Authorization") String authorizationHeader) {
        try {
            String email = getEmailFromJwt(authorizationHeader);

            Map<String, Object> result =  new HashMap<>();
            result.put("referralPoints", getReferralPointsFromDb(email));
            return ResponseEntity.status(200).body(result);
        } catch (Exception e) {
            // If decoding fails, return an invalid token message
            e.printStackTrace();
            return ResponseEntity.status(500).body("User not found");
        }
    }
}
