package com.batalhanaval.core;

import com.batalhanaval.Constants;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents the game board in Battleship.
 */
public class Board implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private final int size;
    private final char[][] grid;
    private final List<Ship> ships;
    private final List<Position> attackedPositions;
    
    /**
     * Creates a new board with the specified size.
     *
     * @param size size of the board (typically 10x10)
     */
    public Board(int size) {
        this.size = size;
        this.grid = new char[size][size];
        this.ships = new ArrayList<>();
        this.attackedPositions = new ArrayList<>();
        
        // Initially, all cells are empty (water)
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                grid[i][j] = Constants.EMPTY;
            }
        }
    }
    
    /**
     * @return the size of the board
     */
    public int getSize() {
        return size;
    }
    
    /**
     * Gets the state of a cell on the board.
     *
     * @param position position to check
     * @return the character representing the cell state
     */
    public char getCellState(Position position) {
        if (position == null || !position.isValidPosition(size)) {
            return ' ';  // Invalid position
        }
        return grid[position.getRow()][position.getCol()];
    }
    
    /**
     * Gets a copy of the internal grid (for display).
     *
     * @return a 2D copy of the grid
     */
    public char[][] getGrid() {
        char[][] copy = new char[size][size];
        for (int i = 0; i < size; i++) {
            System.arraycopy(grid[i], 0, copy[i], 0, size);
        }
        return copy;
    }
    
    /**
     * Attempts to add a ship to the board.
     *
     * @param ship the ship to add
     * @param startPosition starting position of the ship
     * @param isVertical orientation of the ship
     * @return true if the ship was successfully added
     */
    public boolean placeShip(Ship ship, Position startPosition, boolean isVertical) {
        if (ship == null || startPosition == null || !startPosition.isValidPosition(size)) {
            return false;
        }
        
        // Get the positions the ship would occupy
        List<Position> shipPositions = ship.placeShip(startPosition, isVertical, size);
        if (shipPositions == null) {
            return false;  // Ship doesn't fit on the board
        }
        
        // Check for overlap with other ships
        for (Position pos : shipPositions) {
            if (grid[pos.getRow()][pos.getCol()] != Constants.EMPTY) {
                return false;  // Overlap with another ship
            }
        }
        
        // Add the ship to the board
        ships.add(ship);
        
        // Mark the grid cells occupied by the ship
        for (Position pos : shipPositions) {
            grid[pos.getRow()][pos.getCol()] = Constants.SHIP;
        }
        
        return true;
    }
    
    /**
     * Processes an attack on the board.
     *
     * @param position attack position
     * @return one of: ATTACK_HIT, ATTACK_MISS, ATTACK_SUNK or ATTACK_INVALID
     */
    public String processAttack(Position position) {
        if (position == null || !position.isValidPosition(size)) {
            return Constants.ATTACK_INVALID;
        }
        
        // Check if the position has already been attacked
        for (Position pos : attackedPositions) {
            if (pos.equals(position)) {
                return Constants.ATTACK_INVALID;  // Position already attacked
            }
        }
        
        // Register the attack
        attackedPositions.add(position);
        
        // Check if the attack hit any ship
        for (Ship ship : ships) {
            if (ship.containsPosition(position)) {
                ship.hit(position);
                
                // Update the grid
                if (ship.isSunk()) {
                    // Mark all positions of the sunk ship
                    for (Position pos : ship.getPositions()) {
                        grid[pos.getRow()][pos.getCol()] = Constants.SUNK;
                    }
                    return Constants.ATTACK_SUNK;
                } else {
                    grid[position.getRow()][position.getCol()] = Constants.HIT;
                    return Constants.ATTACK_HIT;
                }
            }
        }
        
        // No ship was hit
        grid[position.getRow()][position.getCol()] = Constants.MISS;
        return Constants.ATTACK_MISS;
    }
    
    /**
     * Checks if all ships have been sunk.
     *
     * @return true if all ships are sunk
     */
    public boolean areAllShipsSunk() {
        for (Ship ship : ships) {
            if (!ship.isSunk()) {
                return false;
            }
        }
        return !ships.isEmpty();  // Returns false if there are no ships
    }
    
    /**
     * @return the list of ships on the board
     */
    public List<Ship> getShips() {
        return new ArrayList<>(ships);
    }
    
    /**
     * @return the list of attacked positions
     */
    public List<Position> getAttackedPositions() {
        return new ArrayList<>(attackedPositions);
    }
    
    /**
     * Looks for a ship at a specific position.
     *
     * @param position position to check
     * @return the ship at the position or null if there is no ship
     */
    public Ship getShipAt(Position position) {
        for (Ship ship : ships) {
            if (ship.containsPosition(position)) {
                return ship;
            }
        }
        return null;
    }
    
    /**
     * Clears the board, removing all ships.
     */
    public void clear() {
        ships.clear();
        attackedPositions.clear();
        
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                grid[i][j] = Constants.EMPTY;
            }
        }
    }
} 