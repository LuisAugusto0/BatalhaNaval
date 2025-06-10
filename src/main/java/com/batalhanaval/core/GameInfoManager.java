package com.batalhanaval.core;

import java.awt.Point;
import java.awt.MouseInfo;
import java.awt.PointerInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class to manage and retrieve game information including scores,
 * board hover positions, and ship positions for both teams.
 */
public class GameInfoManager {
    
    private GameState gameState;
    private Position playerBoardHover;
    private Position opponentBoardHover;
    
    /**
     * Constructor that takes a GameState reference.
     * @param gameState Current game state
     */
    public GameInfoManager(GameState gameState) {
        this.gameState = gameState;
        this.playerBoardHover = null;
        this.opponentBoardHover = null;
    }
    
    /**
     * Gets the current score/points for both players.
     * Score is calculated based on successful hits and sunk ships.
     * @return Map with "player" and "opponent" scores
     */
    public Map<String, Integer> getScores() {
        Map<String, Integer> scores = new HashMap<>();
        
        int playerScore = calculateScore(gameState.getOpponentBoard());
        int opponentScore = calculateScore(gameState.getPlayerBoard());
        
        scores.put("player", playerScore);
        scores.put("opponent", opponentScore);
        
        return scores;
    }
    
    /**
     * Calculates score based on hits and sunk ships on a board.
     * @param board Board to calculate score from
     * @return Calculated score
     */
    private int calculateScore(Board board) {
        int score = 0;
        
        // Score calculation:
        // - 1 point for each hit
        // - 5 bonus points for each sunk ship
        // - 50 bonus points if all ships are sunk (victory)
        
        for (Ship ship : board.getShips()) {
            // Add points for hits on this ship
            int hits = ship.getHitCount();
            score += hits;
            
            // Bonus for sinking a ship
            if (ship.isSunk()) {
                score += 5;
            }
        }
        
        // Victory bonus
        if (board.areAllShipsSunk()) {
            score += 50;
        }
        
        return score;
    }
    
    /**
     * Sets the hover position on the player's board.
     * @param position Board position where player is hovering (row, col)
     */
    public void setPlayerBoardHover(Position position) {
        this.playerBoardHover = position;
    }
    
    /**
     * Sets the hover position on the opponent's board.
     * @param position Board position where player is hovering on opponent's board (row, col)
     */
    public void setOpponentBoardHover(Position position) {
        this.opponentBoardHover = position;
    }
    
    /**
     * Gets the current hover position on the player's board.
     * @return Position where hovering on player's board, or null if not hovering
     */
    public Position getPlayerBoardHover() {
        return playerBoardHover;
    }
    
    /**
     * Gets the current hover position on the opponent's board.
     * This is the position that should be transmitted via UDP to the opponent.
     * @return Position where hovering on opponent's board, or null if not hovering
     */
    public Position getOpponentBoardHover() {
        return opponentBoardHover;
    }
    
    /**
     * Gets both hover positions in a convenient format.
     * @return Map with "playerBoard" and "opponentBoard" hover positions
     */
    public Map<String, Position> getAllHoverPositions() {
        Map<String, Position> positions = new HashMap<>();
        positions.put("playerBoard", playerBoardHover);
        positions.put("opponentBoard", opponentBoardHover);
        return positions;
    }
    
    /**
     * Gets the current global mouse position on screen (for debugging).
     * @return Point with mouse coordinates, or null if unable to get position
     */
    public Point getGlobalMousePosition() {
        try {
            PointerInfo pointerInfo = MouseInfo.getPointerInfo();
            if (pointerInfo != null) {
                return pointerInfo.getLocation();
            }
        } catch (Exception e) {
            System.err.println("Error getting mouse position: " + e.getMessage());
        }
        return null;
    }
    
    /**
     * Gets all ship positions for the player team.
     * @return List of ShipInfo objects containing ship details and positions
     */
    public List<ShipInfo> getPlayerShipPositions() {
        return getShipPositions(gameState.getPlayerBoard());
    }
    
    /**
     * Gets all ship positions for the opponent team.
     * @return List of ShipInfo objects containing ship details and positions
     */
    public List<ShipInfo> getOpponentShipPositions() {
        return getShipPositions(gameState.getOpponentBoard());
    }
    
    /**
     * Gets ship positions for both teams.
     * @return Map with "player" and "opponent" ship positions
     */
    public Map<String, List<ShipInfo>> getAllShipPositions() {
        Map<String, List<ShipInfo>> allPositions = new HashMap<>();
        
        allPositions.put("player", getPlayerShipPositions());
        allPositions.put("opponent", getOpponentShipPositions());
        
        return allPositions;
    }
    
    /**
     * Helper method to extract ship positions from a board.
     * @param board Board to extract ship positions from
     * @return List of ShipInfo objects
     */
    private List<ShipInfo> getShipPositions(Board board) {
        List<ShipInfo> shipInfoList = new ArrayList<>();
        
        for (Ship ship : board.getShips()) {
            ShipInfo shipInfo = new ShipInfo(
                ship.getName(),
                ship.getSize(),
                ship.getPositions(),
                ship.isSunk(),
                ship.getHitCount(),
                ship.isVertical()
            );
            shipInfoList.add(shipInfo);
        }
        
        return shipInfoList;
    }
    
    /**
     * Gets detailed game statistics including hover information.
     * @return Map with various game statistics
     */
    public Map<String, Object> getGameStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        // Basic game info
        stats.put("gameStatus", gameState.getGameStatus());
        stats.put("currentPlayer", gameState.isPlayerTurn() ? "player" : "opponent");
        stats.put("gameMessage", gameState.getMessage());
        
        // Scores
        stats.put("scores", getScores());
        
        // Ship counts
        Map<String, Object> shipCounts = new HashMap<>();
        shipCounts.put("playerShipsRemaining", countAliveShips(gameState.getPlayerBoard()));
        shipCounts.put("opponentShipsRemaining", countAliveShips(gameState.getOpponentBoard()));
        shipCounts.put("playerShipsTotal", gameState.getPlayerBoard().getShips().size());
        shipCounts.put("opponentShipsTotal", gameState.getOpponentBoard().getShips().size());
        stats.put("shipCounts", shipCounts);
        
        // Attack statistics
        Map<String, Object> attackStats = new HashMap<>();
        attackStats.put("playerAttacksMade", gameState.getOpponentBoard().getAttackedPositions().size());
        attackStats.put("opponentAttacksMade", gameState.getPlayerBoard().getAttackedPositions().size());
        stats.put("attackStatistics", attackStats);
        
        // Hover positions
        stats.put("hoverPositions", getAllHoverPositions());
        
        // Global mouse position (for debugging)
        stats.put("globalMousePosition", getGlobalMousePosition());
        
        return stats;
    }
    
    /**
     * Counts the number of ships that are still alive on a board.
     * @param board Board to count alive ships from
     * @return Number of alive ships
     */
    private int countAliveShips(Board board) {
        int count = 0;
        for (Ship ship : board.getShips()) {
            if (!ship.isSunk()) {
                count++;
            }
        }
        return count;
    }
    
    /**
     * Formats hover position for UDP transmission.
     * @param position Position to format
     * @return String in format "row,col" or "null" if position is null
     */
    public String formatHoverForTransmission(Position position) {
        if (position == null) {
            return "null";
        }
        return position.getRow() + "," + position.getCol();
    }
    
    /**
     * Parses hover position from UDP transmission.
     * @param hoverString String in format "row,col" or "null"
     * @return Position object or null if string is "null" or invalid
     */
    public Position parseHoverFromTransmission(String hoverString) {
        if (hoverString == null || "null".equals(hoverString.trim())) {
            return null;
        }
        
        try {
            String[] parts = hoverString.split(",");
            if (parts.length == 2) {
                int row = Integer.parseInt(parts[0].trim());
                int col = Integer.parseInt(parts[1].trim());
                return new Position(row, col);
            }
        } catch (NumberFormatException e) {
            System.err.println("Error parsing hover position: " + hoverString);
        }
        
        return null;
    }
    
    /**
     * Inner class to hold ship information.
     */
    public static class ShipInfo {
        private final String name;
        private final int size;
        private final List<Position> positions;
        private final boolean isSunk;
        private final int hitCount;
        private final boolean isVertical;
        
        public ShipInfo(String name, int size, List<Position> positions, 
                       boolean isSunk, int hitCount, boolean isVertical) {
            this.name = name;
            this.size = size;
            this.positions = new ArrayList<>(positions); // Create a copy
            this.isSunk = isSunk;
            this.hitCount = hitCount;
            this.isVertical = isVertical;
        }
        
        // Getters
        public String getName() { return name; }
        public int getSize() { return size; }
        public List<Position> getPositions() { return new ArrayList<>(positions); }
        public boolean isSunk() { return isSunk; }
        public int getHitCount() { return hitCount; }
        public boolean isVertical() { return isVertical; }
        
        /**
         * Gets the starting position of the ship (top-left corner).
         * @return Starting position
         */
        public Position getStartPosition() {
            if (positions.isEmpty()) return null;
            
            Position first = positions.get(0);
            for (Position pos : positions) {
                if (pos.getRow() < first.getRow() || 
                    (pos.getRow() == first.getRow() && pos.getCol() < first.getCol())) {
                    first = pos;
                }
            }
            return first;
        }
        
        @Override
        public String toString() {
            return String.format("ShipInfo{name='%s', size=%d, positions=%s, isSunk=%s, hitCount=%d, isVertical=%s}",
                    name, size, positions, isSunk, hitCount, isVertical);
        }
    }
} 