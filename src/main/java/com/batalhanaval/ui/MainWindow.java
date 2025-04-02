package com.batalhanaval.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import com.batalhanaval.Constants;

/**
 * Janela principal da aplicação Batalha Naval.
 * Contém os painéis de configuração e jogo.
 */
public class MainWindow extends JFrame {
    
    private JPanel currentPanel;
    private SetupPanel setupPanel;
    private GamePanel gamePanel;
    private StatusLabel statusLabel;
    
    /**
     * Construtor da janela principal.
     */
    public MainWindow() {
        // Configurações da janela
        setTitle("Batalha Naval em Rede");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null); // Centraliza a janela
        
        // Aplica o tema escuro
        applyDarkTheme();
        
        // Inicializa a barra de status
        statusLabel = new StatusLabel();
        
        // Configura o layout da janela
        setLayout(new BorderLayout());
        add(statusLabel, BorderLayout.SOUTH);
        
        // Inicializa os painéis (inicialmente mostra o setup)
        setupPanel = new SetupPanel(this);
        gamePanel = new GamePanel(this);
        
        // Mostra o painel de configuração inicial
        showSetupPanel();
        
        // Configura o fechamento da janela
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // Lógica para finalizar conexões de rede
                System.out.println("Fechando aplicação e conexões...");
            }
        });
    }
    
    /**
     * Aplica o tema escuro a toda a aplicação.
     */
    private void applyDarkTheme() {
        // Cores do tema escuro
        Color backgroundColor = Color.BLACK;
        Color foregroundColor = Color.WHITE;
        
        // Aplica as cores ao frame principal
        getContentPane().setBackground(backgroundColor);
        
        // Configura o Look and Feel para ter cores escuras
        try {
            // Usa o look and feel do sistema
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            
            // Configura cores padrão para componentes
            UIManager.put("Panel.background", backgroundColor);
            UIManager.put("Panel.foreground", foregroundColor);
            UIManager.put("Label.foreground", foregroundColor);
            UIManager.put("Button.background", new Color(50, 50, 50));
            UIManager.put("Button.foreground", foregroundColor);
            UIManager.put("ComboBox.background", new Color(50, 50, 50));
            UIManager.put("ComboBox.foreground", foregroundColor);
            UIManager.put("ComboBox.selectionBackground", new Color(80, 80, 80));
            UIManager.put("ComboBox.selectionForeground", foregroundColor);
            UIManager.put("RadioButton.background", backgroundColor);
            UIManager.put("RadioButton.foreground", foregroundColor);
            UIManager.put("OptionPane.background", backgroundColor);
            UIManager.put("OptionPane.foreground", foregroundColor);
            UIManager.put("OptionPane.messageForeground", foregroundColor);
        } catch (Exception e) {
            System.err.println("Erro ao configurar Look and Feel: " + e.getMessage());
        }
    }
    
    /**
     * Mostra o painel de configuração (posicionamento dos navios).
     */
    public void showSetupPanel() {
        if (currentPanel != null) {
            remove(currentPanel);
        }
        
        add(setupPanel, BorderLayout.CENTER);
        currentPanel = setupPanel;
        
        updateStatusMessage("Posicione seus navios no tabuleiro.");
        revalidate();
        repaint();
    }
    
    /**
     * Mostra o painel de jogo (fase de ataque).
     */
    public void showGamePanel() {
        if (currentPanel != null) {
            remove(currentPanel);
        }
        
        // Passa o tabuleiro configurado pelo jogador para o GamePanel
        gamePanel.setPlayerBoard(setupPanel.getPlayerBoard());
        // TODO: Inicializar o tabuleiro do oponente aqui (quando a rede for implementada)
        // gamePanel.setOpponentBoard(new Board(Constants.BOARD_SIZE)); 
        
        add(gamePanel, BorderLayout.CENTER);
        currentPanel = gamePanel;
        
        updateStatusMessage("Jogo iniciado. Aguarde sua vez para atacar.");
        revalidate();
        repaint();
    }
    
    /**
     * Atualiza a mensagem na barra de status.
     * @param message Mensagem a ser exibida.
     */
    public void updateStatusMessage(String message) {
        statusLabel.updateMessage(message);
    }
    
    /**
     * Método principal para teste da interface.
     * @param args Argumentos de linha de comando.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainWindow window = new MainWindow();
            window.setVisible(true);
        });
    }
} 