package com.batalhanaval.ui;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import com.batalhanaval.Constants;
import com.batalhanaval.core.Board;
import com.batalhanaval.core.Position;
import com.batalhanaval.core.Ship;

/**
 * Painel para a fase de jogo, mostrando os dois tabuleiros e permitindo ataques.
 */
public class GamePanel extends JPanel {
    
    private MainWindow mainWindow;
    private Board playerBoard;
    private Board opponentBoard;
    private BoardPanel playerBoardPanel;
    private BoardPanel opponentBoardPanel;
    
    private JPanel controlPanel;
    private JLabel turnLabel;
    private JButton surrenderButton;
    
    private boolean isPlayerTurn = true;
    
    /**
     * Construtor do painel de jogo.
     * @param mainWindow Referência para a janela principal.
     */
    public GamePanel(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        // O playerBoard será definido pelo MainWindow.showGamePanel
        this.playerBoard = new Board(Constants.BOARD_SIZE);
        this.opponentBoard = new Board(Constants.BOARD_SIZE); // Tabuleiro do oponente
        
        // Posiciona os navios do oponente aleatoriamente
        setupOpponentBoard();
        
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setBackground(Color.BLACK);
        
        setupBoardsPanel();
        setupControlPanel();
        
        add(controlPanel, BorderLayout.NORTH);
    }
    
    /**
     * Configura o tabuleiro do oponente com navios posicionados aleatoriamente.
     */
    private void setupOpponentBoard() {
        List<Ship> opponentShips = createStandardShips();
        
        for (Ship ship : opponentShips) {
            boolean placed = false;
            int attempts = 0;
            
            while (!placed && attempts < 100) {
                attempts++;
                int row = (int) (Math.random() * Constants.BOARD_SIZE);
                int col = (int) (Math.random() * Constants.BOARD_SIZE);
                boolean vertical = Math.random() > 0.5;
                
                // Usa o método placeShip do Board diretamente
                if (opponentBoard.placeShip(new Ship(ship.getName(), ship.getSize()), new Position(row, col), vertical)) {
                    placed = true;
                }
            }
            // Se não conseguir posicionar após 100 tentativas, pode haver um problema
            if (!placed) {
                System.err.println("Aviso: Não foi possível posicionar o navio do oponente: " + ship.getName());
            }
        }
        System.out.println("Tabuleiro do oponente configurado com navios aleatórios.");
    }
    
    /**
     * Cria a lista padrão de navios.
     */
    private List<Ship> createStandardShips() {
        List<Ship> ships = new ArrayList<>();
        ships.add(new Ship("Porta-Aviões", Constants.CARRIER_SIZE));
        ships.add(new Ship("Encouraçado", Constants.BATTLESHIP_SIZE));
        ships.add(new Ship("Cruzador", Constants.CRUISER_SIZE));
        ships.add(new Ship("Submarino", Constants.SUBMARINE_SIZE));
        ships.add(new Ship("Destroyer", Constants.DESTROYER_SIZE));
        return ships;
    }

    
    /**
     * Configura o painel com os dois tabuleiros (jogador e oponente).
     */
    private void setupBoardsPanel() {
        JPanel boardsPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        boardsPanel.setBackground(Color.BLACK);
        
        // Painel do jogador
        JPanel playerPanel = new JPanel(new BorderLayout());
        playerPanel.setBackground(Color.BLACK);
        JLabel playerLabel = new JLabel("Seu Tabuleiro", SwingConstants.CENTER);
        playerLabel.setFont(new Font("Arial", Font.BOLD, 14));
        playerLabel.setForeground(Color.WHITE);
        
        // Cria o BoardPanel para o jogador (o tabuleiro real será definido depois)
        playerBoardPanel = new BoardPanel(playerBoard, false);
        
        playerPanel.add(playerLabel, BorderLayout.NORTH);
        playerPanel.add(playerBoardPanel, BorderLayout.CENTER);
        
        // Painel do oponente
        JPanel opponentPanel = new JPanel(new BorderLayout());
        opponentPanel.setBackground(Color.BLACK);
        JLabel opponentLabel = new JLabel("Tabuleiro do Oponente", SwingConstants.CENTER);
        opponentLabel.setFont(new Font("Arial", Font.BOLD, 14));
        opponentLabel.setForeground(Color.WHITE);
        
        opponentBoardPanel = new BoardPanel(opponentBoard, true);
        opponentBoardPanel.setClickHandler(this::handleAttackClick);
        
        opponentPanel.add(opponentLabel, BorderLayout.NORTH);
        opponentPanel.add(opponentBoardPanel, BorderLayout.CENTER);
        
        boardsPanel.add(playerPanel);
        boardsPanel.add(opponentPanel);
        
        add(boardsPanel, BorderLayout.CENTER);
    }
    
    /**
     * Configura o painel de controle superior.
     */
    private void setupControlPanel() {
        controlPanel = new JPanel(new BorderLayout());
        controlPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        controlPanel.setBackground(Color.BLACK);
        
        turnLabel = new JLabel("Seu turno: Ataque o tabuleiro do oponente", SwingConstants.CENTER);
        turnLabel.setFont(new Font("Arial", Font.BOLD, 16));
        turnLabel.setForeground(Color.WHITE);
        
        surrenderButton = new JButton("Render-se");
        surrenderButton.addActionListener(e -> handleSurrender());
        surrenderButton.setBackground(new Color(50, 50, 50));
        surrenderButton.setForeground(Color.WHITE);
        
        controlPanel.add(turnLabel, BorderLayout.CENTER);
        controlPanel.add(surrenderButton, BorderLayout.EAST);
    }
    
    /**
     * Handler para cliques no tabuleiro do oponente (ataques).
     */
    private void handleAttackClick(int row, int col) {
        if (!isPlayerTurn) {
            mainWindow.updateStatusMessage("Aguarde seu turno!");
            return;
        }
        
        Position attackPos = new Position(row, col);
        
        // Verifica se a posição já foi atacada usando a lista de posições atacadas
        for (Position pos : opponentBoard.getAttackedPositions()) {
            if (pos.equals(attackPos)) {
                mainWindow.updateStatusMessage("Posição já atacada! Escolha outra posição.");
                return;
            }
        }
        
        // Simula envio do ataque via rede (será implementado depois)
        mainWindow.updateStatusMessage("Enviando ataque para a posição " + attackPos);
        
        // Processa o ataque NO TABULEIRO REAL DO OPONENTE
        String result = opponentBoard.processAttack(attackPos);
        
        // Processa o resultado do ataque
        if (result.equals(Constants.ATTACK_HIT) || result.equals(Constants.ATTACK_SUNK)) {
            String message = result.equals(Constants.ATTACK_SUNK) ? 
                            "Afundou um navio inimigo!" : "Acertou um navio inimigo!";
            mainWindow.updateStatusMessage(message);
            
            // Verifica se o jogo acabou (jogador venceu)
            if (opponentBoard.areAllShipsSunk()) {
                handleGameOver(true); // Jogador venceu
                return;
            }
            
        } else if (result.equals(Constants.ATTACK_MISS)) {
            mainWindow.updateStatusMessage("Água! Você errou.");
        } else {
             // ATTACK_INVALID (não deveria acontecer aqui devido à checagem anterior)
            mainWindow.updateStatusMessage("Ataque inválido?");
            return; 
        }
        
        // Atualiza o tabuleiro do oponente para mostrar o resultado do ataque
        opponentBoardPanel.repaint();
        
        // Troca o turno
        setPlayerTurn(false);
        
        // Simula o turno do oponente (será implementado depois)
        SwingUtilities.invokeLater(() -> {
            try {
                Thread.sleep(1500); // Simula um pequeno atraso
                simulateOpponentTurn();
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        });
    }
    
    /**
     * Simula o turno do oponente (será implementado com a rede).
     */
    private void simulateOpponentTurn() {
        // Só simula se ainda for turno do oponente (evita dupla execução)
        if (isPlayerTurn) return;
        
        mainWindow.updateStatusMessage("Oponente está atacando...");
        
        // Simula um ataque aleatório do oponente
        boolean validAttack = false;
        Position attackPos = null;
        
        while (!validAttack) {
            int row = (int) (Math.random() * Constants.BOARD_SIZE);
            int col = (int) (Math.random() * Constants.BOARD_SIZE);
            attackPos = new Position(row, col);
            
            boolean alreadyAttacked = false;
            for (Position pos : playerBoard.getAttackedPositions()) {
                if (pos.equals(attackPos)) {
                    alreadyAttacked = true;
                    break;
                }
            }
            
            if (!alreadyAttacked) {
                validAttack = true;
            }
        }
        
        // Processa o ataque no tabuleiro do jogador
        String result = playerBoard.processAttack(attackPos);
        
        // Atualiza o tabuleiro do jogador para mostrar o resultado do ataque do oponente
        playerBoardPanel.repaint();
        
        // Atualiza a mensagem de status baseada no resultado
        if (result.equals(Constants.ATTACK_HIT) || result.equals(Constants.ATTACK_SUNK)) {
            String message = result.equals(Constants.ATTACK_SUNK) ? 
                           "Oponente afundou um navio seu na posição " + attackPos : 
                           "Oponente acertou seu navio na posição " + attackPos;
            mainWindow.updateStatusMessage(message);
            
             // Verifica se o jogo acabou (oponente venceu)
            if (playerBoard.areAllShipsSunk()) {
                handleGameOver(false); // Oponente venceu
                return;
            }
            
        } else {
            mainWindow.updateStatusMessage("Oponente errou o tiro na posição " + attackPos);
        }
        
        // Devolve o turno para o jogador
        setPlayerTurn(true);
    }
    
    /**
     * Define o turno atual.
     */
    public void setPlayerTurn(boolean isPlayerTurn) {
        this.isPlayerTurn = isPlayerTurn;
        
        if (isPlayerTurn) {
            turnLabel.setText("Seu turno: Ataque o tabuleiro do oponente");
        } else {
            turnLabel.setText("Turno do oponente: Aguarde...");
        }
    }
    
    /**
     * Processa fim de jogo.
     */
    private void handleGameOver(boolean playerWon) {
        setPlayerTurn(false); // Impede mais jogadas
        String message = playerWon ? "VOCÊ VENCEU! Todos os navios inimigos foram destruídos." 
                                  : "VOCÊ PERDEU! Todos os seus navios foram destruídos.";
        
        JOptionPane.showMessageDialog(this, message, "Fim de Jogo", 
                                     playerWon ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.WARNING_MESSAGE);
        
        // TODO: Implementar opção de jogar novamente ou fechar?
        // Por enquanto, apenas informa e deixa a tela como está
        // mainWindow.showSetupPanel(); // Não volta mais para o setup automaticamente
        mainWindow.updateStatusMessage("Fim de jogo. " + message);
    }
    
    /**
     * Handler para botão de rendição.
     */
    private void handleSurrender() {
        if (!isPlayerTurn) {
            mainWindow.updateStatusMessage("Aguarde seu turno para se render.");
            return;
        }
        int option = JOptionPane.showConfirmDialog(this, 
                                                  "Tem certeza que deseja se render?", 
                                                  "Confirmação de Rendição", 
                                                  JOptionPane.YES_NO_OPTION,
                                                  JOptionPane.QUESTION_MESSAGE);
        
        if (option == JOptionPane.YES_OPTION) {
            // Implementar lógica de rendição (envio de mensagem ao oponente, etc.)
            // Por enquanto, apenas encerra o jogo como derrota
             handleGameOver(false);
        }
    }
    
    /**
     * Atualiza o tabuleiro do jogador.
     */
    public void setPlayerBoard(Board board) {
        this.playerBoard = board;
        playerBoardPanel.setBoard(board);
        playerBoardPanel.repaint();
    }
    
    /**
     * Atualiza o tabuleiro do oponente.
     */
    public void setOpponentBoard(Board board) {
        this.opponentBoard = board;
        // Garante que o oponente tenha navios (caso venha de uma fonte externa)
        if (this.opponentBoard.getShips().isEmpty()) {
            setupOpponentBoard(); 
        }
        opponentBoardPanel.setBoard(board);
        opponentBoardPanel.repaint();
    }
    
} 