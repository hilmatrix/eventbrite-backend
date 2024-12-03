package com.nurmanhilman.eventbrite.controller;

import com.nurmanhilman.eventbrite.application.UserApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserApplication userApplication;

    @Autowired
    public UserController(UserApplication userApplication) {
        this.userApplication = userApplication;
    }

    @GetMapping
    public ResponseEntity<?> returnUserDetails(@RequestHeader("Authorization") String authorizationHeader) {
        try {
            return ResponseEntity.ok(userApplication.getUserDetails(authorizationHeader));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> updateUserDetails(@RequestHeader("Authorization") String authorizationHeader,
                                               @RequestBody Map<String, Object> signupData) {
        try {
            return ResponseEntity.ok(userApplication.updateUserDetails(authorizationHeader, signupData));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @GetMapping("/isorganizer")
    public ResponseEntity<?> checkIfOrganizer(@RequestHeader("Authorization") String authorizationHeader) {
        try {
            return ResponseEntity.ok(userApplication.isOrganizer(authorizationHeader));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }
}
