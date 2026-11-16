package com.doposoccer.presentation.component;
import com.doposoccer.presentation.style.Palette;
import com.doposoccer.presentation.style.Theme;
import javax.swing.ButtonModel;
import javax.swing.JButton;
import java.awt.BasicStroke;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Graphics;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;

/**
 * Round settings button with a gear drawn in code, so it needs no image.
 * Like the other buttons it swaps its colors when hovered and never takes the keyboard focus
 */
public class GearButton extends JButton {

    private static final int TEETH = 8;
    private static final double TIP_RADIUS = 0.34;  // radius of the teeth as a fraction of the button size
    private static final double ROOT_RADIUS = 0.26;  // radius between the teeth
    private static final double HOLE_RADIUS = 0.11;
    private static final double BASE_WIDTH = 0.30;  // width of a tooth at its base, as a fraction of the pitch
    private static final double TIP_WIDTH = 0.18;
    private static final float RING = 3f;

    /** Creates the button */
    public GearButton() {
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setFocusable(false);
        setOpaque(false);
        setRolloverEnabled(true);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    /**
     * Paints the round body and the gear
     * @param g graphics given by Swing
     */
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        Theme.applyQuality(g2);
        ButtonModel model = getModel();
        boolean active = model.isPressed() || model.isRollover();
        double size = Math.min(getWidth(), getHeight());
        Ellipse2D body = new Ellipse2D.Double(RING, RING, size - 2 * RING, size - 2 * RING);
        g2.setColor(Theme.color(active ? Palette.GOLD : Palette.DARK));
        g2.fill(body);
        g2.setStroke(new BasicStroke(RING));
        g2.setColor(Theme.color(active ? Palette.DARK : Palette.GOLD));
        g2.draw(body);
        g2.setColor(Theme.color(active ? Palette.DARK : Palette.GOLD));
        g2.fill(gear(size / 2, size / 2, size));
        g2.dispose();
    }

    // outline of the gear: a trapezoid tooth every pitch, with the hole cut in the middle
    private Area gear(double cx, double cy, double size) {
        double pitch = 2 * Math.PI / TEETH;
        Path2D outline = new Path2D.Double();
        for (int i = 0; i < TEETH; i++) {
            double a = i * pitch;
            point(outline, i == 0, cx, cy, ROOT_RADIUS * size, a - BASE_WIDTH * pitch);
            point(outline, false, cx, cy, TIP_RADIUS * size, a - TIP_WIDTH * pitch);
            point(outline, false, cx, cy, TIP_RADIUS * size, a + TIP_WIDTH * pitch);
            point(outline, false, cx, cy, ROOT_RADIUS * size, a + BASE_WIDTH * pitch);
            point(outline, false, cx, cy, ROOT_RADIUS * size, a + pitch / 2);  // keeps the root round
        }
        outline.closePath();
        Area gear = new Area(outline);
        double hole = HOLE_RADIUS * size;
        gear.subtract(new Area(new Ellipse2D.Double(cx - hole, cy - hole, 2 * hole, 2 * hole)));
        return gear;
    }

    private void point(Path2D path, boolean first, double cx, double cy, double radius, double angle) {
        double x = cx + radius * Math.cos(angle);
        double y = cy + radius * Math.sin(angle);
        if (first) {
            path.moveTo(x, y);
        } else {
            path.lineTo(x, y);
        }
    }
}
