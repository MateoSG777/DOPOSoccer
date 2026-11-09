package com.doposoccer.presentation.render;
import com.doposoccer.domain.model.Field;
import com.doposoccer.presentation.resource.SpriteCache;
import com.doposoccer.presentation.resource.SpritePath;
import com.doposoccer.presentation.style.Palette;
import com.doposoccer.presentation.style.Theme;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 * Draws the stadium in three layers stretched to the field width:
 * sky and lights at the top, the crowd in the middle and the grass at the bottom
 */
public class StadiumPainter {

    private static final String SKY_KEY = SpritePath.background("top_field");
    private static final String CROWD_KEY = SpritePath.background("people");
    private static final String GRASS_KEY = SpritePath.background("field");
    private final SpriteCache sprites;

    /**
     * Creates the stadium painter
     * @param sprites shared sprite cache
     */
    public StadiumPainter(SpriteCache sprites) {
        this.sprites = sprites;
    }

    /**
     * Draws the whole stadium behind the match
     * @param g graphics in field coordinates
     */
    public void paint(Graphics2D g) {
        g.setColor(Theme.color(Palette.SKY));
        g.fillRect(0, 0, (int) Field.WIDTH, (int) Field.HEIGHT);
        double grassTop = Field.HEIGHT - heightOf(GRASS_KEY);
        double crowdTop = grassTop - heightOf(CROWD_KEY);
        drawStretched(g, SKY_KEY, 0);
        drawStretched(g, CROWD_KEY, crowdTop);
        if (!drawStretched(g, GRASS_KEY, grassTop)) {
            g.setColor(Theme.color(Palette.GRASS));
            g.fillRect(0, (int) (Field.GROUND_Y - 60), (int) Field.WIDTH, (int) (Field.HEIGHT - Field.GROUND_Y + 60));
        }
    }

    private double heightOf(String key) {
        BufferedImage image = sprites.get(key);
        if (image == null) {
            return 0;
        }
        return image.getHeight() * Field.WIDTH / image.getWidth();
    }

    private boolean drawStretched(Graphics2D g, String key, double top) {
        BufferedImage image = sprites.get(key);
        if (image == null) {
            return false;
        }
        g.drawImage(image, 0, (int) top, (int) Field.WIDTH, (int) Math.ceil(heightOf(key)), null);
        return true;
    }
}
