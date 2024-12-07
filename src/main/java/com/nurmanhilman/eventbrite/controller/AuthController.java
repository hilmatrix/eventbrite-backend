package com.nurmanhilman.eventbrite.controller;

import com.nurmanhilman.eventbrite.application.AuthApplication;
import com.nurmanhilman.eventbrite.requests.LoginRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/login")
public class AuthController {

    private final AuthApplication authApplication;

    @Autowired
    public AuthController(AuthApplication authApplication) {
        this.authApplication = authApplication;
    }

    @PostMapping
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            Map<String, Object> tokenResponse = authApplication.login(loginRequest);
            return ResponseEntity.ok(tokenResponse);
        } catch (IllegalArgumentException e) {
            Map<String, String> response = Map.of("result", e.getMessage());
            return ResponseEntity.status(401).body(response);
        } catch (RuntimeException e) {
            Map<String, String> response = Map.of("result", e.getMessage());
            return ResponseEntity.status(404).body(response);
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(@RequestBody LoginRequest loginRequest) {
        try {
            Map<String, String> tokenResponse = authApplication.resetPassword(loginRequest);
            return ResponseEntity.ok(tokenResponse);
        } catch (IllegalArgumentException e) {
            Map<String, String> response = Map.of("result", e.getMessage());
            return ResponseEntity.status(401).body(response);
        } catch (RuntimeException e) {
            Map<String, String> response = Map.of("result", e.getMessage());
            return ResponseEntity.status(404).body(response);
        }
    }
}
