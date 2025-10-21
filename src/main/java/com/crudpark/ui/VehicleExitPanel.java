package com.crudpark.ui;

import com.crudpark.model.Operator;
import com.crudpark.service.ParkingService;
import com.crudpark.service.ParkingService.ExitResult;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.math.BigDecimal;

public class VehicleExitPanel extends JPanel {
    private Operator currentOperator;
    private ParkingService parkingService;
    
    private JTextField plateField;
    private JButton processButton;
    private JTextArea logArea;
    private JPanel infoPanel;
    private JLabel ticketIdLabel;
    private JLabel durationLabel;
    private JLabel amountLabel;
    private JLabel statusLabel;

    public VehicleExitPanel(Operator operator) {
        this.currentOperator = operator;
        this.parkingService = new ParkingService();
        
        setBackground(new Color(30, 30, 30));
        initComponents();
        setupLayout();
        addListeners();
    }

    private void initComponents() {
        plateField = new JTextField(15);
        plateField.setFont(new Font("Monospaced", Font.BOLD, 16));
        plateField.setBackground(new Color(45, 45, 45));
        plateField.setForeground(Color.WHITE);
        plateField.setCaretColor(Color.WHITE);
        plateField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(70, 70, 70), 2),
            new EmptyBorder(8, 12, 8, 12)
        ));
        
        processButton = new JButton("Process Exit");
        processButton.setFont(new Font("Arial", Font.BOLD, 14));
        processButton.setBackground(new Color(244, 67, 54));
        processButton.setForeground(Color.WHITE);
        processButton.setFocusPainted(false);
        processButton.setBorderPainted(false);
        processButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        processButton.setPreferredSize(new Dimension(150, 40));
        
        logArea = new JTextArea(10, 50);
        logArea.setEditable(false);
        logArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        logArea.setBackground(new Color(25, 25, 25));
        logArea.setForeground(new Color(255, 165, 0));
        logArea.setCaretColor(Color.WHITE);
        logArea.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(new Color(40, 40, 40));
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(70, 70, 70), 1),
            new EmptyBorder(20, 20, 20, 20)
        ));
        
        ticketIdLabel = createInfoLabel("");
        durationLabel = createInfoLabel("");
        amountLabel = createInfoLabel("");
        statusLabel = createInfoLabel("");
    }
    
    private JLabel createInfoLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.PLAIN, 14));
        label.setForeground(Color.WHITE);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private void setupLayout() {
        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        topPanel.setBackground(new Color(30, 30, 30));
        
        JLabel plateFieldLabel = new JLabel("Vehicle Plate:");
        plateFieldLabel.setFont(new Font("Arial", Font.BOLD, 14));
        plateFieldLabel.setForeground(Color.WHITE);
        
        topPanel.add(plateFieldLabel);
        topPanel.add(plateField);
        topPanel.add(processButton);
        
        JPanel centerPanel = new JPanel(new BorderLayout(15, 15));
        centerPanel.setBackground(new Color(30, 30, 30));
        
        JPanel leftPanel = new JPanel(new BorderLayout(10, 10));
        leftPanel.setBackground(new Color(30, 30, 30));
        
        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(70, 70, 70), 1),
            "Exit Log",
            0,
            0,
            new Font("Arial", Font.BOLD, 12),
            Color.WHITE
        ));
        scrollPane.setBackground(new Color(30, 30, 30));
        scrollPane.getViewport().setBackground(new Color(25, 25, 25));
        
        leftPanel.add(scrollPane, BorderLayout.CENTER);
        
        JPanel rightPanel = new JPanel(new BorderLayout(10, 10));
        rightPanel.setBackground(new Color(30, 30, 30));
        rightPanel.setPreferredSize(new Dimension(300, 0));
        
        JLabel infoTitle = new JLabel("Exit Information", SwingConstants.CENTER);
        infoTitle.setFont(new Font("Arial", Font.BOLD, 14));
        infoTitle.setForeground(Color.WHITE);
        
        infoPanel.add(ticketIdLabel);
        infoPanel.add(Box.createVerticalStrut(12));
        infoPanel.add(durationLabel);
        infoPanel.add(Box.createVerticalStrut(12));
        infoPanel.add(amountLabel);
        infoPanel.add(Box.createVerticalStrut(12));
        infoPanel.add(statusLabel);
        
        rightPanel.add(infoTitle, BorderLayout.NORTH);
        rightPanel.add(infoPanel, BorderLayout.CENTER);
        
        centerPanel.add(leftPanel, BorderLayout.CENTER);
        centerPanel.add(rightPanel, BorderLayout.EAST);
        
        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        
        clearExitDisplay();
    }

    private void addListeners() {
        processButton.addActionListener(e -> processExit());
        plateField.addActionListener(e -> processExit());
        
        processButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                processButton.setBackground(new Color(255, 87, 74));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                processButton.setBackground(new Color(244, 67, 54));
            }
        });
    }

    private void processExit() {
        String plate = plateField.getText().trim().toUpperCase();
        
        if (plate.isEmpty()) {
            showError("Please enter a vehicle plate");
            return;
        }

        try {
            ExitResult result = parkingService.processExit(plate, currentOperator);
            
            long hours = result.getTotalMinutes() / 60;
            long minutes = result.getTotalMinutes() % 60;
            String timeStr = String.format("%dh %dm", hours, minutes);
            
            displayExitInfo(result, timeStr);
            
            if (result.isPaymentRequired()) {
                String[] options = {"Cash", "Card", "Transfer", "Cancel"};
                int choice = JOptionPane.showOptionDialog(
                    this,
                    String.format("Amount to pay: $%.2f\n\nSelect payment method:", result.getAmountToPay()),
                    "Payment Required",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[0]
                );
                
                if (choice >= 0 && choice < 3) {
                    String[] methods = {"CASH", "CARD", "TRANSFER"};
                    parkingService.registerPayment(
                        result.getTicket().getId(),
                        result.getAmountToPay(),
                        methods[choice],
                        currentOperator.getId()
                    );
                    
                    statusLabel.setText("Status: PAID - " + methods[choice]);
                    statusLabel.setForeground(new Color(76, 175, 80));
                    
                    logArea.append(String.format("[%s] EXIT - Ticket #%06d | %s | Time: %s | Paid: $%.2f (%s)\n",
                        java.time.LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss")),
                        result.getTicket().getId(),
                        plate,
                        timeStr,
                        result.getAmountToPay(),
                        methods[choice]));
                    logArea.setCaretPosition(logArea.getDocument().getLength());
                        
                    showSuccess(String.format("Payment processed successfully!\nAmount: $%.2f\nMethod: %s", 
                        result.getAmountToPay(), methods[choice]));
                }
            } else {
                statusLabel.setText("Status: NO CHARGE");
                statusLabel.setForeground(new Color(76, 175, 80));
                
                logArea.append(String.format("[%s] EXIT - Ticket #%06d | %s | Time: %s | Type: %s (No charge)\n",
                    java.time.LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss")),
                    result.getTicket().getId(),
                    plate,
                    timeStr,
                    result.getTicket().getTicketType()));
                logArea.setCaretPosition(logArea.getDocument().getLength());
                    
                showSuccess(String.format("Exit registered successfully!\nTime: %s\nNo payment required", timeStr));
            }
            
            plateField.setText("");
            plateField.requestFocus();
            
        } catch (IllegalStateException ex) {
            showError(ex.getMessage());
            logArea.append(String.format("[%s] ERROR - %s: %s\n",
                java.time.LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss")),
                plate,
                ex.getMessage()));
            logArea.setCaretPosition(logArea.getDocument().getLength());
        } catch (Exception ex) {
            showError("Database error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    
    private void displayExitInfo(ExitResult result, String duration) {
        ticketIdLabel.setText("Ticket #: " + String.format("%06d", result.getTicket().getId()));
        durationLabel.setText("Duration: " + duration);
        
        if (result.isPaymentRequired()) {
            amountLabel.setText(String.format("Amount: $%.2f", result.getAmountToPay()));
            amountLabel.setForeground(new Color(255, 193, 7));
            statusLabel.setText("Status: PAYMENT REQUIRED");
            statusLabel.setForeground(new Color(255, 152, 0));
        } else {
            amountLabel.setText("Amount: $0.00");
            amountLabel.setForeground(new Color(76, 175, 80));
            statusLabel.setText("Status: NO CHARGE");
            statusLabel.setForeground(new Color(76, 175, 80));
        }
    }
    
    private void clearExitDisplay() {
        ticketIdLabel.setText("");
        durationLabel.setText("");
        amountLabel.setText("");
        statusLabel.setText("");
    }
    
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    private void showSuccess(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }
}