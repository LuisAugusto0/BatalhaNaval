package com.batalhanaval.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

import com.batalhanaval.Constants;
import com.batalhanaval.core.Board;
import com.batalhanaval.core.Position;
import com.batalhanaval.core.Ship;
import com.batalhanaval.core.GameState;
import com.batalhanaval.core.GameInfoManager;
import com.batalhanaval.core.HoverManager;
import com.batalhanaval.network.NetworkManager;
import com.batalhanaval.network.NetworkMessageHandler;
import com.batalhanaval.network.MessageProtocol;

/**
 * Panel for multiplayer game view with network communication.
 * Handles real-time multiplayer battles without AI.
 */
public class MultiplayerGamePanel extends JPanel implements 
    NetworkMessageHandler.GameMessageListener, 
    NetworkMessageHandler.HoverMessageListener {
    
    private MainWindow mainWindow;
    private Board playerBoard;
    private Board opponentBoard;
    private boolean isPlayerTurn;
    private boolean gameStarted = false;
    
    private BoardPanel playerBoardPanel;
    private BoardPanel opponentBoardPanel;
    private JPanel controlPanel;
    private JPanel scoreboardPanel;
    private JButton surrenderButton;
    private JLabel statusLabel;
    
    // Scoreboard components
    private JLabel playerScoreLabel;
    private JLabel opponentScoreLabel;
    private JLabel playerShipsLabel;
    private JLabel opponentShipsLabel;
    
    // Game info management
    private GameState gameState;
    private GameInfoManager gameInfoManager;
    private HoverManager hoverManager;
    
    // Network components
    private NetworkManager networkManager;
    private NetworkMessageHandler messageHandler;
    
    // Attack tracking for opponent board
    private java.util.Map<Position, String> attackResults;
    private java.util.List<Ship> opponentShips;
    
    /**
     * Constructor for the multiplayer game panel.
     * @param mainWindow Reference to the main window
     * @param networkManager Network manager for communication
     */
    public MultiplayerGamePanel(MainWindow mainWindow, NetworkManager networkManager) {
        this.mainWindow = mainWindow;
        this.networkManager = networkManager;
        this.isPlayerTurn = false; // Will be determined by server
        
        // Initialize empty opponent board (no ships until game starts)
        this.opponentBoard = new Board(Constants.BOARD_SIZE);
        
        // Initialize game state and info manager
        this.gameState = new GameState(Constants.BOARD_SIZE);
        this.gameInfoManager = new GameInfoManager(gameState);
        this.hoverManager = new HoverManager(gameInfoManager);
        
        // Initialize network message handler
        this.messageHandler = new NetworkMessageHandler(mainWindow::updateStatusMessage);
        this.messageHandler.setGameMessageListener(this);
        this.messageHandler.setHoverMessageListener(this);
        
        // Initialize attack results tracking
        this.attackResults = new java.util.HashMap<>();
        
        // Initialize opponent ships list (we'll create placeholder ships)
        this.opponentShips = new java.util.ArrayList<>();
        createPlaceholderOpponentShips();
        
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(Color.BLACK);
        
        // Initialize the board panels, control panel, and scoreboard
        setupBoardPanels();
        setupControlPanel();
        setupScoreboardPanel();
        
        // Add panels to the main layout
        JPanel boardsPanel = new JPanel(new GridLayout(1, 2, 30, 0));
        boardsPanel.setBackground(Color.BLACK);
        boardsPanel.add(playerBoardPanel);
        boardsPanel.add(opponentBoardPanel);
        
        // Create right panel with control and scoreboard
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(Color.BLACK);
        rightPanel.add(scoreboardPanel, BorderLayout.NORTH);
        rightPanel.add(controlPanel, BorderLayout.CENTER);
        
        add(boardsPanel, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.EAST);
        
        // Setup hover manager with board panels
        hoverManager.setPlayerBoardPanel(playerBoardPanel);
        hoverManager.setOpponentBoardPanel(opponentBoardPanel);
        
        // Connect hover manager to network game manager
        hoverManager.setNetworkGameManager(mainWindow.getNetworkGameManager());
        
        // Initial status
        mainWindow.updateStatusMessage("Waiting for opponent to be ready...");
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
        
        // Set the placeholder ships for status display
        opponentBoardPanel.setPlaceholderShips(opponentShips);
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
        JLabel titleLabel = new JLabel("Online Battle");
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(Color.WHITE);
        controlPanel.add(titleLabel);
        controlPanel.add(Box.createVerticalStrut(20));
        
        // Status label
        statusLabel = new JLabel("Waiting...");
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        statusLabel.setForeground(Color.YELLOW);
        controlPanel.add(statusLabel);
        controlPanel.add(Box.createVerticalStrut(10));
        
        // Surrender button
        surrenderButton = new JButton("Surrender");
        surrenderButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        surrenderButton.addActionListener(this::handleSurrenderClick);
        surrenderButton.setBackground(new Color(50, 50, 50));
        surrenderButton.setForeground(Color.WHITE);
        surrenderButton.setEnabled(false); // Disabled until game starts
        controlPanel.add(surrenderButton);
        
        // Instructions
        controlPanel.add(Box.createVerticalStrut(30));
        JLabel instructionsLabel = new JLabel("<html><body style='width: 140px; color: white'>Click on the opponent's board to attack when it's your turn.</body></html>");
        instructionsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        controlPanel.add(instructionsLabel);
    }
    
    /**
     * Sets up the scoreboard panel with real-time score tracking.
     */
    private void setupScoreboardPanel() {
        scoreboardPanel = new JPanel();
        scoreboardPanel.setLayout(new BoxLayout(scoreboardPanel, BoxLayout.Y_AXIS));
        scoreboardPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        scoreboardPanel.setPreferredSize(new Dimension(180, 200));
        scoreboardPanel.setBackground(Color.BLACK);
        
        // Title
        JLabel titleLabel = new JLabel("SCOREBOARD");
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(new Color(255, 215, 0)); // Gold color
        scoreboardPanel.add(titleLabel);
        scoreboardPanel.add(Box.createVerticalStrut(15));
        
        // Player score section
        JLabel playerLabel = new JLabel("YOU");
        playerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        playerLabel.setFont(new Font("Arial", Font.BOLD, 14));
        playerLabel.setForeground(new Color(100, 255, 100)); // Light green
        scoreboardPanel.add(playerLabel);
        
        playerScoreLabel = new JLabel("Score: 0");
        playerScoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        playerScoreLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        playerScoreLabel.setForeground(Color.WHITE);
        scoreboardPanel.add(playerScoreLabel);
        
        playerShipsLabel = new JLabel("Ships: 5/5");
        playerShipsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        playerShipsLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        playerShipsLabel.setForeground(Color.WHITE);
        scoreboardPanel.add(playerShipsLabel);
        
        scoreboardPanel.add(Box.createVerticalStrut(20));
        
        // Opponent score section
        JLabel opponentLabel = new JLabel("OPPONENT");
        opponentLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        opponentLabel.setFont(new Font("Arial", Font.BOLD, 14));
        opponentLabel.setForeground(new Color(255, 100, 100)); // Light red
        scoreboardPanel.add(opponentLabel);
        
        opponentScoreLabel = new JLabel("Score: 0");
        opponentScoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        opponentScoreLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        opponentScoreLabel.setForeground(Color.WHITE);
        scoreboardPanel.add(opponentScoreLabel);
        
        opponentShipsLabel = new JLabel("Ships: ?/?");
        opponentShipsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        opponentShipsLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        opponentShipsLabel.setForeground(Color.WHITE);
        scoreboardPanel.add(opponentShipsLabel);
        
        // Initial score update
        updateScoreboard();
    }
    
    /**
     * Updates the scoreboard with current game information.
     */
    private void updateScoreboard() {
        if (gameInfoManager == null || playerBoard == null) {
            return;
        }
        
        // Calculate scores using our scoring system
        int playerScore = calculatePlayerScore();
        int opponentScore = calculateOpponentScore();
        
        // Count remaining ships
        int playerShipsAlive = countAliveShips(playerBoard);
        int playerTotalShips = playerBoard.getShips().size();
        
        // Update labels
        playerScoreLabel.setText("Score: " + playerScore);
        opponentScoreLabel.setText("Score: " + opponentScore);
        playerShipsLabel.setText("Ships: " + playerShipsAlive + "/" + playerTotalShips);
        opponentShipsLabel.setText("Ships: ?/?"); // We don't know opponent's ship count
        
        // Add visual indicators for leading player
        if (playerScore > opponentScore) {
            playerScoreLabel.setForeground(new Color(255, 215, 0)); // Gold
            opponentScoreLabel.setForeground(Color.WHITE);
        } else if (opponentScore > playerScore) {
            opponentScoreLabel.setForeground(new Color(255, 215, 0)); // Gold
            playerScoreLabel.setForeground(Color.WHITE);
        } else {
            playerScoreLabel.setForeground(Color.WHITE);
            opponentScoreLabel.setForeground(Color.WHITE);
        }
    }
    
    /**
     * Calculates player's score based on hits on opponent's board.
     */
    private int calculatePlayerScore() {
        // For multiplayer, we calculate based on attack results received
        int score = 0;
        int sunkShips = 0;
        
        for (String result : attackResults.values()) {
            switch (result) {
                case MessageProtocol.HIT:
                    score += 1; // 1 point per hit
                    break;
                case MessageProtocol.SUNK:
                    score += 1; // 1 point for the hit
                    sunkShips++;
                    break;
                // MISS doesn't add points
            }
        }
        
        // Add bonus points for sunk ships
        score += sunkShips * 5; // 5 bonus points per sunk ship
        
        // Check if we won (all 5 ships sunk)
        if (sunkShips >= 5) {
            score += 50; // 50 bonus points for victory
        }
        
        return score;
    }
    
    /**
     * Calculates opponent's score based on hits on player's board.
     */
    private int calculateOpponentScore() {
        if (playerBoard == null) return 0;
        
        int score = 0;
        for (Ship ship : playerBoard.getShips()) {
            score += ship.getHitCount(); // 1 point per hit
            if (ship.isSunk()) {
                score += 5; // 5 bonus points for sinking a ship
            }
        }
        
        if (playerBoard.areAllShipsSunk()) {
            score += 50; // 50 bonus points for victory
        }
        
        return score;
    }
    
    /**
     * Counts alive ships on a board.
     */
    private int countAliveShips(Board board) {
        if (board == null) return 0;
        
        int count = 0;
        for (Ship ship : board.getShips()) {
            if (!ship.isSunk()) {
                count++;
            }
        }
        return count;
    }
    
    /**
     * Handles a click on the opponent's board as an attack.
     */
    private void handleAttackClick(int row, int col) {
        if (!gameStarted) {
            mainWindow.updateStatusMessage("Game hasn't started yet!");
            return;
        }
        
        if (!isPlayerTurn) {
            mainWindow.updateStatusMessage("Wait for your turn!");
            return;
        }
        
        Position pos = new Position(row, col);
        
        // Check if the position has already been attacked
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
        
        // Send attack to opponent via network
        boolean sent = messageHandler.sendAttack(networkManager, row, col);
        if (sent) {
            mainWindow.updateStatusMessage("Attacking position " + pos + "...");
            setPlayerTurn(false); // Disable further attacks until response
        } else {
            mainWindow.updateStatusMessage("Failed to send attack. Check connection.");
        }
    }
    
    /**
     * Sets the player's turn and updates UI elements accordingly.
     */
    private void setPlayerTurn(boolean isPlayerTurn) {
        this.isPlayerTurn = isPlayerTurn;
        surrenderButton.setEnabled(isPlayerTurn && gameStarted);
        
        // Update status label
        if (gameStarted) {
            if (isPlayerTurn) {
                statusLabel.setText("Your Turn");
                statusLabel.setForeground(Color.GREEN);
            } else {
                statusLabel.setText("Opponent's Turn");
                statusLabel.setForeground(Color.ORANGE);
            }
        }
    }
    
    /**
     * Handles the surrender button click.
     */
    private void handleSurrenderClick(ActionEvent e) {
        if (!isPlayerTurn || !gameStarted) {
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
            messageHandler.sendSurrender(networkManager);
            handleGameOver(false, "You surrendered.");
        }
    }
    
    /**
     * Handles the end of the game.
     * @param playerWon True if the player won, false if the opponent won
     * @param reason Reason for game ending
     */
    private void handleGameOver(boolean playerWon, String reason) {
        gameStarted = false;
        setPlayerTurn(false);
        
        String message = playerWon ? 
            "Victory! " + reason : 
            "Defeat! " + reason;
        
        mainWindow.updateStatusMessage(message);
        statusLabel.setText("Game Over");
        statusLabel.setForeground(playerWon ? Color.GREEN : Color.RED);
        
        // Update ship status displays one final time
        playerBoardPanel.updateShipStatusPanel();
        opponentBoardPanel.updateShipStatusPanel();
        updateScoreboard();
        
        // Show dialog with result
        JOptionPane.showMessageDialog(this, 
            message, 
            "Game Over", 
            playerWon ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.WARNING_MESSAGE);
    }
    
    /**
     * Sets the player's board and sends ready signal.
     * @param board The configured player's board
     */
    public void setPlayerBoard(Board board) {
        this.playerBoard = board;
        this.playerBoardPanel.setBoard(board);
        
        // Update scoreboard when player board is set
        updateScoreboard();
        
        // Send ready signal to opponent via NetworkGameManager
        mainWindow.getNetworkGameManager().setPlayerReady();
        mainWindow.updateStatusMessage("Ready signal sent. Waiting for opponent...");
    }
    
    // ===== NetworkMessageHandler.GameMessageListener Implementation =====
    
    @Override
    public void onReadyToStart() {
        mainWindow.updateStatusMessage("Opponent is ready! Starting game...");
        // Game will start when server sends GAME_START message
    }
    
    @Override
    public void onGameStart(boolean isFirstPlayer) {
        this.gameStarted = true;
        setPlayerTurn(isFirstPlayer);
        
        String message = isFirstPlayer ? 
            "Game started! You go first." : 
            "Game started! Opponent goes first.";
        mainWindow.updateStatusMessage(message);
    }
    
    @Override
    public void onAttackReceived(int row, int col) {
        Position pos = new Position(row, col);
        
        // Process the attack on the player's board
        String result = playerBoard.processAttack(pos);
        
        // Send result back to opponent
        String networkResult;
        switch (result) {
            case Constants.ATTACK_HIT:
                networkResult = MessageProtocol.HIT;
                break;
            case Constants.ATTACK_MISS:
                networkResult = MessageProtocol.MISS;
                break;
            case Constants.ATTACK_SUNK:
                networkResult = MessageProtocol.SUNK;
                break;
            default:
                networkResult = MessageProtocol.MISS; // Fallback
                break;
        }
        
        messageHandler.sendAttackResult(networkManager, networkResult, row, col);
        
        // Update UI
        playerBoardPanel.updateShipStatusPanel();
        playerBoardPanel.repaint();
        updateScoreboard();
        
        // Update status message
        String statusMessage = "Opponent attacked " + pos + " - ";
        switch (result) {
            case Constants.ATTACK_HIT:
                statusMessage += "Hit!";
                break;
            case Constants.ATTACK_MISS:
                statusMessage += "Miss!";
                break;
            case Constants.ATTACK_SUNK:
                statusMessage += "Ship sunk!";
                break;
        }
        mainWindow.updateStatusMessage(statusMessage);
        
        // Check if game is over
        if (playerBoard.areAllShipsSunk()) {
            messageHandler.sendGameOver(networkManager, true); // Opponent won
            handleGameOver(false, "All your ships were sunk!");
        } else {
            // It's now the player's turn
            setPlayerTurn(true);
        }
    }
    
    @Override
    public void onAttackResult(String result, int row, int col) {
        Position pos = new Position(row, col);
        
        // Store the attack result for visual feedback
        attackResults.put(pos, result);
        
        // Process the attack result on the opponent board for visual feedback
        if (result.equals(MessageProtocol.HIT)) {
            // Create a temporary single-cell ship for visual feedback only
            Ship tempShip = new Ship("TempHit", 1);
            try {
                opponentBoard.placeShip(tempShip, pos, false);
                opponentBoard.processAttack(pos); // This will mark as HIT and add to attacked positions
            } catch (Exception e) {
                // Fallback: manually add to attacked positions
                opponentBoard.getAttackedPositions().add(pos);
                setOpponentBoardCellState(pos, Constants.HIT);
            }
        } else if (result.equals(MessageProtocol.SUNK)) {
            // Mark one of our placeholder ships as sunk (for status display)
            markOpponentShipAsSunk();
            
            // Create a temporary single-cell ship for visual feedback
            Ship tempShip = new Ship("TempSunk", 1);
            try {
                opponentBoard.placeShip(tempShip, pos, false);
                opponentBoard.processAttack(pos); // This will mark as HIT
                // Then manually change to SUNK
                setOpponentBoardCellState(pos, Constants.SUNK);
            } catch (Exception e) {
                // Fallback: manually add to attacked positions
                opponentBoard.getAttackedPositions().add(pos);
                setOpponentBoardCellState(pos, Constants.SUNK);
            }
        } else if (result.equals(MessageProtocol.MISS)) {
            // For misses, just process the attack - it will mark as MISS correctly
            opponentBoard.processAttack(pos);
        }
        
        // Update UI based on result
        opponentBoardPanel.updateShipStatusPanel();
        opponentBoardPanel.repaint();
        updateScoreboard();
        
        // Update status message
        String statusMessage = "Your attack on " + pos + " - ";
        switch (result) {
            case MessageProtocol.HIT:
                statusMessage += "Hit!";
                break;
            case MessageProtocol.MISS:
                statusMessage += "Miss!";
                break;
            case MessageProtocol.SUNK:
                statusMessage += "Ship sunk!";
                break;
        }
        mainWindow.updateStatusMessage(statusMessage);
        
        // Check if we won (count sunk ships from our attack results)
        if (result.equals(MessageProtocol.SUNK)) {
            int sunkShips = 0;
            for (String attackResult : attackResults.values()) {
                if (attackResult.equals(MessageProtocol.SUNK)) {
                    sunkShips++;
                }
            }
            
            // Assume 5 ships total (standard battleship)
            if (sunkShips >= 5) {
                messageHandler.sendGameOver(networkManager, false); // We won
                handleGameOver(true, "You sank all opponent ships!");
                return;
            }
        }
        
        // Always switch turns after each attack, regardless of result (hit, miss, or sunk)
        setPlayerTurn(false);
    }
    
    @Override
    public void onTurnEnd() {
        setPlayerTurn(false);
    }
    
    @Override
    public void onGameOver(boolean isWinner) {
        String reason = isWinner ? "You sank all opponent ships!" : "Opponent sank all your ships!";
        handleGameOver(isWinner, reason);
    }
    
    @Override
    public void onOpponentDisconnect() {
        handleGameOver(true, "Opponent disconnected.");
    }
    
    @Override
    public void onOpponentSurrender() {
        handleGameOver(true, "Opponent surrendered.");
    }
    
    // ===== NetworkMessageHandler.HoverMessageListener Implementation =====
    
    @Override
    public void onHoverReceived(Position position) {
        // Update opponent hover on player's board
        hoverManager.updateOpponentHover(position);
    }
    
    @Override
    public void onPingReceived() {
        // Respond to ping with pong
        messageHandler.sendPong(networkManager);
    }
    
    /**
     * Creates placeholder ships for the opponent (we don't know their real positions).
     */
    private void createPlaceholderOpponentShips() {
        opponentShips.clear();
        
        // Create standard battleship fleet (same as Constants)
        opponentShips.add(new Ship("Carrier", Constants.CARRIER_SIZE));
        opponentShips.add(new Ship("Battleship", Constants.BATTLESHIP_SIZE));
        opponentShips.add(new Ship("Cruiser", Constants.CRUISER_SIZE));
        opponentShips.add(new Ship("Submarine", Constants.SUBMARINE_SIZE));
        opponentShips.add(new Ship("Destroyer", Constants.DESTROYER_SIZE));
    }
    
    /**
     * Marks one of the opponent's placeholder ships as sunk.
     */
    private void markOpponentShipAsSunk() {
        // Find the first alive ship and mark it as sunk
        for (Ship ship : opponentShips) {
            if (!ship.isSunk()) {
                ship.forceSink();
                break; // Sink only one ship per SUNK message
            }
        }
    }
    
    /**
     * Sets the cell state on the opponent board (using reflection to access private grid).
     */
    private void setOpponentBoardCellState(Position pos, char state) {
        try {
            // Access the private grid field using reflection
            java.lang.reflect.Field gridField = Board.class.getDeclaredField("grid");
            gridField.setAccessible(true);
            char[][] grid = (char[][]) gridField.get(opponentBoard);
            grid[pos.getRow()][pos.getCol()] = state;
        } catch (Exception e) {
            System.out.println("Could not set cell state: " + e.getMessage());
        }
    }
    
    /**
     * Gets the message handler for external use.
     * @return Network message handler
     */
    public NetworkMessageHandler getMessageHandler() {
        return messageHandler;
    }
} 