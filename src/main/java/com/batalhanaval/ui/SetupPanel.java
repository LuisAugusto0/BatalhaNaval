package com.batalhanaval.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import com.batalhanaval.Constants;
import com.batalhanaval.core.Board;
import com.batalhanaval.core.Position;
import com.batalhanaval.core.Ship;

/**
 * Painel para posicionamento dos navios no tabuleiro.
 */
public class SetupPanel extends JPanel {
    
    private MainWindow mainWindow;
    private Board playerBoard;
    private BoardPanel boardPanel;
    
    private JPanel controlPanel;
    private JComboBox<String> shipTypeCombo;
    private JRadioButton horizontalRadio;
    private JRadioButton verticalRadio;
    private JButton randomizeButton;
    private JButton startGameButton;
    
    private List<Ship> availableShips;
    private Ship selectedShip;
    private boolean isVertical = false;
    
    /**
     * Construtor do painel de configuração inicial.
     * @param mainWindow Referência para a janela principal.
     */
    public SetupPanel(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        this.playerBoard = new Board(Constants.BOARD_SIZE);
        this.availableShips = createAvailableShips();
        
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(Color.BLACK);
        
        // Painel do tabuleiro
        boardPanel = new BoardPanel(playerBoard, false);
        boardPanel.setClickHandler(this::handleBoardClick);
        
        // Painel de controle (direita)
        setupControlPanel();
        
        add(boardPanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.EAST);
        
        updateStartButtonState();
    }
    
    /**
     * Configura o painel de controle com as opções de posicionamento.
     */
    private void setupControlPanel() {
        controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        controlPanel.setPreferredSize(new Dimension(200, 400));
        controlPanel.setBackground(Color.BLACK);
        
        // Título
        JLabel titleLabel = new JLabel("Posicione seus Navios");
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(Color.WHITE);
        controlPanel.add(titleLabel);
        controlPanel.add(Box.createVerticalStrut(20));
        
        // Seleção de navio
        JLabel shipLabel = new JLabel("Tipo de Navio:");
        shipLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        shipLabel.setForeground(Color.WHITE);
        controlPanel.add(shipLabel);
        controlPanel.add(Box.createVerticalStrut(5));
        
        // Cria um painel personalizado para o combobox para melhor visualização
        JPanel comboPanel = new JPanel();
        comboPanel.setBackground(Color.BLACK);
        comboPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        comboPanel.setMaximumSize(new Dimension(180, 30));
        
        // Configura o combobox
        shipTypeCombo = new JComboBox<>();
        shipTypeCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (isSelected) {
                    c.setBackground(new Color(80, 80, 80));
                } else {
                    c.setBackground(new Color(50, 50, 50));
                }
                c.setForeground(Color.WHITE);
                return c;
            }
        });
        
        updateShipTypeCombo();
        shipTypeCombo.setPreferredSize(new Dimension(160, 25));
        shipTypeCombo.setMaximumSize(new Dimension(160, 25));
        shipTypeCombo.addActionListener(this::handleShipTypeChange);
        shipTypeCombo.setBackground(new Color(50, 50, 50));
        shipTypeCombo.setForeground(Color.WHITE);
        
        comboPanel.add(shipTypeCombo);
        controlPanel.add(comboPanel);
        controlPanel.add(Box.createVerticalStrut(20));
        
        // Orientação (horizontal/vertical)
        JLabel orientationLabel = new JLabel("Orientação:");
        orientationLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        orientationLabel.setForeground(Color.WHITE);
        controlPanel.add(orientationLabel);
        controlPanel.add(Box.createVerticalStrut(5));
        
        horizontalRadio = new JRadioButton("Horizontal");
        horizontalRadio.setSelected(true);
        horizontalRadio.setAlignmentX(Component.CENTER_ALIGNMENT);
        horizontalRadio.setBackground(Color.BLACK);
        horizontalRadio.setForeground(Color.WHITE);
        
        verticalRadio = new JRadioButton("Vertical");
        verticalRadio.setAlignmentX(Component.CENTER_ALIGNMENT);
        verticalRadio.setBackground(Color.BLACK);
        verticalRadio.setForeground(Color.WHITE);
        
        ButtonGroup orientationGroup = new ButtonGroup();
        orientationGroup.add(horizontalRadio);
        orientationGroup.add(verticalRadio);
        
        horizontalRadio.addActionListener(e -> isVertical = false);
        verticalRadio.addActionListener(e -> isVertical = true);
        
        JPanel radioPanel = new JPanel();
        radioPanel.add(horizontalRadio);
        radioPanel.add(verticalRadio);
        radioPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        radioPanel.setBackground(Color.BLACK);
        controlPanel.add(radioPanel);
        controlPanel.add(Box.createVerticalStrut(30));
        
        // Botão para posicionamento aleatório
        randomizeButton = new JButton("Posicionar Aleatoriamente");
        randomizeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        randomizeButton.addActionListener(e -> randomizeShips());
        randomizeButton.setBackground(new Color(50, 50, 50));
        randomizeButton.setForeground(Color.WHITE);
        controlPanel.add(randomizeButton);
        controlPanel.add(Box.createVerticalStrut(10));
        
        // Botão para iniciar o jogo
        startGameButton = new JButton("Iniciar Jogo");
        startGameButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        startGameButton.setEnabled(false);
        startGameButton.addActionListener(e -> mainWindow.showGamePanel());
        startGameButton.setBackground(new Color(50, 50, 50));
        startGameButton.setForeground(Color.WHITE);
        controlPanel.add(startGameButton);
        
        // Instruções
        controlPanel.add(Box.createVerticalStrut(30));
        JLabel instructionsLabel = new JLabel("<html><body style='width: 180px; color: white'>Selecione o tipo de navio, a orientação e clique no tabuleiro para posicioná-lo.</body></html>");
        instructionsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        controlPanel.add(instructionsLabel);
        
        // Seleciona o primeiro navio disponível
        if (!availableShips.isEmpty()) {
            selectedShip = availableShips.get(0);
        }
    }
    
    /**
     * Cria a lista de navios disponíveis para posicionamento.
     */
    private List<Ship> createAvailableShips() {
        List<Ship> ships = new ArrayList<>();
        
        // Adiciona os navios conforme as constantes
        ships.add(new Ship("Porta-Aviões", Constants.CARRIER_SIZE));
        ships.add(new Ship("Encouraçado", Constants.BATTLESHIP_SIZE));
        ships.add(new Ship("Cruzador", Constants.CRUISER_SIZE));
        ships.add(new Ship("Submarino", Constants.SUBMARINE_SIZE));
        ships.add(new Ship("Destroyer", Constants.DESTROYER_SIZE));
        
        return ships;
    }
    
    /**
     * Atualiza o combo box com os navios disponíveis.
     */
    private void updateShipTypeCombo() {
        shipTypeCombo.removeAllItems();
        
        for (Ship ship : availableShips) {
            shipTypeCombo.addItem(ship.getName() + " (" + ship.getSize() + ")");
        }
        
        if (shipTypeCombo.getItemCount() > 0) {
            shipTypeCombo.setSelectedIndex(0);
        }
    }
    
    /**
     * Handler para mudança de seleção no combo de navios.
     */
    private void handleShipTypeChange(ActionEvent e) {
        int index = shipTypeCombo.getSelectedIndex();
        if (index >= 0 && index < availableShips.size()) {
            selectedShip = availableShips.get(index);
        }
    }
    
    /**
     * Handler para cliques no tabuleiro.
     */
    private void handleBoardClick(int row, int col) {
        if (selectedShip == null) {
            mainWindow.updateStatusMessage("Selecione um navio primeiro!");
            return;
        }
        
        // Copia o navio selecionado para manter a lista original intacta
        Ship shipToPlace = new Ship(selectedShip.getName(), selectedShip.getSize());
        Position startPos = new Position(row, col);
        
        try {
            if (playerBoard.placeShip(shipToPlace, startPos, isVertical)) {
                // Remove o navio da lista de disponíveis
                availableShips.remove(selectedShip);
                
                // Atualiza a UI
                updateShipTypeCombo();
                boardPanel.repaint();
                
                // Se não houver mais navios, habilita o botão de início
                if (availableShips.isEmpty()) {
                    mainWindow.updateStatusMessage("Todos os navios posicionados! Clique em 'Iniciar Jogo'.");
                } else {
                    mainWindow.updateStatusMessage("Navio posicionado com sucesso!");
                    // Seleciona o próximo navio disponível
                    if (!availableShips.isEmpty()) {
                        selectedShip = availableShips.get(0);
                    } else {
                        selectedShip = null;
                    }
                }
                
                updateStartButtonState();
            }
        } catch (IllegalArgumentException ex) {
            mainWindow.updateStatusMessage("Posição inválida: " + ex.getMessage());
        }
    }
    
    /**
     * Posiciona os navios aleatoriamente no tabuleiro.
     */
    private void randomizeShips() {
        // Limpa o tabuleiro atual
        playerBoard = new Board(Constants.BOARD_SIZE);
        availableShips = createAvailableShips();
        
        // Tenta posicionar cada navio aleatoriamente
        for (Ship ship : new ArrayList<>(availableShips)) {
            boolean placed = false;
            int attempts = 0;
            
            while (!placed && attempts < 100) {
                attempts++;
                
                // Gera posição e orientação aleatórias
                int row = (int) (Math.random() * Constants.BOARD_SIZE);
                int col = (int) (Math.random() * Constants.BOARD_SIZE);
                boolean vertical = Math.random() > 0.5;
                
                Ship shipToPlace = new Ship(ship.getName(), ship.getSize());
                Position startPos = new Position(row, col);
                
                try {
                    if (playerBoard.placeShip(shipToPlace, startPos, vertical)) {
                        placed = true;
                        availableShips.remove(ship);
                    }
                } catch (IllegalArgumentException ex) {
                    // Posição inválida, tenta novamente
                }
            }
        }
        
        // Atualiza a UI
        boardPanel.setBoard(playerBoard);
        updateShipTypeCombo();
        updateStartButtonState();
        
        if (availableShips.isEmpty()) {
            mainWindow.updateStatusMessage("Todos os navios posicionados aleatoriamente!");
        } else {
            mainWindow.updateStatusMessage("Não foi possível posicionar todos os navios aleatoriamente.");
        }
    }
    
    /**
     * Atualiza o estado do botão de início conforme o posicionamento.
     */
    private void updateStartButtonState() {
        startGameButton.setEnabled(availableShips.isEmpty());
    }
    
    /**
     * Retorna o tabuleiro do jogador.
     */
    public Board getPlayerBoard() {
        return playerBoard;
    }
} 