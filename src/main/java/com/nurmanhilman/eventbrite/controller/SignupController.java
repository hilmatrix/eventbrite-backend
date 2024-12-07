package com.nurmanhilman.eventbrite.controller;

import com.nurmanhilman.eventbrite.application.SignupApplication;
import com.nurmanhilman.eventbrite.requests.SignupRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/signup")
public class SignupController {

    @Autowired
    private SignupApplication signupApplication;

    @PostMapping
    public ResponseEntity<?> signup(@RequestBody Map<String, Object> signupData) {
        try {
            SignupRequest signupRequest = new SignupRequest(signupData);
            String message = signupApplication.processSignup(signupRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(message);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error occurred");
        }
    }
}
