package com.nurmanhilman.eventbrite.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import com.nurmanhilman.eventbrite.entities.ReferralDiscountEntity;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class ReferralDiscountRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public boolean isExistReferralDiscount(String code) {
        String sql = "SELECT COUNT(*) FROM referral_discounts WHERE code = ? AND deleted_at IS NULL";
        Integer count = jdbcTemplate.queryForObject(sql, new Object[]{code}, Integer.class);
        return count != null && count > 0;
    }

    // Method to create a referral discount with expiration date set 90 days after creation
    public void createReferralDiscount(Long userId, String code, float percentage) {
        String sql = "INSERT INTO referral_discounts (user_id, code, percentage, expires_at, created_at, updated_at) " +
                "VALUES (?, ?, ?, ?, NOW(), NOW())";

        // Set expiration date to 90 days after creation
        LocalDateTime expiresAt = LocalDateTime.now().plusDays(90);

        jdbcTemplate.update(sql, userId, code, percentage, expiresAt);
    }

    // Method to get a referral discount by its code
    public ReferralDiscountEntity getReferralDiscountByCode(String code) {
        String sql = "SELECT * FROM referral_discounts WHERE code = ? AND deleted_at IS NULL";

        return jdbcTemplate.queryForObject(sql, new Object[]{code}, (rs, rowNum) -> {
            ReferralDiscountEntity discount = new ReferralDiscountEntity();
            discount.setId(rs.getLong("id"));
            discount.setUserId(rs.getLong("user_id"));
            discount.setCode(rs.getString("code"));
            discount.setPercentage(rs.getFloat("percentage"));
            discount.setExpiresAt(rs.getTimestamp("expires_at").toLocalDateTime());
            discount.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            discount.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
            discount.setDeletedAt(rs.getTimestamp("deleted_at") != null ? rs.getTimestamp("deleted_at").toLocalDateTime() : null);
            return discount;
        });
    }

    // Method to check if the referral discount has expired
    public boolean isExpired(ReferralDiscountEntity referralDiscountEntity) {
        return referralDiscountEntity.getExpiresAt().isBefore(LocalDateTime.now());
    }

    // Method to use a referral discount, which involves marking it as deleted and adding a record to used_referral_discounts
    public void useReferralDiscount(ReferralDiscountEntity referralDiscountEntity, long trxId) {
        // Mark the referral discount as deleted by setting the deleted_at timestamp
        String updateSql = "UPDATE referral_discounts SET deleted_at = NOW() WHERE id = ?";
        jdbcTemplate.update(updateSql, referralDiscountEntity.getId());

        // Add a record to used_referral_discounts
        String insertSql = "INSERT INTO used_referral_discounts (referral_discount_id, trx_id, created_at, updated_at) " +
                "VALUES (?, ?, NOW(), NOW())";
        jdbcTemplate.update(insertSql, referralDiscountEntity.getId(), trxId);
    }

    // Method to get all referral discounts for a specific user
    public List<ReferralDiscountEntity> getReferralDiscountByUser(Long userId) {
        String sql = "SELECT * FROM referral_discounts WHERE user_id = ? AND deleted_at IS NULL";

        return jdbcTemplate.query(sql, new Object[]{userId}, (rs, rowNum) -> {
            ReferralDiscountEntity discount = new ReferralDiscountEntity();
            discount.setId(rs.getLong("id"));
            discount.setUserId(rs.getLong("user_id"));
            discount.setCode(rs.getString("code"));
            discount.setPercentage(rs.getFloat("percentage"));
            discount.setExpiresAt(rs.getTimestamp("expires_at").toLocalDateTime());
            discount.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            discount.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
            discount.setDeletedAt(rs.getTimestamp("deleted_at") != null ? rs.getTimestamp("deleted_at").toLocalDateTime() : null);
            return discount;
        });
    }
}
