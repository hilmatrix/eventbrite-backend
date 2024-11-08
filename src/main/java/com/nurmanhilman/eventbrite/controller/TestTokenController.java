package com.nurmanhilman.eventbrite.controller;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestTokenController {

    private final JwtDecoder jwtDecoder;

    public TestTokenController(JwtDecoder jwtDecoder) {
        this.jwtDecoder = jwtDecoder;
    }

    @GetMapping("/testtoken")
    public String testToken(@RequestHeader("Authorization") String authorizationHeader) {
        try {
            String token = authorizationHeader.replace("Bearer ", "");
            Jwt jwt = jwtDecoder.decode(token);
            String email = jwt.getSubject();

            return "From JWT token, the email is " + email;

        } catch (Exception e) {
            // If decoding fails, return an invalid token message
            return "JWT is invalid";
        }
    }
}
