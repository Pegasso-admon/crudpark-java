package com.crudpark.ui;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.print.PrinterJob;

public class PrinterSelectionDialog extends JDialog {
    private JList<PrintService> printerList;
    private JButton selectButton;
    private JButton cancelButton;
    private PrintService selectedPrinter;

    public PrinterSelectionDialog(Frame parent) {
        super(parent, "Select Printer", true);
        
        setSize(500, 400);
        setLocationRelativeTo(parent);
        setResizable(false);
        getContentPane().setBackground(new Color(30, 30, 30));
        
        initComponents();
        setupLayout();
        addListeners();
    }

    private void initComponents() {
        PrintService[] printServices = PrinterJob.lookupPrintServices();
        
        DefaultListModel<PrintService> listModel = new DefaultListModel<>();
        for (PrintService ps : printServices) {
            listModel.addElement(ps);
        }
        
        printerList = new JList<>(listModel);
        printerList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        printerList.setFont(new Font("Arial", Font.PLAIN, 13));
        printerList.setBackground(new Color(45, 45, 45));
        printerList.setForeground(Color.WHITE);
        printerList.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        printerList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                PrintService ps = (PrintService) value;
                String displayName = ps.getName();
                
                PrintService defaultService = PrintServiceLookup.lookupDefaultPrintService();
                if (defaultService != null && ps.equals(defaultService)) {
                    displayName += " (Default)";
                }
                
                super.getListCellRendererComponent(list, displayName, index, isSelected, cellHasFocus);
                
                if (isSelected) {
                    setBackground(new Color(0, 150, 136));
                    setForeground(Color.WHITE);
                } else {
                    setBackground(new Color(45, 45, 45));
                    setForeground(Color.WHITE);
                }
                
                setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(70, 70, 70)),
                    new EmptyBorder(8, 10, 8, 10)
                ));
                
                return this;
            }
        });
        
        PrintService defaultService = PrintServiceLookup.lookupDefaultPrintService();
        if (defaultService != null && listModel.contains(defaultService)) {
            printerList.setSelectedValue(defaultService, true);
        } else if (!listModel.isEmpty()) {
            printerList.setSelectedIndex(0);
        }
        
        selectButton = new JButton("Select");
        selectButton.setFont(new Font("Arial", Font.BOLD, 14));
        selectButton.setBackground(new Color(0, 150, 136));
        selectButton.setForeground(Color.WHITE);
        selectButton.setFocusPainted(false);
        selectButton.setBorderPainted(false);
        selectButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        selectButton.setPreferredSize(new Dimension(120, 40));
        
        cancelButton = new JButton("Cancel");
        cancelButton.setFont(new Font("Arial", Font.BOLD, 14));
        cancelButton.setBackground(new Color(96, 125, 139));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFocusPainted(false);
        cancelButton.setBorderPainted(false);
        cancelButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cancelButton.setPreferredSize(new Dimension(120, 40));
    }

    private void setupLayout() {
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(new Color(30, 30, 30));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("Available Printers");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(Color.WHITE);
        
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setBackground(new Color(30, 30, 30));
        titlePanel.add(titleLabel);
        
        JScrollPane scrollPane = new JScrollPane(printerList);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(70, 70, 70), 1));
        scrollPane.getViewport().setBackground(new Color(45, 45, 45));
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(new Color(30, 30, 30));
        buttonPanel.add(cancelButton);
        buttonPanel.add(selectButton);
        
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }

    private void addListeners() {
        selectButton.addActionListener(e -> {
            selectedPrinter = printerList.getSelectedValue();
            if (selectedPrinter != null) {
                dispose();
            } else {
                JOptionPane.showMessageDialog(
                    this,
                    "Please select a printer",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE
                );
            }
        });
        
        cancelButton.addActionListener(e -> {
            selectedPrinter = null;
            dispose();
        });
        
        printerList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    selectedPrinter = printerList.getSelectedValue();
                    if (selectedPrinter != null) {
                        dispose();
                    }
                }
            }
        });
        
        selectButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                selectButton.setBackground(new Color(0, 180, 164));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                selectButton.setBackground(new Color(0, 150, 136));
            }
        });
        
        cancelButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                cancelButton.setBackground(new Color(120, 144, 156));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                cancelButton.setBackground(new Color(96, 125, 139));
            }
        });
    }

    public PrintService getSelectedPrinter() {
        return selectedPrinter;
    }
    
    public static PrintService showDialog(Frame parent) {
        PrinterSelectionDialog dialog = new PrinterSelectionDialog(parent);
        dialog.setVisible(true);
        return dialog.getSelectedPrinter();
    }
}