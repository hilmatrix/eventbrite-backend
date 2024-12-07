package com.nurmanhilman.eventbrite.controller;

import com.nurmanhilman.eventbrite.entities.TrxEntity;
import com.nurmanhilman.eventbrite.entities.UserEntity;
import com.nurmanhilman.eventbrite.repositories.TrxRepository;
import com.nurmanhilman.eventbrite.service.TrxService;
import com.nurmanhilman.eventbrite.application.TrxApplication;
import com.nurmanhilman.eventbrite.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/transactions")
public class TrxController {

    @Autowired
    private UserService userService;

    @Autowired
    private TrxRepository trxRepository;

    private final TrxService trxService;
    private final TrxApplication trxApplication;

    @Autowired
    public TrxController(TrxService trxService, TrxApplication trxApplication) {
        this.trxService = trxService;
        this.trxApplication = trxApplication;
    }


    @GetMapping
    public ResponseEntity<?> getAllTransactions(@RequestHeader("Authorization") String authorizationHeader) {
        UserEntity userEntity = userService.getUserFromJwt(authorizationHeader);
        List<TrxEntity> transactions = trxRepository.findByUserId(userEntity.getUserId());
        return ResponseEntity.ok(transactions.isEmpty() ? Collections.emptyList() : transactions);
    }


    @GetMapping("/{trxId}")
    public ResponseEntity<TrxEntity> getTransactionById(@RequestHeader("Authorization") String authorizationHeader,
                                                        @PathVariable Long trxId) {
        return trxService.getTransactionById(trxId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> createTransaction(@RequestHeader("Authorization") String authorizationHeader,
                                                       @RequestBody Map<String, Object> trxData) {
        ResponseEntity<TrxEntity> response;
        response = ResponseEntity.ok(trxApplication.processTransaction(authorizationHeader, trxData));
        return response;
    }

    @PostMapping("/preview")
    public ResponseEntity<?> createTransactionPreview(@RequestHeader("Authorization") String authorizationHeader,
                                               @RequestBody Map<String, Object> trxData) {
        ResponseEntity<TrxEntity> response;
        response = ResponseEntity.ok(trxApplication.processTransaction(authorizationHeader, trxData, true));
        return response;
    }

    @PutMapping("/{trxId}")
    public ResponseEntity<TrxEntity> updateTransaction(@RequestHeader("Authorization") String authorizationHeader,
                                                       @PathVariable Long trxId, @RequestBody TrxEntity trxDetails) {
        try {
            TrxEntity updatedTransaction = trxApplication.updateTransaction(trxId, trxDetails);
            return ResponseEntity.ok(updatedTransaction);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{trxId}")
    public ResponseEntity<Void> deleteTransaction(@RequestHeader("Authorization") String authorizationHeader,
                                                  @PathVariable Long trxId) {
        trxService.deleteTransaction(trxId);
        return ResponseEntity.noContent().build();
    }
}
