package com.batalhanaval.network;

import com.batalhanaval.ui.MultiplayerGamePanel;
import java.util.function.Consumer;

/**
 * Manages the coordination between NetworkManager and MultiplayerGamePanel.
 * Handles the integration of network communication with the game UI.
 */
public class NetworkGameManager {
    
    private NetworkManager networkManager;
    private MultiplayerGamePanel gamePanel;
    private NetworkMessageHandler messageHandler;
    private Consumer<String> statusUpdater;
    
    private boolean bothPlayersReady = false;
    private boolean opponentReady = false;
    private boolean playerReady = false;
    
    /**
     * Constructor for NetworkGameManager.
     * @param networkManager Network manager for communication
     * @param statusUpdater Consumer for status updates
     */
    public NetworkGameManager(NetworkManager networkManager, Consumer<String> statusUpdater) {
        this.networkManager = networkManager;
        this.statusUpdater = statusUpdater;
        
        // Create message handler
        this.messageHandler = new NetworkMessageHandler(statusUpdater);
        
        // Set up message processing
        setupMessageProcessing();
    }
    
    /**
     * Sets the multiplayer game panel.
     * @param gamePanel Multiplayer game panel
     */
    public void setGamePanel(MultiplayerGamePanel gamePanel) {
        this.gamePanel = gamePanel;
        
        // Connect the game panel's message handler with network processing
        if (gamePanel != null) {
            NetworkMessageHandler gamePanelHandler = gamePanel.getMessageHandler();
            
            // Set up listeners to forward messages to game panel
            messageHandler.setGameMessageListener(new NetworkMessageHandler.GameMessageListener() {
                @Override
                public void onReadyToStart() {
                    opponentReady = true;
                    checkBothPlayersReady();
                    gamePanelHandler.processTcpMessage(MessageProtocol.READY_TO_START);
                }
                
                @Override
                public void onGameStart(boolean isFirstPlayer) {
                    String message = MessageProtocol.createGameStartMessage(isFirstPlayer);
                    gamePanelHandler.processTcpMessage(message);
                }
                
                @Override
                public void onAttackReceived(int row, int col) {
                    String message = MessageProtocol.createAttackMessage(row, col);
                    gamePanelHandler.processTcpMessage(message);
                }
                
                @Override
                public void onAttackResult(String result, int row, int col) {
                    String message = MessageProtocol.createAttackResultMessage(result, row, col);
                    gamePanelHandler.processTcpMessage(message);
                }
                
                @Override
                public void onTurnEnd() {
                    gamePanelHandler.processTcpMessage(MessageProtocol.TURN_END);
                }
                
                @Override
                public void onGameOver(boolean isWinner) {
                    String message = MessageProtocol.createGameOverMessage(isWinner);
                    gamePanelHandler.processTcpMessage(message);
                }
                
                @Override
                public void onOpponentDisconnect() {
                    gamePanelHandler.processTcpMessage(MessageProtocol.DISCONNECT);
                }
                
                @Override
                public void onOpponentSurrender() {
                    gamePanelHandler.processTcpMessage(MessageProtocol.SURRENDER);
                }
            });
            
            messageHandler.setHoverMessageListener(new NetworkMessageHandler.HoverMessageListener() {
                @Override
                public void onHoverReceived(com.batalhanaval.core.Position position) {
                    String message;
                    if (position != null) {
                        message = MessageProtocol.createHoverMessage(position.getRow(), position.getCol());
                    } else {
                        message = MessageProtocol.createHoverMessage(-1, -1);
                    }
                    gamePanelHandler.processUdpMessage(message);
                }
                
                @Override
                public void onPingReceived() {
                    gamePanelHandler.processUdpMessage(MessageProtocol.PING);
                }
            });
        }
    }
    
    /**
     * Sets up message processing from NetworkManager.
     */
    private void setupMessageProcessing() {
        // This would ideally be integrated with NetworkManager's existing TCP/UDP listeners
        // For now, we'll provide methods to manually process messages
    }
    
    /**
     * Processes a TCP message received from the network.
     * @param message TCP message
     */
    public void processTcpMessage(String message) {
        messageHandler.processTcpMessage(message);
    }
    
    /**
     * Processes a UDP message received from the network.
     * @param message UDP message
     */
    public void processUdpMessage(String message) {
        messageHandler.processUdpMessage(message);
    }
    
    /**
     * Notifies that the local player is ready.
     */
    public void setPlayerReady() {
        playerReady = true;
        messageHandler.sendReadyToStart(networkManager);
        checkBothPlayersReady();
    }
    
    /**
     * Checks if both players are ready and starts the game.
     */
    private void checkBothPlayersReady() {
        if (playerReady && opponentReady && !bothPlayersReady) {
            bothPlayersReady = true;
            startGame();
        }
    }
    
    /**
     * Starts the multiplayer game.
     */
    private void startGame() {
        statusUpdater.accept("Both players ready! Starting game...");
        
        // Determine who goes first (server decides)
        boolean isFirstPlayer = networkManager.isServer();
        
        // Send game start message to opponent
        String gameStartMessage = MessageProtocol.createGameStartMessage(!isFirstPlayer);
        networkManager.sendTcpMessage(gameStartMessage);
        
        // Start the game locally
        if (gamePanel != null) {
            String localGameStartMessage = MessageProtocol.createGameStartMessage(isFirstPlayer);
            gamePanel.getMessageHandler().processTcpMessage(localGameStartMessage);
        }
    }
    
    /**
     * Sends an attack to the opponent.
     * @param row Row coordinate
     * @param col Column coordinate
     * @return True if sent successfully
     */
    public boolean sendAttack(int row, int col) {
        return messageHandler.sendAttack(networkManager, row, col);
    }
    
    /**
     * Sends an attack result to the opponent.
     * @param result Attack result
     * @param row Row coordinate
     * @param col Column coordinate
     * @return True if sent successfully
     */
    public boolean sendAttackResult(String result, int row, int col) {
        return messageHandler.sendAttackResult(networkManager, result, row, col);
    }
    
    /**
     * Sends a hover position to the opponent.
     * @param position Hover position or null to clear
     * @return True if sent successfully
     */
    public boolean sendHover(com.batalhanaval.core.Position position) {
        return messageHandler.sendHover(networkManager, position);
    }
    
    /**
     * Sends a surrender message to the opponent.
     * @return True if sent successfully
     */
    public boolean sendSurrender() {
        return messageHandler.sendSurrender(networkManager);
    }
    
    /**
     * Sends a game over message to the opponent.
     * @param isWinner True if this player won
     * @return True if sent successfully
     */
    public boolean sendGameOver(boolean isWinner) {
        return messageHandler.sendGameOver(networkManager, isWinner);
    }
    
    /**
     * Sends a ping to test connection.
     * @return True if sent successfully
     */
    public boolean sendPing() {
        return messageHandler.sendPing(networkManager);
    }
    
    /**
     * Gets the network manager.
     * @return Network manager
     */
    public NetworkManager getNetworkManager() {
        return networkManager;
    }
    
    /**
     * Gets the message handler.
     * @return Message handler
     */
    public NetworkMessageHandler getMessageHandler() {
        return messageHandler;
    }
    
    /**
     * Checks if both players are ready.
     * @return True if both players are ready
     */
    public boolean areBothPlayersReady() {
        return bothPlayersReady;
    }
    
    /**
     * Checks if the opponent is ready.
     * @return True if opponent is ready
     */
    public boolean isOpponentReady() {
        return opponentReady;
    }
    
    /**
     * Checks if the local player is ready.
     * @return True if local player is ready
     */
    public boolean isPlayerReady() {
        return playerReady;
    }
    
    /**
     * Resets the ready states (for new game).
     */
    public void resetReadyStates() {
        bothPlayersReady = false;
        opponentReady = false;
        playerReady = false;
    }
    
    /**
     * Disconnects from the network game.
     */
    public void disconnect() {
        if (networkManager != null) {
            networkManager.sendTcpMessage(MessageProtocol.DISCONNECT);
            networkManager.stopNetwork();
        }
    }
} 