package br.com.eventos;

import br.com.eventos.view.TelaEventos;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class RenderTest {
    public static void main(String[] args) throws Exception {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        TelaEventos tela = new TelaEventos();
        tela.setSize(1220, 820);
        tela.addNotify();
        tela.validate();

        BufferedImage img = new BufferedImage(1220, 820, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = img.createGraphics();
        g2.setColor(new Color(248, 250, 252));
        g2.fillRect(0, 0, 1220, 820);
        tela.getContentPane().printAll(g2);
        g2.dispose();

        File out = new File("preview_ui.png");
        ImageIO.write(img, "png", out);
        System.out.println("Screenshot atualizado: " + out.getAbsolutePath());
    }
}
