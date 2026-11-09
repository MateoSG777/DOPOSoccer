package com.doposoccer.presentation.render;
import com.doposoccer.domain.model.Player;
import com.doposoccer.domain.model.RenderState;
import com.doposoccer.presentation.resource.SpriteCache;
import com.doposoccer.presentation.resource.SpritePath;
import com.doposoccer.presentation.style.Palette;
import com.doposoccer.presentation.style.Theme;
import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;

/**
 * Draws a player as a puppet of three layers: body with the static leg, big head
 * and the moving leg, which gives the illusion of a single kicking leg
 * The puppet is built facing right and the player on the right side is drawn
 * mirrored. The head images face left, so the head is always flipped inside the puppet
 */
public class PlayerPainter implements FigurePainter {

    private static final double HEAD_DRAW_RATIO = 1.3;
    private static final double PIXEL_ART_SCALE = 2.6;
    private static final double FALLBACK_FOOT_WIDTH = 17;
    private static final double FALLBACK_FOOT_HEIGHT = 9;
    private static final String BODY_KEY = SpritePath.character("body_2");
    private static final String FOOT_KEY = SpritePath.character("foot");

    private final SpriteCache sprites;

    /**
     * Creates the player painter
     * @param sprites shared sprite cache
     */
    public PlayerPainter(SpriteCache sprites) {
        this.sprites = sprites;
    }

    /**
     * Draws body, head and boot of a player
     * @param g graphics in field coordinates
     * @param state what to draw
     */
    @Override
    public void paint(Graphics2D g, RenderState state) {
        AffineTransform original = g.getTransform();
        g.translate(state.getX(), state.getY());
        if (state.isFlipped()) {
            g.scale(-1, 1);
        }
        double top = -state.getHeight() / 2;
        double headCenterY = top + Player.HEAD_RADIUS;

        paintBody(g, headCenterY + Player.HEAD_RADIUS - 4);
        paintHead(g, state, headCenterY);
        paintFoot(g, state);
        g.setTransform(original);
    }

    private double paintBody(Graphics2D g, double topY) {
        BufferedImage body = sprites.get(BODY_KEY);
        if (body == null) {
            return topY;
        }
        double w = body.getWidth() * PIXEL_ART_SCALE;
        double h = body.getHeight() * PIXEL_ART_SCALE;
        drawPixelArt(g, body, -w / 2, topY, w, h);
        return topY + h;
    }

    private void paintHead(Graphics2D g, RenderState state, double headCenterY) {
        double size = Player.HEAD_RADIUS * 2 * HEAD_DRAW_RATIO;
        BufferedImage head = sprites.get(SpritePath.character(state.getSpriteKey()));
        if (head != null) {
            AffineTransform beforeHead = g.getTransform();
            g.scale(-1, 1);
            g.drawImage(head, (int) (-size / 2), (int) (headCenterY - size / 2), (int) size, (int) size, null);
            g.setTransform(beforeHead);
            return;
        }
        Ellipse2D circle = new Ellipse2D.Double(-Player.HEAD_RADIUS, headCenterY - Player.HEAD_RADIUS,
                Player.HEAD_RADIUS * 2, Player.HEAD_RADIUS * 2);
        g.setColor(Theme.countryColor(state.getSpriteKey()));
        g.fill(circle);
        g.setStroke(new BasicStroke(3f));
        g.setColor(Theme.color(Palette.DARK));
        g.draw(circle);
    }

    private void paintFoot(Graphics2D g, RenderState state) {
        BufferedImage foot = sprites.get(FOOT_KEY);
        double w = FALLBACK_FOOT_WIDTH * PIXEL_ART_SCALE;
        double h = FALLBACK_FOOT_HEIGHT * PIXEL_ART_SCALE;
        if (foot != null) {
            w = foot.getWidth() * PIXEL_ART_SCALE;
            h = foot.getHeight() * PIXEL_ART_SCALE;
        }
        AffineTransform beforeFoot = g.getTransform();
        g.translate(Player.footLocalX(state.getAngle()), Player.footLocalY(state.getAngle()));
        g.rotate(Math.toRadians(-state.getAngle()));
        if (foot != null) {
            drawPixelArt(g, foot, -w / 2, -h / 2, w, h);
        } else {
            g.setColor(Theme.color(Palette.DARK));
            g.fillRoundRect((int) (-w / 2), (int) (-h / 2), (int) w, (int) h, 10, 10);
        }
        g.setTransform(beforeFoot);
    }

    private void drawPixelArt(Graphics2D g, BufferedImage image, double x, double y, double width, double height) {
        Object previous = g.getRenderingHint(RenderingHints.KEY_INTERPOLATION);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g.drawImage(image, (int) x, (int) y, (int) width, (int) height, null);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, previous);
    }
}
