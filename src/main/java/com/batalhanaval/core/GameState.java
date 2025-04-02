package com.batalhanaval.core;

import com.batalhanaval.Constants;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages the Battleship game state, including players, boards, and turns.
 */
public class GameState implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private final Board playerBoard;
    private final Board opponentBoard;
    private boolean isPlayerTurn;
    private String gameStatus;
    private String message;
    
    /**
     * Creates a new game state.
     *
     * @param boardSize size of the game board
     */
    public GameState(int boardSize) {
        playerBoard = new Board(boardSize);
        opponentBoard = new Board(boardSize);
        isPlayerTurn = true;  // By default, local player starts
        gameStatus = Constants.GAME_STATE_SETUP;
        message = "Position your ships.";
    }
    
    /**
     * @return the local player's board
     */
    public Board getPlayerBoard() {
        return playerBoard;
    }
    
    /**
     * @return the opponent's board
     */
    public Board getOpponentBoard() {
        return opponentBoard;
    }
    
    /**
     * @return true if it's the local player's turn
     */
    public boolean isPlayerTurn() {
        return isPlayerTurn;
    }
    
    /**
     * @param playerTurn sets whose turn it is (true for local player)
     */
    public void setPlayerTurn(boolean playerTurn) {
        isPlayerTurn = playerTurn;
    }
    
    /**
     * Switches turn between players.
     */
    public void switchTurn() {
        isPlayerTurn = !isPlayerTurn;
    }
    
    /**
     * @return the current game status (GAME_STATE_SETUP, GAME_STATE_PLAYING, GAME_STATE_GAME_OVER)
     */
    public String getGameStatus() {
        return gameStatus;
    }
    
    /**
     * @param status sets the new game status
     */
    public void setGameStatus(String status) {
        this.gameStatus = status;
    }
    
    /**
     * @return the current game message
     */
    public String getMessage() {
        return message;
    }
    
    /**
     * @param message sets a new game message
     */
    public void setMessage(String message) {
        this.message = message;
    }
    
    /**
     * Creates and positions default ships on the player's board.
     * Useful for testing or quick setup.
     *
     * @return true if all ships were successfully positioned
     */
    public boolean createDefaultShips() {
        playerBoard.clear();
        
        List<Ship> ships = new ArrayList<>();
        ships.add(new Ship("Aircraft Carrier", Constants.CARRIER_SIZE));
        ships.add(new Ship("Battleship", Constants.BATTLESHIP_SIZE));
        ships.add(new Ship("Cruiser", Constants.CRUISER_SIZE));
        ships.add(new Ship("Submarine", Constants.SUBMARINE_SIZE));
        ships.add(new Ship("Destroyer", Constants.DESTROYER_SIZE));
        
        boolean success = true;
        
        // Position ships in predefined positions
        // This is just an example - in the real application, the player would position the ships
        success &= playerBoard.placeShip(ships.get(0), new Position(0, 0), false);
        success &= playerBoard.placeShip(ships.get(1), new Position(2, 2), true);
        success &= playerBoard.placeShip(ships.get(2), new Position(5, 5), false);
        success &= playerBoard.placeShip(ships.get(3), new Position(7, 2), true);
        success &= playerBoard.placeShip(ships.get(4), new Position(9, 8), false);
        
        return success;
    }
    
    /**
     * Processes an attack from the local player on the opponent's board.
     *
     * @param position position to attack
     * @return attack result or null if it's not the player's turn or the game is not in progress
     */
    public String processPlayerAttack(Position position) {
        if (!gameStatus.equals(Constants.GAME_STATE_PLAYING) || !isPlayerTurn) {
            return null;  // Not the player's turn or game is not in progress
        }
        
        String result = opponentBoard.processAttack(position);
        
        if (!Constants.ATTACK_INVALID.equals(result)) {
            // Update message based on result
            switch (result) {
                case Constants.ATTACK_HIT:
                    message = "You hit a ship!";
                    break;
                case Constants.ATTACK_MISS:
                    message = "You missed. Water.";
                    break;
                case Constants.ATTACK_SUNK:
                    message = "You sank a ship!";
                    
                    // Check if all ships have been sunk
                    if (opponentBoard.areAllShipsSunk()) {
                        message = "You won! All opponent's ships have been sunk.";
                        gameStatus = Constants.GAME_STATE_GAME_OVER;
                    }
                    break;
            }
            
            // If the game is still in progress, switch turns
            if (gameStatus.equals(Constants.GAME_STATE_PLAYING)) {
                switchTurn();
            }
        }
        
        return result;
    }
    
    /**
     * Processes an attack from the opponent on the local player's board.
     *
     * @param position position to attack
     * @return attack result or null if it's the player's turn or the game is not in progress
     */
    public String processOpponentAttack(Position position) {
        if (!gameStatus.equals(Constants.GAME_STATE_PLAYING) || isPlayerTurn) {
            return null;  // It's the player's turn or game is not in progress
        }
        
        String result = playerBoard.processAttack(position);
        
        if (!Constants.ATTACK_INVALID.equals(result)) {
            // Update message based on result
            switch (result) {
                case Constants.ATTACK_HIT:
                    message = "Opponent hit your ship!";
                    break;
                case Constants.ATTACK_MISS:
                    message = "Opponent missed. Water.";
                    break;
                case Constants.ATTACK_SUNK:
                    message = "Opponent sank your ship!";
                    
                    // Check if all ships have been sunk
                    if (playerBoard.areAllShipsSunk()) {
                        message = "You lost! All your ships have been sunk.";
                        gameStatus = Constants.GAME_STATE_GAME_OVER;
                    }
                    break;
            }
            
            // If the game is still in progress, switch turns
            if (gameStatus.equals(Constants.GAME_STATE_PLAYING)) {
                switchTurn();
            }
        }
        
        return result;
    }
    
    /**
     * Checks if the game is ready to start (setup phase completed).
     *
     * @return true if both players have positioned all ships
     */
    public boolean isReadyToStart() {
        // In a real game, would also check if the opponent is ready
        return playerBoard.getShips().size() == Constants.TOTAL_SHIPS;
    }
    
    /**
     * Starts the game, changing status to GAME_STATE_PLAYING.
     *
     * @return true if the game was successfully started
     */
    public boolean startGame() {
        if (isReadyToStart() && gameStatus.equals(Constants.GAME_STATE_SETUP)) {
            gameStatus = Constants.GAME_STATE_PLAYING;
            message = isPlayerTurn ? "Your turn. Make your attack." : "Waiting for opponent to attack.";
            return true;
        }
        return false;
    }
    
    /**
     * Resets the game, clearing boards and returning to setup phase.
     */
    public void resetGame() {
        playerBoard.clear();
        opponentBoard.clear();
        isPlayerTurn = true;
        gameStatus = Constants.GAME_STATE_SETUP;
        message = "Game reset. Position your ships.";
    }
} 