// dao/OperatorDAO.java
package com.crudpark.dao;

import com.crudpark.model.Operator;
import com.crudpark.util.DbConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class OperatorDAO {

    public Optional<Operator> findByUsername(String username) throws SQLException {
        String sql = "SELECT id, username, password_hash, name, email, active FROM operators WHERE username = ?";
        try (Connection conn = DbConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new Operator(
                            rs.getInt("id"),
                            rs.getString("username"),
                            rs.getString("password_hash"),
                            rs.getString("name"),
                            rs.getString("email"),
                            rs.getBoolean("active")));
                }
            }
        }
        return Optional.empty();
    }
}