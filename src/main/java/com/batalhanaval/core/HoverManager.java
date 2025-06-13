package com.batalhanaval.core;

import com.batalhanaval.ui.BoardPanel;
import com.batalhanaval.network.NetworkGameManager;

/**
 * Manager class to coordinate hover tracking between BoardPanels and GameInfoManager.
 * This class handles the communication between UI components and the game state.
 */
public class HoverManager {
    
    private GameInfoManager gameInfoManager;
    private BoardPanel playerBoardPanel;
    private BoardPanel opponentBoardPanel;
    private NetworkGameManager networkGameManager;
    
    /**
     * Constructor that initializes the hover manager.
     * @param gameInfoManager The game info manager to update with hover information
     */
    public HoverManager(GameInfoManager gameInfoManager) {
        this.gameInfoManager = gameInfoManager;
    }
    
    /**
     * Sets the network game manager for sending hover data.
     * @param networkGameManager The network game manager
     */
    public void setNetworkGameManager(NetworkGameManager networkGameManager) {
        this.networkGameManager = networkGameManager;
    }
    
    /**
     * Connects the player's board panel to the hover manager.
     * @param playerBoardPanel The player's board panel
     */
    public void setPlayerBoardPanel(BoardPanel playerBoardPanel) {
        this.playerBoardPanel = playerBoardPanel;
        
        if (playerBoardPanel != null) {
            // Set up hover handler for player's board
            playerBoardPanel.setHoverHandler((row, col) -> {
                Position hoverPosition = new Position(row, col);
                gameInfoManager.setPlayerBoardHover(hoverPosition);
            });
        }
    }
    
    /**
     * Connects the opponent's board panel to the hover manager.
     * @param opponentBoardPanel The opponent's board panel
     */
    public void setOpponentBoardPanel(BoardPanel opponentBoardPanel) {
        this.opponentBoardPanel = opponentBoardPanel;
        
        if (opponentBoardPanel != null) {
            // Set up hover handler for opponent's board
            opponentBoardPanel.setHoverHandler((row, col) -> {
                Position hoverPosition = new Position(row, col);
                gameInfoManager.setOpponentBoardHover(hoverPosition);
                
                // Send hover position to opponent via UDP
                sendHoverToOpponent(hoverPosition);
            });
            
            // Set up hover clear handler for when mouse exits opponent's board
            opponentBoardPanel.setHoverClearHandler(() -> {
                gameInfoManager.setOpponentBoardHover(null);
                sendHoverToOpponent(null);
            });
        }
    }
    
    /**
     * Updates the opponent's hover position on the player's board.
     * This method should be called when receiving hover data via UDP.
     * @param opponentHoverPosition The position where the opponent is hovering
     */
    public void updateOpponentHover(Position opponentHoverPosition) {
        if (playerBoardPanel != null) {
            playerBoardPanel.setOpponentHoverPosition(opponentHoverPosition);
        }
    }
    
    /**
     * Clears the opponent's hover position.
     * This method should be called when the opponent stops hovering.
     */
    public void clearOpponentHover() {
        updateOpponentHover(null);
    }
    
    /**
     * Gets the current hover position on the opponent's board.
     * This is the position that should be transmitted via UDP.
     * @return The hover position or null if not hovering
     */
    public Position getCurrentOpponentBoardHover() {
        return gameInfoManager.getOpponentBoardHover();
    }
    
    /**
     * Gets the current hover position on the player's board.
     * @return The hover position or null if not hovering
     */
    public Position getCurrentPlayerBoardHover() {
        return gameInfoManager.getPlayerBoardHover();
    }
    
    /**
     * Formats the current opponent board hover for UDP transmission.
     * @return Formatted hover string for network transmission
     */
    public String getHoverForTransmission() {
        Position hover = gameInfoManager.getOpponentBoardHover();
        return gameInfoManager.formatHoverForTransmission(hover);
    }
    
    /**
     * Processes hover data received from UDP transmission.
     * @param hoverData The hover data string received via UDP
     */
    public void processReceivedHover(String hoverData) {
        Position receivedHover = gameInfoManager.parseHoverFromTransmission(hoverData);
        updateOpponentHover(receivedHover);
    }
    
    /**
     * Sends hover data to opponent via UDP.
     * @param hoverPosition The position to send to the opponent (null to clear hover)
     */
    private void sendHoverToOpponent(Position hoverPosition) {
        if (networkGameManager != null) {
            // Send hover via UDP using the network game manager
            networkGameManager.sendHover(hoverPosition);
        } else {
            // Fallback: just print for debugging if network manager not set
            String hoverData = gameInfoManager.formatHoverForTransmission(hoverPosition);
            System.out.println("UDP Send: Hover at " + hoverData + " (NetworkGameManager not set)");
        }
    }
    
    /**
     * Gets hover information in a format suitable for debugging.
     * @return String with current hover information
     */
    public String getHoverDebugInfo() {
        Position playerHover = gameInfoManager.getPlayerBoardHover();
        Position opponentHover = gameInfoManager.getOpponentBoardHover();
        
        StringBuilder debug = new StringBuilder();
        debug.append("Hover Debug Info:\n");
        debug.append("  Player Board Hover: ");
        if (playerHover != null) {
            debug.append("(").append(playerHover.getRow()).append(", ").append(playerHover.getCol()).append(")");
        } else {
            debug.append("None");
        }
        debug.append("\n");
        
        debug.append("  Opponent Board Hover: ");
        if (opponentHover != null) {
            debug.append("(").append(opponentHover.getRow()).append(", ").append(opponentHover.getCol()).append(")");
        } else {
            debug.append("None");
        }
        debug.append("\n");
        
        debug.append("  UDP Format: ").append(getHoverForTransmission());
        
        return debug.toString();
    }
    
    /**
     * Checks if the player is currently hovering over any board.
     * @return true if hovering over any board
     */
    public boolean isHoveringAnyBoard() {
        return gameInfoManager.getPlayerBoardHover() != null || 
               gameInfoManager.getOpponentBoardHover() != null;
    }
    
    /**
     * Checks if the player is hovering over the opponent's board.
     * @return true if hovering over opponent's board
     */
    public boolean isHoveringOpponentBoard() {
        return gameInfoManager.getOpponentBoardHover() != null;
    }
    
    /**
     * Checks if the player is hovering over their own board.
     * @return true if hovering over player's board
     */
    public boolean isHoveringPlayerBoard() {
        return gameInfoManager.getPlayerBoardHover() != null;
    }
} 