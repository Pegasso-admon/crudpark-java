// dao/TicketDAO.java
package com.crudpark.dao;

import com.crudpark.model.Ticket;
import com.crudpark.util.DbConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.Optional;

public class TicketDAO {

    public Optional<Ticket> findActiveByPlate(String plate) throws SQLException {
        String sql = "SELECT t.id, t.plate, t.entry_time, t.exit_time, " +
                "CAST(t.ticket_type AS VARCHAR) as ticket_type, " +
                "t.operator_id, o.name as operator_name, t.active " +
                "FROM tickets t " +
                "JOIN operators o ON t.operator_id = o.id " +
                "WHERE t.plate = ? AND t.active = true";

        try (Connection conn = DbConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, plate);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToTicket(rs));
                }
            }
        }
        return Optional.empty();
    }

    public Ticket create(Ticket ticket) throws SQLException {
        String sql = "INSERT INTO tickets (plate, entry_time, ticket_type, operator_id, active) " +
                "VALUES (?, ?, ?::ticket_type_enum, ?, true)";

        try (Connection conn = DbConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, ticket.getPlate());
            stmt.setTimestamp(2, Timestamp.valueOf(ticket.getEntryTime()));
            stmt.setString(3, ticket.getTicketType());
            stmt.setInt(4, ticket.getOperatorId());

            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    ticket.setId(generatedKeys.getInt(1));
                }
            }
        }
        return ticket;
    }

    public void updateExit(int ticketId, LocalDateTime exitTime) throws SQLException {
        String sql = "UPDATE tickets SET exit_time = ?, active = false WHERE id = ?";

        try (Connection conn = DbConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setTimestamp(1, Timestamp.valueOf(exitTime));
            stmt.setInt(2, ticketId);
            stmt.executeUpdate();
        }
    }

    public Optional<Ticket> findById(int ticketId) throws SQLException {
        String sql = "SELECT t.id, t.plate, t.entry_time, t.exit_time, " +
                "CAST(t.ticket_type AS VARCHAR) as ticket_type, " +
                "t.operator_id, o.name as operator_name, t.active " +
                "FROM tickets t " +
                "JOIN operators o ON t.operator_id = o.id " +
                "WHERE t.id = ?";

        try (Connection conn = DbConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, ticketId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToTicket(rs));
                }
            }
        }
        return Optional.empty();
    }

    private Ticket mapResultSetToTicket(ResultSet rs) throws SQLException {
        Ticket ticket = new Ticket();
        ticket.setId(rs.getInt("id"));
        ticket.setPlate(rs.getString("plate"));

        Timestamp entryTs = rs.getTimestamp("entry_time");
        ticket.setEntryTime(entryTs != null ? entryTs.toLocalDateTime() : null);

        Timestamp exitTs = rs.getTimestamp("exit_time");
        ticket.setExitTime(exitTs != null ? exitTs.toLocalDateTime() : null);

        // Asegurarse de que el tipo se lee correctamente como String
        String ticketType = rs.getString("ticket_type");
        ticket.setTicketType(ticketType != null ? ticketType.toUpperCase() : null);

        ticket.setOperatorId(rs.getInt("operator_id"));
        ticket.setOperatorName(rs.getString("operator_name"));
        ticket.setActive(rs.getBoolean("active"));

        return ticket;
    }
}