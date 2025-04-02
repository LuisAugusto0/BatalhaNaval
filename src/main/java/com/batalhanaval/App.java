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
    public static final boolean DEBUG_MODE = true; // Change to true to enable
    
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
        
        // The code below was just for game logic demonstration
        // and has been replaced by the graphical interface
        
        // Simple demonstration of game logic functionality
        /*
        GameState gameState = new GameState();
        // Create ships in default positions (for testing)
        Board playerBoard = gameState.getPlayerBoard();
        Board opponentBoard = gameState.getOpponentBoard();
        gameState.setupDefaultShips();
        
        // Start the game
        gameState.startGame();
        System.out.println(gameState.getMessage());
        System.out.println(playerBoard.toString());
        System.out.println(opponentBoard.toString());
        
        // Simulate some attacks
        gameState.processPlayerAttack(new Position(0, 0));
        System.out.println(gameState.getMessage());
        System.out.println(playerBoard.toString());
        System.out.println(opponentBoard.toString());
        */
        
        System.out.println("\nGraphical interface started.");
        System.out.println("Part 2 of the project has been successfully completed!");
        System.out.println("Next step: implement TCP/UDP communication and multithreading.");
    }
}
