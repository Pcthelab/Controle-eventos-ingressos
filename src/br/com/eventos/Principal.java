package br.com.eventos;

import br.com.eventos.view.TelaEventos;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Ponto de entrada (main) da aplicação da Avaliação P2.
 * Configura o Look and Feel do sistema operacional para renderização nativa
 * e instancia a interface gráfica na Event Dispatch Thread do Swing.
 */
public class Principal {
    public static void main(String[] args) {
        try {
            // Configura o Look and Feel do Sistema Operacional (Windows/Linux/Mac)
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        SwingUtilities.invokeLater(() -> new TelaEventos().setVisible(true));
    }
}
