package com.batalhanaval.core;

import com.batalhanaval.ui.BoardPanel;

/**
 * Manager class to coordinate hover tracking between BoardPanels and GameInfoManager.
 * This class handles the communication between UI components and the game state.
 */
public class HoverManager {
    
    private GameInfoManager gameInfoManager;
    private BoardPanel playerBoardPanel;
    private BoardPanel opponentBoardPanel;
    
    /**
     * Constructor that initializes the hover manager.
     * @param gameInfoManager The game info manager to update with hover information
     */
    public HoverManager(GameInfoManager gameInfoManager) {
        this.gameInfoManager = gameInfoManager;
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
                
                // This is where you would send UDP message to opponent
                sendHoverToOpponent(hoverPosition);
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
     * Placeholder method for sending hover data to opponent via UDP.
     * This should be implemented to actually send the data over the network.
     * @param hoverPosition The position to send to the opponent
     */
    private void sendHoverToOpponent(Position hoverPosition) {
        // TODO: Implement UDP sending logic here
        String hoverData = gameInfoManager.formatHoverForTransmission(hoverPosition);
        
        // For now, just print what would be sent
        System.out.println("UDP Send: Hover at " + hoverData);
        
        // Example of how this would be implemented:
        // networkManager.sendUDPMessage("HOVER:" + hoverData);
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