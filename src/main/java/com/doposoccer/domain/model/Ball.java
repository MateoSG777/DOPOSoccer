package com.doposoccer.domain.model;

import com.doposoccer.domain.enums.Figure;

/**
 * The ball: affected by gravity, it bounces on the ground, the ceiling, the
 * side borders, the crossbars and the players. It is the expert on how to bounce.
 */
public class Ball extends GameObject {

    /** Radius of the ball. */
    public static final double RADIUS = 18;

    /** Fraction of vertical speed kept after bouncing on the ground. */
    public static final double GROUND_RESTITUTION = 0.75;

    /** Fraction of speed kept after bouncing on walls, crossbars and bodies. */
    public static final double WALL_RESTITUTION = 0.8;

    /** Horizontal speed lost per second while touching the ground. */
    public static final double ROLL_FRICTION = 0.8;

    /** Below this vertical speed the ball stops bouncing and rolls. */
    public static final double MIN_BOUNCE_SPEED = 40;

    /** Maximum speed of the ball. */
    public static final double MAX_SPEED = 1300;

    /** Degrees of rotation per unit of horizontal displacement. */
    private static final double ROTATION_FACTOR = 0.5;

    /** Default color of the ball (white). */
    private static final int WHITE = 0xFFFFFF;

    /** Horizontal speed. */
    private double vx;

    /** Vertical speed (positive goes down). */
    private double vy;

    /** Accumulated rotation in degrees, used only to draw. */
    private double rotation;

    /**
     * Creates a ball at rest.
     *
     * @param x x coordinate of the center
     * @param y y coordinate of the center
     */
    public Ball(double x, double y) {
        super(x, y, RADIUS * 2, RADIUS * 2);
    }

    /**
     * Applies gravity, moves the ball and bounces it on the field borders.
     *
     * @param dt seconds elapsed since the last frame
     */
    @Override
    public void update(double dt) {
        vy += Field.BALL_GRAVITY * dt;
        x += vx * dt;
        y += vy * dt;
        rotation += vx * dt * ROTATION_FACTOR;
        bounceOnGround(dt);
        bounceOnCeiling();
        bounceOnWalls();
    }

    /**
     * The ball does not collide with itself.
     *
     * @param ball the ball of the match (this same object)
     */
    @Override
    public void checkCollision(Ball ball) {
        // No effect: the ball only reacts to the other objects.
    }

    /**
     * Returns the description of the ball to draw it.
     *
     * @return description of the ball to draw it
     */
    @Override
    public RenderState getRenderState() {
        return new RenderState(Figure.BALL, "ball", "", x, y, width, height, rotation, false, WHITE);
    }

    /**
     * Places the ball at rest.
     *
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
     * Sets the speed of the ball (a kick), limited to MAX_SPEED.
     *
     * @param vx new horizontal speed
     * @param vy new vertical speed
     */
    public void kick(double vx, double vy) {
        this.vx = vx;
        this.vy = vy;
        limitSpeed();
    }

    /**
     * Bounces against a moving circle (for example a player's head).
     *
     * @param cx        x of the circle center
     * @param cy        y of the circle center
     * @param radius    radius of the circle
     * @param carrierVx horizontal speed of the circle
     * @param carrierVy vertical speed of the circle
     * @param minSpeed  minimum exit speed in the direction of the hit
     * @return true if there was a collision
     */
    public boolean bounceOffCircle(double cx, double cy, double radius,
                                   double carrierVx, double carrierVy, double minSpeed) {
        double dx = x - cx;
        double dy = y - cy;
        double distance = Math.hypot(dx, dy);
        double minDistance = radius + RADIUS;
        if (distance >= minDistance) {
            return false;
        }
        double nx = 0;
        double ny = -1;
        if (distance > 0) {
            nx = dx / distance;
            ny = dy / distance;
        }
        x = cx + nx * minDistance;
        y = cy + ny * minDistance;

        double relVx = vx - carrierVx;
        double relVy = vy - carrierVy;
        double along = relVx * nx + relVy * ny;
        if (along < 0) {
            relVx -= 2 * along * nx;
            relVy -= 2 * along * ny;
        }
        vx = relVx + carrierVx;
        vy = relVy + carrierVy;
        double exitSpeed = vx * nx + vy * ny;
        if (exitSpeed < minSpeed) {
            vx += (minSpeed - exitSpeed) * nx;
            vy += (minSpeed - exitSpeed) * ny;
        }
        limitSpeed();
        return true;
    }

    /**
     * Bounces against a fixed rectangle (crossbar, player body).
     *
     * @param left   x of the left edge
     * @param top    y of the top edge
     * @param right  x of the right edge
     * @param bottom y of the bottom edge
     * @return true if there was a collision
     */
    public boolean bounceOffRect(double left, double top, double right, double bottom) {
        double closestX = Math.max(left, Math.min(x, right));
        double closestY = Math.max(top, Math.min(y, bottom));
        double dx = x - closestX;
        double dy = y - closestY;
        double distance = Math.hypot(dx, dy);
        if (distance >= RADIUS) {
            return false;
        }
        double nx = 0;
        double ny = -1;
        if (distance > 0) {
            nx = dx / distance;
            ny = dy / distance;
        }
        x = closestX + nx * RADIUS;
        y = closestY + ny * RADIUS;
        double along = vx * nx + vy * ny;
        if (along < 0) {
            vx -= (1 + WALL_RESTITUTION) * along * nx;
            vy -= (1 + WALL_RESTITUTION) * along * ny;
        }
        return true;
    }

    /**
     * Bounces on the grass, stops tiny bounces and applies rolling friction.
     *
     * @param dt seconds elapsed since the last frame
     */
    private void bounceOnGround(double dt) {
        if (y + RADIUS < Field.GROUND_Y) {
            return;
        }
        y = Field.GROUND_Y - RADIUS;
        vy = -vy * GROUND_RESTITUTION;
        if (Math.abs(vy) < MIN_BOUNCE_SPEED) {
            vy = 0;
        }
        vx -= vx * ROLL_FRICTION * dt;
    }

    /** Bounces on the top border of the screen. */
    private void bounceOnCeiling() {
        if (y - RADIUS > 0) {
            return;
        }
        y = RADIUS;
        vy = Math.abs(vy) * WALL_RESTITUTION;
    }

    /** Bounces on the left and right borders of the screen. */
    private void bounceOnWalls() {
        if (x - RADIUS <= 0) {
            x = RADIUS;
            vx = Math.abs(vx) * WALL_RESTITUTION;
        } else if (x + RADIUS >= Field.WIDTH) {
            x = Field.WIDTH - RADIUS;
            vx = -Math.abs(vx) * WALL_RESTITUTION;
        }
    }

    /** Scales the speed down to MAX_SPEED when it is higher. */
    private void limitSpeed() {
        double speed = Math.hypot(vx, vy);
        if (speed > MAX_SPEED) {
            vx = vx / speed * MAX_SPEED;
            vy = vy / speed * MAX_SPEED;
        }
    }

    /**
     * Returns the horizontal speed.
     *
     * @return horizontal speed
     */
    public double getVx() {
        return vx;
    }

    /**
     * Returns the vertical speed (positive goes down).
     *
     * @return vertical speed (positive goes down)
     */
    public double getVy() {
        return vy;
    }

    /**
     * Returns the radius of the ball.
     *
     * @return radius of the ball
     */
    public double getRadius() {
        return RADIUS;
    }
}
