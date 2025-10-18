// MainApp.java
package com.crudpark;

import com.crudpark.model.Operator;
import com.crudpark.ui.LoginDialog;
import com.crudpark.ui.MainFrame; // Will create this later

import javax.swing.*;
import java.awt.*;

public class MainApp {
    public static void main(String[] args) {
        // Ensure GUI updates are done on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            // Create a dummy parent frame for the dialog (can be invisible initially)
            JFrame tempFrame = new JFrame();
            tempFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            // tempFrame.setSize(0, 0); // Make it invisible
            // tempFrame.setVisible(true);

            LoginDialog loginDialog = new LoginDialog(tempFrame);
            loginDialog.setVisible(true);

            Operator authenticatedOperator = loginDialog.getAuthenticatedOperator();

            if (authenticatedOperator != null) {
                // If login successful, open the main application window
                MainFrame mainFrame = new MainFrame(authenticatedOperator);
                mainFrame.setVisible(true);
                // tempFrame.dispose(); // Dispose the temporary frame if it was visible
            } else {
                // If login failed or was cancelled, exit the application
                System.out.println("Login cancelled or failed. Exiting application.");
                System.exit(0);
            }
        });
    }
}