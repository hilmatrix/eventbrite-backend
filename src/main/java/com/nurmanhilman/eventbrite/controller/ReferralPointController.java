package com.nurmanhilman.eventbrite.controller;

import com.nurmanhilman.eventbrite.entities.UserEntity;
import com.nurmanhilman.eventbrite.service.ReferralPointsService;
import com.nurmanhilman.eventbrite.service.UserService;
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
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ReferralPointsService referralPointsService;

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<?> getReferralPoints(@RequestHeader("Authorization") String authorizationHeader) {
        try {
            UserEntity userEntity = userService.getUserFromJwt(authorizationHeader);

            Map<String, Object> result =  new HashMap<>();
            result.put("referralPoints", referralPointsService.getPoints(userEntity.getUserId()));
            return ResponseEntity.status(200).body(result);
        } catch (Exception e) {
            // If decoding fails, return an invalid token message
            e.printStackTrace();
            return ResponseEntity.status(500).body("User not found");
        }
    }

    @GetMapping("/details")
    public ResponseEntity<?> getReferralPointsDetails(@RequestHeader("Authorization") String authorizationHeader) {
        try {
            UserEntity userEntity = userService.getUserFromJwt(authorizationHeader);

            referralPointsService.updateExpiredPoints(userEntity.getUserId());

            return ResponseEntity.status(200).body(referralPointsService.getPointsDetails(userEntity.getUserId()));
        } catch (Exception e) {
            // If decoding fails, return an invalid token message
            e.printStackTrace();
            return ResponseEntity.status(500).body("User not found");
        }
    }
}
