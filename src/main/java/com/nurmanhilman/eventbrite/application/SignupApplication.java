package com.nurmanhilman.eventbrite.application;

import com.nurmanhilman.eventbrite.repositories.ReferralDiscountRepository;
import com.nurmanhilman.eventbrite.requests.SignupRequest;
import com.nurmanhilman.eventbrite.service.ReferralPointsService;
import com.nurmanhilman.eventbrite.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import static com.nurmanhilman.eventbrite.util.AlphaNumericGenerator.generateCode;

@Component
public class SignupApplication {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserService userService;

    @Autowired
    private ReferralPointsService referralPointsService;

    @Autowired
    private ReferralDiscountRepository referralDiscountRepository;



    public String processSignup(SignupRequest signupRequest) {
        if (!signupRequest.isValid) {
            throw new IllegalArgumentException("Invalid signup request: " + signupRequest.errorList);
        }

        if (userService.isEmailExist(signupRequest.email)) {
            throw new IllegalStateException("Email " + signupRequest.email + " already exists");
        }

        if (signupRequest.isReferralExist && !userService.isReferralCodeExist(signupRequest.referral)) {
            throw new IllegalStateException("Referral " + signupRequest.referral + " is not found");
        }

        // Encode the password
        String passwordHash = passwordEncoder.encode(signupRequest.password);
        String referralCode = userService.generateUniqueReferralCode(8);

        Long nextUserId = userService.getNextIdUsers();

        int rowsInserted = userService.addRecord(nextUserId, signupRequest.name, signupRequest.email, passwordHash,
                signupRequest.isOrganizer, referralCode);

        if (signupRequest.isReferralExist) {
            Long referralOwnerId = userService.getIdFromReferralCode(signupRequest.referral);
            referralPointsService.addRecord(referralOwnerId, nextUserId, 10000);
        }

        if (rowsInserted <= 0) {
            throw new RuntimeException("Failed to insert user record");
        }

        if (signupRequest.isReferralExist)
            referralDiscountRepository.createReferralDiscount(nextUserId, "DISCOUNT-" + generateCode(8), 10f);

        return "User signed up successfully";
    }
}
