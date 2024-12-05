package com.nurmanhilman.eventbrite.requests;

import com.nurmanhilman.eventbrite.entities.TrxEntity;

import java.util.List;
import java.util.Map;

public class TrxRequest extends TemplateRequest {

    public Long eventId;
    public Integer ticketAmount;
    public String promoCode;
    public String referralDiscount;
    public Integer referralPointsUsed;

    public boolean isExistPromoCode;
    public boolean isExistReferralPointsUsed;
    public boolean isExistReferralDiscount;

    public TrxRequest(Map<String, Object> trxData) {
        super();
        this.request = trxData;
        validate();
    }

    public void validate() {
        if (isEmpty(getField("eventId")))
            addError("eventId should not Empty");
        else
            eventId = Long.parseLong(getField("eventId"));

        if (isEmpty(getField("ticketAmount")))
            addError("ticketAmount should not Empty");
        else
            ticketAmount = Integer.parseInt(getField("ticketAmount"));

        if (!isEmpty(getField("promoCode"))) {
            promoCode = getField("promoCode");
            isExistPromoCode = true;
        }

        if (!isEmpty(getField("referralPointsUsed"))) {
            referralPointsUsed = Integer.parseInt(getField("referralPointsUsed"));
            isExistReferralPointsUsed = true;
        }

        if (!isEmpty(getField("referralDiscount"))) {
            referralDiscount = getField("referralDiscount");
            isExistReferralDiscount = true;
        }

    }

}