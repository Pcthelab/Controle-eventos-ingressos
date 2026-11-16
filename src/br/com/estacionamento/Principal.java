package br.com.estacionamento;

import br.com.estacionamento.view.TelaEstacionamento;

import javax.swing.*;

/**
 * Classe Principal com o ponto de entrada da aplicação (main).
 * Inicializa a interface gráfica do sistema de Estacionamento Rotativo.
 */
public class Principal {

    public static void main(String[] args) {
        // Tenta aplicar o Look and Feel Nimbus ou do Sistema Operacional para melhor estética
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // Se Nimbus não estiver disponível, prossegue com o padrão do Swing
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
        }

        // Executa a criação e exibição da janela na Event Dispatch Thread (boas práticas Swing)
        SwingUtilities.invokeLater(() -> {
            TelaEstacionamento tela = new TelaEstacionamento();
            tela.setVisible(true);
        });
    }
}
