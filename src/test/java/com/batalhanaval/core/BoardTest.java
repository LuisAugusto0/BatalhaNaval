package com.batalhanaval.core;

import com.batalhanaval.Constants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Board class.
 */
class BoardTest {
    private Board board;
    private Ship ship;
    
    @BeforeEach
    void setUp() {
        board = new Board(Constants.BOARD_SIZE);
        ship = new Ship("Test", 3);
    }
    
    @Test
    void testBoardInitialization() {
        // Verify if the board was initialized correctly
        char[][] grid = board.getGrid();
        
        assertEquals(Constants.BOARD_SIZE, board.getSize());
        assertEquals(Constants.BOARD_SIZE, grid.length);
        assertEquals(Constants.BOARD_SIZE, grid[0].length);
        
        // All cells should be empty at the beginning
        for (int i = 0; i < Constants.BOARD_SIZE; i++) {
            for (int j = 0; j < Constants.BOARD_SIZE; j++) {
                assertEquals(Constants.EMPTY, grid[i][j]);
            }
        }
    }
    
    @Test
    void testPlaceShipHorizontal() {
        // Try to position a ship horizontally
        Position pos = new Position(5, 5);
        boolean result = board.placeShip(ship, pos, false);
        
        assertTrue(result);
        assertEquals(1, board.getShips().size());
        
        // Verify if the board cells were updated
        assertEquals(Constants.SHIP, board.getCellState(new Position(5, 5)));
        assertEquals(Constants.SHIP, board.getCellState(new Position(5, 6)));
        assertEquals(Constants.SHIP, board.getCellState(new Position(5, 7)));
        
        // Adjacent position should be empty
        assertEquals(Constants.EMPTY, board.getCellState(new Position(5, 8)));
    }
    
    @Test
    void testPlaceShipVertical() {
        // Try to position a ship vertically
        Position pos = new Position(2, 3);
        boolean result = board.placeShip(ship, pos, true);
        
        assertTrue(result);
        assertEquals(1, board.getShips().size());
        
        // Verify if the board cells were updated
        assertEquals(Constants.SHIP, board.getCellState(new Position(2, 3)));
        assertEquals(Constants.SHIP, board.getCellState(new Position(3, 3)));
        assertEquals(Constants.SHIP, board.getCellState(new Position(4, 3)));
        
        // Adjacent position should be empty
        assertEquals(Constants.EMPTY, board.getCellState(new Position(5, 3)));
    }
    
    @Test
    void testPlaceShipOutOfBounds() {
        // Try to position a ship that would go out of bounds
        Position pos = new Position(9, 9);
        boolean result = board.placeShip(ship, pos, false);
        
        assertFalse(result);
        assertEquals(0, board.getShips().size());
    }
    
    @Test
    void testPlaceShipOverlap() {
        // Position the first ship
        Position pos1 = new Position(5, 5);
        boolean result1 = board.placeShip(ship, pos1, false);
        
        assertTrue(result1);
        
        // Try to position a second ship that overlaps with the first
        Ship ship2 = new Ship("Test2", 4);
        Position pos2 = new Position(4, 6);
        boolean result2 = board.placeShip(ship2, pos2, true);
        
        assertFalse(result2);
        assertEquals(1, board.getShips().size());
    }
    
    @Test
    void testProcessAttackMiss() {
        // Position a ship
        Position shipPos = new Position(5, 5);
        board.placeShip(ship, shipPos, false);
        
        // Attack a position without a ship
        Position attackPos = new Position(1, 1);
        String result = board.processAttack(attackPos);
        
        assertEquals(Constants.ATTACK_MISS, result);
        assertEquals(Constants.MISS, board.getCellState(attackPos));
    }
    
    @Test
    void testProcessAttackHit() {
        // Position a ship
        Position shipPos = new Position(5, 5);
        board.placeShip(ship, shipPos, false);
        
        // Attack a position with a ship
        Position attackPos = new Position(5, 6);
        String result = board.processAttack(attackPos);
        
        assertEquals(Constants.ATTACK_HIT, result);
        assertEquals(Constants.HIT, board.getCellState(attackPos));
        
        // The ship should not be sunk yet
        Ship hitShip = board.getShipAt(shipPos);
        assertFalse(hitShip.isSunk());
    }
    
    @Test
    void testProcessAttackSunk() {
        // Position a ship
        Position shipPos = new Position(5, 5);
        board.placeShip(ship, shipPos, false);
        
        // Attack all positions of the ship
        board.processAttack(new Position(5, 5));
        board.processAttack(new Position(5, 6));
        String result = board.processAttack(new Position(5, 7));
        
        assertEquals(Constants.ATTACK_SUNK, result);
        
        // All positions of the ship should be marked as sunk
        assertEquals(Constants.SUNK, board.getCellState(new Position(5, 5)));
        assertEquals(Constants.SUNK, board.getCellState(new Position(5, 6)));
        assertEquals(Constants.SUNK, board.getCellState(new Position(5, 7)));
        
        // The ship should be sunk
        Ship sunkShip = board.getShipAt(shipPos);
        assertTrue(sunkShip.isSunk());
    }
    
    @Test
    void testProcessAttackInvalidPosition() {
        // Attack a position outside the board
        Position attackPos = new Position(20, 20);
        String result = board.processAttack(attackPos);
        
        assertEquals(Constants.ATTACK_INVALID, result);
    }
    
    @Test
    void testProcessAttackAlreadyAttacked() {
        // Attack a position
        Position attackPos = new Position(5, 5);
        board.processAttack(attackPos);
        
        // Try to attack the same position again
        String result = board.processAttack(attackPos);
        
        assertEquals(Constants.ATTACK_INVALID, result);
    }
    
    @Test
    void testAreAllShipsSunk() {
        // Initially, there are no ships, so areAllShipsSunk should return false
        assertFalse(board.areAllShipsSunk());
        
        // Add a ship
        Position shipPos = new Position(5, 5);
        board.placeShip(ship, shipPos, false);
        
        // Not all ships have been sunk yet
        assertFalse(board.areAllShipsSunk());
        
        // Sink the ship
        board.processAttack(new Position(5, 5));
        board.processAttack(new Position(5, 6));
        board.processAttack(new Position(5, 7));
        
        // Now all ships are sunk
        assertTrue(board.areAllShipsSunk());
    }
    
    @Test
    void testClear() {
        // Add a ship
        Position shipPos = new Position(5, 5);
        board.placeShip(ship, shipPos, false);
        
        // Attack a position
        board.processAttack(new Position(1, 1));
        
        // Clear the board
        board.clear();
        
        // Verify if the board was reset
        assertEquals(0, board.getShips().size());
        assertEquals(0, board.getAttackedPositions().size());
        
        char[][] grid = board.getGrid();
        for (int i = 0; i < Constants.BOARD_SIZE; i++) {
            for (int j = 0; j < Constants.BOARD_SIZE; j++) {
                assertEquals(Constants.EMPTY, grid[i][j]);
            }
        }
    }
} 