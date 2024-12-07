package com.nurmanhilman.eventbrite.util;

import org.springframework.jdbc.core.JdbcTemplate;

public class DatabaseHelper {
    public static Long getNextId(JdbcTemplate jdbcTemplate, String table, String idName) {
        String sql = "SELECT MAX(" + idName +") FROM " +table;
        Long maxId = jdbcTemplate.queryForObject(sql, Long.class);
        return (maxId != null) ? maxId + 1 : 1L;
    }
}
