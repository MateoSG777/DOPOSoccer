package com.doposoccer.presentation.render;
import com.doposoccer.domain.model.Field;
import com.doposoccer.domain.model.RenderState;
import com.doposoccer.presentation.resource.SpriteCache;
import com.doposoccer.presentation.resource.SpritePath;
import com.doposoccer.presentation.style.Palette;
import com.doposoccer.presentation.style.Theme;
import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

/**
 * Draws a goal in two layers to give depth: the post behind the players and the
 * ball, and the net in front of them, so the ball is seen inside the net
 * The images show the right goal; the left one is drawn mirrored
 */
public class GoalPainter implements FigurePainter {

    private static final String BACK_KEY = SpritePath.match("goalpost2");
    private static final String FRONT_KEY = SpritePath.match("goalpost1");
    private static final double IMAGE_CROSSBAR_ROW = 3;
    private static final double IMAGE_GROUND_ROW = 94;
    private static final double IMAGE_MOUTH_COLUMN = 7;
    private static final double IMAGE_BACK_COLUMN = 25;
    private final SpriteCache sprites;

    /**
     * Creates the goal painter
     * @param sprites shared sprite cache
     */
    public GoalPainter(SpriteCache sprites) {
        this.sprites = sprites;
    }

    /**
     * Draws the post, behind players and ball.
     * @param g graphics in field coordinates
     * @param state what to draw
     */
    @Override
    public void paintBack(Graphics2D g, RenderState state) {
        drawLayer(g, state, BACK_KEY);
    }

    /**
     * Draws the net, in front of players and ball
     * @param g graphics in field coordinates
     * @param state what to draw
     */
    @Override
    public void paintFront(Graphics2D g, RenderState state) {
        if (!drawLayer(g, state, FRONT_KEY)) {
            drawFallbackFrame(g, state);
        }
    }

    private boolean drawLayer(Graphics2D g, RenderState state, String key) {
        BufferedImage image = sprites.get(key);
        if (image == null) {
            return false;
        }
        double crossbarTop = Field.crossbarY() - Field.CROSSBAR_THICKNESS;
        double scaleY = (Field.GROUND_Y - crossbarTop) / (IMAGE_GROUND_ROW - IMAGE_CROSSBAR_ROW);
        double scaleX = state.getWidth() / (IMAGE_BACK_COLUMN - IMAGE_MOUTH_COLUMN);
        double left = state.getX() - state.getWidth() / 2;
        double x = left - IMAGE_MOUTH_COLUMN * scaleX;
        double y = crossbarTop - IMAGE_CROSSBAR_ROW * scaleY;

        AffineTransform original = g.getTransform();
        mirrorIfNeeded(g, state);
        g.drawImage(image, (int) x, (int) y, (int) (image.getWidth() * scaleX), (int) (image.getHeight() * scaleY), null);
        g.setTransform(original);
        return true;
    }

    private void drawFallbackFrame(Graphics2D g, RenderState state) {
        AffineTransform original = g.getTransform();
        mirrorIfNeeded(g, state);
        int left = (int) (state.getX() - state.getWidth() / 2);
        int right = (int) (state.getX() + state.getWidth() / 2);
        int crossbar = (int) Field.crossbarY();
        g.setColor(Theme.color(Palette.NET));
        g.setStroke(new BasicStroke(6f));
        g.drawLine(left, crossbar, right, crossbar);
        g.drawLine(left, crossbar, left, (int) Field.GROUND_Y);
        g.setStroke(new BasicStroke(1f));
        for (int i = crossbar; i < Field.GROUND_Y; i += 12) {
            g.drawLine(left, i, right, i);
        }
        g.setTransform(original);
    }

    private void mirrorIfNeeded(Graphics2D g, RenderState state) {
        if (!state.isFlipped()) {
            g.translate(state.getX(), 0);
            g.scale(-1, 1);
            g.translate(-state.getX(), 0);
        }
    }
}
