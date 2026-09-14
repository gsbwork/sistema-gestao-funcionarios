package com.saam.gestao.util;

import javax.swing.JTextField;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;

/**
 * JTextField com um texto de exemplo (placeholder) exibido em cinza claro
 * enquanto o campo estiver vazio, para indicar o formato esperado
 * (ex: "dd/mm/aaaa"). O texto some assim que o usuario comeca a digitar.
 * O placeholder e uma propriedade de bean para poder ser definido pelo
 * GUI Builder do NetBeans (arquivo .form).
 */
public class PlaceholderTextField extends JTextField {

    private String placeholder;
    private final Color corPlaceholder = new Color(175, 175, 175);

    public PlaceholderTextField() {
    }

    public PlaceholderTextField(String placeholder) {
        this.placeholder = placeholder;
    }

    public String getPlaceholder() {
        return placeholder;
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (!getText().isEmpty() || placeholder == null || placeholder.isEmpty()) {
            return;
        }
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setColor(corPlaceholder);
        g2.setFont(getFont());
        Insets insets = getInsets();
        FontMetrics fm = g2.getFontMetrics();
        int x = insets.left;
        int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
        g2.drawString(placeholder, x, y);
        g2.dispose();
    }
}
