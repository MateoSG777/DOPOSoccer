package com.doposoccer.presentation.render;

import com.doposoccer.domain.model.Field;
import com.doposoccer.presentation.SpriteCache;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 * Draws the stadium in three layers stretched to the field width:
 * sky and lights at the top, the crowd in the middle and the grass at the bottom.
 */
public class StadiumPainter {

    /** Key of the sky and lights image. */
    private static final String SKY_KEY = "top_field";

    /** Key of the crowd image. */
    private static final String CROWD_KEY = "people";

    /** Key of the grass image. */
    private static final String GRASS_KEY = "field";

    /** Sky color used when the image is missing. */
    private static final Color SKY = new Color(90, 170, 230);

    /** Grass color used when the image is missing. */
    private static final Color GRASS = new Color(110, 170, 40);

    /** Shared sprite cache. */
    private final SpriteCache sprites;

    /**
     * Creates the stadium painter.
     *
     * @param sprites shared sprite cache
     */
    public StadiumPainter(SpriteCache sprites) {
        this.sprites = sprites;
    }

    /**
     * Draws the whole stadium behind the match.
     *
     * @param g graphics in field coordinates
     */
    public void paint(Graphics2D g) {
        g.setColor(SKY);
        g.fillRect(0, 0, (int) Field.WIDTH, (int) Field.HEIGHT);
        double grassTop = Field.HEIGHT - heightOf(GRASS_KEY);
        double crowdTop = grassTop - heightOf(CROWD_KEY);
        drawStretched(g, SKY_KEY, 0);
        drawStretched(g, CROWD_KEY, crowdTop);
        if (!drawStretched(g, GRASS_KEY, grassTop)) {
            g.setColor(GRASS);
            g.fillRect(0, (int) (Field.GROUND_Y - 60), (int) Field.WIDTH, (int) (Field.HEIGHT - Field.GROUND_Y + 60));
        }
    }

    /**
     * Returns the height of a layer once stretched to the field width.
     *
     * @param key key of the layer image
     * @return its height in field coordinates (0 if it is missing)
     */
    private double heightOf(String key) {
        BufferedImage image = sprites.get(key);
        if (image == null) {
            return 0;
        }
        return image.getHeight() * Field.WIDTH / image.getWidth();
    }

    /**
     * Draws a layer stretched to the field width.
     *
     * @param g   graphics in field coordinates
     * @param key key of the layer image
     * @param top y where the layer starts
     * @return true if the image exists and was drawn
     */
    private boolean drawStretched(Graphics2D g, String key, double top) {
        BufferedImage image = sprites.get(key);
        if (image == null) {
            return false;
        }
        g.drawImage(image, 0, (int) top, (int) Field.WIDTH, (int) Math.ceil(heightOf(key)), null);
        return true;
    }
}
