package com.batalhanaval.core;

import java.awt.Point;
import java.util.List;
import java.util.Map;

/**
 * Example class demonstrating how to use GameInfoManager functions.
 * This class shows how to retrieve scores, mouse position, and ship positions.
 */
public class GameInfoExample {
    
    private GameInfoManager gameInfoManager;
    
    /**
     * Constructor that initializes the example with a GameState.
     * @param gameState Current game state
     */
    public GameInfoExample(GameState gameState) {
        this.gameInfoManager = new GameInfoManager(gameState);
    }
    
    /**
     * Example method to demonstrate getting scores.
     */
    public void demonstrateScoreRetrieval() {
        System.out.println("=== PONTUAÇÃO ===");
        
        Map<String, Integer> scores = gameInfoManager.getScores();
        int playerScore = scores.get("player");
        int opponentScore = scores.get("opponent");
        
        System.out.println("Pontuação do Jogador: " + playerScore);
        System.out.println("Pontuação do Oponente: " + opponentScore);
        
        if (playerScore > opponentScore) {
            System.out.println("Jogador está vencendo!");
        } else if (opponentScore > playerScore) {
            System.out.println("Oponente está vencendo!");
        } else {
            System.out.println("Empate!");
        }
        System.out.println();
    }
    
    /**
     * Example method to demonstrate getting mouse position.
     */
    public void demonstrateMousePosition() {
        System.out.println("=== POSIÇÕES DE HOVER ===");
        
        // Board hover positions
        Map<String, Position> hoverPositions = gameInfoManager.getAllHoverPositions();
        Position playerHover = hoverPositions.get("playerBoard");
        Position opponentHover = hoverPositions.get("opponentBoard");
        
        System.out.println("Hover no seu tabuleiro: " + 
            (playerHover != null ? "(" + playerHover.getRow() + ", " + playerHover.getCol() + ")" : "Nenhum"));
        System.out.println("Hover no tabuleiro inimigo: " + 
            (opponentHover != null ? "(" + opponentHover.getRow() + ", " + opponentHover.getCol() + ")" : "Nenhum"));
        
        // Global mouse position for debugging
        Point globalMouse = gameInfoManager.getGlobalMousePosition();
        if (globalMouse != null) {
            System.out.println("Posição global do mouse (debug): (" + globalMouse.x + ", " + globalMouse.y + ")");
        }
        
        // Show formatted hover for UDP transmission
        if (opponentHover != null) {
            String formattedHover = gameInfoManager.formatHoverForTransmission(opponentHover);
            System.out.println("Hover formatado para UDP: " + formattedHover);
        }
        
        System.out.println();
    }
    
    /**
     * Example method to demonstrate hover tracking functionality.
     */
    public void demonstrateHoverTracking() {
        System.out.println("=== RASTREAMENTO DE HOVER ===");
        
        // Simulate setting hover positions
        System.out.println("Simulando hover nas posições...");
        
        // Simulate player hovering over their own board
        Position playerBoardPos = new Position(3, 5);
        gameInfoManager.setPlayerBoardHover(playerBoardPos);
        System.out.println("Hover definido no seu tabuleiro: (" + playerBoardPos.getRow() + ", " + playerBoardPos.getCol() + ")");
        
        // Simulate player hovering over opponent's board (this would be transmitted via UDP)
        Position opponentBoardPos = new Position(7, 2);
        gameInfoManager.setOpponentBoardHover(opponentBoardPos);
        System.out.println("Hover definido no tabuleiro inimigo: (" + opponentBoardPos.getRow() + ", " + opponentBoardPos.getCol() + ")");
        
        // Show how to format for network transmission
        String hoverForUDP = gameInfoManager.formatHoverForTransmission(opponentBoardPos);
        System.out.println("Dados para envio via UDP: " + hoverForUDP);
        
        // Show how to parse received UDP data
        Position parsedHover = gameInfoManager.parseHoverFromTransmission(hoverForUDP);
        System.out.println("Hover recebido via UDP: " + 
            (parsedHover != null ? "(" + parsedHover.getRow() + ", " + parsedHover.getCol() + ")" : "Inválido"));
        
        System.out.println();
    }
    
    /**
     * Example method to demonstrate getting ship positions for both teams.
     */
    public void demonstrateShipPositions() {
        System.out.println("=== POSIÇÕES DOS NAVIOS ===");
        
        // Get ship positions for both teams
        Map<String, List<GameInfoManager.ShipInfo>> allShips = gameInfoManager.getAllShipPositions();
        
        // Display player ships
        System.out.println("--- NAVIOS DO JOGADOR ---");
        List<GameInfoManager.ShipInfo> playerShips = allShips.get("player");
        displayShipsInfo(playerShips);
        
        // Display opponent ships
        System.out.println("--- NAVIOS DO OPONENTE ---");
        List<GameInfoManager.ShipInfo> opponentShips = allShips.get("opponent");
        displayShipsInfo(opponentShips);
        
        System.out.println();
    }
    
    /**
     * Helper method to display ship information.
     * @param ships List of ship information
     */
    private void displayShipsInfo(List<GameInfoManager.ShipInfo> ships) {
        if (ships.isEmpty()) {
            System.out.println("  Nenhum navio encontrado.");
            return;
        }
        
        for (GameInfoManager.ShipInfo ship : ships) {
            System.out.println("  Navio: " + ship.getName());
            System.out.println("    Tamanho: " + ship.getSize());
            System.out.println("    Afundado: " + (ship.isSunk() ? "Sim" : "Não"));
            System.out.println("    Acertos: " + ship.getHitCount() + "/" + ship.getSize());
            System.out.println("    Orientação: " + (ship.isVertical() ? "Vertical" : "Horizontal"));
            System.out.println("    Posição inicial: " + ship.getStartPosition());
            System.out.print("    Todas as posições: ");
            for (Position pos : ship.getPositions()) {
                System.out.print("(" + pos.getRow() + "," + pos.getCol() + ") ");
            }
            System.out.println();
            System.out.println();
        }
    }
    
    /**
     * Example method to demonstrate getting detailed game statistics.
     */
    public void demonstrateGameStatistics() {
        System.out.println("=== ESTATÍSTICAS DO JOGO ===");
        
        Map<String, Object> stats = gameInfoManager.getGameStatistics();
        
        System.out.println("Status do jogo: " + stats.get("gameStatus"));
        System.out.println("Jogador atual: " + stats.get("currentPlayer"));
        System.out.println("Mensagem: " + stats.get("gameMessage"));
        
        // Scores
        @SuppressWarnings("unchecked")
        Map<String, Integer> scores = (Map<String, Integer>) stats.get("scores");
        System.out.println("Pontuações: " + scores);
        
        // Ship counts
        @SuppressWarnings("unchecked")
        Map<String, Object> shipCounts = (Map<String, Object>) stats.get("shipCounts");
        System.out.println("Navios restantes - Jogador: " + shipCounts.get("playerShipsRemaining"));
        System.out.println("Navios restantes - Oponente: " + shipCounts.get("opponentShipsRemaining"));
        
        // Attack statistics
        @SuppressWarnings("unchecked")
        Map<String, Object> attackStats = (Map<String, Object>) stats.get("attackStatistics");
        System.out.println("Ataques do jogador: " + attackStats.get("playerAttacksMade"));
        System.out.println("Ataques do oponente: " + attackStats.get("opponentAttacksMade"));
        
        System.out.println();
    }
    
    /**
     * Runs all demonstration methods.
     */
    public void runAllDemonstrations() {
        System.out.println("========================================");
        System.out.println("DEMONSTRAÇÃO DO GAMEINFOMANAGER");
        System.out.println("========================================");
        
        demonstrateScoreRetrieval();
        demonstrateMousePosition();
        demonstrateHoverTracking();
        demonstrateShipPositions();
        demonstrateGameStatistics();
        
        System.out.println("========================================");
        System.out.println("FIM DA DEMONSTRAÇÃO");
        System.out.println("========================================");
    }
    
    /**
     * Simple methods for direct access to the main functionalities requested.
     */
    
    /**
     * Gets scores in a simple format.
     * @return Array with [playerScore, opponentScore]
     */
    public int[] getSimpleScores() {
        Map<String, Integer> scores = gameInfoManager.getScores();
        return new int[]{scores.get("player"), scores.get("opponent")};
    }
    
    /**
     * Gets hover position on opponent board in a simple format.
     * @return Array with [row, col] coordinates, or null if not hovering
     */
    public int[] getSimpleOpponentBoardHover() {
        Position hoverPos = gameInfoManager.getOpponentBoardHover();
        if (hoverPos != null) {
            return new int[]{hoverPos.getRow(), hoverPos.getCol()};
        }
        return null;
    }
    
    /**
     * Gets hover position on player board in a simple format.
     * @return Array with [row, col] coordinates, or null if not hovering
     */
    public int[] getSimplePlayerBoardHover() {
        Position hoverPos = gameInfoManager.getPlayerBoardHover();
        if (hoverPos != null) {
            return new int[]{hoverPos.getRow(), hoverPos.getCol()};
        }
        return null;
    }
    
    /**
     * Gets global mouse position in a simple format (for debugging).
     * @return Array with [x, y] coordinates, or null if unavailable
     */
    public int[] getSimpleGlobalMousePosition() {
        Point mousePos = gameInfoManager.getGlobalMousePosition();
        if (mousePos != null) {
            return new int[]{mousePos.x, mousePos.y};
        }
        return null;
    }
    
    /**
     * Gets a summary of ship positions for quick access.
     * @return String array with ship summaries
     */
    public String[] getShipSummaries() {
        Map<String, List<GameInfoManager.ShipInfo>> allShips = gameInfoManager.getAllShipPositions();
        
        List<String> summaries = new java.util.ArrayList<>();
        
        // Player ships
        for (GameInfoManager.ShipInfo ship : allShips.get("player")) {
            summaries.add("PLAYER - " + ship.getName() + " at " + ship.getStartPosition() + 
                         " (" + (ship.isVertical() ? "V" : "H") + ")" + 
                         (ship.isSunk() ? " [SUNK]" : " [ALIVE]"));
        }
        
        // Opponent ships
        for (GameInfoManager.ShipInfo ship : allShips.get("opponent")) {
            summaries.add("OPPONENT - " + ship.getName() + " at " + ship.getStartPosition() + 
                         " (" + (ship.isVertical() ? "V" : "H") + ")" + 
                         (ship.isSunk() ? " [SUNK]" : " [ALIVE]"));
        }
        
        return summaries.toArray(new String[0]);
    }
} 