package com.crudpark.ui;

import com.crudpark.model.Operator;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class MainFrame extends JFrame {
    private Operator currentOperator;
    private JTabbedPane tabbedPane;
    private VehicleEntryPanel entryPanel;
    private VehicleExitPanel exitPanel;
    private ActiveTicketsPanel activeTicketsPanel;

    public MainFrame(Operator operator) {
        this.currentOperator = operator;
        initComponents();
        setupLayout();
        setFrameProperties();
    }

    private void initComponents() {
        tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(new Color(30, 30, 30));
        tabbedPane.setForeground(Color.WHITE);
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 13));
        
        entryPanel = new VehicleEntryPanel(currentOperator);
        exitPanel = new VehicleExitPanel(currentOperator);
        activeTicketsPanel = new ActiveTicketsPanel(currentOperator);
        
        tabbedPane.addTab(" 🚗 Vehicle Entry ", entryPanel);
        tabbedPane.addTab(" 🚪 Vehicle Exit ", exitPanel);
        tabbedPane.addTab(" 📋 Active Tickets ", activeTicketsPanel);
        
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
    }

    private void setupLayout() {
        getContentPane().setBackground(new Color(30, 30, 30));
        
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(25, 25, 25));
        topPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(0, 150, 136)),
            new EmptyBorder(15, 20, 15, 20)
        ));
        
        JPanel leftInfo = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        leftInfo.setBackground(new Color(25, 25, 25));
        
        JLabel titleLabel = new JLabel("CrudPark");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(new Color(0, 150, 136));
        
        JLabel operatorLabel = new JLabel("Operator: " + currentOperator.getName());
        operatorLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        operatorLabel.setForeground(Color.WHITE);
        
        leftInfo.add(titleLabel);
        leftInfo.add(Box.createHorizontalStrut(30));
        leftInfo.add(operatorLabel);
        
        JButton logoutButton = new JButton("Logout");
        logoutButton.setFont(new Font("Arial", Font.BOLD, 12));
        logoutButton.setBackground(new Color(244, 67, 54));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFocusPainted(false);
        logoutButton.setBorderPainted(false);
        logoutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutButton.setPreferredSize(new Dimension(100, 35));
        logoutButton.addActionListener(e -> logout());
        
        logoutButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                logoutButton.setBackground(new Color(255, 87, 74));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                logoutButton.setBackground(new Color(244, 67, 54));
            }
        });
        
        topPanel.add(leftInfo, BorderLayout.WEST);
        topPanel.add(logoutButton, BorderLayout.EAST);
        
        add(topPanel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
    }

    private void setFrameProperties() {
        setTitle("CrudPark - Operations System");
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                if (activeTicketsPanel != null) {
                    activeTicketsPanel.stopAutoRefresh();
                }
            }
        });
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to logout?",
            "Confirm Logout",
            JOptionPane.YES_NO_OPTION
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (activeTicketsPanel != null) {
                activeTicketsPanel.stopAutoRefresh();
            }
            dispose();
            System.exit(0);
        }
    }
}