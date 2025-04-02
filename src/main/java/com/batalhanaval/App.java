package com.batalhanaval;

import javax.swing.SwingUtilities;

import com.batalhanaval.core.Board;
import com.batalhanaval.core.GameState;
import com.batalhanaval.core.Position;
import com.batalhanaval.core.Ship;
import com.batalhanaval.ui.MainWindow;

/**
 * Main class for the Network Battleship game.
 */
public class App {
    
    // Flag to enable/disable debug mode (reveal opponent's ships)
    public static final boolean DEBUG_MODE = true; // Change to false to disable
    
    public static void main(String[] args) {
        System.out.println("Starting Network Battleship...");
        if (DEBUG_MODE) {
            System.out.println("*** DEBUG MODE ENABLED ***");
        }
        
        // Start the graphical interface using Swing
        SwingUtilities.invokeLater(() -> {
            MainWindow mainWindow = new MainWindow();
            mainWindow.setVisible(true);
        });
        
        System.out.println("\nApplication started successfully!");
        System.out.println("Choose between playing against AI or connecting for online play.");
    }
}
