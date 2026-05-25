package ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

public final class Theme {

    public static final int CARD_RADIUS = 8;

    public static final Color BACKGROUND_TOP = new Color(18, 19, 21);
    public static final Color BACKGROUND_BOTTOM = new Color(31, 39, 35);
    public static final Color CARD_START = new Color(38, 40, 40, 238);
    public static final Color CARD_END = new Color(23, 25, 26, 246);
    public static final Color CARD_SOFT = new Color(45, 48, 47, 238);
    public static final Color CARD_ACCENT = new Color(42, 49, 45, 238);
    public static final Color BORDER = new Color(255, 255, 255, 42);
    public static final Color TEXT = new Color(248, 244, 235);
    public static final Color MUTED = new Color(184, 190, 184);
    public static final Color GOLD = new Color(228, 183, 88);
    public static final Color TEAL = new Color(78, 177, 161);
    public static final Color ROSE = new Color(205, 91, 112);
    public static final Color GREEN = new Color(118, 194, 126);
    public static final Color DEAD = new Color(127, 132, 134);
    public static final Color INPUT = new Color(20, 22, 23);

    public static final Font TITLE_FONT = new Font("Segoe UI Semibold", Font.BOLD, 34);
    public static final Font SECTION_FONT = new Font("Segoe UI Semibold", Font.BOLD, 20);
    public static final Font BODY_FONT = new Font("Segoe UI", Font.PLAIN, 15);
    public static final Font SMALL_FONT = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font BUTTON_FONT = new Font("Segoe UI Semibold", Font.BOLD, 15);
    public static final Font BIG_ROLE_FONT = new Font("Segoe UI Semibold", Font.BOLD, 30);

    private Theme() {
    }

    public static Border cardPadding() {
        return new EmptyBorder(20, 22, 20, 22);
    }

    public static Border softLine() {
        return BorderFactory.createLineBorder(BORDER);
    }

    public static Border fieldBorder() {
        return new CompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 255, 255, 34)),
                new EmptyBorder(10, 12, 10, 12));
    }

    public static void styleTextField(JTextField field) {
        field.setFont(BODY_FONT);
        field.setForeground(TEXT);
        field.setCaretColor(TEXT);
        field.setOpaque(true);
        field.setBackground(INPUT);
        field.setBorder(fieldBorder());
        field.setHorizontalAlignment(SwingConstants.LEFT);
    }

    public static void styleTextArea(JTextArea area, boolean transparent) {
        area.setFont(BODY_FONT);
        area.setForeground(TEXT);
        area.setCaretColor(TEXT);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setEditable(false);
        area.setOpaque(!transparent);
        if (!transparent) {
            area.setBackground(INPUT);
            area.setBorder(fieldBorder());
        } else {
            area.setBackground(new Color(0, 0, 0, 0));
            area.setBorder(new EmptyBorder(0, 0, 0, 0));
        }
    }

    public static JScrollPane createScrollPane(Component component) {
        JScrollPane scrollPane = new JScrollPane(component);
        scrollPane.setBorder(new EmptyBorder(0, 0, 0, 0));
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        styleScrollBar(scrollPane.getVerticalScrollBar());
        styleScrollBar(scrollPane.getHorizontalScrollBar());
        return scrollPane;
    }

    public static void styleComponent(JComponent component) {
        component.setOpaque(false);
        component.setForeground(TEXT);
        component.setFont(BODY_FONT);
    }

    public static void prepareButton(AccentButton button) {
        button.setFont(BUTTON_FONT);
        button.setForeground(TEXT);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
    }

    private static void styleScrollBar(JScrollBar bar) {
        if (bar == null) {
            return;
        }
        bar.setOpaque(false);
        bar.setUnitIncrement(18);
        bar.setPreferredSize(new Dimension(10, 10));
        bar.setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                thumbColor = new Color(228, 183, 88, 150);
                trackColor = new Color(0, 0, 0, 0);
            }

            @Override
            protected JButton createDecreaseButton(int orientation) {
                return createZeroButton();
            }

            @Override
            protected JButton createIncreaseButton(int orientation) {
                return createZeroButton();
            }

            @Override
            protected void paintTrack(Graphics graphics, JComponent component, Rectangle bounds) {
                Graphics2D g2 = (Graphics2D) graphics.create();
                g2.setColor(new Color(255, 255, 255, 14));
                g2.fillRoundRect(bounds.x + 3, bounds.y, Math.max(3, bounds.width - 6), bounds.height, 8, 8);
                g2.dispose();
            }

            @Override
            protected void paintThumb(Graphics graphics, JComponent component, Rectangle bounds) {
                if (bounds.isEmpty() || !bar.isEnabled()) {
                    return;
                }
                Graphics2D g2 = (Graphics2D) graphics.create();
                g2.setColor(thumbColor);
                g2.fillRoundRect(bounds.x + 2, bounds.y + 2, Math.max(4, bounds.width - 4),
                        Math.max(8, bounds.height - 4), 8, 8);
                g2.dispose();
            }
        });
    }

    private static JButton createZeroButton() {
        JButton button = new JButton();
        button.setPreferredSize(new Dimension(0, 0));
        button.setMinimumSize(new Dimension(0, 0));
        button.setMaximumSize(new Dimension(0, 0));
        return button;
    }
}
