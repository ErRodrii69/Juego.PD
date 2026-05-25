package ui;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JPanel;

public class RoundedPanel extends JPanel {

    private final Color startColor;
    private final Color endColor;
    private final int arc;

    public RoundedPanel(Color startColor, Color endColor, int arc) {
        this.startColor = startColor;
        this.endColor = endColor;
        this.arc = Math.min(arc, Theme.CARD_RADIUS);
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        Graphics2D g2 = (Graphics2D) graphics.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(new Color(0, 0, 0, 44));
        g2.fillRoundRect(3, 5, getWidth() - 6, getHeight() - 6, arc, arc);

        g2.setPaint(new GradientPaint(0, 0, startColor, getWidth(), getHeight(), endColor));
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);

        g2.setColor(Theme.BORDER);
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);
        g2.dispose();

        super.paintComponent(graphics);
    }
}
