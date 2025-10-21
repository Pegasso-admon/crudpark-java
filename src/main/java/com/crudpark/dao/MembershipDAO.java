// dao/MembershipDAO.java
package com.crudpark.dao;

import com.crudpark.model.Membership;
import com.crudpark.util.DbConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.Optional;

public class MembershipDAO {

    public Optional<Membership> findValidByPlate(String plate) throws SQLException {
        String sql = "SELECT id, owner_name, owner_email, plate, start_date, end_date, active " +
                "FROM memberships " +
                "WHERE plate = ? AND active = true " +
                "AND CURRENT_DATE BETWEEN start_date AND end_date";

        try (Connection conn = DbConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, plate);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToMembership(rs));
                }
            }
        }
        return Optional.empty();
    }

    private Membership mapResultSetToMembership(ResultSet rs) throws SQLException {
        Membership membership = new Membership();
        membership.setId(rs.getInt("id"));
        membership.setOwnerName(rs.getString("owner_name"));
        membership.setOwnerEmail(rs.getString("owner_email"));
        membership.setPlate(rs.getString("plate"));

        Date startDate = rs.getDate("start_date");
        membership.setStartDate(startDate != null ? startDate.toLocalDate() : null);

        Date endDate = rs.getDate("end_date");
        membership.setEndDate(endDate != null ? endDate.toLocalDate() : null);

        membership.setActive(rs.getBoolean("active"));

        return membership;
    }
}