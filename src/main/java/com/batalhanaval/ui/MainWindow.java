package com.batalhanaval.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import com.batalhanaval.Constants;
import com.batalhanaval.core.Board;
import com.batalhanaval.network.NetworkManager;

/**
 * Main window of the Battleship game, containing the setup and game panels.
 */
public class MainWindow extends JFrame {
    
    // Panel identifiers for CardLayout
    private static final String MAIN_MENU_PANEL = "mainMenu";
    private static final String SETUP_PANEL = "setup";
    private static final String GAME_PANEL = "game";
    private static final String ONLINE_SETUP_PANEL = "onlineSetup";
    
    // Panels
    private MainMenuPanel mainMenuPanel;
    private SetupPanel setupPanel;
    private GamePanel gamePanel;
    private OnlineSetupPanel onlineSetupPanel;
    
    // Layout manager
    private CardLayout cardLayout;
    private JPanel contentPanel;
    
    // Network manager
    private NetworkManager networkManager;
    
    // Status bar
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
        
        // Initialize network manager
        networkManager = new NetworkManager();
        
        // Configure window layout
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(Color.BLACK);
        
        // Initialize panels
        mainMenuPanel = new MainMenuPanel(this);
        setupPanel = new SetupPanel(this);
        gamePanel = new GamePanel(this);
        onlineSetupPanel = new OnlineSetupPanel(this, networkManager);
        
        // Add panels to card layout
        contentPanel.add(mainMenuPanel, MAIN_MENU_PANEL);
        contentPanel.add(setupPanel, SETUP_PANEL);
        contentPanel.add(gamePanel, GAME_PANEL);
        contentPanel.add(onlineSetupPanel, ONLINE_SETUP_PANEL);
        
        // Add panels to main layout
        setLayout(new BorderLayout());
        add(contentPanel, BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);
        
        // Show the initial main menu panel
        showMainMenuPanel();
        
        // Configure window closing
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // Clean up network resources
                if (networkManager != null) {
                    networkManager.stopNetwork();
                }
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
        statusLabel = new JLabel("Welcome to Battleship!");
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
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
     * Shows the main menu panel.
     */
    public void showMainMenuPanel() {
        cardLayout.show(contentPanel, MAIN_MENU_PANEL);
        updateStatusMessage("Welcome to Battleship! Choose a game mode.");
    }
    
    /**
     * Shows the setup panel.
     */
    public void showSetupPanel() {
        cardLayout.show(contentPanel, SETUP_PANEL);
        updateStatusMessage("Position your ships on the board.");
    }
    
    /**
     * Shows the online setup panel.
     */
    public void showOnlineSetupPanel() {
        cardLayout.show(contentPanel, ONLINE_SETUP_PANEL);
        updateStatusMessage("Set up network connection for online play.");
    }
    
    /**
     * Shows the game panel and passes the player's configured board.
     */
    public void showGamePanel() {
        // Pass the board configured by the player to the GamePanel
        Board playerBoard = setupPanel.getPlayerBoard();
        gamePanel.setPlayerBoard(playerBoard);
        
        // Show the game panel
        cardLayout.show(contentPanel, GAME_PANEL);
        updateStatusMessage("Game started! Attack the opponent's board.");
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