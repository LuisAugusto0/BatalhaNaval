package com.batalhanaval.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.BiConsumer;

import com.batalhanaval.Constants;
import com.batalhanaval.core.Board;
import com.batalhanaval.core.Position;
import com.batalhanaval.core.Ship;

/**
 * Painel para desenhar e interagir com o tabuleiro do jogo.
 */
public class BoardPanel extends JPanel {
    
    private Board board;
    private boolean isOpponentBoard;
    private BiConsumer<Integer, Integer> clickHandler;
    
    private final int CELL_SIZE = 30;
    private final int GRID_SIZE = Constants.BOARD_SIZE;
    private final int BOARD_PIXEL_SIZE = GRID_SIZE * CELL_SIZE;
    
    // Cores para o tabuleiro
    private final Color WATER_COLOR = Color.WHITE;        // Água (quadrado não descoberto)
    private final Color SHIP_COLOR = Color.DARK_GRAY;     // Navio
    private final Color MISS_COLOR = Color.BLUE;          // Erro (água)
    private final Color HIT_COLOR = Color.RED;            // Acerto (navio)
    
    /**
     * Construtor do painel do tabuleiro.
     * @param board Tabuleiro a ser exibido.
     * @param isOpponentBoard Flag que indica se este é o tabuleiro do oponente.
     */
    public BoardPanel(Board board, boolean isOpponentBoard) {
        this.board = board;
        this.isOpponentBoard = isOpponentBoard;
        
        setPreferredSize(new Dimension(BOARD_PIXEL_SIZE, BOARD_PIXEL_SIZE));
        setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        setBackground(Color.BLACK);
        
        // Adiciona listener de mouse para capturar cliques
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (clickHandler != null) {
                    int col = e.getX() / CELL_SIZE;
                    int row = e.getY() / CELL_SIZE;
                    
                    // Valida se está dentro do tabuleiro
                    if (col >= 0 && col < GRID_SIZE && row >= 0 && row < GRID_SIZE) {
                        clickHandler.accept(row, col);
                    }
                }
            }
        });
    }
    
    /**
     * Define o handler para cliques no tabuleiro.
     * @param handler Função que recebe coordenadas (linha, coluna)
     */
    public void setClickHandler(BiConsumer<Integer, Integer> handler) {
        this.clickHandler = handler;
    }
    
    /**
     * Atualiza o tabuleiro a ser exibido.
     * @param board Novo tabuleiro.
     */
    public void setBoard(Board board) {
        this.board = board;
        repaint();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Desenha o grid e as células
        drawGrid(g2d);
        
        // Desenha as letras e números das coordenadas
        drawCoordinates(g2d);
        
        // Desenha os navios (se não for tabuleiro do oponente)
        if (!isOpponentBoard) {
            drawShips(g2d);
        }
        
        // Desenha os ataques (acertos e erros)
        drawAttacks(g2d);
    }
    
    /**
     * Desenha o grid do tabuleiro.
     */
    private void drawGrid(Graphics2D g) {
        // Preenche as células com branco (representando água não descoberta)
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                g.setColor(WATER_COLOR);
                g.fillRect(col * CELL_SIZE + 1, row * CELL_SIZE + 1, CELL_SIZE - 1, CELL_SIZE - 1);
            }
        }
        
        // Desenha as linhas do grid
        g.setColor(Color.DARK_GRAY);
        
        // Linhas horizontais
        for (int row = 0; row <= GRID_SIZE; row++) {
            g.drawLine(0, row * CELL_SIZE, BOARD_PIXEL_SIZE, row * CELL_SIZE);
        }
        
        // Linhas verticais
        for (int col = 0; col <= GRID_SIZE; col++) {
            g.drawLine(col * CELL_SIZE, 0, col * CELL_SIZE, BOARD_PIXEL_SIZE);
        }
    }
    
    /**
     * Desenha as coordenadas (A-J, 1-10).
     */
    private void drawCoordinates(Graphics2D g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 12));
        
        // Coordenadas horizontais (A-J)
        for (int col = 0; col < GRID_SIZE; col++) {
            char letter = (char) ('A' + col);
            g.drawString(String.valueOf(letter), 
                         col * CELL_SIZE + CELL_SIZE/2 - 5, 
                         CELL_SIZE/4);
        }
        
        // Coordenadas verticais (1-10)
        for (int row = 0; row < GRID_SIZE; row++) {
            g.drawString(String.valueOf(row + 1), 
                         5, 
                         row * CELL_SIZE + CELL_SIZE/2 + 5);
        }
    }
    
    /**
     * Desenha os navios no tabuleiro.
     */
    private void drawShips(Graphics2D g) {
        g.setColor(SHIP_COLOR);
        
        for (Ship ship : board.getShips()) {
            for (Position pos : ship.getPositions()) {
                int row = pos.getRow();
                int col = pos.getCol();
                
                char cellState = board.getCellState(pos);
                
                // Só desenha navios que não foram atingidos ou desenha em outra cor
                if (cellState != Constants.HIT && cellState != Constants.SUNK) {
                    // Desenha cada parte do navio
                    g.fillRect(col * CELL_SIZE + 1, row * CELL_SIZE + 1, CELL_SIZE - 1, CELL_SIZE - 1);
                }
            }
        }
    }
    
    /**
     * Desenha os ataques realizados (acertos e erros).
     */
    private void drawAttacks(Graphics2D g) {
        for (Position pos : board.getAttackedPositions()) {
            int row = pos.getRow();
            int col = pos.getCol();
            
            char cellState = board.getCellState(pos);
            
            if (cellState == Constants.MISS) {
                // Erros: quadrado azul (água atingida)
                g.setColor(MISS_COLOR);
                g.fillRect(col * CELL_SIZE + 1, row * CELL_SIZE + 1, CELL_SIZE - 1, CELL_SIZE - 1);
            } else if (cellState == Constants.HIT || cellState == Constants.SUNK) {
                // Acertos: quadrado vermelho (navio atingido)
                g.setColor(HIT_COLOR);
                g.fillRect(col * CELL_SIZE + 1, row * CELL_SIZE + 1, CELL_SIZE - 1, CELL_SIZE - 1);
            }
        }
    }
} 