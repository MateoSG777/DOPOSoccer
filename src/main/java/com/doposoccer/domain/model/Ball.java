package com.doposoccer.domain.model;

import com.doposoccer.domain.enums.Figure;

/**
 * The ball: affected by gravity, it bounces on the ground, the ceiling, the
 * side borders, the crossbars and the players
 */
public class Ball extends GameObject {

    // Radius of the ball
    public static final double RADIUS = 18;

    // Fraction of vertical speed kept after bouncing on the ground
    public static final double GROUND_RESTITUTION = 0.75;  // keeps 75 % of the speed, but it stops after a few bounces

    // Fraction of speed kept after bouncing on walls, crossbars and bodies
    public static final double WALL_RESTITUTION = 0.8;  // walls and posts are a bit harder than the grass

    // Horizontal speed lost per second while touching the ground
    public static final double ROLL_FRICTION = 0.8;  // a rolling ball loses about half of its speed every second

    // Below this vertical speed the ball stops bouncing and rolls
    public static final double MIN_BOUNCE_SPEED = 40;  // below this the bounce is not visible, so the ball just rolls

    // Maximum speed of the ball
    public static final double MAX_SPEED = 1300;  // about one field width per second: fast but still visible

    private static final double ROTATION_FACTOR = 0.5;  // the ball turns about as fast as it rolls

    private double vx;
    private double vy;
    private double rotation;

    /**
     * Creates a ball at rest
     * @param x x coordinate of the center
     * @param y y coordinate of the center
     */
    public Ball(double x, double y) {
        super(x, y, RADIUS * 2, RADIUS * 2);
    }

    /**
     * Applies gravity, moves the ball and bounces it on the field borders
     * @param dt seconds elapsed since the last frame
     */
    @Override
    public void update(double dt) {
        vy += Field.BALL_GRAVITY * dt;
        x += vx * dt;
        y += vy * dt;
        rotation += vx * dt * ROTATION_FACTOR;
        keepInsideField(dt);
    }

    /**
     * The ball does not collide with itself
     * @param ball the ball of the match
     */
    @Override
    public void checkCollision(Ball ball) {
    }

    /**
     * Returns the description of the ball to draw it
     * @return description of the ball to draw it
     */
    @Override
    public RenderState getRenderState() {
        return new RenderState(Figure.BALL, "ball", x, y, width, height, rotation, false);
    }

    /**
     * Places the ball at rest
     * @param x new x coordinate of the center
     * @param y new y coordinate of the center
     */
    public void resetAt(double x, double y) {
        this.x = x;
        this.y = y;
        this.vx = 0;
        this.vy = 0;
    }

    /**
     * Sets the speed of the ball, limited to MAX_SPEED
     * @param vx new horizontal speed
     * @param vy new vertical speed
     */
    public void kick(double vx, double vy) {
        this.vx = vx;
        this.vy = vy;
        limitSpeed();
    }

    /**
     * Bounces against a moving circle
     * @param cx x of the circle center
     * @param cy y of the circle center
     * @param radius radius of the circle
     * @param carrierVx horizontal speed of the circle
     * @param carrierVy vertical speed of the circle
     * @param minSpeed  minimum exit speed in the direction of the hit
     * @return true if there was a collision
     */
    public boolean bounceOffCircle(double cx, double cy, double radius,
                                   double carrierVx, double carrierVy, double minSpeed) {
        Vector2D normal = pushOutOf(cx, cy, radius + RADIUS);
        if (normal == null) {
            return false;
        }
        reflect(normal, carrierVx, carrierVy, 1);
        double exitSpeed = vx * normal.getX() + vy * normal.getY();
        if (exitSpeed < minSpeed) {
            vx += (minSpeed - exitSpeed) * normal.getX();
            vy += (minSpeed - exitSpeed) * normal.getY();
        }
        limitSpeed();
        return true;
    }

    /**
     * Bounces against a fixed rectangle
     * @param left x of the left edge
     * @param top y of the top edge
     * @param right x of the right edge
     * @param bottom y of the bottom edge
     * @return true if there was a collision
     */
    public boolean bounceOffRect(double left, double top, double right, double bottom) {
        double closestX = Math.max(left, Math.min(x, right));
        double closestY = Math.max(top, Math.min(y, bottom));
        Vector2D normal = pushOutOf(closestX, closestY, RADIUS);
        if (normal == null) {
            return false;
        }
        reflect(normal, 0, 0, WALL_RESTITUTION);
        return true;
    }

    private Vector2D pushOutOf(double pointX, double pointY, double minDistance) {
        double dx = x - pointX;
        double dy = y - pointY;
        double distance = Math.hypot(dx, dy);
        if (distance >= minDistance) {
            return null;
        }
        Vector2D normal = new Vector2D(0, -1);
        if (distance > 0) {
            normal = new Vector2D(dx / distance, dy / distance);
        }
        x = pointX + normal.getX() * minDistance;
        y = pointY + normal.getY() * minDistance;
        return normal;
    }

    private void reflect(Vector2D normal, double carrierVx, double carrierVy, double restitution) {
        double relVx = vx - carrierVx;
        double relVy = vy - carrierVy;
        double along = relVx * normal.getX() + relVy * normal.getY();
        if (along < 0) {
            relVx -= (1 + restitution) * along * normal.getX();
            relVy -= (1 + restitution) * along * normal.getY();
        }
        vx = relVx + carrierVx;
        vy = relVy + carrierVy;
    }

    private void keepInsideField(double dt) {
        if (y + RADIUS >= Field.GROUND_Y) {
            y = Field.GROUND_Y - RADIUS;
            vy = -vy * GROUND_RESTITUTION;
            if (Math.abs(vy) < MIN_BOUNCE_SPEED) {
                vy = 0;
            }
            vx -= vx * ROLL_FRICTION * dt;
        }
        if (y - RADIUS <= 0) {
            y = RADIUS;
            vy = Math.abs(vy) * WALL_RESTITUTION;
        }
        if (x - RADIUS <= 0) {
            x = RADIUS;
            vx = Math.abs(vx) * WALL_RESTITUTION;
        }
        if (x + RADIUS >= Field.WIDTH) {
            x = Field.WIDTH - RADIUS;
            vx = -Math.abs(vx) * WALL_RESTITUTION;
        }
    }

    private void limitSpeed() {
        double speed = Math.hypot(vx, vy);
        if (speed > MAX_SPEED) {
            vx = vx / speed * MAX_SPEED;
            vy = vy / speed * MAX_SPEED;
        }
    }

    /**
     * Returns the horizontal speed
     * @return horizontal speed
     */
    public double getVx() {
        return vx;
    }

    /**
     * Returns the vertical speed
     * @return vertical speed
     */
    public double getVy() {
        return vy;
    }

    /**
     * Returns the radius of the ball
     * @return radius of the ball
     */
    public double getRadius() {
        return RADIUS;
    }
}
