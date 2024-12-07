package com.nurmanhilman.eventbrite.service;

import com.nurmanhilman.eventbrite.entities.TrxEntity;
import com.nurmanhilman.eventbrite.repositories.TrxRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TrxService {
    @Autowired
    private TrxRepository trxRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<TrxEntity> getAllTransactions() {
        return trxRepository.findAll();
    }

    public Optional<TrxEntity> getTransactionById(Long trxId) {
        return trxRepository.findById(trxId);
    }

    public TrxEntity saveTransaction(TrxEntity transaction) {
        return trxRepository.save(transaction);
    }

    public void deleteTransaction(Long trxId) {
        trxRepository.deleteById(trxId);
    }

    public int getEventTransactions(Long eventId) {
        String sql = """
            SELECT 
                SUM(ticket_amount) 
            FROM 
                trx
            WHERE 
                event_id = ? 
                AND deleted_at IS NULL
            """;

        Integer totalTickets = jdbcTemplate.queryForObject(sql, Integer.class, eventId);
        return totalTickets != null ? totalTickets : 0; // Return 0 if no transactions are found
    }
}
