// dao/PaymentDAO.java
package com.crudpark.dao;

import com.crudpark.model.Payment;
import com.crudpark.util.DbConnection;

import java.sql.*;

public class PaymentDAO {

    public Payment create(Payment payment) throws SQLException {
        // SOLUCIÓN: Agregar ::payment_method_enum al placeholder
        String sql = "INSERT INTO payments (ticket_id, amount, payment_method, payment_time, operator_id) " +
                "VALUES (?, ?, ?::payment_method_enum, ?, ?)";

        try (Connection conn = DbConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, payment.getTicketId());
            stmt.setBigDecimal(2, payment.getAmount());
            stmt.setString(3, payment.getPaymentMethod()); // Ahora PostgreSQL sabe que es un enum
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