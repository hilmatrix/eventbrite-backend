package com.nurmanhilman.eventbrite.service;

import com.nurmanhilman.eventbrite.entities.TrxEntity;
import com.nurmanhilman.eventbrite.repositories.TrxRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service

public class TrxService {
    private final TrxRepository trxRepository;

    @Autowired
    public TrxService(TrxRepository trxRepository) {
        this.trxRepository = trxRepository;
    }
    public List<TrxEntity> getAllTransactions() {
        return trxRepository.findAll();
    }

    public Optional<TrxEntity> getTransactionById(Long trxId) {
        return trxRepository.findById(trxId);
    }

    public TrxEntity createTransaction(TrxEntity transaction) {
        return trxRepository.save(transaction);
    }

    public TrxEntity updateTransaction(Long trxId, TrxEntity transactionDetails) {
        return trxRepository.findById(trxId).map(transaction -> {
            transaction.setTicketAmount(transactionDetails.getTicketAmount());
            transaction.setTotalPrice(transactionDetails.getTotalPrice());
            transaction.setReferralCodeUsed(transactionDetails.getReferralCodeUsed());
            transaction.setUpdatedAt(Instant.now());
            return trxRepository.save(transaction);
        }).orElseThrow(() -> new RuntimeException("Transaction not found with id " + trxId));
    }

    public void deleteTransaction(Long trxId) {
        trxRepository.deleteById(trxId);
    }

}
