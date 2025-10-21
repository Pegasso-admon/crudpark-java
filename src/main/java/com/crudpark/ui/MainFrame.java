// ui/MainFrame.java
package com.crudpark.ui;

import com.crudpark.model.Operator;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private Operator currentOperator;
    private JTabbedPane tabbedPane;
    private VehicleEntryPanel entryPanel;
    private VehicleExitPanel exitPanel;

    public MainFrame(Operator operator) {
        this.currentOperator = operator;
        initComponents();
        setupLayout();
        setFrameProperties();
    }

    private void initComponents() {
        tabbedPane = new JTabbedPane();
        entryPanel = new VehicleEntryPanel(currentOperator);
        exitPanel = new VehicleExitPanel(currentOperator);
    }

    private void setupLayout() {
        tabbedPane.addTab("Vehicle Entry", entryPanel);
        tabbedPane.addTab("Vehicle Exit", exitPanel);
        
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel welcomeLabel = new JLabel("Operator: " + currentOperator.getName());
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 14));
        topPanel.add(welcomeLabel, BorderLayout.WEST);
        
        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> logout());
        topPanel.add(logoutButton, BorderLayout.EAST);
        
        add(topPanel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
    }

    private void setFrameProperties() {
        setTitle("CrudPark - Operations System");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to logout?",
            "Confirm Logout",
            JOptionPane.YES_NO_OPTION
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            System.exit(0);
        }
    }
}