package com.nurmanhilman.eventbrite.controller;

import com.nurmanhilman.eventbrite.entities.TrxEntity;
import com.nurmanhilman.eventbrite.service.TrxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/transactions")
public class TrxController {

    private final TrxService transactionService;

    @Autowired
    public TrxController(TrxService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping
    public List<TrxEntity> getAllTransactions() {
        return transactionService.getAllTransactions();
    }

    @GetMapping("/{trxId}")
    public ResponseEntity<TrxEntity> getTransactionById(@PathVariable Long trxId) {
        return transactionService.getTransactionById(trxId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public TrxEntity createTransaction(@RequestBody TrxEntity transaction) {
        return transactionService.createTransaction(transaction);
    }

    @PutMapping("/{trxId}")
    public ResponseEntity<TrxEntity> updateTransaction(@PathVariable Long trxId, @RequestBody TrxEntity transactionDetails) {
        try {
            TrxEntity updatedTransaction = transactionService.updateTransaction(trxId, transactionDetails);
            return ResponseEntity.ok(updatedTransaction);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{trxId}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long trxId) {
        transactionService.deleteTransaction(trxId);
        return ResponseEntity.noContent().build();
    }
}

