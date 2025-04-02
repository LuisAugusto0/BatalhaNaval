package com.batalhanaval;

/**
 * Constants used throughout the Battleship game.
 */
public class Constants {
    // Board Configuration
    public static final int BOARD_SIZE = 10;
    
    // Network Communication Ports
    public static final int DEFAULT_PORT = 6969;
    public static final int DISCOVERY_PORT = 6970;
    
    // Ship Types and Dimensions
    public static final int CARRIER_SIZE = 5;
    public static final int BATTLESHIP_SIZE = 4;
    public static final int CRUISER_SIZE = 3;
    public static final int SUBMARINE_SIZE = 3;
    public static final int DESTROYER_SIZE = 2;
    
    // Total Number of Ships in the Game
    public static final int TOTAL_SHIPS = 5;
    
    // Board Cell States
    public static final char EMPTY = '~';      // Water
    public static final char SHIP = 'S';       // Ship
    public static final char HIT = 'X';        // Hit ship
    public static final char MISS = 'O';       // Missed shot
    public static final char SUNK = '#';       // Sunk ship
    
    // Attack Results
    public static final String ATTACK_HIT = "HIT";
    public static final String ATTACK_MISS = "MISS";
    public static final String ATTACK_SUNK = "SUNK";
    public static final String ATTACK_INVALID = "INVALID";
    
    // Game States
    public static final String GAME_STATE_SETUP = "SETUP";
    public static final String GAME_STATE_PLAYING = "PLAYING";
    public static final String GAME_STATE_GAME_OVER = "GAME_OVER";
} 