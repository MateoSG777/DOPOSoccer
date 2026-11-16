package com.doposoccer.presentation.style;

import com.doposoccer.domain.Log;
import com.doposoccer.domain.enums.Country;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.text.AttributedString;
import java.awt.FontFormatException;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

/**
 * Visual style shared by every screen: colors, fonts and text helpers
 */
public final class Theme {

    /** Logical font family used if the game font cannot be read*/
    public static final String FONT_FAMILY = Font.SANS_SERIF;

    /** Game font, Russo One, inside the sprites folder*/
    public static final String FONT_PATH = "/sprites/fonts/RussoOne-Regular.ttf";

    private static final Font BASE_FONT = loadFont();

    private static final int GLOW_ALPHA = 130;  // the figure is still seen through the glow
    private static final Map<Palette, Color> COLORS = new EnumMap<>(Palette.class);
    private static final Map<String, Palette> COUNTRY_COLORS = new HashMap<>();

    static {
        COLORS.put(Palette.GOLD, new Color(255, 214, 64));
        COLORS.put(Palette.DARK, new Color(45, 25, 5));
        COLORS.put(Palette.WHITE, new Color(255, 255, 255));
        COLORS.put(Palette.BLACK, new Color(0, 0, 0));
        COLORS.put(Palette.MUTED, new Color(200, 200, 200));
        COLORS.put(Palette.ALERT, new Color(235, 60, 40));
        COLORS.put(Palette.SHADE, new Color(0, 0, 0, 150));
        COLORS.put(Palette.BAND, new Color(10, 30, 5, 250));
        COLORS.put(Palette.SKY, new Color(90, 170, 230));
        COLORS.put(Palette.GRASS, new Color(110, 170, 40));
        COLORS.put(Palette.FIELD, new Color(70, 140, 40));
        COLORS.put(Palette.NET, new Color(240, 240, 240));
        COLORS.put(Palette.LIFE, new Color(76, 200, 80));
        COLORS.put(Palette.POWER, new Color(60, 150, 255));
        COLORS.put(Palette.KOREA, new Color(211, 47, 47));
        COLORS.put(Palette.CAMEROON, new Color(46, 125, 50));
        COLORS.put(Palette.NIGERIA, new Color(67, 160, 71));
        COLORS.put(Palette.USA, new Color(21, 101, 192));

        COUNTRY_COLORS.put("korea", Palette.KOREA);
        COUNTRY_COLORS.put("cameroon", Palette.CAMEROON);
        COUNTRY_COLORS.put("nigeria", Palette.NIGERIA);
        COUNTRY_COLORS.put("usa", Palette.USA);
    }

    private Theme() {
    }

    private static Font loadFont() {
        try (InputStream input = Theme.class.getResourceAsStream(FONT_PATH)) {
            if (input != null) {
                return Font.createFont(Font.TRUETYPE_FONT, input);
            }
        } catch (IOException | FontFormatException e) {
            Log.record(e);
        }
        return new Font(FONT_FAMILY, Font.BOLD, 1);
    }

    /**
     * Returns the game font of the given size
     * @param size font size in points
     * @return Russo One, or a bold sans-serif font if Russo One cannot be read
     */
    public static Font font(float size) {
        return BASE_FONT.deriveFont(size);
    }

    /**
     * Looks up the value of a color in the color dictionary
     * @param name name of the color
     * @return the Swing color
     */
    public static Color color(Palette name) {
        return COLORS.get(name);
    }

    /**
     * Looks up the color drawn in place of the image of a country
     * @param spriteKey key of the sprites of the country
     * @return the color of that country, or the muted color if the country is unknown
     */
    public static Color countryColor(String spriteKey) {
        return color(COUNTRY_COLORS.getOrDefault(spriteKey, Palette.MUTED));
    }

    /**
     * Returns the translucent color of the glow of a power, the same yellow for every country
     * @return the glow color, half transparent
     */
    public static Color glowColor() {
        Color base = color(Palette.GOLD);
        return new Color(base.getRed(), base.getGreen(), base.getBlue(), GLOW_ALPHA);
    }

    /**
     * Turns on antialiasing and quality interpolation
     * @param g graphics to configure
     */
    public static void applyQuality(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    }

    /**
     * Gives the text the game font; the characters it does not have, such as the arrows, use the system font
     * @param text text to write
     * @param font game font
     * @return the text with a font for every character
     */
    public static AttributedString withFallback(String text, Font font) {
        AttributedString styled = new AttributedString(text);
        styled.addAttribute(TextAttribute.FONT, font);
        Font fallback = new Font(FONT_FAMILY, Font.BOLD, 1).deriveFont(font.getSize2D());
        for (int i = 0; i < text.length(); i++) {
            if (!font.canDisplay(text.charAt(i))) {
                styled.addAttribute(TextAttribute.FONT, fallback, i, i + 1);
            }
        }
        return styled;
    }

    /**
     * Draws a text centered horizontally, with an outline so it is readable on any background.
     * @param g graphics to draw on
     * @param text text to draw
     * @param centerX x of the horizontal center of the text
     * @param baseline y of the text baseline
     * @param font font to use
     * @param fill color of the letters
     * @param outline color of the outline
     * @param outlineWidth width of the outline
     */
    public static void drawCenteredText(Graphics2D g, String text, double centerX, double baseline,
                                        Font font, Palette fill, Palette outline, float outlineWidth) {
        if (text == null || text.isEmpty()) {
            return;
        }
        TextLayout layout = new TextLayout(withFallback(text, font).getIterator(), g.getFontRenderContext());
        double x = centerX - layout.getAdvance() / 2;
        Shape shape = layout.getOutline(AffineTransform.getTranslateInstance(x, baseline));
        if (outlineWidth > 0) {
            g.setStroke(new BasicStroke(outlineWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.setColor(color(outline));
            g.draw(shape);
        }
        g.setColor(color(fill));
        g.fill(shape);
    }

    /**
     * Draws a square image of a country (head or flag) centered at a point
     * If the image is missing, a circle of the country color is drawn instead
     *
     * @param g graphics to draw on
     * @param image image to draw, or null if it is missing
     * @param country country the image belongs to
     * @param centerX x of the center
     * @param centerY y of the center
     * @param size width and height to draw
     */
    public static void drawCountryImage(Graphics2D g, BufferedImage image, Country country,
                                        double centerX, double centerY, double size) {
        int left = (int) (centerX - size / 2);
        int top = (int) (centerY - size / 2);
        if (image != null) {
            g.drawImage(image, left, top, (int) size, (int) size, null);
            return;
        }
        g.setColor(countryColor(country.getSpriteKey()));
        g.fillOval(left, top, (int) size, (int) size);
    }
}