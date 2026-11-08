package com.doposoccer.presentation;

import com.doposoccer.domain.model.Field;

import javax.swing.JComponent;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 * Panel with a background image scaled to cover the whole panel.
 * Children are placed with coordinates of a 1024 x 640 design, which are
 * scaled to the real size of the panel, so every screen adapts to any size.
 */
public class BackgroundPanel extends JPanel {

    /** Width of the design coordinates. */
    public static final double DESIGN_WIDTH = Field.WIDTH;

    /** Height of the design coordinates. */
    public static final double DESIGN_HEIGHT = Field.HEIGHT;

    /** Color used when the background image is missing. */
    private static final Color FALLBACK = new Color(70, 140, 40);

    /** Shared sprite cache. */
    protected final SpriteCache sprites;

    /** Key of the background image. */
    private final String backgroundKey;

    /** Fraction of the image height to hide at the bottom (for example, a watermark). */
    private final double cropBottom;

    /**
     * Creates a panel with a background image.
     *
     * @param sprites       shared sprite cache
     * @param backgroundKey key of the background image
     * @param cropBottom    fraction of the image height hidden at the bottom (0 to keep it all)
     */
    public BackgroundPanel(SpriteCache sprites, String backgroundKey, double cropBottom) {
        super(null);
        this.sprites = sprites;
        this.backgroundKey = backgroundKey;
        this.cropBottom = cropBottom;
    }

    /**
     * Returns the factor between the real size and the design size.
     *
     * @return scale of the panel
     */
    protected double scale() {
        return Math.min(getWidth() / DESIGN_WIDTH, getHeight() / DESIGN_HEIGHT);
    }

    /**
     * Places a child using design coordinates.
     *
     * @param component child to place
     * @param x         x in design coordinates
     * @param y         y in design coordinates
     * @param width     width in design coordinates
     * @param height    height in design coordinates
     */
    protected void place(JComponent component, double x, double y, double width, double height) {
        double s = scale();
        component.setBounds((int) (x * s), (int) (y * s), (int) (width * s), (int) (height * s));
    }

    /**
     * Paints the background image covering the panel.
     *
     * @param g graphics given by Swing
     */
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        Theme.applyQuality(g2);
        BufferedImage image = sprites.get(backgroundKey);
        if (image == null) {
            g2.setColor(FALLBACK);
            g2.fillRect(0, 0, getWidth(), getHeight());
        } else {
            int sourceHeight = (int) (image.getHeight() * (1 - cropBottom));
            double cover = Math.max(getWidth() / (double) image.getWidth(), getHeight() / (double) sourceHeight);
            int drawWidth = (int) Math.ceil(image.getWidth() * cover);
            int drawHeight = (int) Math.ceil(sourceHeight * cover);
            int x = (getWidth() - drawWidth) / 2;
            int y = (getHeight() - drawHeight) / 2;
            g2.drawImage(image, x, y, x + drawWidth, y + drawHeight,
                    0, 0, image.getWidth(), sourceHeight, null);
        }
        g2.dispose();
    }

    /**
     * Draws an image centered at a point, in design coordinates.
     *
     * @param g       graphics already scaled to design coordinates
     * @param key     key of the image
     * @param centerX x of the center
     * @param centerY y of the center
     * @param width   width to draw
     * @param height  height to draw
     * @return true if the image exists and was drawn
     */
    protected boolean drawCentered(Graphics2D g, String key, double centerX, double centerY,
                                   double width, double height) {
        BufferedImage image = sprites.get(key);
        if (image == null) {
            return false;
        }
        g.drawImage(image, (int) (centerX - width / 2), (int) (centerY - height / 2),
                (int) width, (int) height, null);
        return true;
    }

    /**
     * Creates a copy of the graphics scaled to design coordinates.
     *
     * @param g graphics given by Swing
     * @return a scaled copy that must be disposed by the caller
     */
    protected Graphics2D designGraphics(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        Theme.applyQuality(g2);
        double s = scale();
        g2.scale(s, s);
        return g2;
    }
}
