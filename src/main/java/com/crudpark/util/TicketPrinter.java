// util/TicketPrinter.java
package com.crudpark.util;

import com.crudpark.model.Ticket;
import com.google.zxing.WriterException;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.print.*;
import java.io.File;
import java.time.format.DateTimeFormatter;

public class TicketPrinter {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final int TICKET_WIDTH = 300;

    public static void printTicket(Ticket ticket) {
        try {
            // Generate QR code
            long timestamp = ticket.getEntryTime().atZone(java.time.ZoneId.systemDefault()).toEpochSecond();
            String qrData = QRCodeGenerator.formatTicketData(ticket.getId(), ticket.getPlate(), timestamp);
            BufferedImage qrImage = QRCodeGenerator.generateQRCode(qrData, 150, 150);

            // Create printable ticket
            PrinterJob job = PrinterJob.getPrinterJob();
            PageFormat pageFormat = job.defaultPage();
            Paper paper = new Paper();
            
            // Thermal printer size (80mm width)
            double width = 226.77; // ~80mm in points
            double height = 500;
            paper.setSize(width, height);
            paper.setImageableArea(0, 0, width, height);
            pageFormat.setPaper(paper);

            job.setPrintable(new TicketPrintable(ticket, qrImage), pageFormat);

            // For testing, show dialog. In production, use job.print() directly
            if (job.printDialog()) {
                job.print();
                System.out.println("Ticket printed successfully");
            }
        } catch (PrinterException | WriterException e) {
            System.err.println("Printing error: " + e.getMessage());
            e.printStackTrace();
            
            // Fallback: save as image
            savePrintPreview(ticket);
        }
    }

    private static void savePrintPreview(Ticket ticket) {
        try {
            long timestamp = ticket.getEntryTime().atZone(java.time.ZoneId.systemDefault()).toEpochSecond();
            String qrData = QRCodeGenerator.formatTicketData(ticket.getId(), ticket.getPlate(), timestamp);
            BufferedImage qrImage = QRCodeGenerator.generateQRCode(qrData, 150, 150);
            
            BufferedImage preview = createTicketImage(ticket, qrImage);
            
            File output = new File("ticket_" + ticket.getId() + ".png");
            ImageIO.write(preview, "PNG", output);
            System.out.println("Ticket saved as image: " + output.getAbsolutePath());
            
            // Show preview dialog
            JLabel label = new JLabel(new ImageIcon(preview));
            JOptionPane.showMessageDialog(null, label, "Ticket Preview", JOptionPane.PLAIN_MESSAGE);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static BufferedImage createTicketImage(Ticket ticket, BufferedImage qrImage) {
        int height = 500;
        BufferedImage image = new BufferedImage(TICKET_WIDTH, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();
        
        // White background
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, TICKET_WIDTH, height);
        
        // Draw ticket content
        g2d.setColor(Color.BLACK);
        int y = 20;
        
        // Header
        g2d.setFont(new Font("Monospaced", Font.BOLD, 16));
        drawCenteredString(g2d, "CrudPark - Crudzaso", TICKET_WIDTH, y);
        y += 25;
        
        drawLine(g2d, y);
        y += 20;
        
        // Ticket info
        g2d.setFont(new Font("Monospaced", Font.PLAIN, 12));
        g2d.drawString("Ticket #: " + String.format("%06d", ticket.getId()), 20, y);
        y += 20;
        g2d.drawString("Plate: " + ticket.getPlate(), 20, y);
        y += 20;
        g2d.drawString("Type: " + ticket.getTicketType(), 20, y);
        y += 20;
        g2d.drawString("Entry: " + ticket.getEntryTime().format(DATE_FORMAT), 20, y);
        y += 20;
        g2d.drawString("Operator: " + ticket.getOperatorName(), 20, y);
        y += 25;
        
        drawLine(g2d, y);
        y += 20;
        
        // QR Code
        int qrX = (TICKET_WIDTH - qrImage.getWidth()) / 2;
        g2d.drawImage(qrImage, qrX, y, null);
        y += qrImage.getHeight() + 20;
        
        drawLine(g2d, y);
        y += 20;
        
        // Footer
        g2d.setFont(new Font("Monospaced", Font.ITALIC, 10));
        drawCenteredString(g2d, "Thank you for your visit", TICKET_WIDTH, y);
        
        g2d.dispose();
        return image;
    }

    private static void drawCenteredString(Graphics2D g2d, String text, int width, int y) {
        FontMetrics metrics = g2d.getFontMetrics();
        int x = (width - metrics.stringWidth(text)) / 2;
        g2d.drawString(text, x, y);
    }

    private static void drawLine(Graphics2D g2d, int y) {
        g2d.drawLine(20, y, TICKET_WIDTH - 20, y);
    }

    // Printable implementation
    private static class TicketPrintable implements Printable {
        private Ticket ticket;
        private BufferedImage qrImage;

        public TicketPrintable(Ticket ticket, BufferedImage qrImage) {
            this.ticket = ticket;
            this.qrImage = qrImage;
        }

        @Override
        public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) {
            if (pageIndex > 0) {
                return NO_SUCH_PAGE;
            }

            Graphics2D g2d = (Graphics2D) graphics;
            g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

            // Draw ticket
            BufferedImage ticketImage = createTicketImage(ticket, qrImage);
            g2d.drawImage(ticketImage, 0, 0, null);

            return PAGE_EXISTS;
        }
    }
}