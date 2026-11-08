package com.doposoccer.presentation.render;

import com.doposoccer.domain.model.Field;
import com.doposoccer.domain.model.RenderState;
import com.doposoccer.presentation.SpriteCache;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

/**
 * Draws a goal in two layers to give depth: the post behind the players and the
 * ball, and the net in front of them, so the ball is seen inside the net.
 * The images show the right goal (mouth facing left); the left one is drawn mirrored.
 */
public class GoalPainter implements FigurePainter {

    /** Key of the post image (drawn behind players and ball). */
    private static final String BACK_KEY = "goalpost2";

    /** Key of the net image (drawn in front of players and ball). */
    private static final String FRONT_KEY = "goalpost1";

    /** Row of the image where the crossbar starts. */
    private static final double IMAGE_CROSSBAR_ROW = 3;

    /** Row of the image that touches the grass. */
    private static final double IMAGE_GROUND_ROW = 94;

    /** Column of the image where the goal mouth (front post) is. */
    private static final double IMAGE_MOUTH_COLUMN = 7;

    /** Column of the image where the back of the net is, at mid height. */
    private static final double IMAGE_BACK_COLUMN = 25;

    /** Color of the frame when the images are missing. */
    private static final Color FRAME = new Color(240, 240, 240);

    /** Shared sprite cache. */
    private final SpriteCache sprites;

    /**
     * Creates the goal painter.
     *
     * @param sprites shared sprite cache
     */
    public GoalPainter(SpriteCache sprites) {
        this.sprites = sprites;
    }

    /**
     * Draws the post, behind players and ball.
     *
     * @param g     graphics in field coordinates
     * @param state what to draw
     */
    @Override
    public void paintBack(Graphics2D g, RenderState state) {
        drawLayer(g, state, BACK_KEY);
    }

    /**
     * Draws the net, in front of players and ball.
     *
     * @param g     graphics in field coordinates
     * @param state what to draw
     */
    @Override
    public void paintFront(Graphics2D g, RenderState state) {
        if (!drawLayer(g, state, FRONT_KEY)) {
            drawFallbackFrame(g, state);
        }
    }

    /**
     * Draws one layer of the goal fitted to the goal area of the domain.
     *
     * @param g     graphics in field coordinates
     * @param state what to draw
     * @param key   key of the layer image
     * @return true if the image exists and was drawn
     */
    private boolean drawLayer(Graphics2D g, RenderState state, String key) {
        BufferedImage image = sprites.get(key);
        if (image == null) {
            return false;
        }
        double crossbarTop = Field.crossbarY() - Field.CROSSBAR_THICKNESS;
        double scaleY = (Field.GROUND_Y - crossbarTop) / (IMAGE_GROUND_ROW - IMAGE_CROSSBAR_ROW);
        double scaleX = state.width() / (IMAGE_BACK_COLUMN - IMAGE_MOUTH_COLUMN);
        double left = state.x() - state.width() / 2;
        double x = left - IMAGE_MOUTH_COLUMN * scaleX;
        double y = crossbarTop - IMAGE_CROSSBAR_ROW * scaleY;

        AffineTransform original = g.getTransform();
        mirrorIfNeeded(g, state);
        g.drawImage(image, (int) x, (int) y, (int) (image.getWidth() * scaleX), (int) (image.getHeight() * scaleY), null);
        g.setTransform(original);
        return true;
    }

    /**
     * Draws a simple frame when the images are missing.
     *
     * @param g     graphics in field coordinates
     * @param state what to draw
     */
    private void drawFallbackFrame(Graphics2D g, RenderState state) {
        AffineTransform original = g.getTransform();
        mirrorIfNeeded(g, state);
        int left = (int) (state.x() - state.width() / 2);
        int right = (int) (state.x() + state.width() / 2);
        int crossbar = (int) Field.crossbarY();
        g.setColor(FRAME);
        g.setStroke(new BasicStroke(6f));
        g.drawLine(left, crossbar, right, crossbar);
        g.drawLine(left, crossbar, left, (int) Field.GROUND_Y);
        g.setStroke(new BasicStroke(1f));
        for (int i = crossbar; i < Field.GROUND_Y; i += 12) {
            g.drawLine(left, i, right, i);
        }
        g.setTransform(original);
    }

    /**
     * Mirrors the drawing around the center of the goal for the left goal,
     * because the images show the right goal (state.flipped() is true for the right goal).
     *
     * @param g     graphics to transform
     * @param state what to draw
     */
    private void mirrorIfNeeded(Graphics2D g, RenderState state) {
        if (!state.flipped()) {
            g.translate(state.x(), 0);
            g.scale(-1, 1);
            g.translate(-state.x(), 0);
        }
    }
}
