package com.batalhanaval.network;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

/**
 * Manages network connections and communications for the Battleship game.
 * Handles both TCP and UDP connections.
 */
public class NetworkManager {
    
    // Executors for managing network threads
    private ExecutorService executorService;
    
    // TCP components
    private ServerSocket tcpServerSocket;
    private Socket tcpClientSocket;
    private PrintWriter tcpOut;
    private BufferedReader tcpIn;
    
    // UDP components
    private DatagramSocket udpSocket;
    private InetAddress remoteAddress;
    private int remoteUdpPort;
    private int localUdpPort;
    
    // Connection state
    private boolean isServer = false;
    private boolean isConnected = false;
    
    // Game manager integration
    private NetworkGameManager gameManager;
    
    /**
     * Constructor for the NetworkManager.
     */
    public NetworkManager() {
        // Create thread pool for network operations
        executorService = Executors.newCachedThreadPool();
    }
    
    /**
     * Starts a server that listens for client connections.
     * 
     * @param tcpPort Port to listen for TCP connections
     * @param udpPort Port to listen for UDP messages
     * @param statusUpdater Consumer that handles status messages
     * @throws IOException If there's an error starting the server
     */
    public void startServer(int tcpPort, int udpPort, Consumer<String> statusUpdater) throws IOException {
        isServer = true;
        
        // Start TCP server socket
        tcpServerSocket = new ServerSocket(tcpPort);
        statusUpdater.accept("TCP Server started on port " + tcpPort);
        
        // Start UDP socket
        udpSocket = new DatagramSocket(udpPort);
        localUdpPort = udpPort;
        statusUpdater.accept("UDP Server started on port " + udpPort);
        
        // Accept client connection in a separate thread
        executorService.submit(() -> {
            try {
                statusUpdater.accept("Waiting for opponent to connect...");
                tcpClientSocket = tcpServerSocket.accept();
                
                // Get remote address and setup streams
                remoteAddress = tcpClientSocket.getInetAddress();
                statusUpdater.accept("Client connected from " + remoteAddress.getHostAddress());
                
                setupStreams(statusUpdater);
                isConnected = true;
                
                // Start listening for UDP messages
                startUdpListener(statusUpdater);
                
                // Start TCP message listener
                startTcpListener(statusUpdater);
                
                // Send local UDP port information to client
                sendTcpMessage("UDP_PORT:" + localUdpPort);
                
                // Wait a bit for client to set up UDP
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                
            } catch (IOException e) {
                statusUpdater.accept("Error accepting client connection: " + e.getMessage());
            }
        });
    }
    
    /**
     * Connects to a server.
     * 
     * @param host Server's hostname or IP address
     * @param tcpPort Server's TCP port
     * @param udpPort Server's expected UDP port (for sending messages to server)
     * @param statusUpdater Consumer that handles status messages
     * @throws IOException If there's an error connecting to the server
     */
    public void connectToServer(String host, int tcpPort, int udpPort, Consumer<String> statusUpdater) throws IOException {
        isServer = false;
        
        // Connect to TCP server
        statusUpdater.accept("Connecting to " + host + ":" + tcpPort + "...");
        tcpClientSocket = new Socket(host, tcpPort);
        
        // Setup UDP socket with a dynamic port (0)
        udpSocket = new DatagramSocket(0);  // Use port 0 to get any available port
        localUdpPort = udpSocket.getLocalPort();
        statusUpdater.accept("UDP Client started on port " + localUdpPort);
        
        // Get remote address
        remoteAddress = tcpClientSocket.getInetAddress();
        remoteUdpPort = udpPort;
        
        statusUpdater.accept("Connected to server!");
        
        // Setup streams
        setupStreams(statusUpdater);
        isConnected = true;
        
        // Send local UDP port to server for callbacks
        sendTcpMessage("CLIENT_UDP_PORT:" + localUdpPort);
        
        // Wait a bit for server to process the port information
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Start UDP listener
        startUdpListener(statusUpdater);
        
        // Start TCP message listener
        startTcpListener(statusUpdater);
        
        // Send initial UDP ping to establish connection
        executorService.submit(() -> {
            try {
                Thread.sleep(1000); // Wait for UDP listener to be ready
                for (int i = 0; i < 3; i++) {
                    if (sendUdpMessage(MessageProtocol.PING)) {
                        statusUpdater.accept("UDP handshake ping sent (attempt " + (i + 1) + ")");
                        Thread.sleep(500);
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
    }
    
    /**
     * Sets up input and output streams for TCP communication.
     * 
     * @param statusUpdater Consumer that handles status messages
     * @throws IOException If there's an error setting up the streams
     */
    private void setupStreams(Consumer<String> statusUpdater) throws IOException {
        // Setup output stream
        tcpOut = new PrintWriter(new BufferedWriter(
                 new OutputStreamWriter(tcpClientSocket.getOutputStream())), true);
        
        // Setup input stream
        tcpIn = new BufferedReader(
                new InputStreamReader(tcpClientSocket.getInputStream()));
        
        statusUpdater.accept("Communication streams established");
    }
    
    /**
     * Starts a listener for TCP messages.
     * 
     * @param statusUpdater Consumer that handles status messages
     */
    private void startTcpListener(Consumer<String> statusUpdater) {
        executorService.submit(() -> {
            try {
                String message;
                while ((message = tcpIn.readLine()) != null) {
                    final String receivedMessage = message;
                    statusUpdater.accept("TCP received: " + receivedMessage);
                    
                    // Handle UDP port information from client/server
                    if (receivedMessage.startsWith("UDP_PORT:")) {
                        String portStr = receivedMessage.substring(9);
                        try {
                            remoteUdpPort = Integer.parseInt(portStr);
                            statusUpdater.accept("Updated remote UDP port to: " + remoteUdpPort);
                        } catch (NumberFormatException e) {
                            statusUpdater.accept("Invalid UDP port received: " + portStr);
                        }
                    } else if (receivedMessage.startsWith("CLIENT_UDP_PORT:")) {
                        String portStr = receivedMessage.substring(16);
                        try {
                            remoteUdpPort = Integer.parseInt(portStr);
                            statusUpdater.accept("Updated client UDP port to: " + remoteUdpPort);
                        } catch (NumberFormatException e) {
                            statusUpdater.accept("Invalid client UDP port received: " + portStr);
                        }
                    } else {
                        // Process game messages via NetworkGameManager
                        if (gameManager != null) {
                            gameManager.processTcpMessage(receivedMessage);
                        }
                    }
                }
                
                // If we reach here, the connection was closed
                statusUpdater.accept("Connection closed by remote host");
                isConnected = false;
                
            } catch (IOException e) {
                statusUpdater.accept("Error reading TCP message: " + e.getMessage());
                isConnected = false;
            }
        });
    }
    
    /**
     * Starts a listener for UDP messages.
     * 
     * @param statusUpdater Consumer that handles status messages
     */
    private void startUdpListener(Consumer<String> statusUpdater) {
        executorService.submit(() -> {
            try {
                statusUpdater.accept("UDP listener started on port " + localUdpPort);
                while (isConnected) {
                    // Create new buffer for each message to avoid corruption
                    byte[] buffer = new byte[1024];
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    
                    // Receive packet (blocks until data is received)
                    udpSocket.receive(packet);
                    
                    // Process received data with explicit UTF-8 encoding
                    String receivedMessage = new String(packet.getData(), 0, packet.getLength(), 
                                                      java.nio.charset.StandardCharsets.UTF_8);
                    
                    // Validate minimum message length
                    if (receivedMessage.length() < 3) {
                        statusUpdater.accept("UDP message too short, ignoring: " + receivedMessage.length() + " bytes");
                        continue;
                    }
                    
                    // Don't log hover messages to avoid spam
                    if (!receivedMessage.startsWith("HOVER:")) {
                        statusUpdater.accept("UDP received: " + receivedMessage);
                    }
                    
                    // Store the sender's address and port for replies if needed
                    if (remoteAddress == null) {
                        remoteAddress = packet.getAddress();
                        remoteUdpPort = packet.getPort();
                        statusUpdater.accept("Updated remote UDP address: " + remoteAddress.getHostAddress() + ":" + remoteUdpPort);
                    }
                    
                    // Process UDP messages via NetworkGameManager
                    if (gameManager != null) {
                        gameManager.processUdpMessage(receivedMessage);
                    }
                }
            } catch (IOException e) {
                if (isConnected) {
                    statusUpdater.accept("Error reading UDP message: " + e.getMessage());
                }
            }
        });
    }
    
    /**
     * Sends a TCP message to the remote player.
     * 
     * @param message Message to send
     * @return True if the message was sent successfully, false otherwise
     */
    public boolean sendTcpMessage(String message) {
        if (!isConnected || tcpOut == null) {
            return false;
        }
        
        tcpOut.println(message);
        return !tcpOut.checkError(); // Returns true if no error occurred
    }
    
    /**
     * Sends a UDP message to the remote player.
     * 
     * @param message Message to send
     * @return True if the message was sent successfully, false otherwise
     */
    public boolean sendUdpMessage(String message) {
        if (!isConnected || udpSocket == null || remoteAddress == null) {
            return false;
        }
        
        try {
            // Use UTF-8 explicitly to avoid encoding issues
            byte[] buffer = message.getBytes(java.nio.charset.StandardCharsets.UTF_8);
            
            // Validate message size
            if (buffer.length > 1024) {
                System.err.println("UDP message too large: " + buffer.length + " bytes");
                return false;
            }
            
            DatagramPacket packet = new DatagramPacket(
                    buffer, buffer.length, remoteAddress, remoteUdpPort);
            udpSocket.send(packet);
            return true;
        } catch (IOException e) {
            System.err.println("Error sending UDP message: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Returns the local UDP port.
     * 
     * @return The local UDP port
     */
    public int getLocalUdpPort() {
        return localUdpPort;
    }
    
    /**
     * Sets the remote address for UDP communication.
     * 
     * @param address Remote address
     * @param udpPort Remote UDP port
     */
    public void setRemoteAddress(InetAddress address, int udpPort) {
        this.remoteAddress = address;
        this.remoteUdpPort = udpPort;
    }
    
    /**
     * Stops all network connections and cleans up resources.
     */
    public void stopNetwork() {
        isConnected = false;
        
        // Close TCP resources
        try {
            if (tcpOut != null) tcpOut.close();
            if (tcpIn != null) tcpIn.close();
            if (tcpClientSocket != null && !tcpClientSocket.isClosed()) tcpClientSocket.close();
            if (tcpServerSocket != null && !tcpServerSocket.isClosed()) tcpServerSocket.close();
        } catch (IOException e) {
            System.err.println("Error closing TCP resources: " + e.getMessage());
        }
        
        // Close UDP socket
        if (udpSocket != null && !udpSocket.isClosed()) {
            udpSocket.close();
        }
        
        // Shutdown executor service
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdownNow();
        }
    }
    
    /**
     * Checks if currently connected.
     * 
     * @return True if connected, false otherwise
     */
    public boolean isConnected() {
        return isConnected;
    }
    
    /**
     * Checks if running as a server.
     * 
     * @return True if server, false if client
     */
    public boolean isServer() {
        return isServer;
    }
    
    /**
     * Sets the network game manager for message processing.
     * @param gameManager Network game manager
     */
    public void setGameManager(NetworkGameManager gameManager) {
        this.gameManager = gameManager;
    }
    
    /**
     * Gets the network game manager.
     * @return Network game manager
     */
    public NetworkGameManager getGameManager() {
        return gameManager;
    }
    
    /**
     * Tests UDP connectivity with the remote peer.
     * @param statusUpdater Consumer for status messages
     * @return True if UDP connection is working
     */
    public boolean testUdpConnectivity(Consumer<String> statusUpdater) {
        if (!isConnected || udpSocket == null || remoteAddress == null) {
            statusUpdater.accept("UDP test failed: Not properly connected");
            return false;
        }
        
        try {
            // Send test message
            String testMessage = "UDP_TEST:" + System.currentTimeMillis();
            byte[] buffer = testMessage.getBytes(java.nio.charset.StandardCharsets.UTF_8);
            
            DatagramPacket packet = new DatagramPacket(
                    buffer, buffer.length, remoteAddress, remoteUdpPort);
            udpSocket.send(packet);
            
            statusUpdater.accept("UDP test message sent: " + testMessage);
            return true;
            
        } catch (IOException e) {
            statusUpdater.accept("UDP test failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Gets diagnostic information about the current network state.
     * @return Diagnostic string
     */
    public String getNetworkDiagnostics() {
        StringBuilder diag = new StringBuilder();
        diag.append("=== Network Diagnostics ===\n");
        diag.append("Is Server: ").append(isServer).append("\n");
        diag.append("Is Connected: ").append(isConnected).append("\n");
        diag.append("Local UDP Port: ").append(localUdpPort).append("\n");
        diag.append("Remote UDP Port: ").append(remoteUdpPort).append("\n");
        diag.append("Remote Address: ").append(remoteAddress != null ? remoteAddress.getHostAddress() : "null").append("\n");
        diag.append("UDP Socket: ").append(udpSocket != null ? "OK" : "null").append("\n");
        diag.append("TCP Socket: ").append(tcpClientSocket != null && !tcpClientSocket.isClosed() ? "OK" : "closed/null").append("\n");
        return diag.toString();
    }
}