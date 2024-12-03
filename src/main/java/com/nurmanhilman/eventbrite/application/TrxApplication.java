package com.nurmanhilman.eventbrite.application;

import com.nurmanhilman.eventbrite.entities.*;
import com.nurmanhilman.eventbrite.repositories.PromotionRepository;
import com.nurmanhilman.eventbrite.repositories.TrxPromoRepository;
import com.nurmanhilman.eventbrite.requests.TrxRequest;
import com.nurmanhilman.eventbrite.service.PromotionService;
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

    //BigDecimal discountedPrice = transaction.getTotalPrice().subtract(promotion.getPriceCut());
    //transaction.setTotalPrice(discountedPrice);

    public TrxEntity processTransaction(String authorizationHeader, Map<String, Object> trxData) {
        UserEntity userEntity = userService.getUserFromJwt(authorizationHeader);

        if (userEntity.isEventOrganizer()) {
            throw new CustomResponseStatusException(HttpStatus.FORBIDDEN, "Event organizers cannot create transactions");
        }

        TrxRequest trxRequest = new TrxRequest(trxData);

        if (!trxRequest.isValid) {
            throw new CustomResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid signup request: " + trxRequest.errorList);
        }

        Optional<EventEntity> eventEntity = eventRepository.findById(trxRequest.eventId);
        if (eventEntity.isEmpty()) {
            throw new CustomResponseStatusException(HttpStatus.NOT_FOUND, "Event with id " + trxRequest.eventId + " is not found");
        }

        if (trxRequest.ticketAmount <= 0) {
            throw new CustomResponseStatusException(HttpStatus.BAD_REQUEST, "Ticket amount should greater than 0");
        }

        int eventTransactions = trxService.getEventTransactions(trxRequest.eventId);

        if (eventEntity.get().getAvailableSeats() == eventTransactions) {
            throw new CustomResponseStatusException(HttpStatus.CONFLICT, "No seats / tickets left");
        }

        if ((eventEntity.get().getAvailableSeats() < (eventTransactions + trxRequest.ticketAmount))) {
            throw new CustomResponseStatusException(HttpStatus.CONFLICT, "Ticket amount exceeded available seats. Available seats = "
            + ((eventEntity.get().getAvailableSeats() - (eventTransactions))));
        }

        PromotionEntity promotionEntity = new PromotionEntity();

        if (trxRequest.isExistPromoCode) {
            Optional<PromotionEntity> optionalPromotionEntity = promotionRepository.findByPromoCode(trxRequest.promoCode);
            if (optionalPromotionEntity.isEmpty()) {
                throw new CustomResponseStatusException(HttpStatus.NOT_FOUND, "Promo code " + trxRequest.promoCode + " is not found");
            }
            promotionEntity = optionalPromotionEntity.get();
        }

        BigDecimal totalPrice = BigDecimal.valueOf(eventEntity.get().getPrice() * trxRequest.ticketAmount);

        if (trxRequest.isExistPromoCode) {
            totalPrice = totalPrice.subtract(promotionEntity.getPriceCut());
        }

        TrxEntity transaction = new TrxEntity();
        transaction.setEventId(trxRequest.eventId);
        transaction.setUserId(userEntity.getUserId());
        transaction.setTicketAmount(trxRequest.ticketAmount);
        transaction.setTotalPrice(totalPrice);
        transaction.setCreatedAt(Instant.now());
        transaction.setUpdatedAt(Instant.now());

        TrxEntity savedTransaction = trxService.saveTransaction(transaction);

        if (trxRequest.isExistPromoCode) {
            TrxPromoEntity trxPromoEntity = new TrxPromoEntity();
            trxPromoEntity.setTrxId(savedTransaction.getTrxId());
            trxPromoEntity.setPromoId(promotionEntity.getPromoId());
            trxPromoRepository.save(trxPromoEntity);
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
