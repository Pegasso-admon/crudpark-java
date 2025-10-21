// ui/VehicleEntryPanel.java
package com.crudpark.ui;

import com.crudpark.model.Operator;
import com.crudpark.model.Ticket;
import com.crudpark.service.ParkingService;
import com.crudpark.util.TicketPrinter;

import javax.swing.*;
import java.awt.*;

public class VehicleEntryPanel extends JPanel {
    private Operator currentOperator;
    private ParkingService parkingService;
    
    private JTextField plateField;
    private JButton registerButton;
    private JTextArea logArea;

    public VehicleEntryPanel(Operator operator) {
        this.currentOperator = operator;
        this.parkingService = new ParkingService();
        
        initComponents();
        setupLayout();
        addListeners();
    }

    private void initComponents() {
        plateField = new JTextField(15);
        registerButton = new JButton("Register Entry");
        logArea = new JTextArea(15, 50);
        logArea.setEditable(false);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
    }

    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Input panel
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        inputPanel.add(new JLabel("Vehicle Plate:"));
        inputPanel.add(plateField);
        inputPanel.add(registerButton);
        
        // Log panel with scroll
        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Entry Log"));
        
        add(inputPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void addListeners() {
        registerButton.addActionListener(e -> registerEntry());
        plateField.addActionListener(e -> registerEntry());
    }

    private void registerEntry() {
        String plate = plateField.getText().trim().toUpperCase();
        
        if (plate.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please enter a vehicle plate", 
                "Input Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Ticket ticket = parkingService.registerEntry(plate, currentOperator);
            
            // Print ticket
            TicketPrinter.printTicket(ticket);
            
            // Log success
            logArea.append(String.format("[%s] SUCCESS - Ticket #%d created for %s (%s)\n",
                java.time.LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss")),
                ticket.getId(),
                ticket.getPlate(),
                ticket.getTicketType()));
            
            plateField.setText("");
            plateField.requestFocus();
            
            JOptionPane.showMessageDialog(this,
                String.format("Entry registered successfully!\nTicket: #%d\nType: %s", 
                    ticket.getId(), ticket.getTicketType()),
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
                
        } catch (IllegalStateException ex) {
            JOptionPane.showMessageDialog(this,
                ex.getMessage(),
                "Entry Error",
                JOptionPane.WARNING_MESSAGE);
            logArea.append(String.format("[%s] ERROR - %s: %s\n",
                java.time.LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss")),
                plate,
                ex.getMessage()));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Database error: " + ex.getMessage(),
                "System Error",
                JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}