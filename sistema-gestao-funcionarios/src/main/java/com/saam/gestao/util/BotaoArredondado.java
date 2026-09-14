package com.saam.gestao.util;

import javax.swing.ButtonModel;
import javax.swing.JButton;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/**
 * JButton com fundo de cantos arredondados, pintado com a cor de
 * "background" do proprio botao. Instanciado nas telas via Custom Creation
 * Code do NetBeans (o .form continua declarando javax.swing.JButton).
 * Ignora setOpaque/setContentAreaFilled(true) para que o Look and Feel
 * nao pinte o retangulo quadrado por baixo do fundo arredondado.
 */
public class BotaoArredondado extends JButton {

    private static final int RAIO = 10;

    public BotaoArredondado() {
        super.setOpaque(false);
        super.setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
    }

    @Override
    public void setOpaque(boolean isOpaque) {
        super.setOpaque(false);
    }

    @Override
    public void setContentAreaFilled(boolean b) {
        super.setContentAreaFilled(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(corDeFundoAtual());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), RAIO, RAIO);
        g2.dispose();
        super.paintComponent(g);
    }

    private Color corDeFundoAtual() {
        Color base = getBackground();
        ButtonModel modelo = getModel();
        if (!isEnabled()) {
            return base.brighter();
        }
        if (modelo.isPressed()) {
            return base.darker();
        }
        if (modelo.isRollover()) {
            return new Color(
                    Math.min(255, base.getRed() + 18),
                    Math.min(255, base.getGreen() + 18),
                    Math.min(255, base.getBlue() + 18));
        }
        return base;
    }
}
