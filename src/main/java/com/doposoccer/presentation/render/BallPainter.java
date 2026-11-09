package com.doposoccer.presentation.render;
import com.doposoccer.domain.model.RenderState;
import com.doposoccer.presentation.resource.SpriteCache;
import com.doposoccer.presentation.resource.SpritePath;
import com.doposoccer.presentation.style.Palette;
import com.doposoccer.presentation.style.Theme;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

/**
 * Draws the ball rotating as it rolls
 */
public class BallPainter implements FigurePainter {

    private static final double DRAW_SCALE = 1.0;
    private static final double OFFSET_Y = 0;

    private final SpriteCache sprites;

    /**
     * Creates the ball painter
     * @param sprites shared sprite cache
     */
    public BallPainter(SpriteCache sprites) {
        this.sprites = sprites;
    }

    /**
     * Draws the rotated ball
     * @param g graphics in field coordinates
     * @param state what to draw
     */
    @Override
    public void paint(Graphics2D g, RenderState state) {
        AffineTransform original = g.getTransform();
        g.translate(state.getX(), state.getY() + OFFSET_Y);
        g.rotate(Math.toRadians(state.getAngle()));
        int w = (int) (state.getWidth() * DRAW_SCALE);
        int h = (int) (state.getHeight() * DRAW_SCALE);
        BufferedImage ball = sprites.get(SpritePath.match(state.getSpriteKey()));
        if (ball != null) {
            g.drawImage(ball, -w / 2, -h / 2, w, h, null);
        } else {
            g.setColor(Theme.color(Palette.WHITE));
            g.fillOval(-w / 2, -h / 2, w, h);
            g.setColor(Theme.color(Palette.BLACK));
            g.drawOval(-w / 2, -h / 2, w, h);
        }
        g.setTransform(original);
    }
}