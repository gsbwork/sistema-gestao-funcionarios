package com.saam.gestao.util;

import javax.swing.border.AbstractBorder;
import javax.swing.text.JTextComponent;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;

/**
 * Borda de cantos arredondados para campos de texto, aplicada em runtime
 * (depois de initComponents()) para nao depender de classes customizadas no
 * .form do NetBeans. Fica azul (cor primaria) quando o campo tem foco.
 */
public class BordaArredondada extends AbstractBorder {

    private static final Color COR_NORMAL = new Color(200, 208, 234);
    private static final Color COR_FOCO = new Color(63, 81, 181);

    private final int raio;
    private final Insets insets;

    public BordaArredondada(int raio, Insets insets) {
        this.raio = raio;
        this.insets = insets;
    }

    /** Deixa os campos nao-opacos (o fundo branco do cartao aparece por tras) e aplica a borda arredondada. */
    public static void aplicar(JTextComponent... campos) {
        for (JTextComponent campo : campos) {
            campo.setOpaque(false);
            campo.setBorder(new BordaArredondada(10, new Insets(6, 10, 6, 10)));
        }
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        boolean focado = c.hasFocus();
        g2.setColor(focado ? COR_FOCO : COR_NORMAL);
        g2.setStroke(new BasicStroke(focado ? 1.5f : 1f));
        g2.drawRoundRect(x, y, width - 1, height - 1, raio, raio);
        g2.dispose();
    }

    @Override
    public Insets getBorderInsets(Component c) {
        return new Insets(insets.top, insets.left, insets.bottom, insets.right);
    }

    @Override
    public boolean isBorderOpaque() {
        return false;
    }
}
