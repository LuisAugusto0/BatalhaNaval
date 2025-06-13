package com.batalhanaval.ui;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

import com.batalhanaval.Constants;
import com.batalhanaval.network.NetworkManager;

/**
 * Panel for setting up an online game (create or join).
 */
public class OnlineSetupPanel extends JPanel {
    
    private MainWindow mainWindow;
    private NetworkManager networkManager;
    
    private JTextField serverIPField;
    private JTextArea statusTextArea;
    private JButton createGameButton;
    private JButton joinGameButton;
    private JButton backButton;
    private JButton testUdpButton;
    private JButton proceedToSetupButton;
    
    /**
     * Constructor for the online setup panel.
     * 
     * @param mainWindow Reference to the main window
     * @param networkManager Reference to the network manager
     */
    public OnlineSetupPanel(MainWindow mainWindow, NetworkManager networkManager) {
        this.mainWindow = mainWindow;
        this.networkManager = networkManager;
        
        // Panel settings
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.BLACK);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Create components
        JLabel titleLabel = new JLabel("Online Game Setup", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        
        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.BLACK);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Create text fields and labels
        JLabel ipLabel = new JLabel("Server IP:");
        ipLabel.setForeground(Color.WHITE);
        
        serverIPField = new JTextField("localhost", 20);
        serverIPField.setBackground(new Color(30, 30, 30));
        serverIPField.setForeground(Color.WHITE);
        serverIPField.setCaretColor(Color.WHITE);
        
        // Buttons
        createGameButton = new JButton("Create Game (Host)");
        joinGameButton = new JButton("Join Game");
        backButton = new JButton("Back to Menu");
        testUdpButton = new JButton("Test UDP Message");
        proceedToSetupButton = new JButton("Proceed to Ship Setup");
        testUdpButton.setEnabled(false);  // Initially disabled
        proceedToSetupButton.setEnabled(false);  // Initially disabled
        
        // Style buttons
        for (JButton button : new JButton[]{createGameButton, joinGameButton, backButton, testUdpButton, proceedToSetupButton}) {
            button.setBackground(new Color(50, 50, 50));
            button.setForeground(Color.WHITE);
            button.setFocusPainted(false);
        }
        
        // Status text area
        statusTextArea = new JTextArea(8, 30);
        statusTextArea.setEditable(false);
        statusTextArea.setBackground(new Color(20, 20, 20));
        statusTextArea.setForeground(Color.GREEN);
        statusTextArea.setCaretColor(Color.WHITE);
        statusTextArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(statusTextArea);
        
        // Add components to form
        JPanel ipPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        ipPanel.setBackground(Color.BLACK);
        ipPanel.add(ipLabel);
        ipPanel.add(serverIPField);
        
        formPanel.add(ipPanel, gbc);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(Color.BLACK);
        buttonPanel.add(createGameButton);
        buttonPanel.add(joinGameButton);
        buttonPanel.add(backButton);
        
        JPanel testPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        testPanel.setBackground(Color.BLACK);
        testPanel.add(testUdpButton);
        testPanel.add(proceedToSetupButton);
        
        formPanel.add(buttonPanel, gbc);
        formPanel.add(testPanel, gbc);
        formPanel.add(new JLabel("Status:"), gbc);
        formPanel.add(scrollPane, gbc);
        
        // Add components to main panel
        add(titleLabel, BorderLayout.NORTH);
        add(formPanel, BorderLayout.CENTER);
        
        // Add action listeners
        createGameButton.addActionListener(e -> createGame());
        joinGameButton.addActionListener(e -> joinGame());
        backButton.addActionListener(e -> mainWindow.showMainMenuPanel());
        testUdpButton.addActionListener(e -> testUdpMessage());
        proceedToSetupButton.addActionListener(e -> proceedToShipSetup());
        
        // Initial status message
        updateStatus("Ready to start or join a game.");
    }
    
    /**
     * Creates a game (acts as server).
     */
    private void createGame() {
        // Disable buttons while connecting
        setButtonsEnabled(false);
        updateStatus("Starting server...");
        
        // Start server in a separate thread
        new Thread(() -> {
            try {
                networkManager.startServer(
                    Constants.DEFAULT_PORT, 
                    Constants.DISCOVERY_PORT, 
                    this::updateStatus
                );
                
                // Enable test button and proceed button when connected
                SwingUtilities.invokeLater(() -> {
                    testUdpButton.setEnabled(true);
                    proceedToSetupButton.setEnabled(true);
                    updateStatus("Server started! You can now proceed to ship setup.");
                });
                
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    updateStatus("Error starting server: " + e.getMessage());
                    setButtonsEnabled(true);
                });
            }
        }).start();
    }
    
    /**
     * Joins a game (acts as client).
     */
    private void joinGame() {
        String serverIP = serverIPField.getText().trim();
        if (serverIP.isEmpty()) {
            updateStatus("Please enter a valid server IP address.");
            return;
        }
        
        // Disable buttons while connecting
        setButtonsEnabled(false);
        updateStatus("Connecting to " + serverIP + "...");
        
        // Connect to server in a separate thread
        new Thread(() -> {
            try {
                networkManager.connectToServer(
                    serverIP,
                    Constants.DEFAULT_PORT,
                    Constants.DISCOVERY_PORT,
                    this::updateStatus
                );
                
                // Enable test button and proceed button when connected
                SwingUtilities.invokeLater(() -> {
                    testUdpButton.setEnabled(true);
                    proceedToSetupButton.setEnabled(true);
                    updateStatus("Connected to server! You can now proceed to ship setup.");
                });
                
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    updateStatus("Connection error: " + e.getMessage());
                    setButtonsEnabled(true);
                });
            }
        }).start();
    }
    
    /**
     * Sends a test UDP message to the remote player.
     */
    private void testUdpMessage() {
        if (networkManager.isConnected()) {
            String message = "Hello from " + (networkManager.isServer() ? "SERVER" : "CLIENT") + 
                             " at " + System.currentTimeMillis();
            
            boolean sent = networkManager.sendUdpMessage(message);
            updateStatus("UDP test message " + (sent ? "sent" : "failed to send") + ": " + message);
        } else {
            updateStatus("Cannot send UDP message - not connected");
        }
    }
    
    /**
     * Proceeds to ship setup after successful connection.
     */
    private void proceedToShipSetup() {
        if (networkManager.isConnected()) {
            updateStatus("Proceeding to ship setup...");
            mainWindow.showSetupPanel();
        } else {
            updateStatus("Cannot proceed - not connected to opponent");
        }
    }
    
    /**
     * Updates the status text area.
     * 
     * @param message Status message
     */
    public void updateStatus(String message) {
        SwingUtilities.invokeLater(() -> {
            statusTextArea.append(message + "\n");
            // Scroll to bottom
            statusTextArea.setCaretPosition(statusTextArea.getDocument().getLength());
        });
    }
    
    /**
     * Enables or disables the buttons.
     * 
     * @param enabled Whether buttons should be enabled
     */
    private void setButtonsEnabled(boolean enabled) {
        SwingUtilities.invokeLater(() -> {
            createGameButton.setEnabled(enabled);
            joinGameButton.setEnabled(enabled);
            serverIPField.setEnabled(enabled);
        });
    }
}