package com.nurmanhilman.eventbrite.controller;

import lombok.Data;

@Data
public class LoginRequest {
    private String email;
    private String password;
}