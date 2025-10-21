package com.crudpark.ui;

import com.crudpark.model.Operator;
import com.crudpark.model.Ticket;
import com.crudpark.service.ParkingService;
import com.crudpark.util.QRCodeGenerator;
import com.crudpark.util.TicketPrinter;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;

public class VehicleEntryPanel extends JPanel {
    private Operator currentOperator;
    private ParkingService parkingService;
    
    private JTextField plateField;
    private JButton registerButton;
    private JButton printButton;
    private JTextArea logArea;
    private JLabel qrLabel;
    private JPanel ticketInfoPanel;
    private JLabel ticketIdLabel;
    private JLabel ticketTypeLabel;
    private JLabel plateLabel;
    private JLabel timeLabel;
    private Ticket lastTicket;

    public VehicleEntryPanel(Operator operator) {
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
        
        registerButton = new JButton("Register Entry");
        registerButton.setFont(new Font("Arial", Font.BOLD, 14));
        registerButton.setBackground(new Color(0, 150, 136));
        registerButton.setForeground(Color.WHITE);
        registerButton.setFocusPainted(false);
        registerButton.setBorderPainted(false);
        registerButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        registerButton.setPreferredSize(new Dimension(150, 40));
        
        printButton = new JButton("Print Ticket");
        printButton.setFont(new Font("Arial", Font.BOLD, 14));
        printButton.setBackground(new Color(96, 125, 139));
        printButton.setForeground(Color.WHITE);
        printButton.setFocusPainted(false);
        printButton.setBorderPainted(false);
        printButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        printButton.setPreferredSize(new Dimension(150, 40));
        printButton.setEnabled(false);
        
        logArea = new JTextArea(10, 50);
        logArea.setEditable(false);
        logArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        logArea.setBackground(new Color(25, 25, 25));
        logArea.setForeground(new Color(0, 255, 0));
        logArea.setCaretColor(Color.WHITE);
        logArea.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        qrLabel = new JLabel();
        qrLabel.setHorizontalAlignment(SwingConstants.CENTER);
        qrLabel.setPreferredSize(new Dimension(200, 200));
        qrLabel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0, 150, 136), 2),
            new EmptyBorder(10, 10, 10, 10)
        ));
        qrLabel.setBackground(Color.WHITE);
        qrLabel.setOpaque(true);
        
        ticketInfoPanel = new JPanel();
        ticketInfoPanel.setLayout(new BoxLayout(ticketInfoPanel, BoxLayout.Y_AXIS));
        ticketInfoPanel.setBackground(new Color(40, 40, 40));
        ticketInfoPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(70, 70, 70), 1),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        ticketIdLabel = createInfoLabel("");
        ticketTypeLabel = createInfoLabel("");
        plateLabel = createInfoLabel("");
        timeLabel = createInfoLabel("");
    }
    
    private JLabel createInfoLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.PLAIN, 13));
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
        topPanel.add(registerButton);
        topPanel.add(printButton);
        
        JPanel centerPanel = new JPanel(new BorderLayout(15, 15));
        centerPanel.setBackground(new Color(30, 30, 30));
        
        JPanel leftPanel = new JPanel(new BorderLayout(10, 10));
        leftPanel.setBackground(new Color(30, 30, 30));
        
        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(70, 70, 70), 1),
            "Entry Log",
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
        rightPanel.setPreferredSize(new Dimension(250, 0));
        
        JLabel qrTitle = new JLabel("QR Code", SwingConstants.CENTER);
        qrTitle.setFont(new Font("Arial", Font.BOLD, 14));
        qrTitle.setForeground(Color.WHITE);
        
        ticketInfoPanel.add(ticketIdLabel);
        ticketInfoPanel.add(Box.createVerticalStrut(8));
        ticketInfoPanel.add(ticketTypeLabel);
        ticketInfoPanel.add(Box.createVerticalStrut(8));
        ticketInfoPanel.add(plateLabel);
        ticketInfoPanel.add(Box.createVerticalStrut(8));
        ticketInfoPanel.add(timeLabel);
        
        rightPanel.add(qrTitle, BorderLayout.NORTH);
        rightPanel.add(qrLabel, BorderLayout.CENTER);
        rightPanel.add(ticketInfoPanel, BorderLayout.SOUTH);
        
        centerPanel.add(leftPanel, BorderLayout.CENTER);
        centerPanel.add(rightPanel, BorderLayout.EAST);
        
        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        
        clearTicketDisplay();
    }

    private void addListeners() {
        registerButton.addActionListener(e -> registerEntry());
        plateField.addActionListener(e -> registerEntry());
        printButton.addActionListener(e -> printLastTicket());
        
        registerButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                registerButton.setBackground(new Color(0, 180, 164));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                registerButton.setBackground(new Color(0, 150, 136));
            }
        });
        
        printButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (printButton.isEnabled()) {
                    printButton.setBackground(new Color(120, 144, 156));
                }
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (printButton.isEnabled()) {
                    printButton.setBackground(new Color(96, 125, 139));
                }
            }
        });
    }

    private void registerEntry() {
        String plate = plateField.getText().trim().toUpperCase();
        
        if (plate.isEmpty()) {
            showError("Please enter a vehicle plate");
            return;
        }

        try {
            Ticket ticket = parkingService.registerEntry(plate, currentOperator);
            lastTicket = ticket;
            
            displayTicketInfo(ticket);
            printButton.setEnabled(true);
            
            logArea.append(String.format("[%s] SUCCESS - Ticket #%06d created for %s (%s)\n",
                java.time.LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss")),
                ticket.getId(),
                ticket.getPlate(),
                ticket.getTicketType()));
            logArea.setCaretPosition(logArea.getDocument().getLength());
            
            plateField.setText("");
            plateField.requestFocus();
            
            showSuccess(String.format("Entry registered successfully!\nTicket: #%06d\nType: %s", 
                ticket.getId(), ticket.getTicketType()));
                
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
    
    private void printLastTicket() {
        if (lastTicket != null) {
            Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
            TicketPrinter.printTicket(lastTicket, parentFrame);
        }
    }
    
    private void displayTicketInfo(Ticket ticket) {
        try {
            long timestamp = ticket.getEntryTime().atZone(java.time.ZoneId.systemDefault()).toEpochSecond();
            String qrData = QRCodeGenerator.formatTicketData(ticket.getId(), ticket.getPlate(), timestamp);
            BufferedImage qrImage = QRCodeGenerator.generateQRCode(qrData, 180, 180);
            qrLabel.setIcon(new ImageIcon(qrImage));
            qrLabel.setText(null);
            
            ticketIdLabel.setText("Ticket #: " + String.format("%06d", ticket.getId()));
            ticketTypeLabel.setText("Type: " + ticket.getTicketType());
            plateLabel.setText("Plate: " + ticket.getPlate());
            timeLabel.setText("Time: " + ticket.getEntryTime().format(
                java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss")
            ));
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void clearTicketDisplay() {
        qrLabel.setIcon(null);
        qrLabel.setText("No QR Code");
        qrLabel.setForeground(Color.GRAY);
        ticketIdLabel.setText("");
        ticketTypeLabel.setText("");
        plateLabel.setText("");
        timeLabel.setText("");
        printButton.setEnabled(false);
        lastTicket = null;
    }
    
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    private void showSuccess(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }
}