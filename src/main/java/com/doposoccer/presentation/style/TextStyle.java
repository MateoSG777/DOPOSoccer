package com.doposoccer.presentation.style;
import java.awt.Font;
import java.awt.Graphics2D;

/**
 * Text styles of the game: size, fill color and outline of each kind of text
 * Screens draw a text by naming one of the shared styles instead of repeating its values
 */
public final class TextStyle {

    /** Pause message over the field*/
    public static final TextStyle PAUSE = new TextStyle(80f, Palette.GOLD, Palette.DARK, 8f);

    /** Title of a screen*/
    public static final TextStyle TITLE = new TextStyle(40f, Palette.GOLD, Palette.DARK, 5f);

    /** Score of a match*/
    public static final TextStyle SCORE = new TextStyle(46f, Palette.GOLD, Palette.DARK, 5f);

    /** Highlighted line such as the player number or the points earned*/
    public static final TextStyle SUBTITLE = new TextStyle(28f, Palette.GOLD, Palette.DARK, 4f);

    /** Main line in white, such as a country name*/
    public static final TextStyle HEADING = new TextStyle(26f, Palette.WHITE, Palette.DARK, 4f);

    /** Warning such as sudden death*/
    public static final TextStyle WARNING = new TextStyle(26f, Palette.ALERT, Palette.WHITE, 4f);

    /** Name of a section of a form*/
    public static final TextStyle SECTION = new TextStyle(22f, Palette.GOLD, Palette.DARK, 3f);

    /** Normal text such as the clock or a message*/
    public static final TextStyle TEXT = new TextStyle(22f, Palette.WHITE, Palette.DARK, 3f);

    /** Secondary highlighted text such as the power shot name*/
    public static final TextStyle DETAIL = new TextStyle(16f, Palette.GOLD, Palette.DARK, 3f);

    /** Small highlighted label*/
    public static final TextStyle TAG = new TextStyle(13f, Palette.GOLD, Palette.DARK, 2f);

    /** Small text such as the keys of the players*/
    public static final TextStyle SMALL = new TextStyle(13f, Palette.WHITE, Palette.DARK, 2f);

    private final Font font;
    private final Palette fill;
    private final Palette outline;
    private final float outlineWidth;

    private TextStyle(float size, Palette fill, Palette outline, float outlineWidth) {
        this.font = Theme.font(size);
        this.fill = fill;
        this.outline = outline;
        this.outlineWidth = outlineWidth;
    }

    /**
     * Draws a text centered horizontally
     * @param g  graphics to draw on
     * @param text text to draw
     * @param centerX x of the horizontal center of the text
     * @param baseline y of the text baseline
     */
    public void draw(Graphics2D g, String text, double centerX, double baseline) {
        Theme.drawCenteredText(g, text, centerX, baseline, font, fill, outline, outlineWidth);
    }

    /**
     * Draws a text aligned to the left.
     * @param g graphics to draw on
     * @param text text to draw
     * @param left x where the text starts
     * @param baseline y of the text baseline
     */
    public void drawFromLeft(Graphics2D g, String text, double left, double baseline) {
        draw(g, text, left + g.getFontMetrics(font).stringWidth(text) / 2.0, baseline);
    }
}
