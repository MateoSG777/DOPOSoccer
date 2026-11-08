package com.doposoccer.presentation;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;

/**
 * Visual style shared by every screen: colors, fonts and text helpers.
 */
public final class Theme {

    /** Gold used for titles and highlighted texts. */
    public static final Color GOLD = new Color(255, 214, 64);

    /** Dark brown used for outlines and text on gold buttons. */
    public static final Color DARK = new Color(45, 25, 5);

    /** Translucent black used behind texts and panels. */
    public static final Color SHADE = new Color(0, 0, 0, 150);

    /** Almost opaque black used to hide parts of a background image. */
    public static final Color BAND = new Color(10, 30, 5, 250);

    /** Light gray used for secondary texts and disabled buttons. */
    public static final Color MUTED = new Color(200, 200, 200);

    /** Red used for warnings such as sudden death. */
    public static final Color ALERT = new Color(235, 60, 40);

    /** Logical font family available on every platform. */
    public static final String FONT_FAMILY = Font.SANS_SERIF;

    /** Utility class: it must not be instantiated. */
    private Theme() {
    }

    /**
     * Returns the bold game font of the given size.
     *
     * @param size font size in points
     * @return a bold sans-serif font
     */
    public static Font font(float size) {
        return new Font(FONT_FAMILY, Font.BOLD, 1).deriveFont(size);
    }

    /**
     * Converts a domain color (0xRRGGBB) into a Swing color.
     *
     * @param rgb color in 0xRRGGBB format
     * @return the equivalent opaque color
     */
    public static Color toColor(int rgb) {
        return new Color(rgb);
    }

    /**
     * Turns on antialiasing and quality interpolation.
     *
     * @param g graphics to configure
     */
    public static void applyQuality(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    }

    /**
     * Draws a text centered horizontally, with an outline so it is readable on any background.
     *
     * @param g            graphics to draw on
     * @param text         text to draw (nothing is drawn if it is empty)
     * @param centerX      x of the horizontal center of the text
     * @param baseline     y of the text baseline
     * @param font         font to use
     * @param fill         color of the letters
     * @param outline      color of the outline
     * @param outlineWidth width of the outline (0 for none)
     */
    public static void drawCenteredText(Graphics2D g, String text, double centerX, double baseline,
                                        Font font, Color fill, Color outline, float outlineWidth) {
        if (text == null || text.isEmpty()) {
            return;
        }
        TextLayout layout = new TextLayout(text, font, g.getFontRenderContext());
        double x = centerX - layout.getAdvance() / 2;
        Shape shape = layout.getOutline(AffineTransform.getTranslateInstance(x, baseline));
        if (outlineWidth > 0) {
            g.setStroke(new BasicStroke(outlineWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.setColor(outline);
            g.draw(shape);
        }
        g.setColor(fill);
        g.fill(shape);
    }
}
