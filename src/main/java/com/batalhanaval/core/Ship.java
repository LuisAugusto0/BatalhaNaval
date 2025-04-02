package com.batalhanaval.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a ship in the Battleship game.
 */
public class Ship implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String name;
    private int size;
    private List<Position> positions;
    private List<Position> hitPositions;
    private boolean isVertical;
    
    /**
     * Creates a new ship.
     *
     * @param name name of the ship
     * @param size size of the ship
     */
    public Ship(String name, int size) {
        this.name = name;
        this.size = size;
        this.positions = new ArrayList<>();
        this.hitPositions = new ArrayList<>();
        this.isVertical = false;
    }
    
    /**
     * @return the name of the ship
     */
    public String getName() {
        return name;
    }
    
    /**
     * @return the size of the ship
     */
    public int getSize() {
        return size;
    }
    
    /**
     * @return true if the ship is oriented vertically
     */
    public boolean isVertical() {
        return isVertical;
    }
    
    /**
     * Changes the orientation of the ship.
     *
     * @param vertical true for vertical orientation, false for horizontal
     */
    public void setVertical(boolean vertical) {
        isVertical = vertical;
    }
    
    /**
     * Places the ship on the board.
     * 
     * @param startPosition starting position
     * @param isVertical orientation (true for vertical, false for horizontal)
     * @param boardSize size of the board
     * @return list of occupied positions or null if the position is invalid
     */
    public List<Position> placeShip(Position startPosition, boolean isVertical, int boardSize) {
        if (startPosition == null || !startPosition.isValidPosition(boardSize)) {
            return null;
        }
        
        this.isVertical = isVertical;
        positions.clear();
        hitPositions.clear();
        
        int startRow = startPosition.getRow();
        int startCol = startPosition.getCol();
        
        // Check if the ship fits on the board
        if (isVertical) {
            if (startRow + size > boardSize) {
                return null;  // Doesn't fit vertically
            }
        } else {
            if (startCol + size > boardSize) {
                return null;  // Doesn't fit horizontally
            }
        }
        
        // Create all positions for the ship
        for (int i = 0; i < size; i++) {
            int row = isVertical ? startRow + i : startRow;
            int col = isVertical ? startCol : startCol + i;
            positions.add(new Position(row, col));
        }
        
        return positions;
    }
    
    /**
     * @return list of positions occupied by the ship
     */
    public List<Position> getPositions() {
        return new ArrayList<>(positions);
    }
    
    /**
     * Checks if the ship contains a specific position.
     * 
     * @param position position to check
     * @return true if the ship occupies the position
     */
    public boolean containsPosition(Position position) {
        for (Position pos : positions) {
            if (pos.equals(position)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Registers an attack on the ship.
     * 
     * @param position attack position
     * @return true if the attack hit the ship
     */
    public boolean hit(Position position) {
        if (containsPosition(position) && !hitPositions.contains(position)) {
            hitPositions.add(position);
            return true;
        }
        return false;
    }
    
    /**
     * Checks if the ship has been completely hit (sunk).
     * 
     * @return true if the ship is sunk
     */
    public boolean isSunk() {
        return hitPositions.size() == size;
    }
    
    /**
     * @return number of hits taken
     */
    public int getHitCount() {
        return hitPositions.size();
    }
    
    /**
     * Checks if a specific position of the ship has been hit.
     * 
     * @param position position to check
     * @return true if the position has been hit
     */
    public boolean isPositionHit(Position position) {
        return hitPositions.contains(position);
    }
} 