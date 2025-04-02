package com.batalhanaval.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.BiConsumer;

import com.batalhanaval.Constants;
import com.batalhanaval.App;
import com.batalhanaval.core.Board;
import com.batalhanaval.core.Position;
import com.batalhanaval.core.Ship;

/**
 * Panel for drawing and interacting with the game board.
 */
public class BoardPanel extends JPanel {
    
    private Board board;
    private boolean isOpponentBoard;
    private BiConsumer<Integer, Integer> clickHandler;
    
    private final int CELL_SIZE = 30;
    private final int GRID_SIZE = Constants.BOARD_SIZE;
    private final int BOARD_PIXEL_SIZE = GRID_SIZE * CELL_SIZE;
    
    private final int LINE_THICKNESS = 4; // Thickness of the orientation line
    private final int COORDINATE_MARGIN = 20; // Margin for drawing coordinates
    
    // Panel components
    private JPanel boardPanel;
    private JPanel statusPanel;
    
    // Colors for the board
    private final Color WATER_COLOR = Color.WHITE;        // Water (undiscovered square)
    private final Color SHIP_COLOR = Color.DARK_GRAY;     // Ship (visible on player's board)
    private final Color MISS_COLOR = Color.BLUE;          // Miss (water hit)
    private final Color HIT_COLOR = Color.RED;            // Hit (ship hit)
    private final Color DEBUG_SHIP_COLOR = Color.YELLOW;  // Color to reveal opponent's ships in debug mode
    private final Color ORIENTATION_LINE_COLOR = Color.BLACK; // Color for the orientation line
    private final Color ALIVE_COLOR = new Color(0, 180, 0); // Green for alive ships
    private final Color SUNK_COLOR = new Color(180, 0, 0);  // Red for sunk ships
    private final Color COORD_COLOR = new Color(150, 200, 255); // Light blue for coordinates
    
    /**
     * Constructor for the board panel.
     * @param board Board to be displayed.
     * @param isOpponentBoard Flag indicating if this is the opponent's board.
     */
    public BoardPanel(Board board, boolean isOpponentBoard) {
        this.board = board;
        this.isOpponentBoard = isOpponentBoard;
        
        setLayout(new BorderLayout());
        
        createComponents();
    }
    
    /**
     * Creates and initializes all components of the board panel.
     */
    private void createComponents() {
        // Create the board drawing area
        boardPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawBoard((Graphics2D) g);
            }
        };
        
        // Adding margins for coordinates
        int panelSize = BOARD_PIXEL_SIZE + COORDINATE_MARGIN * 2;
        boardPanel.setPreferredSize(new Dimension(panelSize, panelSize));
        boardPanel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        boardPanel.setBackground(Color.BLACK);
        
        // Add mouse listener to the board panel
        boardPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (clickHandler != null) {
                    // Adjust coordinates to account for the margin
                    int col = (e.getX() - COORDINATE_MARGIN) / CELL_SIZE;
                    int row = (e.getY() - COORDINATE_MARGIN) / CELL_SIZE;
                    
                    if (col >= 0 && col < GRID_SIZE && row >= 0 && row < GRID_SIZE) {
                        clickHandler.accept(row, col);
                    }
                }
            }
        });
        
        // Create the status panel for ships
        statusPanel = new JPanel();
        statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.Y_AXIS));
        statusPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        statusPanel.setBackground(Color.BLACK);
        
        // Add components to main panel
        add(boardPanel, BorderLayout.CENTER);
        add(statusPanel, BorderLayout.SOUTH);
        
        // Initial update of the status panel
        updateStatusPanel();
    }
    
    /**
     * Sets the handler for clicks on the board.
     * @param handler Function that receives coordinates (row, column)
     */
    public void setClickHandler(BiConsumer<Integer, Integer> handler) {
        this.clickHandler = handler;
    }
    
    /**
     * Updates the board to be displayed.
     * @param board New board.
     */
    public void setBoard(Board board) {
        this.board = board;
        updateStatusPanel();
        repaint();
    }
    
    /**
     * Updates the ship status panel to show current ship states.
     */
    public void updateShipStatusPanel() {
        updateStatusPanel();
    }
    
    /**
     * Updates the status panel with ship information.
     */
    private void updateStatusPanel() {
        if (board == null) return;
        
        statusPanel.removeAll();
        
        // Title
        JLabel titleLabel = new JLabel(isOpponentBoard ? "Enemy Fleet Status:" : "Your Fleet Status:");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        statusPanel.add(titleLabel);
        statusPanel.add(Box.createVerticalStrut(5));
        
        // Create grid for ship status
        JPanel shipGrid = new JPanel(new GridLayout(0, 2, 10, 2));
        shipGrid.setBackground(Color.BLACK);
        
        // Add each ship and its status
        for (Ship ship : board.getShips()) {
            JLabel nameLabel = new JLabel(ship.getName() + ":");
            nameLabel.setForeground(Color.WHITE);
            shipGrid.add(nameLabel);
            
            boolean isSunk = ship.isSunk();
            JLabel statusLabel = new JLabel(isSunk ? "Sunk" : "Alive");
            statusLabel.setForeground(isSunk ? SUNK_COLOR : ALIVE_COLOR);
            statusLabel.setFont(new Font("Arial", Font.BOLD, 12));
            shipGrid.add(statusLabel);
        }
        
        statusPanel.add(shipGrid);
        statusPanel.revalidate();
        statusPanel.repaint();
    }
    
    /**
     * Main method to draw the board and all its elements.
     */
    private void drawBoard(Graphics2D g) {
        // Enable anti-aliasing
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Draw coordinates before the grid
        drawCoordinates(g);
        
        // Draw the grid and cells
        drawGrid(g);
        
        // Draw ships
        if (!isOpponentBoard) {
            drawShips(g);
        } else if (App.DEBUG_MODE) {
            drawOpponentShipsDebug(g);
        }
        
        // Draw attacks
        drawAttacks(g);
    }
    
    /**
     * Draws the board grid.
     */
    private void drawGrid(Graphics2D g) {
        // Fill cells with white (representing undiscovered water)
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                g.setColor(WATER_COLOR);
                g.fillRect(
                    col * CELL_SIZE + COORDINATE_MARGIN + 1, 
                    row * CELL_SIZE + COORDINATE_MARGIN + 1, 
                    CELL_SIZE - 1, 
                    CELL_SIZE - 1
                );
            }
        }
        
        // Draw grid lines
        g.setColor(Color.DARK_GRAY);
        
        // Horizontal lines
        for (int row = 0; row <= GRID_SIZE; row++) {
            g.drawLine(
                COORDINATE_MARGIN, 
                row * CELL_SIZE + COORDINATE_MARGIN, 
                BOARD_PIXEL_SIZE + COORDINATE_MARGIN, 
                row * CELL_SIZE + COORDINATE_MARGIN
            );
        }
        
        // Vertical lines
        for (int col = 0; col <= GRID_SIZE; col++) {
            g.drawLine(
                col * CELL_SIZE + COORDINATE_MARGIN, 
                COORDINATE_MARGIN, 
                col * CELL_SIZE + COORDINATE_MARGIN, 
                BOARD_PIXEL_SIZE + COORDINATE_MARGIN
            );
        }
    }
    
    /**
     * Draws the coordinates (A-J, 1-10).
     */
    private void drawCoordinates(Graphics2D g) {
        g.setFont(new Font("Arial", Font.BOLD, 14));
        
        // Draw column labels (A-J)
        g.setColor(COORD_COLOR);
        for (int col = 0; col < GRID_SIZE; col++) {
            char letter = (char) ('A' + col);
            FontMetrics metrics = g.getFontMetrics();
            int width = metrics.stringWidth(String.valueOf(letter));
            
            g.drawString(
                String.valueOf(letter), 
                col * CELL_SIZE + COORDINATE_MARGIN + (CELL_SIZE - width) / 2, 
                COORDINATE_MARGIN - 5
            );
        }
        
        // Draw row labels (1-10)
        for (int row = 0; row < GRID_SIZE; row++) {
            String number = String.valueOf(row + 1);
            FontMetrics metrics = g.getFontMetrics();
            int width = metrics.stringWidth(number);
            int height = metrics.getHeight();
            
            g.drawString(
                number, 
                COORDINATE_MARGIN - width - 5, 
                row * CELL_SIZE + COORDINATE_MARGIN + (CELL_SIZE + height) / 2 - 2
            );
        }
    }
    
    /**
     * Draws the PLAYER'S ships on the board.
     */
    private void drawShips(Graphics2D g) {
        g.setColor(SHIP_COLOR);
        
        for (Ship ship : board.getShips()) {
            for (Position pos : ship.getPositions()) {
                int row = pos.getRow();
                int col = pos.getCol();
                
                char cellState = board.getCellState(pos);
                
                // Only draw ship parts that haven't been hit
                if (cellState != Constants.HIT && cellState != Constants.SUNK) {
                    g.fillRect(
                        col * CELL_SIZE + COORDINATE_MARGIN + 1, 
                        row * CELL_SIZE + COORDINATE_MARGIN + 1, 
                        CELL_SIZE - 1, 
                        CELL_SIZE - 1
                    );
                }
            }
        }
    }
    
    /**
     * Draws the OPPONENT'S ships in yellow for debug.
     */
    private void drawOpponentShipsDebug(Graphics2D g) {
        g.setColor(DEBUG_SHIP_COLOR);
        
        for (Ship ship : board.getShips()) {
            for (Position pos : ship.getPositions()) {
                int row = pos.getRow();
                int col = pos.getCol();
                
                char cellState = board.getCellState(pos);
                
                // Only draw ship parts that haven't been hit
                // (doesn't overlay red hits or blue misses)
                if (cellState != Constants.HIT && cellState != Constants.SUNK && cellState != Constants.MISS) {
                    g.fillRect(
                        col * CELL_SIZE + COORDINATE_MARGIN + 1, 
                        row * CELL_SIZE + COORDINATE_MARGIN + 1, 
                        CELL_SIZE - 1, 
                        CELL_SIZE - 1
                    );
                }
            }
        }
    }
    
    /**
     * Draws the attacks made (hits and misses).
     */
    private void drawAttacks(Graphics2D g) {
        for (Position pos : board.getAttackedPositions()) {
            int row = pos.getRow();
            int col = pos.getCol();
            
            char cellState = board.getCellState(pos);
            
            if (cellState == Constants.MISS) {
                // Misses: blue square (water hit)
                g.setColor(MISS_COLOR);
                g.fillRect(
                    col * CELL_SIZE + COORDINATE_MARGIN + 1, 
                    row * CELL_SIZE + COORDINATE_MARGIN + 1, 
                    CELL_SIZE - 1, 
                    CELL_SIZE - 1
                );
            } else if (cellState == Constants.HIT || cellState == Constants.SUNK) {
                // Hits: red square (ship hit)
                g.setColor(HIT_COLOR);
                g.fillRect(
                    col * CELL_SIZE + COORDINATE_MARGIN + 1, 
                    row * CELL_SIZE + COORDINATE_MARGIN + 1, 
                    CELL_SIZE - 1, 
                    CELL_SIZE - 1
                );
                
                // Draw orientation line based on ship's orientation
                Ship hitShip = findShipAtPosition(pos);
                if (hitShip != null) {
                    g.setColor(ORIENTATION_LINE_COLOR);
                    g.setStroke(new BasicStroke(LINE_THICKNESS));
                    
                    int x = col * CELL_SIZE + COORDINATE_MARGIN + 1;
                    int y = row * CELL_SIZE + COORDINATE_MARGIN + 1;
                    int width = CELL_SIZE - 1;
                    int height = CELL_SIZE - 1;
                    
                    if (hitShip.isVertical()) {
                        // Vertical ship - draw vertical line (down the middle)
                        g.drawLine(x + width/2, y, x + width/2, y + height);
                    } else {
                        // Horizontal ship - draw horizontal line (across the middle)
                        g.drawLine(x, y + height/2, x + width, y + height/2);
                    }
                    // Reset stroke to default
                    g.setStroke(new BasicStroke(1));
                }
            }
        }
    }
    
    /**
     * Finds a ship at a specific position.
     * @param position Position to check
     * @return The ship at the position or null if no ship is found
     */
    private Ship findShipAtPosition(Position position) {
        for (Ship ship : board.getShips()) {
            if (ship.containsPosition(position)) {
                return ship;
            }
        }
        return null;
    }
    
    @Override
    public void repaint() {
        super.repaint();
        
        if (boardPanel != null) {
            boardPanel.repaint();
        }
        
        if (statusPanel != null) {
            updateStatusPanel();
        }
    }
}