package com.batalhanaval.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Main menu panel with options to play against AI or play online.
 */
public class MainMenuPanel extends JPanel {
    
    private MainWindow mainWindow;
    private JButton playAIButton;
    private JButton playOnlineButton;
    
    /**
     * Constructor for the main menu panel.
     * 
     * @param mainWindow Reference to the main window
     */
    public MainMenuPanel(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        
        // Panel settings
        setLayout(new BorderLayout());
        setBackground(Color.BLACK);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Create and configure the title
        JLabel titleLabel = new JLabel("BATALHA NAVAL", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setForeground(Color.WHITE);
        
        // Create and configure the buttons panel
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.Y_AXIS));
        buttonsPanel.setBackground(Color.BLACK);
        buttonsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Create buttons
        playAIButton = createMenuButton("Play vs AI");
        playOnlineButton = createMenuButton("Play Online");
        
        // Add buttons to panel with spacing
        buttonsPanel.add(Box.createVerticalGlue());
        buttonsPanel.add(playAIButton);
        buttonsPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        buttonsPanel.add(playOnlineButton);
        buttonsPanel.add(Box.createVerticalGlue());
        
        // Add components to main panel
        add(titleLabel, BorderLayout.NORTH);
        
        // Center the buttons panel
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(Color.BLACK);
        centerPanel.add(buttonsPanel);
        add(centerPanel, BorderLayout.CENTER);
        
        // Setup action listeners
        playAIButton.addActionListener(e -> mainWindow.showSetupPanel());
        playOnlineButton.addActionListener(e -> mainWindow.showOnlineSetupPanel());
    }
    
    /**
     * Creates a standardized menu button with proper styling.
     * 
     * @param text Button text
     * @return Styled JButton
     */
    private JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(200, 50));
        button.setPreferredSize(new Dimension(200, 50));
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setBackground(new Color(50, 50, 50));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        return button;
    }
}