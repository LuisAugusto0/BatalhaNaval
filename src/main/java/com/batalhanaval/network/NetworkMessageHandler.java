package com.batalhanaval.network;

import java.util.function.Consumer;

import com.batalhanaval.core.Position;

/**
 * Handles incoming network messages for multiplayer Battleship game.
 * Parses and dispatches messages to appropriate handlers.
 */
public class NetworkMessageHandler {
    
    // Message handlers
    private Consumer<String> statusUpdater;
    private GameMessageListener gameMessageListener;
    private HoverMessageListener hoverMessageListener;
    
    /**
     * Interface for handling game-related messages (TCP).
     */
    public interface GameMessageListener {
        void onReadyToStart();
        void onGameStart(boolean isFirstPlayer);
        void onAttackReceived(int row, int col);
        void onAttackResult(String result, int row, int col);
        void onTurnEnd();
        void onGameOver(boolean isWinner);
        void onOpponentDisconnect();
        void onOpponentSurrender();
    }
    
    /**
     * Interface for handling hover messages (UDP).
     */
    public interface HoverMessageListener {
        void onHoverReceived(Position position);
        void onPingReceived();
    }
    
    /**
     * Constructor for NetworkMessageHandler.
     * @param statusUpdater Consumer for status messages
     */
    public NetworkMessageHandler(Consumer<String> statusUpdater) {
        this.statusUpdater = statusUpdater;
    }
    
    /**
     * Sets the game message listener.
     * @param listener Listener for game messages
     */
    public void setGameMessageListener(GameMessageListener listener) {
        this.gameMessageListener = listener;
    }
    
    /**
     * Sets the hover message listener.
     * @param listener Listener for hover messages
     */
    public void setHoverMessageListener(HoverMessageListener listener) {
        this.hoverMessageListener = listener;
    }
    
    /**
     * Processes a TCP message.
     * @param message Received TCP message
     */
    public void processTcpMessage(String message) {
        if (message == null || message.trim().isEmpty()) {
            return;
        }
        
        // Validate message format
        if (!MessageProtocol.isValidMessage(message)) {
            statusUpdater.accept("Invalid TCP message received: " + message);
            return;
        }
        
        String command = MessageProtocol.getCommand(message);
        String[] parts = MessageProtocol.getMessageParts(message);
        
        statusUpdater.accept("TCP received: " + message);
        
        // Dispatch to appropriate handler
        if (gameMessageListener != null) {
            try {
                switch (command) {
                    case MessageProtocol.READY_TO_START:
                        gameMessageListener.onReadyToStart();
                        break;
                        
                    case MessageProtocol.GAME_START:
                        boolean isFirstPlayer = parts[0].equals(MessageProtocol.FIRST_PLAYER);
                        gameMessageListener.onGameStart(isFirstPlayer);
                        break;
                        
                    case MessageProtocol.ATTACK:
                        int[] attackCoords = MessageProtocol.parseCoordinates(parts[0]);
                        if (attackCoords != null) {
                            gameMessageListener.onAttackReceived(attackCoords[0], attackCoords[1]);
                        }
                        break;
                        
                    case MessageProtocol.ATTACK_RESULT:
                        String result = parts[0];
                        int[] resultCoords = MessageProtocol.parseCoordinates(parts[1]);
                        if (resultCoords != null) {
                            gameMessageListener.onAttackResult(result, resultCoords[0], resultCoords[1]);
                        }
                        break;
                        
                    case MessageProtocol.TURN_END:
                        gameMessageListener.onTurnEnd();
                        break;
                        
                    case MessageProtocol.GAME_OVER:
                        boolean isWinner = parts[0].equals(MessageProtocol.WINNER);
                        gameMessageListener.onGameOver(isWinner);
                        break;
                        
                    case MessageProtocol.DISCONNECT:
                        gameMessageListener.onOpponentDisconnect();
                        break;
                        
                    case MessageProtocol.SURRENDER:
                        gameMessageListener.onOpponentSurrender();
                        break;
                        
                    default:
                        statusUpdater.accept("Unknown TCP command: " + command);
                        break;
                }
            } catch (Exception e) {
                statusUpdater.accept("Error processing TCP message: " + e.getMessage());
            }
        }
    }
    
    /**
     * Processes a UDP message.
     * @param message Received UDP message
     */
    public void processUdpMessage(String message) {
        if (message == null || message.trim().isEmpty()) {
            return;
        }
        
        // Additional validation for UDP messages
        if (message.length() > 1000) {
            statusUpdater.accept("UDP message too large, ignoring: " + message.length() + " chars");
            return;
        }
        
        // Check for invalid characters that might indicate corruption
        if (message.contains("\u0000") || message.contains("\uFFFD")) {
            statusUpdater.accept("UDP message contains invalid characters, ignoring");
            return;
        }
        
        // Validate message format
        if (!MessageProtocol.isValidMessage(message)) {
            statusUpdater.accept("Invalid UDP message received: " + message);
            return;
        }
        
        String command = MessageProtocol.getCommand(message);
        String[] parts = MessageProtocol.getMessageParts(message);
        
        // Don't log every hover message to avoid spam
        if (!command.equals(MessageProtocol.HOVER)) {
            statusUpdater.accept("UDP received: " + message);
        }
        
        // Dispatch to appropriate handler
        if (hoverMessageListener != null) {
            try {
                switch (command) {
                    case MessageProtocol.HOVER:
                        Position hoverPosition = null;
                        if (!parts[0].equals(MessageProtocol.NULL_VALUE)) {
                            int[] hoverCoords = MessageProtocol.parseCoordinates(parts[0]);
                            if (hoverCoords != null) {
                                hoverPosition = new Position(hoverCoords[0], hoverCoords[1]);
                            }
                        }
                        hoverMessageListener.onHoverReceived(hoverPosition);
                        break;
                        
                    case MessageProtocol.PING:
                        hoverMessageListener.onPingReceived();
                        break;
                        
                    case MessageProtocol.PONG:
                        // Just acknowledge pong, no action needed
                        break;
                        
                    default:
                        statusUpdater.accept("Unknown UDP command: " + command);
                        break;
                }
            } catch (Exception e) {
                statusUpdater.accept("Error processing UDP message: " + e.getMessage());
            }
        }
    }
    
    /**
     * Creates and sends a ready message.
     * @param networkManager Network manager to send through
     * @return True if sent successfully
     */
    public boolean sendReadyToStart(NetworkManager networkManager) {
        return networkManager.sendTcpMessage(MessageProtocol.READY_TO_START);
    }
    
    /**
     * Creates and sends an attack message.
     * @param networkManager Network manager to send through
     * @param row Row coordinate
     * @param col Column coordinate
     * @return True if sent successfully
     */
    public boolean sendAttack(NetworkManager networkManager, int row, int col) {
        String message = MessageProtocol.createAttackMessage(row, col);
        return networkManager.sendTcpMessage(message);
    }
    
    /**
     * Creates and sends an attack result message.
     * @param networkManager Network manager to send through
     * @param result Attack result (HIT, MISS, SUNK)
     * @param row Row coordinate
     * @param col Column coordinate
     * @return True if sent successfully
     */
    public boolean sendAttackResult(NetworkManager networkManager, String result, int row, int col) {
        String message = MessageProtocol.createAttackResultMessage(result, row, col);
        return networkManager.sendTcpMessage(message);
    }
    
    /**
     * Creates and sends a turn end message.
     * @param networkManager Network manager to send through
     * @return True if sent successfully
     */
    public boolean sendTurnEnd(NetworkManager networkManager) {
        return networkManager.sendTcpMessage(MessageProtocol.TURN_END);
    }
    
    /**
     * Creates and sends a game over message.
     * @param networkManager Network manager to send through
     * @param isWinner True if this player won
     * @return True if sent successfully
     */
    public boolean sendGameOver(NetworkManager networkManager, boolean isWinner) {
        String message = MessageProtocol.createGameOverMessage(isWinner);
        return networkManager.sendTcpMessage(message);
    }
    
    /**
     * Creates and sends a surrender message.
     * @param networkManager Network manager to send through
     * @return True if sent successfully
     */
    public boolean sendSurrender(NetworkManager networkManager) {
        return networkManager.sendTcpMessage(MessageProtocol.SURRENDER);
    }
    
    /**
     * Creates and sends a hover message.
     * @param networkManager Network manager to send through
     * @param position Hover position or null to clear hover
     * @return True if sent successfully
     */
    public boolean sendHover(NetworkManager networkManager, Position position) {
        String message;
        if (position != null) {
            message = MessageProtocol.createHoverMessage(position.getRow(), position.getCol());
        } else {
            message = MessageProtocol.createHoverMessage(-1, -1);
        }
        return networkManager.sendUdpMessage(message);
    }
    
    /**
     * Creates and sends a ping message.
     * @param networkManager Network manager to send through
     * @return True if sent successfully
     */
    public boolean sendPing(NetworkManager networkManager) {
        return networkManager.sendUdpMessage(MessageProtocol.PING);
    }
    
    /**
     * Creates and sends a pong response.
     * @param networkManager Network manager to send through
     * @return True if sent successfully
     */
    public boolean sendPong(NetworkManager networkManager) {
        return networkManager.sendUdpMessage(MessageProtocol.PONG);
    }
} 