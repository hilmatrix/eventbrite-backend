package com.nurmanhilman.eventbrite.application;

import com.nurmanhilman.eventbrite.entities.UserEntity;
import com.nurmanhilman.eventbrite.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

@Component
public class UserApplication {

    private final JwtDecoder jwtDecoder;
    private final UserRepository userRepository;

    @Autowired
    public UserApplication(JwtDecoder jwtDecoder, UserRepository userRepository) {
        this.jwtDecoder = jwtDecoder;
        this.userRepository = userRepository;
    }

    public String getEmailFromJwt(String authorizationHeader) {
        try {
            String token = authorizationHeader.replace("Bearer ", "");
            Jwt jwt = jwtDecoder.decode(token);
            return jwt.getSubject();
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid JWT token");
        }
    }

    public UserEntity getUserDetails(String authorizationHeader) {
        String email = getEmailFromJwt(authorizationHeader);
        return userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
    }

    public UserEntity updateUserDetails(String authorizationHeader, Map<String, Object> signupData) {
        String email = getEmailFromJwt(authorizationHeader);
        UserEntity user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        // Update user details based on signupData (example logic)
        if (signupData.containsKey("name")) {
            user.setName((String) signupData.get("name"));
        }
        // Save the updated user entity
        return userRepository.save(user);
    }

    public boolean isOrganizer(String authorizationHeader) {
        String email = getEmailFromJwt(authorizationHeader);
        UserEntity user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        return user.isEventOrganizer();
    }
}
