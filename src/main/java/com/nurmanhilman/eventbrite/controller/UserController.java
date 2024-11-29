package com.nurmanhilman.eventbrite.controller;

import com.nurmanhilman.eventbrite.entities.UserEntity;
import com.nurmanhilman.eventbrite.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController  // Only use @RestController here
@RequestMapping("/api/v1/user")  // Set the base URL path here
public class UserController {

    private final JwtDecoder jwtDecoder;
    private final UserRepository userRepository;

    @Autowired
    public UserController(JwtDecoder jwtDecoder, UserRepository userRepository) {
        this.jwtDecoder = jwtDecoder;
        this.userRepository = userRepository;
    }

    public String getEmailFromJwt(String authorizationHeader) {
        String token = authorizationHeader.replace("Bearer ", "");
        Jwt jwt = jwtDecoder.decode(token);
        return jwt.getSubject();
    }

    @GetMapping
    public ResponseEntity<?> returnUserDetails(@RequestHeader("Authorization") String authorizationHeader) {
        try {
            String email = getEmailFromJwt(authorizationHeader);
            Optional<UserEntity> userOptional = userRepository.findByEmail(email);

            if (userOptional.isPresent()) {
                UserEntity user = userOptional.get();
                return ResponseEntity.ok(user);
            } else {
                return ResponseEntity.status(404).body("User not found");
            }
        } catch (Exception e) {
            return ResponseEntity.status(400).body("JWT is invalid");
        }
    }

    @PostMapping
    public ResponseEntity<?> updateUserDetails(@RequestHeader("Authorization") String authorizationHeader,
                                               @RequestBody Map<String, Object> signupData) {
        try {
            String email = getEmailFromJwt(authorizationHeader);
            Optional<UserEntity> userOptional = userRepository.findByEmail(email);

            if (userOptional.isPresent()) {
                UserEntity user = userOptional.get();
                return ResponseEntity.ok(user);
            } else {
                return ResponseEntity.status(404).body("User not found");
            }
        } catch (Exception e) {
            return ResponseEntity.status(400).body("JWT is invalid");
        }
    }

    @GetMapping("/isorganizer")
    public ResponseEntity<?> checkIfOrganizer(@RequestHeader("Authorization") String authorizationHeader) {
        try {
            String email = getEmailFromJwt(authorizationHeader);
            Optional<UserEntity> userOptional = userRepository.findByEmail(email);

            if (userOptional.isPresent()) {
                UserEntity user = userOptional.get();
                boolean isOrganizer = user.isEventOrganizer();
                return ResponseEntity.ok(isOrganizer);
            } else {
                return ResponseEntity.status(404).body("User not found");
            }
        } catch (Exception e) {
            return ResponseEntity.status(400).body("JWT is invalid");
        }
    }

}
