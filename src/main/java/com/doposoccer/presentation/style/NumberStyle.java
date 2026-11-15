package com.doposoccer.presentation.style;
import com.doposoccer.presentation.resource.SpriteCache;
import com.doposoccer.presentation.resource.SpritePath;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 * Numbers drawn with digit sprites instead of letters of the font
 * The digits are images; any other character, such as ':' or '-', is written with the game font
 */
public final class NumberStyle {

    /** Big green digits, for the score*/
    public static final NumberStyle BIG = new NumberStyle("big", Palette.WHITE, -0.08);

    /** Small silver digits, for the clock, the points and the prices*/
    public static final NumberStyle SMALL = new NumberStyle("small", Palette.WHITE, 0.08);

    private static final double SYMBOL_GAP = 0.12;  // a symbol such as ':' or '-' keeps some air around it
    private static final double SYMBOL_SIZE = 0.9;  // the font is a bit smaller than the digits
    private static final double BASELINE_RATIO = 0.35;  // moves the baseline so a symbol is centered on the digits

    private final String name;
    private final Palette symbolColor;
    private final double gap;

    // gap: space between digits as a fraction of their height; the big digits are italic, so they overlap a little
    private NumberStyle(String name, Palette symbolColor, double gap) {
        this.name = name;
        this.symbolColor = symbolColor;
        this.gap = gap;
    }

    /**
     * Draws a number centered at a point
     * @param g graphics to draw on
     * @param sprites shared sprite cache
     * @param text number to draw, such as "02:15"
     * @param centerX x of the center
     * @param centerY y of the center
     * @param height height of the digits
     */
    public void draw(Graphics2D g, SpriteCache sprites, String text, double centerX, double centerY, double height) {
        drawFromLeft(g, sprites, text, centerX - width(g, sprites, text, height) / 2, centerY, height);
    }

    /**
     * Draws a number that ends at a point
     * @param g graphics to draw on
     * @param sprites shared sprite cache
     * @param text number to draw
     * @param right x where the number ends
     * @param centerY y of the center
     * @param height height of the digits
     */
    public void drawToLeft(Graphics2D g, SpriteCache sprites, String text, double right, double centerY, double height) {
        drawFromLeft(g, sprites, text, right - width(g, sprites, text, height), centerY, height);
    }

    /**
     * Returns the width that a number takes
     * @param g graphics used to measure the symbols
     * @param sprites shared sprite cache
     * @param text number to measure
     * @param height height of the digits
     * @return the width of the whole number
     */
    public double width(Graphics2D g, SpriteCache sprites, String text, double height) {
        double total = 0;
        for (int i = 0; i < text.length(); i++) {
            total += charWidth(g, sprites, text.charAt(i), height) + spacing(text, i, height);
        }
        return total;
    }

    private void drawFromLeft(Graphics2D g, SpriteCache sprites, String text, double left, double centerY, double height) {
        double x = left;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            double w = charWidth(g, sprites, c, height);
            BufferedImage digit = image(sprites, c);
            if (digit != null) {
                g.drawImage(digit, (int) x, (int) (centerY - height / 2), (int) w, (int) height, null);
            } else {
                Theme.drawCenteredText(g, String.valueOf(c), x + w / 2, centerY + height * BASELINE_RATIO,
                        symbolFont(height), symbolColor, Palette.DARK, 3f);
            }
            x += w + spacing(text, i, height);
        }
    }

    private double spacing(String text, int i, double height) {
        boolean betweenDigits = i + 1 < text.length() && Character.isDigit(text.charAt(i))
                && Character.isDigit(text.charAt(i + 1));
        return betweenDigits ? gap * height : SYMBOL_GAP * height;
    }

    private double charWidth(Graphics2D g, SpriteCache sprites, char c, double height) {
        BufferedImage digit = image(sprites, c);
        if (digit != null) {
            return digit.getWidth() * height / digit.getHeight();
        }
        return g.getFontMetrics(symbolFont(height)).charWidth(c);
    }

    private BufferedImage image(SpriteCache sprites, char c) {
        if (!Character.isDigit(c)) {
            return null;
        }
        return sprites.get(SpritePath.digit(name, c));
    }

    private Font symbolFont(double height) {
        return Theme.font((float) (height * SYMBOL_SIZE));
    }
}
