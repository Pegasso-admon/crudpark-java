// service/OperatorService.java
package com.crudpark.service;

import com.crudpark.dao.OperatorDAO;
import com.crudpark.model.Operator;

import java.sql.SQLException;
import java.util.Optional;

public class OperatorService {
    private OperatorDAO operatorDAO = new OperatorDAO();

    public Optional<Operator> authenticate(String username, String password) throws SQLException {
        Optional<Operator> operator = operatorDAO.findByUsername(username);

        if (operator.isPresent() && operator.get().isActive()) {
            // WARNING: In a real application, 'password' should be hashed and compared against 'operator.getPasswordHash()'
            // For this challenge, we'll assume the password in DB is plain text for simplicity,
            // or that passwordHash already contains the plain text for direct comparison.
            // Example for production: if (PasswordUtil.checkPassword(password, operator.get().getPasswordHash()))
            if (password.equals(operator.get().getPasswordHash())) { // Direct comparison for challenge simplicity
                return operator;
            }
        }
        return Optional.empty();
    }
}