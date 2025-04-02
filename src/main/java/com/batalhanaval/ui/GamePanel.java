package com.batalhanaval.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.batalhanaval.Constants;
import com.batalhanaval.core.Board;
import com.batalhanaval.core.Position;
import com.batalhanaval.core.Ship;

/**
 * Panel for the main game view with both player and opponent boards.
 */
public class GamePanel extends JPanel {
    
    private MainWindow mainWindow;
    private Board playerBoard;
    private Board opponentBoard;
    private boolean isPlayerTurn;
    
    private BoardPanel playerBoardPanel;
    private BoardPanel opponentBoardPanel;
    private JPanel controlPanel;
    private JButton surrenderButton;
    
    private Random random = new Random();
    
    /**
     * Constructor for the game panel.
     * @param mainWindow Reference to the main window.
     */
    public GamePanel(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        this.isPlayerTurn = true;
        
        // The playerBoard will be set by MainWindow.showGamePanel
        this.playerBoard = null;
        this.opponentBoard = new Board(Constants.BOARD_SIZE); // Opponent's board
        
        // Randomly position opponent's ships
        setupOpponentBoard();
        
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(Color.BLACK);
        
        // Initialize the board panels and control panel
        setupBoardPanels();
        setupControlPanel();
        
        // Add panels to the main layout
        JPanel boardsPanel = new JPanel(new GridLayout(1, 2, 30, 0));
        boardsPanel.setBackground(Color.BLACK);
        boardsPanel.add(playerBoardPanel);
        boardsPanel.add(opponentBoardPanel);
        
        add(boardsPanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.EAST);
    }
    
    /**
     * Sets up the opponent's board with randomly positioned ships.
     */
    private void setupOpponentBoard() {
        List<Ship> ships = createStandardShips();
        Random random = new Random();
        
        for (Ship ship : ships) {
            boolean placed = false;
            int attempts = 0;
            
            while (!placed && attempts < 100) {
                int row = random.nextInt(Constants.BOARD_SIZE);
                int col = random.nextInt(Constants.BOARD_SIZE);
                boolean vertical = random.nextBoolean();
                
                // Use Board's placeShip method directly
                placed = opponentBoard.placeShip(ship, new Position(row, col), vertical);
                attempts++;
            }
            
            // If we can't position after 100 attempts, there might be a problem
            if (!placed) {
                System.err.println("Failed to place ship: " + ship.getName());
            }
        }
    }
    
    /**
     * Creates a standard set of ships for the game.
     */
    private List<Ship> createStandardShips() {
        List<Ship> ships = new ArrayList<>();
        
        ships.add(new Ship("Carrier", Constants.CARRIER_SIZE));
        ships.add(new Ship("Battleship", Constants.BATTLESHIP_SIZE));
        ships.add(new Ship("Cruiser", Constants.CRUISER_SIZE));
        ships.add(new Ship("Submarine", Constants.SUBMARINE_SIZE));
        ships.add(new Ship("Destroyer", Constants.DESTROYER_SIZE));
        
        return ships;
    }
    
    /**
     * Sets up the player and opponent board panels.
     */
    private void setupBoardPanels() {
        // Player's panel
        JPanel playerPanel = new JPanel(new BorderLayout(5, 5));
        playerPanel.setBackground(Color.BLACK);
        playerPanel.add(new JLabel("Your Fleet", JLabel.CENTER), BorderLayout.NORTH);
        
        // Create the BoardPanel for the player (the real board will be set later)
        playerBoardPanel = new BoardPanel(new Board(Constants.BOARD_SIZE), false);
        playerPanel.add(playerBoardPanel, BorderLayout.CENTER);
        
        // Opponent's panel
        JPanel opponentPanel = new JPanel(new BorderLayout(5, 5));
        opponentPanel.setBackground(Color.BLACK);
        opponentPanel.add(new JLabel("Enemy Fleet", JLabel.CENTER), BorderLayout.NORTH);
        
        opponentBoardPanel = new BoardPanel(opponentBoard, true);
        opponentBoardPanel.setClickHandler(this::handleAttackClick);
        opponentPanel.add(opponentBoardPanel, BorderLayout.CENTER);
        
        // Style labels
        Component[] labels = new Component[] {
            playerPanel.getComponent(0),
            opponentPanel.getComponent(0)
        };
        
        for (Component c : labels) {
            if (c instanceof JLabel) {
                JLabel label = (JLabel) c;
                label.setForeground(Color.WHITE);
                label.setFont(new Font("Arial", Font.BOLD, 16));
            }
        }
    }
    
    /**
     * Sets up the control panel with game buttons.
     */
    private void setupControlPanel() {
        controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        controlPanel.setPreferredSize(new Dimension(150, 400));
        controlPanel.setBackground(Color.BLACK);
        
        // Title
        JLabel titleLabel = new JLabel("Battle in Progress");
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(Color.WHITE);
        controlPanel.add(titleLabel);
        controlPanel.add(Box.createVerticalStrut(20));
        
        // Surrender button
        surrenderButton = new JButton("Surrender");
        surrenderButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        surrenderButton.addActionListener(this::handleSurrenderClick);
        surrenderButton.setBackground(new Color(50, 50, 50));
        surrenderButton.setForeground(Color.WHITE);
        controlPanel.add(surrenderButton);
        
        // Instructions
        controlPanel.add(Box.createVerticalStrut(30));
        JLabel instructionsLabel = new JLabel("<html><body style='width: 140px; color: white'>Click on the opponent's board to attack.</body></html>");
        instructionsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        controlPanel.add(instructionsLabel);
    }
    
    /**
     * Handles a click on the opponent's board as an attack.
     */
    private void handleAttackClick(int row, int col) {
        if (!isPlayerTurn) {
            mainWindow.updateStatusMessage("Wait for your turn!");
            return;
        }
        
        Position pos = new Position(row, col);
        
        // Check if the position has already been attacked using the list of attacked positions
        boolean alreadyAttacked = false;
        for (Position attackedPos : opponentBoard.getAttackedPositions()) {
            if (attackedPos.equals(pos)) {
                alreadyAttacked = true;
                break;
            }
        }
        
        if (alreadyAttacked) {
            mainWindow.updateStatusMessage("You've already attacked this position!");
            return;
        }
        
        mainWindow.updateStatusMessage("Attacking position " + pos + "...");
        
        // Simulate network attack sending (will be implemented later)
        
        // Process the attack ON THE REAL OPPONENT'S BOARD
        String result = opponentBoard.processAttack(pos);
        
        // Process the attack result
        if (result.equals(Constants.ATTACK_HIT)) {
            mainWindow.updateStatusMessage("Hit! The enemy ship was damaged!");
        } else if (result.equals(Constants.ATTACK_MISS)) {
            mainWindow.updateStatusMessage("Miss! You hit the water!");
        } else if (result.equals(Constants.ATTACK_SUNK)) {
            mainWindow.updateStatusMessage("Ship sunk! You destroyed an enemy ship!");
            
            // Check if the game is over (player won)
            if (opponentBoard.areAllShipsSunk()) {
                handleGameOver(true); // Player won
                return;
            }
        } else if (result.equals(Constants.ATTACK_INVALID)) {
            // ATTACK_INVALID (shouldn't happen here due to previous check)
            mainWindow.updateStatusMessage("Invalid attack. Try again.");
            return;
        }
        
        // Update the opponent's board to show the attack result and ship status
        opponentBoardPanel.updateShipStatusPanel();
        opponentBoardPanel.repaint();
        
        // Switch turns
        setPlayerTurn(false);
        
        // Simulate the opponent's turn (will be implemented later)
        try {
            Thread.sleep(1500); // Simulate a small delay
            simulateOpponentTurn();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Simulates the opponent's turn with a random attack.
     */
    private void simulateOpponentTurn() {
        // Only simulate if it's still the opponent's turn (avoid double execution)
        if (isPlayerTurn) {
            return;
        }
        
        // Simulate a random attack from the opponent
        Position pos;
        boolean validPosition = false;
        
        // Find a position that hasn't been attacked yet
        do {
            int row = random.nextInt(Constants.BOARD_SIZE);
            int col = random.nextInt(Constants.BOARD_SIZE);
            pos = new Position(row, col);
            
            // Check if this position has already been attacked
            boolean alreadyAttacked = false;
            for (Position attackedPos : playerBoard.getAttackedPositions()) {
                if (attackedPos.equals(pos)) {
                    alreadyAttacked = true;
                    break;
                }
            }
            
            validPosition = !alreadyAttacked;
        } while (!validPosition);
        
        // Display attack message
        mainWindow.updateStatusMessage("Enemy is attacking position " + pos + "...");
        
        try {
            // Add a small delay for better visuals
            Thread.sleep(1000);
            
            // Process the attack on the player's board
            String result = playerBoard.processAttack(pos);
            
            // Update the player's board to show the result of the opponent's attack and ship status
            playerBoardPanel.updateShipStatusPanel();
            playerBoardPanel.repaint();
            
            // Update the status message based on the result
            if (result.equals(Constants.ATTACK_HIT)) {
                mainWindow.updateStatusMessage("Your ship was hit!");
            } else if (result.equals(Constants.ATTACK_MISS)) {
                mainWindow.updateStatusMessage("The enemy missed!");
            } else if (result.equals(Constants.ATTACK_SUNK)) {
                mainWindow.updateStatusMessage("Your ship was sunk!");
                
                // Check if the game is over (opponent won)
                if (playerBoard.areAllShipsSunk()) {
                    handleGameOver(false); // Opponent won
                    return;
                }
            }
            
            // Return the turn to the player
            Thread.sleep(1000);
            setPlayerTurn(true);
            mainWindow.updateStatusMessage("Your turn - attack the enemy fleet!");
            
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Handles the end of the game.
     * @param playerWon True if the player won, false if the opponent won.
     */
    private void handleGameOver(boolean playerWon) {
        String message = playerWon ? 
            "Victory! You sunk all enemy ships!" : 
            "Defeat! All your ships were sunk!";
        
        mainWindow.updateStatusMessage(message);
        
        // Update ship status displays one final time
        playerBoardPanel.updateShipStatusPanel();
        opponentBoardPanel.updateShipStatusPanel();
        
        // Disable further plays
        setPlayerTurn(false); // Prevent more plays
        
        // Show dialog with result
        JOptionPane.showMessageDialog(this, 
            message, 
            "Game Over", 
            playerWon ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.WARNING_MESSAGE);
        
        // TODO: Implement option to play again or close?
        // For now, just inform and leave the screen as is
        // mainWindow.showSetupPanel(); // No longer automatically returns to setup
    }
    
    /**
     * Sets the player's turn and updates UI elements accordingly.
     */
    private void setPlayerTurn(boolean isPlayerTurn) {
        this.isPlayerTurn = isPlayerTurn;
        surrenderButton.setEnabled(isPlayerTurn);
    }
    
    /**
     * Handles the surrender button click.
     */
    private void handleSurrenderClick(ActionEvent e) {
        if (!isPlayerTurn) {
            mainWindow.updateStatusMessage("You can only surrender during your turn!");
            return;
        }
        
        int option = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to surrender?",
            "Confirm Surrender",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        
        if (option == JOptionPane.YES_OPTION) {
            // Implement surrender logic (send message to opponent, etc.)
            // For now, just end the game as a defeat
            handleGameOver(false);
        }
    }
    
    /**
     * Sets the player's board.
     * @param board The configured player's board.
     */
    public void setPlayerBoard(Board board) {
        this.playerBoard = board;
        this.playerBoardPanel.setBoard(board);
        
        // Ensure the opponent has ships (in case it comes from an external source)
        if (opponentBoard.getShips().isEmpty()) {
            setupOpponentBoard();
        }
    }
} 