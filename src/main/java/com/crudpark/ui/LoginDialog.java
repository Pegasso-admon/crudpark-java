// ui/LoginDialog.java
package com.crudpark.ui;

import com.crudpark.model.Operator;
import com.crudpark.service.OperatorService;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.Optional;

public class LoginDialog extends JDialog {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private OperatorService operatorService;
    private Operator authenticatedOperator;

    public LoginDialog(Frame parent) {
        super(parent, "Operator Login", true); // Modal dialog
        operatorService = new OperatorService();
        authenticatedOperator = null;

        setTitle("CrudPark - Operator Login");
        setSize(350, 200);
        setLocationRelativeTo(parent);
        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        initComponents();
        setupLayout();
        addListeners();
    }

    private void initComponents() {
        usernameField = new JTextField(15);
        passwordField = new JPasswordField(15);
        loginButton = new JButton("Login");
    }

    private void setupLayout() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Padding

        // Username Label
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(new JLabel("Username:"), gbc);

        // Username Field
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(usernameField, gbc);

        // Password Label
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Password:"), gbc);

        // Password Field
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(passwordField, gbc);

        // Login Button
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2; // Span two columns
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(loginButton, gbc);

        add(panel, BorderLayout.CENTER);
    }

    private void addListeners() {
        loginButton.addActionListener(e -> performLogin());
        passwordField.addActionListener(e -> performLogin()); // Allow pressing Enter on password field
    }

    private void performLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()); // Get password as String

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username and password cannot be empty.", "Login Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Optional<Operator> operator = operatorService.authenticate(username, password);
            if (operator.isPresent()) {
                authenticatedOperator = operator.get();
                dispose(); // Close dialog on successful login
            } else {
                JOptionPane.showMessageDialog(this, "Invalid username or password, or operator is inactive.", "Login Failed", JOptionPane.ERROR_MESSAGE);
                passwordField.setText(""); // Clear password field
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Login Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    public Operator getAuthenticatedOperator() {
        return authenticatedOperator;
    }
}