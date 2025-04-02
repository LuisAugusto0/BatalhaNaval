package com.batalhanaval.core;

import java.io.Serializable;

/**
 * Represents a position (coordinate) on the game board.
 */
public class Position implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int row;
    private int col;
    
    /**
     * Creates a new position on the board.
     * 
     * @param row row index (0-based)
     * @param col column index (0-based)
     */
    public Position(int row, int col) {
        this.row = row;
        this.col = col;
    }
    
    /**
     * @return the row index (0-based)
     */
    public int getRow() {
        return row;
    }
    
    /**
     * @return the column index (0-based)
     */
    public int getCol() {
        return col;
    }
    
    /**
     * Checks if this position is within the board boundaries.
     * 
     * @param boardSize the size of the board
     * @return true if the position is valid
     */
    public boolean isValidPosition(int boardSize) {
        return row >= 0 && row < boardSize && col >= 0 && col < boardSize;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Position position = (Position) obj;
        return row == position.row && col == position.col;
    }
    
    @Override
    public int hashCode() {
        return 31 * row + col;
    }
    
    @Override
    public String toString() {
        // Converts to game notation: A1, B5, etc.
        char colChar = (char) ('A' + col);
        return "" + colChar + (row + 1);
    }
    
    /**
     * Creates a position from a game notation (e.g., "A1", "B5").
     * 
     * @param notation the notation in the format letter (column) + number (row)
     * @return the position or null if the notation is invalid
     */
    public static Position fromNotation(String notation) {
        if (notation == null || notation.length() < 2) {
            return null;
        }
        
        char colChar = Character.toUpperCase(notation.charAt(0));
        try {
            int col = colChar - 'A';
            int row = Integer.parseInt(notation.substring(1)) - 1;
            return new Position(row, col);
        } catch (NumberFormatException e) {
            return null;
        }
    }
} 