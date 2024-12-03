package com.nurmanhilman.eventbrite.application;

import com.nurmanhilman.eventbrite.requests.LoginRequest;
import com.nurmanhilman.eventbrite.service.UserService;
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
    private final UserService userService;

    @Autowired
    public AuthApplication(JwtEncoder jwtEncoder, JdbcTemplate jdbcTemplate, PasswordEncoder passwordEncoder, UserService userService) {
        this.jwtEncoder = jwtEncoder;
        this.jdbcTemplate = jdbcTemplate;
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
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

    private boolean isEmpty(String data) {
        return (data == null) || (data.isEmpty());
    }

    public Map<String, String> resetPassword(LoginRequest loginRequest) {
        String password = loginRequest.getPassword();

        if (!userService.isEmailExist(loginRequest.getEmail())) {
            throw new RuntimeException(loginRequest.getEmail() + " is not found");
        }

        if (isEmpty(password)) {
            throw new IllegalArgumentException("Password should not empty");
        } else if (!isEmpty(password) && password.length() < 8) {
            throw new IllegalArgumentException("Password length should not less than 8");
        }

        String passwordHash = passwordEncoder.encode(password);

        try {
            String sql = "UPDATE users SET password_hash = ? WHERE email = ?";

            int rowsAffected = jdbcTemplate.update(sql, passwordHash, loginRequest.getEmail());

            if (rowsAffected > 0) {
                System.out.println("Password updated successfully.");
            } else {
                System.out.println("No user found with the given email.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error reset password");
        }

        Map<String, String> response = new HashMap<>();
        response.put("result", "Password Updated");
        return response;
    }
}
