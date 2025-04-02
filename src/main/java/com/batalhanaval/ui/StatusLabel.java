package com.batalhanaval.ui;

import javax.swing.*;
import java.awt.*;

/**
 * Componente para exibir mensagens de status do jogo.
 */
public class StatusLabel extends JPanel {
    
    private JLabel messageLabel;
    
    /**
     * Construtor da barra de status.
     */
    public StatusLabel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 1));
        setPreferredSize(new Dimension(800, 30));
        setBackground(Color.BLACK);
        
        messageLabel = new JLabel(" ");
        messageLabel.setHorizontalAlignment(SwingConstants.LEFT);
        messageLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        messageLabel.setForeground(Color.WHITE);
        
        add(messageLabel, BorderLayout.CENTER);
    }
    
    /**
     * Atualiza a mensagem exibida na barra de status.
     * @param message Mensagem a ser exibida.
     */
    public void updateMessage(String message) {
        messageLabel.setText(message);
    }
} 