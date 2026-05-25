package ui;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JPanel;

public class BackdropPanel extends JPanel {

    public BackdropPanel() {
        setOpaque(true);
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        Graphics2D g2 = (Graphics2D) graphics.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setPaint(new GradientPaint(0, 0, Theme.BACKGROUND_TOP, 0, getHeight(), Theme.BACKGROUND_BOTTOM));
        g2.fillRect(0, 0, getWidth(), getHeight());

        g2.setColor(new Color(255, 205, 120, 26));
        g2.fillOval(-120, -90, 360, 260);

        g2.setColor(new Color(92, 183, 174, 24));
        g2.fillOval(getWidth() - 280, 30, 260, 220);

        g2.setColor(new Color(255, 255, 255, 12));
        g2.fillOval(getWidth() / 2 - 180, getHeight() - 200, 320, 180);

        g2.dispose();
    }
}
