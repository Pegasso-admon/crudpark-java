// dao/RateDAO.java
package com.crudpark.dao;

import com.crudpark.model.Rate;
import com.crudpark.util.DbConnection;

import java.sql.*;
import java.util.Optional;

public class RateDAO {

    public Optional<Rate> getActiveRate() throws SQLException {
        String sql = "SELECT id, base_rate, fraction_rate, daily_cap, grace_minutes, active " +
                     "FROM rates " +
                     "WHERE active = true " +
                     "LIMIT 1";
        
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                Rate rate = new Rate();
                rate.setId(rs.getInt("id"));
                rate.setBaseRate(rs.getBigDecimal("base_rate"));
                rate.setFractionRate(rs.getBigDecimal("fraction_rate"));
                rate.setDailyCap(rs.getBigDecimal("daily_cap"));
                rate.setGraceMinutes(rs.getInt("grace_minutes"));
                rate.setActive(rs.getBoolean("active"));
                return Optional.of(rate);
            }
        }
        return Optional.empty();
    }
}