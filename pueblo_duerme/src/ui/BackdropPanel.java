package ui;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
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

        g2.setColor(new Color(255, 255, 255, 10));
        for (int x = -80; x < getWidth(); x += 180) {
            g2.drawLine(x, 0, x + 220, getHeight());
        }

        int baseY = getHeight() - 86;
        g2.setColor(new Color(11, 13, 14, 135));
        g2.fillRect(0, baseY + 38, getWidth(), 90);

        g2.setColor(new Color(16, 21, 20, 170));
        Polygon hill = new Polygon();
        hill.addPoint(0, baseY + 30);
        hill.addPoint(getWidth() / 5, baseY - 8);
        hill.addPoint(getWidth() / 2, baseY + 18);
        hill.addPoint(getWidth() * 4 / 5, baseY - 18);
        hill.addPoint(getWidth(), baseY + 22);
        hill.addPoint(getWidth(), getHeight());
        hill.addPoint(0, getHeight());
        g2.fillPolygon(hill);

        paintVillage(g2, baseY + 32);

        g2.dispose();
    }

    private void paintVillage(Graphics2D g2, int y) {
        int houseWidth = 72;
        for (int x = -20; x < getWidth(); x += houseWidth + 28) {
            int height = 34 + Math.abs((x / 11) % 22);
            g2.setColor(new Color(13, 15, 15, 210));
            g2.fillRect(x, y - height, houseWidth, height);

            Polygon roof = new Polygon();
            roof.addPoint(x - 8, y - height);
            roof.addPoint(x + houseWidth / 2, y - height - 26);
            roof.addPoint(x + houseWidth + 8, y - height);
            g2.fillPolygon(roof);

            g2.setColor(new Color(228, 183, 88, 120));
            g2.fillRect(x + 16, y - height + 14, 10, 12);
            if ((x / houseWidth) % 2 == 0) {
                g2.fillRect(x + 44, y - height + 19, 10, 12);
            }
        }
    }
}
