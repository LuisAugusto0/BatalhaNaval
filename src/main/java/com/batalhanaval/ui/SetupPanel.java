package com.batalhanaval.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import com.batalhanaval.Constants;
import com.batalhanaval.core.Board;
import com.batalhanaval.core.Position;
import com.batalhanaval.core.Ship;

/**
 * Panel for positioning ships on the board.
 */
public class SetupPanel extends JPanel {
    
    private MainWindow mainWindow;
    private Board playerBoard;
    private BoardPanel boardPanel;
    
    private JPanel controlPanel;
    private JComboBox<String> shipTypeCombo;
    private JRadioButton horizontalRadio;
    private JRadioButton verticalRadio;
    private JButton randomizeButton;
    private JButton startGameButton;
    
    private List<Ship> availableShips;
    private Ship selectedShip;
    private boolean isVertical = false;
    
    /**
     * Constructor for the setup panel.
     * @param mainWindow Reference to the main window.
     */
    public SetupPanel(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        this.playerBoard = new Board(Constants.BOARD_SIZE);
        this.availableShips = createAvailableShips();
        
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(Color.BLACK);
        
        // Board panel
        boardPanel = new BoardPanel(playerBoard, false);
        boardPanel.setClickHandler(this::handleBoardClick);
        
        // Control panel (right)
        setupControlPanel();
        
        add(boardPanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.EAST);
        
        updateStartButtonState();
    }
    
    /**
     * Sets up the control panel with positioning options.
     */
    private void setupControlPanel() {
        controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
        contr   olPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        controlPanel.setPreferredSize(new Dimension(200, 400));
        controlPanel.setBackground(Color.BLACK);
        
        // Title
        JLabel titleLabel = new JLabel("Position Your Ships");
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(Color.WHITE);
        controlPanel.add(titleLabel);
        controlPanel.add(Box.createVerticalStrut(20));
        
        // Ship selection
        JLabel shipLabel = new JLabel("Ship Type:");
        shipLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        shipLabel.setForeground(Color.WHITE);
        controlPanel.add(shipLabel);
        controlPanel.add(Box.createVerticalStrut(5));
        
        // Create a custom panel for the combobox for better visibility
        JPanel comboPanel = new JPanel();
        comboPanel.setBackground(Color.BLACK);
        comboPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        comboPanel.setMaximumSize(new Dimension(180, 30));
        
        // Configure the combobox
        shipTypeCombo = new JComboBox<>();
        shipTypeCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (isSelected) {
                    c.setBackground(new Color(80, 80, 80)); 
                } else {
                    c.setBackground(new Color(50, 50, 50));
                }
                c.setForeground(Color.WHITE);
                return c;
            }
        });
        
        updateShipTypeCombo();
        shipTypeCombo.setPreferredSize(new Dimension(160, 25));
        shipTypeCombo.setMaximumSize(new Dimension(160, 25));
        shipTypeCombo.addActionListener(this::handleShipTypeChange);
        shipTypeCombo.setBackground(new Color(50, 50, 50));
        shipTypeCombo.setForeground(Color.WHITE);
        
        comboPanel.add(shipTypeCombo);
        controlPanel.add(comboPanel);
        controlPanel.add(Box.createVerticalStrut(20));
        
        // Orientation (horizontal/vertical)
        JLabel orientationLabel = new JLabel("Orientation:");
        orientationLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        orientationLabel.setForeground(Color.WHITE);
        controlPanel.add(orientationLabel);
        controlPanel.add(Box.createVerticalStrut(5));
        
        horizontalRadio = new JRadioButton("Horizontal");
        horizontalRadio.setSelected(true);
        horizontalRadio.setAlignmentX(Component.CENTER_ALIGNMENT);
        horizontalRadio.setBackground(Color.BLACK);
        horizontalRadio.setForeground(Color.WHITE);
        
        verticalRadio = new JRadioButton("Vertical");
        verticalRadio.setAlignmentX(Component.CENTER_ALIGNMENT);
        verticalRadio.setBackground(Color.BLACK);
        verticalRadio.setForeground(Color.WHITE);
        
        ButtonGroup orientationGroup = new ButtonGroup();
        orientationGroup.add(horizontalRadio);
        orientationGroup.add(verticalRadio);
        
        horizontalRadio.addActionListener(e -> isVertical = false);
        verticalRadio.addActionListener(e -> isVertical = true);
        
        JPanel radioPanel = new JPanel();
        radioPanel.add(horizontalRadio);
        radioPanel.add(verticalRadio);
        radioPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        radioPanel.setBackground(Color.BLACK);
        controlPanel.add(radioPanel);
        controlPanel.add(Box.createVerticalStrut(30));
        
        // Button for random positioning
        randomizeButton = new JButton("Randomize Positions");
        randomizeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        randomizeButton.addActionListener(e -> randomizeShips());
        randomizeButton.setBackground(new Color(50, 50, 50));
        randomizeButton.setForeground(Color.WHITE);
        controlPanel.add(randomizeButton);
        controlPanel.add(Box.createVerticalStrut(10));
        
        // Button to start the game
        startGameButton = new JButton("Start Game");
        startGameButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        startGameButton.setEnabled(false);
        startGameButton.addActionListener(e -> startGame());
        startGameButton.setBackground(new Color(50, 50, 50));
        startGameButton.setForeground(Color.WHITE);
        controlPanel.add(startGameButton);
        
        // Instructions
        controlPanel.add(Box.createVerticalStrut(30));
        JLabel instructionsLabel = new JLabel("<html><body style='width: 180px; color: white'>Select the ship type, orientation, and click on the board to position it.</body></html>");
        instructionsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        controlPanel.add(instructionsLabel);
        
        // Select the first available ship
        if (!availableShips.isEmpty()) {
            selectedShip = availableShips.get(0);
        }
    }
    
    /**
     * Creates the list of available ships for positioning.
     */
    private List<Ship> createAvailableShips() {
        List<Ship> ships = new ArrayList<>();
        
        // Add ships according to constants
        ships.add(new Ship("Carrier", Constants.CARRIER_SIZE));
        ships.add(new Ship("Battleship", Constants.BATTLESHIP_SIZE));
        ships.add(new Ship("Cruiser", Constants.CRUISER_SIZE));
        ships.add(new Ship("Submarine", Constants.SUBMARINE_SIZE));
        ships.add(new Ship("Destroyer", Constants.DESTROYER_SIZE));
        
        return ships;
    }
    
    /**
     * Updates the combo box with available ships.
     */
    private void updateShipTypeCombo() {
        shipTypeCombo.removeAllItems();
        
        for (Ship ship : availableShips) {
            shipTypeCombo.addItem(ship.getName() + " (" + ship.getSize() + ")");
        }
        
        if (shipTypeCombo.getItemCount() > 0) {
            shipTypeCombo.setSelectedIndex(0);
        }
    }
    
    /**
     * Handler for selection change in the ship combo.
     */
    private void handleShipTypeChange(ActionEvent e) {
        int index = shipTypeCombo.getSelectedIndex();
        if (index >= 0 && index < availableShips.size()) {
            selectedShip = availableShips.get(index);
        }
    }
    
    /**
     * Handler for clicks on the board.
     */
    private void handleBoardClick(int row, int col) {
        if (selectedShip == null) {
            mainWindow.updateStatusMessage("Select a ship first!");
            return;
        }
        
        // Copy the selected ship to keep the original list intact
        Ship shipToPlace = new Ship(selectedShip.getName(), selectedShip.getSize());
        Position startPos = new Position(row, col);
        
        try {
            if (playerBoard.placeShip(shipToPlace, startPos, isVertical)) {
                // Remove the ship from the available list
                availableShips.remove(selectedShip);
                
                // Update the UI
                updateShipTypeCombo();
                boardPanel.repaint();
                
                // If there are no more ships, enable the start button
                if (availableShips.isEmpty()) {
                    mainWindow.updateStatusMessage("All ships positioned! Click 'Start Game'.");
                } else {
                    mainWindow.updateStatusMessage("Ship positioned successfully!");
                    // Select the next available ship
                    if (!availableShips.isEmpty()) {
                        selectedShip = availableShips.get(0);
                        shipTypeCombo.setSelectedIndex(0);
                    } else {
                        selectedShip = null;
                    }
                }
                
                updateStartButtonState();
            } else {
                mainWindow.updateStatusMessage("Cannot place ship here. Try another position.");
            }
        } catch (Exception ex) {
            mainWindow.updateStatusMessage("Error placing ship: " + ex.getMessage());
        }
    }
    
    /**
     * Randomly positions all ships on the board.
     */
    private void randomizeShips() {
        // Clear the board and reset available ships
        playerBoard = new Board(Constants.BOARD_SIZE);
        availableShips = createAvailableShips();
        
        java.util.Random random = new java.util.Random();
        List<Ship> shipsToPlace = new ArrayList<>(availableShips);
        
        for (Ship ship : shipsToPlace) {
            boolean placed = false;
            int attempts = 0;
            
            while (!placed && attempts < 100) {
                int row = random.nextInt(Constants.BOARD_SIZE);
                int col = random.nextInt(Constants.BOARD_SIZE);
                boolean vertical = random.nextBoolean();
                
                Ship newShip = new Ship(ship.getName(), ship.getSize());
                if (playerBoard.placeShip(newShip, new Position(row, col), vertical)) {
                    placed = true;
                }
                
                attempts++;
            }
        }
        
        // Update the UI
        boardPanel.setBoard(playerBoard);
        availableShips.clear();
        updateShipTypeCombo();
        updateStartButtonState();
        
        mainWindow.updateStatusMessage("Ships positioned randomly. Ready to start!");
    }
    
    /**
     * Updates the state of the start button.
     */
    private void updateStartButtonState() {
        startGameButton.setEnabled(availableShips.isEmpty());
    }
    
    /**
     * Starts the game - either single player or multiplayer based on network connection.
     */
    private void startGame() {
        // Check if we're connected to a network (multiplayer mode)
        if (mainWindow.getNetworkManager().isConnected()) {
            // Multiplayer mode
            mainWindow.showMultiplayerGamePanel();
        } else {
            // Single player mode
            mainWindow.showGamePanel();
        }
    }
    
    /**
     * Returns the player's board.
     */
    public Board getPlayerBoard() {
        return playerBoard;
    }
} 