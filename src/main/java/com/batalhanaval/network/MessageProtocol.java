package com.batalhanaval.network;

/**
 * Protocol definitions for network communication in multiplayer Battleship.
 * Defines message formats for both TCP (game moves) and UDP (hover/notifications).
 */
public class MessageProtocol {
    
    // ===== TCP MESSAGES (Game Logic) =====
    
    /** Player is ready to start the game after positioning ships */
    public static final String READY_TO_START = "READY_TO_START";
    
    /** Game starts, indicates which player goes first */
    public static final String GAME_START = "GAME_START";
    
    /** Attack command: ATTACK:row,col */
    public static final String ATTACK = "ATTACK";
    
    /** Attack result: ATTACK_RESULT:HIT|MISS|SUNK:row,col */
    public static final String ATTACK_RESULT = "ATTACK_RESULT";
    
    /** End of current player's turn */
    public static final String TURN_END = "TURN_END";
    
    /** Game over: GAME_OVER:WINNER|LOSER */
    public static final String GAME_OVER = "GAME_OVER";
    
    /** Player disconnected */
    public static final String DISCONNECT = "DISCONNECT";
    
    /** Player surrendered */
    public static final String SURRENDER = "SURRENDER";
    
    // ===== UDP MESSAGES (Real-time Updates) =====
    
    /** Hover position: HOVER:row,col or HOVER:null */
    public static final String HOVER = "HOVER";
    
    /** Ping for connection testing */
    public static final String PING = "PING";
    
    /** Pong response to ping */
    public static final String PONG = "PONG";
    
    // ===== MESSAGE SEPARATORS =====
    
    /** Separator for message parts */
    public static final String SEPARATOR = ":";
    
    /** Separator for coordinates */
    public static final String COORD_SEPARATOR = ",";
    
    /** Null value indicator */
    public static final String NULL_VALUE = "null";
    
    // ===== GAME STATES =====
    
    /** Player goes first */
    public static final String FIRST_PLAYER = "FIRST";
    
    /** Player goes second */
    public static final String SECOND_PLAYER = "SECOND";
    
    /** Player won the game */
    public static final String WINNER = "WINNER";
    
    /** Player lost the game */
    public static final String LOSER = "LOSER";
    
    // ===== ATTACK RESULTS =====
    
    /** Attack hit a ship */
    public static final String HIT = "HIT";
    
    /** Attack missed */
    public static final String MISS = "MISS";
    
    /** Attack sunk a ship */
    public static final String SUNK = "SUNK";
    
    // ===== UTILITY METHODS =====
    
    /**
     * Creates an attack message.
     * @param row Row coordinate (0-9)
     * @param col Column coordinate (0-9)
     * @return Formatted attack message
     */
    public static String createAttackMessage(int row, int col) {
        return ATTACK + SEPARATOR + row + COORD_SEPARATOR + col;
    }
    
    /**
     * Creates an attack result message.
     * @param result HIT, MISS, or SUNK
     * @param row Row coordinate
     * @param col Column coordinate
     * @return Formatted attack result message
     */
    public static String createAttackResultMessage(String result, int row, int col) {
        return ATTACK_RESULT + SEPARATOR + result + SEPARATOR + row + COORD_SEPARATOR + col;
    }
    
    /**
     * Creates a hover message.
     * @param row Row coordinate (0-9) or -1 for null
     * @param col Column coordinate (0-9) or -1 for null
     * @return Formatted hover message
     */
    public static String createHoverMessage(int row, int col) {
        if (row == -1 || col == -1) {
            return HOVER + SEPARATOR + NULL_VALUE;
        }
        return HOVER + SEPARATOR + row + COORD_SEPARATOR + col;
    }
    
    /**
     * Creates a game start message.
     * @param isFirstPlayer True if this player goes first
     * @return Formatted game start message
     */
    public static String createGameStartMessage(boolean isFirstPlayer) {
        return GAME_START + SEPARATOR + (isFirstPlayer ? FIRST_PLAYER : SECOND_PLAYER);
    }
    
    /**
     * Creates a game over message.
     * @param isWinner True if this player won
     * @return Formatted game over message
     */
    public static String createGameOverMessage(boolean isWinner) {
        return GAME_OVER + SEPARATOR + (isWinner ? WINNER : LOSER);
    }
    
    /**
     * Parses coordinates from a message part.
     * @param coordString String in format "row,col"
     * @return Array with [row, col] or null if invalid
     */
    public static int[] parseCoordinates(String coordString) {
        if (coordString == null || coordString.equals(NULL_VALUE)) {
            return null;
        }
        
        try {
            String[] parts = coordString.split(COORD_SEPARATOR);
            if (parts.length == 2) {
                int row = Integer.parseInt(parts[0].trim());
                int col = Integer.parseInt(parts[1].trim());
                return new int[]{row, col};
            }
        } catch (NumberFormatException e) {
            System.err.println("Error parsing coordinates: " + coordString);
        }
        
        return null;
    }
    
    /**
     * Validates if a message follows the protocol format.
     * @param message Message to validate
     * @return True if message is valid
     */
    public static boolean isValidMessage(String message) {
        if (message == null || message.trim().isEmpty()) {
            return false;
        }
        
        String[] parts = message.split(SEPARATOR);
        if (parts.length == 0) {
            return false;
        }
        
        String command = parts[0];
        
        // Validate based on command type
        switch (command) {
            case READY_TO_START:
            case TURN_END:
            case DISCONNECT:
            case SURRENDER:
            case PING:
            case PONG:
                return parts.length == 1;
                
            case GAME_START:
                return parts.length == 2 && 
                       (parts[1].equals(FIRST_PLAYER) || parts[1].equals(SECOND_PLAYER));
                       
            case GAME_OVER:
                return parts.length == 2 && 
                       (parts[1].equals(WINNER) || parts[1].equals(LOSER));
                       
            case ATTACK:
                return parts.length == 2 && parseCoordinates(parts[1]) != null;
                
            case ATTACK_RESULT:
                return parts.length == 3 && 
                       (parts[1].equals(HIT) || parts[1].equals(MISS) || parts[1].equals(SUNK)) &&
                       parseCoordinates(parts[2]) != null;
                       
            case HOVER:
                return parts.length == 2 && 
                       (parts[1].equals(NULL_VALUE) || parseCoordinates(parts[1]) != null);
                       
            default:
                return false;
        }
    }
    
    /**
     * Gets the command type from a message.
     * @param message Message to parse
     * @return Command type or null if invalid
     */
    public static String getCommand(String message) {
        if (message == null || message.trim().isEmpty()) {
            return null;
        }
        
        String[] parts = message.split(SEPARATOR);
        return parts.length > 0 ? parts[0] : null;
    }
    
    /**
     * Gets message parts after the command.
     * @param message Message to parse
     * @return Array of message parts (excluding command) or empty array
     */
    public static String[] getMessageParts(String message) {
        if (message == null || message.trim().isEmpty()) {
            return new String[0];
        }
        
        String[] parts = message.split(SEPARATOR);
        if (parts.length <= 1) {
            return new String[0];
        }
        
        String[] result = new String[parts.length - 1];
        System.arraycopy(parts, 1, result, 0, parts.length - 1);
        return result;
    }
} 