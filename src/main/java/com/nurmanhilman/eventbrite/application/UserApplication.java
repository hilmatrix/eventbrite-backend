package com.nurmanhilman.eventbrite.application;

import com.nurmanhilman.eventbrite.entities.UserEntity;
import com.nurmanhilman.eventbrite.repositories.UserRepository;
import com.nurmanhilman.eventbrite.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

@Component
public class UserApplication {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    public UserEntity getUserDetails(String authorizationHeader) {
        String email = userService.getEmailFromJwt(authorizationHeader);
        return userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
    }

    public UserEntity updateUserDetails(String authorizationHeader, Map<String, Object> signupData) {
        String email = userService.getEmailFromJwt(authorizationHeader);
        UserEntity user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        // Update user details based on signupData (example logic)
        if (signupData.containsKey("name")) {
            user.setName((String) signupData.get("name"));
        }
        // Save the updated user entity
        return userRepository.save(user);
    }

    public boolean isOrganizer(String authorizationHeader) {
        String email = userService.getEmailFromJwt(authorizationHeader);
        UserEntity user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        return user.isEventOrganizer();
    }
}
