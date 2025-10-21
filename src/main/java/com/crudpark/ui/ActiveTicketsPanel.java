package com.crudpark.ui;

import com.crudpark.dao.TicketDAO;
import com.crudpark.model.Operator;
import com.crudpark.model.Ticket;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ActiveTicketsPanel extends JPanel {
    private Operator currentOperator;
    private TicketDAO ticketDAO;
    private JTable ticketsTable;
    private DefaultTableModel tableModel;
    private JButton refreshButton;
    private JLabel totalTicketsLabel;
    private JLabel lastUpdateLabel;
    private Timer autoRefreshTimer;

    public ActiveTicketsPanel(Operator operator) {
        this.currentOperator = operator;
        this.ticketDAO = new TicketDAO();
        
        setBackground(new Color(30, 30, 30));
        initComponents();
        setupLayout();
        addListeners();
        loadActiveTickets();
        startAutoRefresh();
    }

    private void initComponents() {
        String[] columnNames = {"Ticket #", "Plate", "Type", "Entry Time", "Duration", "Operator"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        ticketsTable = new JTable(tableModel);
        ticketsTable.setFont(new Font("Monospaced", Font.PLAIN, 12));
        ticketsTable.setRowHeight(30);
        ticketsTable.setBackground(new Color(45, 45, 45));
        ticketsTable.setForeground(Color.WHITE);
        ticketsTable.setGridColor(new Color(70, 70, 70));
        ticketsTable.setSelectionBackground(new Color(0, 150, 136));
        ticketsTable.setSelectionForeground(Color.WHITE);
        ticketsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        ticketsTable.getTableHeader().setBackground(new Color(25, 25, 25));
        ticketsTable.getTableHeader().setForeground(Color.WHITE);
        ticketsTable.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(0, 150, 136)));
        
        ticketsTable.getColumnModel().getColumn(0).setPreferredWidth(80);
        ticketsTable.getColumnModel().getColumn(1).setPreferredWidth(100);
        ticketsTable.getColumnModel().getColumn(2).setPreferredWidth(80);
        ticketsTable.getColumnModel().getColumn(3).setPreferredWidth(150);
        ticketsTable.getColumnModel().getColumn(4).setPreferredWidth(100);
        ticketsTable.getColumnModel().getColumn(5).setPreferredWidth(120);
        
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        ticketsTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        ticketsTable.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        ticketsTable.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
        
        DefaultTableCellRenderer typeRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(SwingConstants.CENTER);
                
                if (!isSelected) {
                    if ("MONTHLY".equals(value)) {
                        setBackground(new Color(76, 175, 80, 30));
                        setForeground(new Color(129, 199, 132));
                    } else if ("GUEST".equals(value)) {
                        setBackground(new Color(255, 193, 7, 30));
                        setForeground(new Color(255, 213, 79));
                    }
                } else {
                    setBackground(table.getSelectionBackground());
                    setForeground(table.getSelectionForeground());
                }
                
                return this;
            }
        };
        ticketsTable.getColumnModel().getColumn(2).setCellRenderer(typeRenderer);
        
        refreshButton = new JButton("Refresh");
        refreshButton.setFont(new Font("Arial", Font.BOLD, 13));
        refreshButton.setBackground(new Color(0, 150, 136));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFocusPainted(false);
        refreshButton.setBorderPainted(false);
        refreshButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        refreshButton.setPreferredSize(new Dimension(100, 35));
        
        totalTicketsLabel = new JLabel("Total Active Tickets: 0");
        totalTicketsLabel.setFont(new Font("Arial", Font.BOLD, 14));
        totalTicketsLabel.setForeground(Color.WHITE);
        
        lastUpdateLabel = new JLabel("Last Update: Never");
        lastUpdateLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        lastUpdateLabel.setForeground(Color.LIGHT_GRAY);
    }

    private void setupLayout() {
        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setBackground(new Color(30, 30, 30));
        
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(new Color(30, 30, 30));
        
        totalTicketsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        lastUpdateLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        infoPanel.add(totalTicketsLabel);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(lastUpdateLabel);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(new Color(30, 30, 30));
        buttonPanel.add(refreshButton);
        
        topPanel.add(infoPanel, BorderLayout.WEST);
        topPanel.add(buttonPanel, BorderLayout.EAST);
        
        JScrollPane scrollPane = new JScrollPane(ticketsTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(70, 70, 70), 1));
        scrollPane.getViewport().setBackground(new Color(45, 45, 45));
        
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void addListeners() {
        refreshButton.addActionListener(e -> loadActiveTickets());
        
        refreshButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                refreshButton.setBackground(new Color(0, 180, 164));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                refreshButton.setBackground(new Color(0, 150, 136));
            }
        });
    }

    private void loadActiveTickets() {
        SwingWorker<List<Ticket>, Void> worker = new SwingWorker<List<Ticket>, Void>() {
            @Override
            protected List<Ticket> doInBackground() throws Exception {
                return getAllActiveTickets();
            }
            
            @Override
            protected void done() {
                try {
                    List<Ticket> tickets = get();
                    updateTable(tickets);
                    
                    totalTicketsLabel.setText("Total Active Tickets: " + tickets.size());
                    lastUpdateLabel.setText("Last Update: " + 
                        LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
                    
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(
                        ActiveTicketsPanel.this,
                        "Error loading tickets: " + e.getMessage(),
                        "Database Error",
                        JOptionPane.ERROR_MESSAGE
                    );
                    e.printStackTrace();
                }
            }
        };
        
        worker.execute();
    }

    private List<Ticket> getAllActiveTickets() throws SQLException {
        List<Ticket> tickets = new ArrayList<>();
        
        try (java.sql.Connection conn = com.crudpark.util.DbConnection.getConnection();
             java.sql.PreparedStatement stmt = conn.prepareStatement(
                "SELECT t.id, t.plate, t.entry_time, t.exit_time, " +
                "CAST(t.ticket_type AS VARCHAR) as ticket_type, " +
                "t.operator_id, o.name as operator_name, t.active " +
                "FROM tickets t " +
                "JOIN operators o ON t.operator_id = o.id " +
                "WHERE t.active = true " +
                "ORDER BY t.entry_time DESC");
             java.sql.ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Ticket ticket = new Ticket();
                ticket.setId(rs.getInt("id"));
                ticket.setPlate(rs.getString("plate"));
                
                java.sql.Timestamp entryTs = rs.getTimestamp("entry_time");
                ticket.setEntryTime(entryTs != null ? entryTs.toLocalDateTime() : null);
                
                java.sql.Timestamp exitTs = rs.getTimestamp("exit_time");
                ticket.setExitTime(exitTs != null ? exitTs.toLocalDateTime() : null);
                
                String ticketType = rs.getString("ticket_type");
                ticket.setTicketType(ticketType != null ? ticketType.toUpperCase() : null);
                
                ticket.setOperatorId(rs.getInt("operator_id"));
                ticket.setOperatorName(rs.getString("operator_name"));
                ticket.setActive(rs.getBoolean("active"));
                
                tickets.add(ticket);
            }
        }
        
        return tickets;
    }

    private void updateTable(List<Ticket> tickets) {
        tableModel.setRowCount(0);
        
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        
        for (Ticket ticket : tickets) {
            Duration duration = Duration.between(ticket.getEntryTime(), LocalDateTime.now());
            long hours = duration.toHours();
            long minutes = duration.toMinutes() % 60;
            String durationStr = String.format("%dh %dm", hours, minutes);
            
            Object[] row = {
                String.format("#%06d", ticket.getId()),
                ticket.getPlate(),
                ticket.getTicketType(),
                ticket.getEntryTime().format(timeFormatter),
                durationStr,
                ticket.getOperatorName()
            };
            
            tableModel.addRow(row);
        }
    }

    private void startAutoRefresh() {
        autoRefreshTimer = new Timer(30000, e -> loadActiveTickets());
        autoRefreshTimer.start();
    }

    public void stopAutoRefresh() {
        if (autoRefreshTimer != null) {
            autoRefreshTimer.stop();
        }
    }
}