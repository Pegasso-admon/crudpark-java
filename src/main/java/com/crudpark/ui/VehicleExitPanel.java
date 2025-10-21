// ui/VehicleExitPanel.java
package com.crudpark.ui;

import com.crudpark.model.Operator;
import com.crudpark.service.ParkingService;
import com.crudpark.service.ParkingService.ExitResult;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;

public class VehicleExitPanel extends JPanel {
    private Operator currentOperator;
    private ParkingService parkingService;
    
    private JTextField plateField;
    private JButton processButton;
    private JTextArea logArea;

    public VehicleExitPanel(Operator operator) {
        this.currentOperator = operator;
        this.parkingService = new ParkingService();
        
        initComponents();
        setupLayout();
        addListeners();
    }

    private void initComponents() {
        plateField = new JTextField(15);
        processButton = new JButton("Process Exit");
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
        inputPanel.add(processButton);
        
        // Log panel with scroll
        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Exit Log"));
        
        add(inputPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void addListeners() {
        processButton.addActionListener(e -> processExit());
        plateField.addActionListener(e -> processExit());
    }

    private void processExit() {
        String plate = plateField.getText().trim().toUpperCase();
        
        if (plate.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please enter a vehicle plate", 
                "Input Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            ExitResult result = parkingService.processExit(plate, currentOperator);
            
            long hours = result.getTotalMinutes() / 60;
            long minutes = result.getTotalMinutes() % 60;
            
            String timeStr = String.format("%dh %dm", hours, minutes);
            
            if (result.isPaymentRequired()) {
                // Show payment dialog
                int paymentChoice = showPaymentDialog(result.getAmountToPay());
                
                if (paymentChoice != -1) {
                    String[] methods = {"CASH", "CARD", "TRANSFER"};
                    parkingService.registerPayment(
                        result.getTicket().getId(),
                        result.getAmountToPay(),
                        methods[paymentChoice],
                        currentOperator.getId()
                    );
                    
                    logArea.append(String.format("[%s] EXIT - Ticket #%d | %s | Time: %s | Paid: $%.2f (%s)\n",
                        java.time.LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss")),
                        result.getTicket().getId(),
                        plate,
                        timeStr,
                        result.getAmountToPay(),
                        methods[paymentChoice]));
                        
                    JOptionPane.showMessageDialog(this,
                        String.format("Payment processed successfully!\nAmount: $%.2f\nMethod: %s", 
                            result.getAmountToPay(), methods[paymentChoice]),
                        "Exit Complete",
                        JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                logArea.append(String.format("[%s] EXIT - Ticket #%d | %s | Time: %s | Type: %s (No charge)\n",
                    java.time.LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss")),
                    result.getTicket().getId(),
                    plate,
                    timeStr,
                    result.getTicket().getTicketType()));
                    
                JOptionPane.showMessageDialog(this,
                    String.format("Exit registered successfully!\nTime: %s\nNo payment required", timeStr),
                    "Exit Complete",
                    JOptionPane.INFORMATION_MESSAGE);
            }
            
            plateField.setText("");
            plateField.requestFocus();
            
        } catch (IllegalStateException ex) {
            JOptionPane.showMessageDialog(this,
                ex.getMessage(),
                "Exit Error",
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

    private int showPaymentDialog(BigDecimal amount) {
        String[] options = {"Cash", "Card", "Transfer", "Cancel"};
        
        return JOptionPane.showOptionDialog(
            this,
            String.format("Amount to pay: $%.2f\n\nSelect payment method:", amount),
            "Payment Required",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[0]
        );
    }
}