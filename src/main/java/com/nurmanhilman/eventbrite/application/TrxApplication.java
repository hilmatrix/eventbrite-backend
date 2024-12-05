package com.nurmanhilman.eventbrite.application;

import com.nurmanhilman.eventbrite.entities.*;
import com.nurmanhilman.eventbrite.repositories.PromotionRepository;
import com.nurmanhilman.eventbrite.repositories.TicketRepository;
import com.nurmanhilman.eventbrite.repositories.TrxPromoRepository;
import com.nurmanhilman.eventbrite.requests.TrxRequest;
import com.nurmanhilman.eventbrite.service.PromotionService;
import com.nurmanhilman.eventbrite.service.ReferralPointsService;
import com.nurmanhilman.eventbrite.service.TrxService;
import com.nurmanhilman.eventbrite.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.server.ResponseStatusException;
import com.nurmanhilman.eventbrite.exception.CustomResponseStatusException;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import com.nurmanhilman.eventbrite.repositories.EventRepository;

import static com.nurmanhilman.eventbrite.util.AlphaNumericGenerator.generateCode;

@Component
public class TrxApplication {

    @Autowired
    private TrxService trxService;

    @Autowired
    private PromotionService promotionService;

    @Autowired
    private PromotionRepository promotionRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private TrxPromoRepository trxPromoRepository;

    @Autowired
    private ReferralPointsService referralPointsService;

    @Autowired
    private TicketRepository ticketRepository;

    //BigDecimal discountedPrice = transaction.getTotalPrice().subtract(promotion.getPriceCut());
    //transaction.setTotalPrice(discountedPrice);

    public TrxEntity processTransaction(String authorizationHeader, Map<String, Object> trxData) {
        UserEntity userEntity = userService.getUserFromJwt(authorizationHeader);

        // Don't allow organizer for  transaction
        if (userEntity.isEventOrganizer()) {
            throw new CustomResponseStatusException(HttpStatus.FORBIDDEN, "Event organizers cannot create transactions");
        }

        TrxRequest trxRequest = new TrxRequest(trxData);

        // JSON Body request should be valid
        if (!trxRequest.isValid) {
            throw new CustomResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid signup request: " + trxRequest.errorList);
        }

        //  event id should exist in database
        Optional<EventEntity> eventEntity = eventRepository.findById(trxRequest.eventId);
        if (eventEntity.isEmpty()) {
            throw new CustomResponseStatusException(HttpStatus.NOT_FOUND, "Event with id " + trxRequest.eventId + " is not found");
        }

        // ticket amount should is greater than 0
        if (trxRequest.ticketAmount <= 0) {
            throw new CustomResponseStatusException(HttpStatus.BAD_REQUEST, "Ticket amount should greater than 0");
        }

        int eventTransactions = trxService.getEventTransactions(trxRequest.eventId);

        // Check if seats available
        if (eventEntity.get().getAvailableSeats() == eventTransactions) {
            throw new CustomResponseStatusException(HttpStatus.CONFLICT, "No seats / tickets left");
        }

        int availableSeats = (eventEntity.get().getAvailableSeats() - (eventTransactions));

        // ticket amount should less or equal available seats
        if (availableSeats < trxRequest.ticketAmount) {
            throw new CustomResponseStatusException(HttpStatus.CONFLICT, "Ticket amount exceeded available seats. Available seats = "
            + availableSeats);
        }

        PromotionEntity promotionEntity = new PromotionEntity();

        // Check if Promocode Exist
        if (trxRequest.isExistPromoCode) {
            Optional<PromotionEntity> optionalPromotionEntity = promotionRepository.findByPromoCode(trxRequest.promoCode);
            if (optionalPromotionEntity.isEmpty()) {
                throw new CustomResponseStatusException(HttpStatus.NOT_FOUND, "Promo code " + trxRequest.promoCode + " is not found");
            }
            promotionEntity = optionalPromotionEntity.get();
        }

        // promo code event should equal to transaction event
        if (trxRequest.eventId != promotionEntity.getEventId()) {
            throw new CustomResponseStatusException(HttpStatus.CONFLICT, "Promo code "
                    + trxRequest.promoCode + " is for event " + promotionEntity.getEventId());
        }

        int referralPoints = 0;

        // ReferralPointsUsed should less than available
        if (trxRequest.isExistReferralPointsUsed) {
            referralPointsService.updateExpiredPoints(userEntity.getUserId());

            referralPoints = referralPointsService.getPoints(userEntity.getUserId());

            if (trxRequest.referralPointsUsed > referralPoints) {
                throw new CustomResponseStatusException(HttpStatus.CONFLICT, "Not enough referral points. ReferralPoints = " + referralPoints);
            }

            // for now, ReferralPointsUsed should be multiple of 10k
            if ((trxRequest.referralPointsUsed % 10000) > 0) {
                throw new CustomResponseStatusException(HttpStatus.CONFLICT, "ReferralPoints used should be multiple of 10000");
            }
        }

        int referralPoints10k = referralPoints / 10000;

        BigDecimal totalPrice = BigDecimal.valueOf(eventEntity.get().getPrice() * trxRequest.ticketAmount);

        // total referral points should now greater than total price
        if (totalPrice.compareTo(BigDecimal.valueOf(referralPoints)) < 0) {
            throw new CustomResponseStatusException(HttpStatus.CONFLICT, "ReferralPoints should not greater than total price");
        }

        if (trxRequest.isExistReferralPointsUsed) {
            totalPrice = totalPrice.subtract(BigDecimal.valueOf(referralPoints));
        }

        // processing promo code
        if (trxRequest.isExistPromoCode) {
            BigDecimal priceCut = promotionEntity.getPriceCut();
            if (promotionEntity.getIsPercentage()) {
                priceCut = priceCut.multiply(totalPrice).divide(BigDecimal.valueOf(100.0));
            }
            totalPrice = totalPrice.subtract(priceCut);
        }

        // Saving transaction
        TrxEntity transaction = new TrxEntity();
        transaction.setEventId(trxRequest.eventId);
        transaction.setUserId(userEntity.getUserId());
        transaction.setTicketAmount(trxRequest.ticketAmount);
        transaction.setTotalPrice(totalPrice);
        transaction.setCreatedAt(Instant.now());
        transaction.setUpdatedAt(Instant.now());

        TrxEntity savedTransaction = trxService.saveTransaction(transaction);

        if (trxRequest.isExistReferralPointsUsed) {
            referralPointsService.usePoints10k(userEntity.getUserId(), 2);
        }

        // Create record of used promo code
        if (trxRequest.isExistPromoCode) {
            TrxPromoEntity trxPromoEntity = new TrxPromoEntity();
            trxPromoEntity.setTrxId(savedTransaction.getTrxId());
            trxPromoEntity.setPromoId(promotionEntity.getPromoId());
            trxPromoRepository.save(trxPromoEntity);
        }

        for (int loop = 0; loop < trxRequest.ticketAmount; loop++) {
            ticketRepository.createTicket(
                    userEntity.getUserId(),
                    eventEntity.get().getEventId(),
                    savedTransaction.getTrxId(),
                    "TICKET-"+generateCode(8));
        }

        return savedTransaction;
    }

    public TrxEntity updateTransaction(Long trxId, TrxEntity transactionDetails) {
        return trxService.getTransactionById(trxId).map(transaction -> {
            transaction.setTicketAmount(transactionDetails.getTicketAmount());
            transaction.setTotalPrice(transactionDetails.getTotalPrice());
            transaction.setUpdatedAt(Instant.now());
            return trxService.saveTransaction(transaction);
        }).orElseThrow(() -> new RuntimeException("Transaction not found with id " + trxId));
    }
}
