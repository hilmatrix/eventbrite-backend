package com.nurmanhilman.eventbrite.application;

import com.nurmanhilman.eventbrite.requests.LoginRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Component
public class AuthApplication {

    private final JwtEncoder jwtEncoder;
    private final JdbcTemplate jdbcTemplate;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthApplication(JwtEncoder jwtEncoder, JdbcTemplate jdbcTemplate, PasswordEncoder passwordEncoder) {
        this.jwtEncoder = jwtEncoder;
        this.jdbcTemplate = jdbcTemplate;
        this.passwordEncoder = passwordEncoder;
    }

    public Map<String, String> login(LoginRequest loginRequest) {
        String sql = "SELECT password_hash FROM users WHERE email = ?";
        try {
            Map<String, Object> result = jdbcTemplate.queryForMap(sql, loginRequest.getEmail());
            String storedPasswordHash = (String) result.get("password_hash");

            if (passwordEncoder.matches(loginRequest.getPassword(), storedPasswordHash)) {
                JwtClaimsSet claims = JwtClaimsSet.builder()
                        .issuer("your-issuer") // Replace with your issuer name
                        .subject(loginRequest.getEmail())
                        .issuedAt(Instant.now())
                        .expiresAt(Instant.now().plusSeconds(3600)) // Token valid for 1 hour
                        .build();

                Jwt jwt = jwtEncoder.encode(JwtEncoderParameters.from(claims));

                Map<String, String> response = new HashMap<>();
                response.put("token", jwt.getTokenValue());
                return response;
            } else {
                throw new IllegalArgumentException("Wrong password");
            }
        } catch (Exception e) {
            throw new RuntimeException("User with email " + loginRequest.getEmail() + " does not exist");
        }
    }
}
