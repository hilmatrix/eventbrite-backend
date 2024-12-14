package com.nurmanhilman.eventbrite.service;

import com.nurmanhilman.eventbrite.entities.TrxEntity;
import com.nurmanhilman.eventbrite.repositories.TrxRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    public BigDecimal getTotalTransactionPriceByOrganizerId(Long organizerId) {
        String sql = "SELECT COALESCE(SUM(t.total_price), 0) AS total_price " +
                "FROM trx t " +
                "JOIN events e ON t.event_id = e.event_id " +
                "WHERE e.user_id = ?";

        return jdbcTemplate.queryForObject(sql, new Object[]{organizerId}, BigDecimal.class);
    }

    public Map<String, BigDecimal> getMonthlyTransactionPriceByOrganizerId(Long organizerId, int year) {
        String sql = "SELECT EXTRACT(MONTH FROM t.created_at) AS month, COALESCE(SUM(t.total_price), 0) AS total_price " +
                "FROM trx t " +
                "JOIN events e ON t.event_id = e.event_id " +
                "WHERE e.user_id = ? AND EXTRACT(YEAR FROM t.created_at) = ? " +
                "GROUP BY EXTRACT(MONTH FROM t.created_at)";

        Map<String, BigDecimal> monthlyProfits = new HashMap<>();

        // Initialize the map with all months from 1 to 12 as keys, and 0 as default profit value
        for (int month = 1; month <= 12; month++) {
            monthlyProfits.put("month_"+String.valueOf(month), BigDecimal.ZERO);
        }

        jdbcTemplate.query(sql, new Object[]{organizerId, year}, rs -> {
            int month = rs.getInt("month");
            BigDecimal totalPrice = rs.getBigDecimal("total_price");
            monthlyProfits.put("month_"+String.valueOf(month), totalPrice);
        });

        return monthlyProfits;
    }
}
