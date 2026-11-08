package com.doposoccer.presentation;

import javax.swing.ButtonModel;
import javax.swing.JButton;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 * Game-styled button: draws the gold button image with its text on top.
 * It shows the brown image when pressed, hovered or highlighted (selected option),
 * and a gray version when disabled. It never takes the keyboard focus, so the
 * players' keys keep reaching the game.
 */
public class ImageButton extends JButton {

    /** Key of the normal button image. */
    public static final String NORMAL_KEY = "btn";

    /** Key of the pressed/selected button image. */
    public static final String PRESSED_KEY = "btn_pressed";

    /** Font size as a fraction of the button height. */
    private static final double FONT_RATIO = 0.30;

    /** Largest fraction of the button width the text may use. */
    private static final double MAX_TEXT_WIDTH_RATIO = 0.78;

    /** Vertical center of the button body as a fraction of the image height (the image has a glow). */
    private static final double BODY_CENTER_RATIO = 0.50;

    /** Distance from the vertical center to the baseline, as a fraction of the font size. */
    private static final double BASELINE_OFFSET_RATIO = 0.36;

    /** Shared sprite cache. */
    private final SpriteCache sprites;

    /** True if the button represents the currently selected option. */
    private boolean highlighted;

    /**
     * Creates a game-styled button.
     *
     * @param text    text shown on the button
     * @param sprites shared sprite cache
     */
    public ImageButton(String text, SpriteCache sprites) {
        super(text);
        this.sprites = sprites;
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setFocusable(false);
        setOpaque(false);
        setRolloverEnabled(true);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    /**
     * Marks the button as the selected option of a group.
     *
     * @param highlighted true to show it as selected
     */
    public void setHighlighted(boolean highlighted) {
        this.highlighted = highlighted;
        repaint();
    }

    /**
     * Tells whether the button is shown as the selected option.
     *
     * @return true if it is highlighted
     */
    public boolean isHighlighted() {
        return highlighted;
    }

    /**
     * Paints the button image and its centered text.
     *
     * @param g graphics given by Swing
     */
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        Theme.applyQuality(g2);
        ButtonModel model = getModel();
        boolean active = model.isPressed() || model.isRollover() || highlighted;

        BufferedImage image = chooseImage(active);
        int w = getWidth();
        int h = getHeight();
        if (image != null) {
            g2.drawImage(image, 0, 0, w, h, null);
        } else {
            g2.setColor(active ? Theme.DARK : Theme.GOLD);
            g2.fillRoundRect(2, 2, w - 4, h - 4, h, h);
        }

        Color fill = Theme.DARK;
        Color outline = Theme.GOLD;
        if (!isEnabled()) {
            fill = Theme.MUTED;
            outline = Theme.DARK;
        } else if (active) {
            fill = Theme.GOLD;
            outline = Theme.DARK;
        }
        float fontSize = fittedFontSize(g2, w, h);
        Theme.drawCenteredText(g2, getText(), w / 2.0, h * BODY_CENTER_RATIO + fontSize * BASELINE_OFFSET_RATIO,
                Theme.font(fontSize), fill, outline, 1.5f);
        g2.dispose();
    }

    /**
     * Computes a font size proportional to the height that still fits in the width.
     *
     * @param g graphics used to measure the text
     * @param w width of the button
     * @param h height of the button
     * @return the font size to use
     */
    private float fittedFontSize(Graphics2D g, int w, int h) {
        float size = (float) (h * FONT_RATIO);
        int textWidth = g.getFontMetrics(Theme.font(size)).stringWidth(getText());
        double maxWidth = w * MAX_TEXT_WIDTH_RATIO;
        if (textWidth > maxWidth) {
            size = (float) (size * maxWidth / textWidth);
        }
        return size;
    }

    /**
     * Picks the image that matches the state of the button.
     *
     * @param active true if the button is pressed, hovered or highlighted
     * @return the image to draw, or null if it is missing
     */
    private BufferedImage chooseImage(boolean active) {
        if (!isEnabled()) {
            return sprites.getGray(NORMAL_KEY);
        }
        if (active) {
            return sprites.get(PRESSED_KEY);
        }
        return sprites.get(NORMAL_KEY);
    }
}
