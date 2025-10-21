// dao/PaymentDAO.java
package com.crudpark.dao;

import com.crudpark.model.Payment;
import com.crudpark.util.DbConnection;

import java.sql.*;

public class PaymentDAO {

    public Payment create(Payment payment) throws SQLException {
        String sql = "INSERT INTO payments (ticket_id, amount, payment_method, payment_time, operator_id) " +
                     "VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, payment.getTicketId());
            stmt.setBigDecimal(2, payment.getAmount());
            stmt.setString(3, payment.getPaymentMethod());
            stmt.setTimestamp(4, Timestamp.valueOf(payment.getPaymentTime()));
            stmt.setInt(5, payment.getOperatorId());
            
            stmt.executeUpdate();
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    payment.setId(generatedKeys.getInt(1));
                }
            }
        }
        return payment;
    }
}