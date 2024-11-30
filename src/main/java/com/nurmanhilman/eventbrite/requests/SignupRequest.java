package com.nurmanhilman.eventbrite.requests;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SignupRequest {
    public boolean isValid;
    public List<String> errorList;

    public String name;
    public String email;
    public String password;
    public String role;
    public String referral;
    public boolean isReferralExist;

    public boolean isOrganizer;

    public SignupRequest(Map<String, Object> signupData) {
        isValid = true;
        errorList = new ArrayList<>();

        validate(signupData);
    }

    public SignupRequest addError(String error) {
        isValid = false;
        errorList.add(error);
        return this;
    }

    private boolean isEmpty(String data) {
        return (data == null) || (data.isEmpty());
    }

    public void validate(Map<String, Object> signupData) {
        name = (String) signupData.get("name");
        email = (String) signupData.get("email");
        password = (String) signupData.get("password");
        role = (String) signupData.get("role");
        referral = (String) signupData.get("referral");

        if (isEmpty(name))
            addError("Name should not Empty");

        if (isEmpty(email))
            addError("Email should not Empty");

        if (isEmpty(password)) {
            addError("Password should not Empty");
        } else if (!isEmpty(password) && password.length() < 8) {
            addError("Password length should not less than 8");
        }

        if (isEmpty(role)) {
            addError("Role should not Empty");
        }  else if (!role.equals("customer") && !role.equals("organizer")) {
            addError("Role should be customer or organizer");
        } else {
            isOrganizer = role.equals("organizer") ? true: false;
        }

        isReferralExist = isEmpty(referral) ? false : true;
    }
}