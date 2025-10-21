// MainApp.java - MEJORADO
package com.crudpark;

import com.crudpark.model.Operator;
import com.crudpark.ui.LoginDialog;
import com.crudpark.ui.MainFrame;
import com.crudpark.util.DbConnection;

import javax.swing.*;

public class MainApp {
    public static void main(String[] args) {
        // Test database connection first
        System.out.println("🚀 Starting CrudPark Application...");
        DbConnection.testConnection();
        
        // Ensure GUI updates are done on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            try {
                // Set Look and Feel (opcional, hace que se vea mejor)
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                System.err.println("Could not set Look and Feel: " + e.getMessage());
            }
            
            // Create a parent frame for the login dialog
            JFrame parentFrame = new JFrame();
            parentFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            
            // Show login dialog
            LoginDialog loginDialog = new LoginDialog(parentFrame);
            loginDialog.setVisible(true);

            Operator authenticatedOperator = loginDialog.getAuthenticatedOperator();

            if (authenticatedOperator != null) {
                System.out.println("✅ Login successful: " + authenticatedOperator.getName());
                
                // Open main application window
                MainFrame mainFrame = new MainFrame(authenticatedOperator);
                mainFrame.setVisible(true);
                
                // Dispose the parent frame (was only for the dialog)
                parentFrame.dispose();
            } else {
                // If login failed or was cancelled, exit
                System.out.println("❌ Login cancelled or failed. Exiting application.");
                System.exit(0);
            }
        });
    }
}