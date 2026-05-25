package ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import javax.swing.JButton;

public class AccentButton extends JButton {

    private final Color baseColor;
    private final Color pressedColor;
    private final Color disabledColor;

    public AccentButton(String text, Color baseColor) {
        super(text);
        this.baseColor = baseColor;
        this.pressedColor = baseColor.darker();
        this.disabledColor = new Color(88, 97, 116);
        Theme.prepareButton(this);
        setMargin(new Insets(9, 16, 9, 16));
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        Graphics2D g2 = (Graphics2D) graphics.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Color fill = !isEnabled() ? disabledColor : getModel().isPressed() ? pressedColor : baseColor;
        if (getModel().isRollover() && isEnabled()) {
            fill = fill.brighter();
        }

        g2.setColor(fill);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
        g2.setColor(new Color(255, 255, 255, 28));
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
        g2.dispose();

        super.paintComponent(graphics);
    }
}
