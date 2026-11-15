package com.doposoccer.domain.model;

import com.doposoccer.domain.enums.Figure;
import com.doposoccer.domain.power.Hit;
import com.doposoccer.domain.power.ImpactHandler;
import java.util.ArrayList;
import java.util.List;

/**
 * The ball: affected by gravity, it bounces on the ground, the ceiling, the
 * side borders, the crossbars and the players
 * A player with the aura turns it into a power shot: it flies straight to the rival goal without
 * gravity, and the power ends at the first thing it bounces on. If that thing is the rival, it is hurt
 */
public class Ball extends GameObject {

    private static final long serialVersionUID = 1L;

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

    // Horizontal speed of a power shot
    public static final double POWER_SHOT_SPEED = 1100;  // faster than a normal kick, below the maximum speed

    // Fake balls shown by the illusion
    public static final int FAKE_BALLS = 7;

    private static final double ROTATION_FACTOR = 0.5;  // the ball turns about as fast as it rolls
    private static final double FAKE_SPREAD_X = 25;  // each fake ball flies a bit farther from the real one
    private static final double FAKE_SPREAD_Y = 60;  // fake balls stay within a jump of the real one

    private double vx;
    private double vy;
    private double rotation;
    private Player shooter;
    private double illusionTimer;

    /**
     * Creates a ball at rest
     * @param x x coordinate of the center
     * @param y y coordinate of the center
     */
    public Ball(double x, double y) {
        super(x, y, RADIUS * 2, RADIUS * 2);
    }

    /**
     * Moves the ball and bounces it on the field borders. A power shot flies straight, without gravity
     * @param dt seconds elapsed since the last frame
     */
    @Override
    public void update(double dt) {
        illusionTimer = Math.max(0, illusionTimer - dt);
        x += vx * dt;
        rotation += vx * dt * ROTATION_FACTOR;
        if (isPowerShot()) {
            keepInsideSides();
            return;
        }
        vy += Field.BALL_GRAVITY * dt;
        y += vy * dt;
        keepInsideField(dt);
    }

    /**
     * A player touched the ball. A power shot that reaches the rival goes through the chain of impact
     * and ends; a player with the aura starts a new power shot towards the rival goal
     * If the rival also has the aura, it is not hurt and sends its own power shot back at once
     * A power shot never flies above the head of the shooter, so a header can still reach a rival standing
     * @param player the player that touched the ball
     */
    public void touchedBy(Player player) {
        if (isPowerShot()) {
            ImpactHandler.POWER.handle(new Hit(shooter, player,
                    shooter.getCountry().getBaseDamage() * shooter.getKickFactor()));
            endPowerShot();
        }
        if (player.hasAura()) {
            shooter = player;
            y = Math.max(y, player.getHeadY());
            player.releaseAura();
            kick(player.getSide().getDirection() * POWER_SHOT_SPEED, 0);
            player.getCountry().shoot(this);
        }
    }

    /** The power shot bounced on something: the power goes away and the ball is normal again */
    public void endPowerShot() {
        shooter = null;
    }

    /**
     * Tells whether this ball is the power shot of a player, which passes through it
     * @param player player to check
     * @return true if that player launched the power shot in flight
     */
    public boolean isShotBy(Player player) {
        return shooter == player;
    }

    /**
     * Shows fake balls next to the real one for a while
     * @param seconds duration of the illusion
     */
    public void startIllusion(double seconds) {
        illusionTimer = seconds;
    }

    /**
     * Returns the fake balls of the illusion to draw them
     * @return one description per fake ball, empty when there is no illusion
     */
    public List<RenderState> getFakeStates() {
        List<RenderState> fakes = new ArrayList<>();
        if (illusionTimer <= 0) {
            return fakes;
        }
        for (int i = 1; i <= FAKE_BALLS; i++) {
            double fakeX = x + Math.cos(i * 2.4 + illusionTimer * 5) * FAKE_SPREAD_X * i;
            double fakeY = y + Math.sin(i * 1.7 + illusionTimer * 7) * FAKE_SPREAD_Y;
            fakeY = Math.min(fakeY, Field.GROUND_Y - RADIUS);
            fakes.add(new RenderState(Figure.BALL, "ball", fakeX, fakeY, width, height, rotation, false, glow(), false));
        }
        return fakes;
    }

    private String glow() {
        if (shooter == null) {
            return null;
        }
        return shooter.getCountry().getSpriteKey();
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
        return new RenderState(Figure.BALL, "ball", x, y, width, height, rotation, false, glow(), false);
    }

    /**
     * Places the ball at rest. Any power shot ends without effect
     * @param x new x coordinate of the center
     * @param y new y coordinate of the center
     */
    public void resetAt(double x, double y) {
        this.x = x;
        this.y = y;
        this.vx = 0;
        this.vy = 0;
        this.shooter = null;
        this.illusionTimer = 0;
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

    private void keepInsideSides() {
        if (x - RADIUS <= 0 || x + RADIUS >= Field.WIDTH) {
            endPowerShot();
            keepInsideField(0);
        }
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
     * Tells whether the ball is a power shot
     * @return true while a power shot is flying
     */
    public boolean isPowerShot() {
        return shooter != null;
    }

    /**
     * Returns the radius of the ball
     * @return radius of the ball
     */
    public double getRadius() {
        return RADIUS;
    }
}
