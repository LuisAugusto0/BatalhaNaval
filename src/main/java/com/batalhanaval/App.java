package com.batalhanaval;

import javax.swing.SwingUtilities;

import com.batalhanaval.core.GameState;
import com.batalhanaval.core.Position;
import com.batalhanaval.ui.MainWindow;

/**
 * Main class for the Network Battleship game.
 */
public class App {
    public static void main(String[] args) {
        System.out.println("Starting Network Battleship...");
        
        // Inicia a interface gráfica usando Swing
        SwingUtilities.invokeLater(() -> {
            MainWindow mainWindow = new MainWindow();
            mainWindow.setVisible(true);
        });
        
        // O código abaixo era apenas para demonstração da lógica do jogo
        // e foi substituído pela interface gráfica
        /*
        // Simple demonstration of game logic functionality
        GameState gameState = new GameState(Constants.BOARD_SIZE);
        
        // Create ships in default positions (for testing)
        boolean shipsPlaced = gameState.createDefaultShips();
        System.out.println("Ships positioned: " + shipsPlaced);
        
        // Start the game
        boolean gameStarted = gameState.startGame();
        System.out.println("Game started: " + gameStarted);
        System.out.println("Message: " + gameState.getMessage());
        
        // Simulate some attacks
        String result = gameState.processPlayerAttack(new Position(2, 3));
        System.out.println("Attack at C3: " + result);
        System.out.println("Message: " + gameState.getMessage());
        */
        
        System.out.println("\nInterface gráfica iniciada.");
        System.out.println("A parte 2 do projeto foi concluída com sucesso!");
        System.out.println("Próxima etapa: implementar comunicação TCP/UDP e multithreading.");
    }
}
