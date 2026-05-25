package ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

public final class Theme {

    public static final Color BACKGROUND_TOP = new Color(8, 17, 31);
    public static final Color BACKGROUND_BOTTOM = new Color(29, 46, 71);
    public static final Color CARD_START = new Color(18, 31, 49, 235);
    public static final Color CARD_END = new Color(10, 19, 34, 245);
    public static final Color CARD_SOFT = new Color(24, 39, 61, 235);
    public static final Color CARD_ACCENT = new Color(34, 59, 87, 235);
    public static final Color BORDER = new Color(255, 255, 255, 34);
    public static final Color TEXT = new Color(244, 238, 226);
    public static final Color MUTED = new Color(172, 184, 204);
    public static final Color GOLD = new Color(229, 188, 92);
    public static final Color TEAL = new Color(85, 182, 174);
    public static final Color ROSE = new Color(191, 92, 111);
    public static final Color GREEN = new Color(122, 205, 145);
    public static final Color DEAD = new Color(115, 124, 142);
    public static final Color INPUT = new Color(13, 25, 41);

    public static final Font TITLE_FONT = new Font("Georgia", Font.BOLD, 34);
    public static final Font SECTION_FONT = new Font("Georgia", Font.BOLD, 21);
    public static final Font BODY_FONT = new Font("Segoe UI", Font.PLAIN, 15);
    public static final Font SMALL_FONT = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font BUTTON_FONT = new Font("Trebuchet MS", Font.BOLD, 15);
    public static final Font BIG_ROLE_FONT = new Font("Georgia", Font.BOLD, 28);

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
                BorderFactory.createLineBorder(new Color(255, 255, 255, 26)),
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
    }
}
