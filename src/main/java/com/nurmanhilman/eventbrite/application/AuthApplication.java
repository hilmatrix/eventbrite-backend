package com.nurmanhilman.eventbrite.application;

import com.nurmanhilman.eventbrite.entities.UserEntity;
import com.nurmanhilman.eventbrite.repositories.UserRepository;
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
    private UserRepository userRepository;

    @Autowired
    public AuthApplication(JwtEncoder jwtEncoder, JdbcTemplate jdbcTemplate, PasswordEncoder passwordEncoder, UserService userService) {
        this.jwtEncoder = jwtEncoder;
        this.jdbcTemplate = jdbcTemplate;
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
    }

    public Map<String, Object> login(LoginRequest loginRequest) {
        String sql = "SELECT password_hash,is_event_organizer FROM users WHERE email = ?";
        try {
            UserEntity userEntity = userRepository.findByEmail(loginRequest.getEmail()).get();
            String storedPasswordHash = userEntity.getPasswordHash();

            if (passwordEncoder.matches(loginRequest.getPassword(), storedPasswordHash)) {
                JwtClaimsSet claims = JwtClaimsSet.builder()
                        .issuer("your-issuer") // Replace with your issuer name
                        .subject(loginRequest.getEmail())
                        .issuedAt(Instant.now())
                        .expiresAt(Instant.now().plusSeconds(3600)) // Token valid for 1 hour
                        .build();

                Jwt jwt = jwtEncoder.encode(JwtEncoderParameters.from(claims));

                Map<String, Object> response = new HashMap<>();
                response.put("token", jwt.getTokenValue());
                response.put("user", userEntity);
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
