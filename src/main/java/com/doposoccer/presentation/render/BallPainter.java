package com.doposoccer.presentation.render;

import com.doposoccer.domain.model.RenderState;
import com.doposoccer.presentation.SpriteCache;
import com.doposoccer.presentation.Theme;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

/**
 * Draws the ball rotating as it rolls.
 */
public class BallPainter implements FigurePainter {

    /** Size of the drawn ball compared to its collision size (1.0 = same size). */
    private static final double DRAW_SCALE = 1.0;

    /** Vertical shift of the drawn ball (negative = up, positive = down). Only visual. */
    private static final double OFFSET_Y = 0;

    /** Shared sprite cache. */
    private final SpriteCache sprites;

    /**
     * Creates the ball painter.
     *
     * @param sprites shared sprite cache
     */
    public BallPainter(SpriteCache sprites) {
        this.sprites = sprites;
    }

    /**
     * Draws the rotated ball.
     *
     * @param g     graphics in field coordinates
     * @param state what to draw
     */
    @Override
    public void paint(Graphics2D g, RenderState state) {
        AffineTransform original = g.getTransform();
        g.translate(state.x(), state.y() + OFFSET_Y);
        g.rotate(Math.toRadians(state.angle()));
        int w = (int) (state.width() * DRAW_SCALE);
        int h = (int) (state.height() * DRAW_SCALE);
        BufferedImage ball = sprites.get(state.spriteKey());
        if (ball != null) {
            g.drawImage(ball, -w / 2, -h / 2, w, h, null);
        } else {
            g.setColor(Theme.toColor(state.color()));
            g.fillOval(-w / 2, -h / 2, w, h);
            g.setColor(Color.BLACK);
            g.drawOval(-w / 2, -h / 2, w, h);
        }
        g.setTransform(original);
    }
}
