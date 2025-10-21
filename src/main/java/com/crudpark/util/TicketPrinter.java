// util/TicketPrinter.java - OPTIMIZADO PARA LINUX
package com.crudpark.util;

import com.crudpark.model.Ticket;
import com.google.zxing.WriterException;

import javax.imageio.ImageIO;
import javax.print.*;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.MediaSizeName;
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
            System.out.println("\n🖨️ Iniciando proceso de impresión...");
            
            // Generate QR code
            long timestamp = ticket.getEntryTime().atZone(java.time.ZoneId.systemDefault()).toEpochSecond();
            String qrData = QRCodeGenerator.formatTicketData(ticket.getId(), ticket.getPlate(), timestamp);
            BufferedImage qrImage = QRCodeGenerator.generateQRCode(qrData, 150, 150);

            // Detectar impresoras disponibles
            PrintService[] printServices = PrinterJob.lookupPrintServices();
            
            if (printServices.length == 0) {
                System.out.println("⚠️ No se detectaron impresoras. Guardando como imagen...");
                savePrintPreview(ticket, qrImage);
                return;
            }

            // Mostrar impresoras disponibles
            System.out.println("\n📋 Impresoras disponibles:");
            for (int i = 0; i < printServices.length; i++) {
                System.out.println("  [" + i + "] " + printServices[i].getName());
            }

            // Buscar impresora térmica o usar la predeterminada
            PrintService selectedPrinter = findThermalPrinter(printServices);
            
            if (selectedPrinter != null) {
                System.out.println("✅ Usando impresora: " + selectedPrinter.getName());
                printToService(ticket, qrImage, selectedPrinter);
            } else {
                // Mostrar diálogo de selección de impresora
                System.out.println("🔍 Mostrando diálogo de selección...");
                printWithDialog(ticket, qrImage);
            }
            
        } catch (Exception e) {
            System.err.println("❌ Error de impresión: " + e.getMessage());
            e.printStackTrace();
            
            try {
                // Fallback: guardar como imagen
                long timestamp = ticket.getEntryTime().atZone(java.time.ZoneId.systemDefault()).toEpochSecond();
                String qrData = QRCodeGenerator.formatTicketData(ticket.getId(), ticket.getPlate(), timestamp);
                BufferedImage qrImage = QRCodeGenerator.generateQRCode(qrData, 150, 150);
                savePrintPreview(ticket, qrImage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private static PrintService findThermalPrinter(PrintService[] services) {
        // Buscar impresoras que contengan palabras clave comunes de impresoras térmicas
        String[] thermalKeywords = {"thermal", "pos", "receipt", "ticket", "80mm", "58mm", "tsc", "zebra", "epson"};
        
        for (PrintService service : services) {
            String printerName = service.getName().toLowerCase();
            for (String keyword : thermalKeywords) {
                if (printerName.contains(keyword)) {
                    return service;
                }
            }
        }
        
        // Si no encuentra térmica, devolver la predeterminada
        return PrintServiceLookup.lookupDefaultPrintService();
    }

    private static void printToService(Ticket ticket, BufferedImage qrImage, PrintService printService) throws PrinterException {
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setPrintService(printService);
        
        PageFormat pageFormat = job.defaultPage();
        Paper paper = new Paper();
        
        // Tamaño de papel térmico 80mm
        double width = 226.77;  // ~80mm en puntos
        double height = 600;
        paper.setSize(width, height);
        paper.setImageableArea(0, 0, width, height);
        pageFormat.setPaper(paper);
        pageFormat.setOrientation(PageFormat.PORTRAIT);

        job.setPrintable(new TicketPrintable(ticket, qrImage), pageFormat);
        
        // Imprimir sin diálogo
        job.print();
        System.out.println("✅ Ticket enviado a impresora: " + printService.getName());
    }

    private static void printWithDialog(Ticket ticket, BufferedImage qrImage) throws PrinterException {
        PrinterJob job = PrinterJob.getPrinterJob();
        
        PageFormat pageFormat = job.defaultPage();
        Paper paper = new Paper();
        
        // Tamaño de papel térmico 80mm
        double width = 226.77;  // ~80mm en puntos
        double height = 600;
        paper.setSize(width, height);
        paper.setImageableArea(0, 0, width, height);
        pageFormat.setPaper(paper);

        job.setPrintable(new TicketPrintable(ticket, qrImage), pageFormat);

        // Mostrar diálogo de impresión
        if (job.printDialog()) {
            job.print();
            System.out.println("✅ Ticket impreso correctamente");
        } else {
            System.out.println("⚠️ Impresión cancelada por el usuario");
            // Guardar como imagen si se cancela
            savePrintPreview(ticket, qrImage);
        }
    }

    private static void savePrintPreview(Ticket ticket, BufferedImage qrImage) {
        try {
            BufferedImage preview = createTicketImage(ticket, qrImage);
            
            // Crear directorio si no existe
            File outputDir = new File("tickets");
            if (!outputDir.exists()) {
                outputDir.mkdirs();
            }
            
            File output = new File(outputDir, "ticket_" + ticket.getId() + ".png");
            ImageIO.write(preview, "PNG", output);
            System.out.println("✅ Ticket guardado como imagen: " + output.getAbsolutePath());
            
            // Mostrar preview en un diálogo
            SwingUtilities.invokeLater(() -> {
                JLabel label = new JLabel(new ImageIcon(preview));
                JPanel panel = new JPanel(new BorderLayout());
                panel.add(label, BorderLayout.CENTER);
                
                JLabel infoLabel = new JLabel(
                    "<html><center>Ticket guardado en:<br>" + 
                    output.getAbsolutePath() + 
                    "</center></html>"
                );
                infoLabel.setHorizontalAlignment(SwingConstants.CENTER);
                infoLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                panel.add(infoLabel, BorderLayout.SOUTH);
                
                JOptionPane.showMessageDialog(
                    null, 
                    panel, 
                    "Vista Previa del Ticket", 
                    JOptionPane.PLAIN_MESSAGE
                );
            });
            
        } catch (Exception e) {
            System.err.println("❌ Error al guardar imagen: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static BufferedImage createTicketImage(Ticket ticket, BufferedImage qrImage) {
        int height = 550;
        BufferedImage image = new BufferedImage(TICKET_WIDTH, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();
        
        // Habilitar antialiasing para mejor calidad
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        // Fondo blanco
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, TICKET_WIDTH, height);
        
        // Dibujar contenido del ticket
        g2d.setColor(Color.BLACK);
        int y = 20;
        
        // Encabezado
        g2d.setFont(new Font("Arial", Font.BOLD, 18));
        drawCenteredString(g2d, "CrudPark", TICKET_WIDTH, y);
        y += 20;
        
        g2d.setFont(new Font("Arial", Font.PLAIN, 12));
        drawCenteredString(g2d, "Sistema de Parqueo", TICKET_WIDTH, y);
        y += 25;
        
        drawLine(g2d, y);
        y += 20;
        
        // Información del ticket
        g2d.setFont(new Font("Monospaced", Font.BOLD, 14));
        g2d.drawString("Ticket #: " + String.format("%06d", ticket.getId()), 20, y);
        y += 25;
        
        g2d.setFont(new Font("Monospaced", Font.PLAIN, 12));
        g2d.drawString("Placa: " + ticket.getPlate(), 20, y);
        y += 20;
        g2d.drawString("Tipo: " + ticket.getTicketType(), 20, y);
        y += 20;
        g2d.drawString("Entrada: ", 20, y);
        y += 15;
        g2d.setFont(new Font("Monospaced", Font.PLAIN, 10));
        g2d.drawString("  " + ticket.getEntryTime().format(DATE_FORMAT), 20, y);
        y += 20;
        g2d.setFont(new Font("Monospaced", Font.PLAIN, 12));
        g2d.drawString("Operador: " + ticket.getOperatorName(), 20, y);
        y += 25;
        
        drawLine(g2d, y);
        y += 20;
        
        // Código QR
        int qrX = (TICKET_WIDTH - qrImage.getWidth()) / 2;
        g2d.drawImage(qrImage, qrX, y, null);
        y += qrImage.getHeight() + 20;
        
        drawLine(g2d, y);
        y += 20;
        
        // Pie de página
        g2d.setFont(new Font("Arial", Font.ITALIC, 10));
        drawCenteredString(g2d, "Gracias por su visita", TICKET_WIDTH, y);
        y += 15;
        drawCenteredString(g2d, "Conserve este ticket", TICKET_WIDTH, y);
        
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

    // Implementación Printable
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

            // Dibujar ticket
            BufferedImage ticketImage = createTicketImage(ticket, qrImage);
            
            // Escalar si es necesario para que quepa en el papel
            double scaleX = pageFormat.getImageableWidth() / ticketImage.getWidth();
            double scaleY = pageFormat.getImageableHeight() / ticketImage.getHeight();
            double scale = Math.min(scaleX, scaleY);
            
            if (scale < 1.0) {
                int scaledWidth = (int) (ticketImage.getWidth() * scale);
                int scaledHeight = (int) (ticketImage.getHeight() * scale);
                g2d.drawImage(ticketImage, 0, 0, scaledWidth, scaledHeight, null);
            } else {
                g2d.drawImage(ticketImage, 0, 0, null);
            }

            return PAGE_EXISTS;
        }
    }
    
    // Método de utilidad para listar todas las impresoras disponibles
    public static void listAvailablePrinters() {
        System.out.println("\n🖨️ Listando todas las impresoras disponibles:");
        PrintService[] printServices = PrinterJob.lookupPrintServices();
        
        if (printServices.length == 0) {
            System.out.println("❌ No se detectaron impresoras en el sistema");
            System.out.println("\n💡 Sugerencias para Linux Ubuntu:");
            System.out.println("  1. Verificar que CUPS esté instalado: sudo apt install cups");
            System.out.println("  2. Iniciar servicio CUPS: sudo systemctl start cups");
            System.out.println("  3. Agregar impresora en Configuración del Sistema");
            System.out.println("  4. Verificar: lpstat -p -d");
        } else {
            for (int i = 0; i < printServices.length; i++) {
                PrintService ps = printServices[i];
                System.out.println("\n  [" + i + "] " + ps.getName());
                System.out.println("      Estado: " + (ps.isDocFlavorSupported(DocFlavor.SERVICE_FORMATTED.PRINTABLE) ? "✅ Compatible" : "⚠️ Verificar compatibilidad"));
            }
        }
        
        PrintService defaultService = PrintServiceLookup.lookupDefaultPrintService();
        if (defaultService != null) {
            System.out.println("\n📌 Impresora predeterminada: " + defaultService.getName());
        } else {
            System.out.println("\n⚠️ No hay impresora predeterminada configurada");
        }
    }
}