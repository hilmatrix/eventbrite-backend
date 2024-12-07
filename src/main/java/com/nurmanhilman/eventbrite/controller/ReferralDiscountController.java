package com.nurmanhilman.eventbrite.controller;

import com.nurmanhilman.eventbrite.entities.UserEntity;
import com.nurmanhilman.eventbrite.repositories.ReferralDiscountRepository;
import com.nurmanhilman.eventbrite.repositories.TicketRepository;
import com.nurmanhilman.eventbrite.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/referral-discounts")
public class ReferralDiscountController {
    @Autowired
    private UserService userService;

    @Autowired
    private ReferralDiscountRepository referralDiscountRepository;

    @GetMapping
    public ResponseEntity<?> getAllReferralDiscounts(@RequestHeader("Authorization") String authorizationHeader) {
        UserEntity userEntity = userService.getUserFromJwt(authorizationHeader);
        return ResponseEntity.ok(referralDiscountRepository.getReferralDiscountByUser(userEntity.getUserId()));
    }
}