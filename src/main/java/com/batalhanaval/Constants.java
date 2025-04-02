package com.batalhanaval;

/**
 * Constants used in the Battleship game.
 */
public class Constants {
    // Board Configuration
    public static final int BOARD_SIZE = 10;
    
    // Network Communication Ports
    public static final int DEFAULT_TCP_PORT = 5000;
    public static final int DEFAULT_UDP_PORT = 5001;
    
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
    public static final int GAME_SETUP = 0;
    public static final int GAME_PLAYING = 1;
    public static final int GAME_OVER = 2;
} 