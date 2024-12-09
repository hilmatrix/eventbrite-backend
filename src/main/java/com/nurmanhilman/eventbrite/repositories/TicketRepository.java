package com.nurmanhilman.eventbrite.repositories;

import com.nurmanhilman.eventbrite.entities.TicketEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TicketRepository {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    // Method to create a new ticket
    public void createTicket(Long userId, Long eventId, Long trxId, String code) {
        String sql = "INSERT INTO tickets (user_id, event_id, trx_id, code, is_used, created_at, updated_at) " +
                "VALUES (?, ?, ?, ?, FALSE, NOW(), NOW())";

        jdbcTemplate.update(sql, userId, eventId, trxId, code);
    }

    // Method to get a ticket by its ID
    public TicketEntity getTicket(Long id) {
        String sql = "SELECT * FROM tickets WHERE id = ?";

        return jdbcTemplate.queryForObject(sql, new Object[]{id}, (rs, rowNum) -> {
            TicketEntity ticket = new TicketEntity();
            ticket.setId(rs.getLong("id"));
            ticket.setUserId(rs.getLong("user_id"));
            ticket.setEventId(rs.getLong("event_id"));
            ticket.setTrxId(rs.getLong("trx_id"));
            ticket.setCode(rs.getString("code"));
            ticket.setIsUsed(rs.getBoolean("is_used"));
            ticket.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            ticket.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
            ticket.setDeletedAt(rs.getTimestamp("deleted_at") != null ? rs.getTimestamp("deleted_at").toLocalDateTime() : null);
            return ticket;
        });
    }

    // Method to get all tickets for a specific user
    public List<TicketEntity> getUserTickets(Long userId) {
        String sql = "SELECT * FROM tickets WHERE user_id = ?";

        return jdbcTemplate.query(sql, new Object[]{userId}, new RowMapper<TicketEntity>() {
            @Override
            public TicketEntity mapRow(java.sql.ResultSet rs, int rowNum) throws java.sql.SQLException {
                TicketEntity ticket = new TicketEntity();
                ticket.setId(rs.getLong("id"));
                ticket.setUserId(rs.getLong("user_id"));
                ticket.setEventId(rs.getLong("event_id"));
                ticket.setTrxId(rs.getLong("trx_id"));
                ticket.setCode(rs.getString("code"));
                ticket.setIsUsed(rs.getBoolean("is_used"));
                ticket.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                ticket.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
                ticket.setDeletedAt(rs.getTimestamp("deleted_at") != null ? rs.getTimestamp("deleted_at").toLocalDateTime() : null);
                return ticket;
            }
        });
    }

    public List<TicketEntity> findAllTicketsByOrganizerId(Long organizerId) {
        String sql = "SELECT t.* FROM tickets t " +
                "JOIN events e ON t.event_id = e.event_id " +
                "WHERE e.user_id = ?";

        return jdbcTemplate.query(sql, new Object[]{organizerId}, (rs, rowNum) -> {
            TicketEntity ticket = new TicketEntity();
            ticket.setId(rs.getLong("id"));
            ticket.setEventId(rs.getLong("event_id"));
            // Map other fields as necessary
            return ticket;
        });
    }
}
