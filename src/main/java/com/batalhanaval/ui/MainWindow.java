package com.batalhanaval.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import com.batalhanaval.Constants;
import com.batalhanaval.core.Board;

/**
 * Main window of the Battleship game, containing the setup and game panels.
 */
public class MainWindow extends JFrame {
    
    private SetupPanel setupPanel;
    private GamePanel gamePanel;
    private JLabel statusLabel;
    
    /**
     * Constructor for the main window.
     */
    public MainWindow() {
        // Window settings
        setTitle("Battleship");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setMinimumSize(new Dimension(800, 500));
        setLocationRelativeTo(null); // Center the window
        
        // Apply dark theme
        applyDarkTheme();
        
        // Initialize status bar
        setupStatusBar();
        
        // Configure window layout
        setLayout(new BorderLayout());
        
        // Initialize panels (initially shows setup)
        setupPanel = new SetupPanel(this);
        gamePanel = new GamePanel(this);
        
        // Show the initial setup panel
        showSetupPanel();
        
        // Configure window closing
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // Logic to close network connections
                System.out.println("Closing application...");
                System.exit(0);
            }
        });
    }
    
    /**
     * Applies dark theme to the UI.
     */
    private void applyDarkTheme() {
        // Dark theme colors
        Color bgColor = Color.BLACK;
        Color fgColor = Color.WHITE;
        
        // Apply colors to main frame
        this.getContentPane().setBackground(bgColor);
        
        // Configure Look and Feel to have dark colors
        try {
            // Use system look and feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            
            // Configure default colors for components
            UIManager.put("Panel.background", bgColor);
            UIManager.put("Panel.foreground", fgColor);
            UIManager.put("Label.background", bgColor);
            UIManager.put("Label.foreground", fgColor);
            UIManager.put("Button.background", new Color(50, 50, 50));
            UIManager.put("Button.foreground", fgColor);
            UIManager.put("ComboBox.background", new Color(50, 50, 50));
            UIManager.put("ComboBox.foreground", fgColor);
            UIManager.put("ScrollPane.background", bgColor);
            UIManager.put("TextArea.background", new Color(30, 30, 30));
            UIManager.put("TextArea.foreground", fgColor);
            UIManager.put("TextField.background", new Color(30, 30, 30));
            UIManager.put("TextField.foreground", fgColor);
            UIManager.put("List.background", new Color(30, 30, 30));
            UIManager.put("List.foreground", fgColor);
            
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception e) {
            System.err.println("Failed to set look and feel: " + e.getMessage());
        }
    }
    
    /**
     * Sets up the status bar.
     */
    private void setupStatusBar() {
        statusLabel = new JLabel("Welcome to Battleship! Position your ships on the board.");
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        add(statusLabel, BorderLayout.SOUTH);
    }
    
    /**
     * Updates the status message in the status bar.
     * 
     * @param message Message to display
     */
    public void updateStatusMessage(String message) {
        statusLabel.setText(message);
    }
    
    /**
     * Shows the setup panel.
     */
    public void showSetupPanel() {
        getContentPane().removeAll();
        add(statusLabel, BorderLayout.SOUTH);
        add(setupPanel, BorderLayout.CENTER);
        
        updateStatusMessage("Position your ships on the board.");
        validate();
        repaint();
    }
    
    /**
     * Shows the game panel and passes the player's configured board.
     */
    public void showGamePanel() {
        // Pass the board configured by the player to the GamePanel
        Board playerBoard = setupPanel.getPlayerBoard();
        gamePanel.setPlayerBoard(playerBoard);
        
        // TODO: Initialize opponent's board here (when network is implemented)
        // gamePanel.setOpponentBoard(new Board(Constants.BOARD_SIZE));
        
        getContentPane().removeAll();
        add(statusLabel, BorderLayout.SOUTH);
        add(gamePanel, BorderLayout.CENTER);
        
        updateStatusMessage("Game started! Attack the opponent's board.");
        validate();
        repaint();
    }
    
    /**
     * Main method for testing the interface.
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainWindow window = new MainWindow();
            window.setVisible(true);
        });
    }
} 