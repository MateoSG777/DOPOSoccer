package com.doposoccer.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests of Ball.
 */
class BallTest {

    /** Tolerance to compare doubles. */
    private static final double DELTA = 1e-6;

    /** Should fall by gravity. */
    @Test
    void shouldFallByGravity() {
        Ball ball = new Ball(500, 100);
        ball.update(0.1);
        assertEquals(Field.BALL_GRAVITY * 0.1, ball.getVy(), DELTA);
        assertTrue(ball.getY() > 100);
    }

    /** Should bounce on ground losing energy. */
    @Test
    void shouldBounceOnGroundLosingEnergy() {
        Ball ball = new Ball(500, Field.GROUND_Y - Ball.RADIUS - 1);
        ball.kick(0, 400);
        ball.update(0.02);
        assertTrue(ball.getVy() < 0);
        assertTrue(Math.abs(ball.getVy()) < 400 + Field.BALL_GRAVITY * 0.02);
        assertTrue(ball.getBottom() <= Field.GROUND_Y);
    }

    /** Should stop bouncing when too slow. */
    @Test
    void shouldStopBouncingWhenTooSlow() {
        Ball ball = new Ball(500, Field.GROUND_Y - Ball.RADIUS);
        ball.update(0.01);
        assertEquals(0, ball.getVy(), DELTA);
        assertEquals(Field.GROUND_Y - Ball.RADIUS, ball.getY(), DELTA);
    }

    /** Should bounce on left wall. */
    @Test
    void shouldBounceOnLeftWall() {
        Ball ball = new Ball(Ball.RADIUS + 1, 300);
        ball.kick(-500, 0);
        ball.update(0.01);
        assertTrue(ball.getVx() > 0);
        assertTrue(ball.getLeft() >= 0);
    }

    /** Should bounce on right wall. */
    @Test
    void shouldBounceOnRightWall() {
        Ball ball = new Ball(Field.WIDTH - Ball.RADIUS - 1, 300);
        ball.kick(500, 0);
        ball.update(0.01);
        assertTrue(ball.getVx() < 0);
        assertTrue(ball.getRight() <= Field.WIDTH);
    }

    /** Should bounce on ceiling. */
    @Test
    void shouldBounceOnCeiling() {
        Ball ball = new Ball(500, Ball.RADIUS + 1);
        ball.kick(0, -600);
        ball.update(0.01);
        assertTrue(ball.getVy() > 0);
    }

    /** Should bounce off circle from above. */
    @Test
    void shouldBounceOffCircleFromAbove() {
        Ball ball = new Ball(500, 300 - 30 - Ball.RADIUS + 5);
        ball.kick(0, 200);
        assertTrue(ball.bounceOffCircle(500, 300, 30, 0, 0, 100));
        assertTrue(ball.getVy() < 0);
    }

    /** Should not bounce off far circle. */
    @Test
    void shouldNotBounceOffFarCircle() {
        Ball ball = new Ball(100, 100);
        ball.kick(0, 200);
        assertFalse(ball.bounceOffCircle(500, 300, 30, 0, 0, 100));
        assertEquals(200, ball.getVy(), DELTA);
    }

    /** Should bounce off rect from above. */
    @Test
    void shouldBounceOffRectFromAbove() {
        Ball ball = new Ball(500, 300 - Ball.RADIUS + 3);
        ball.kick(0, 300);
        assertTrue(ball.bounceOffRect(450, 300, 550, 310));
        assertTrue(ball.getVy() < 0);
    }

    /** Should limit speed. */
    @Test
    void shouldLimitSpeed() {
        Ball ball = new Ball(500, 300);
        ball.kick(5000, 0);
        assertTrue(Math.hypot(ball.getVx(), ball.getVy()) <= Ball.MAX_SPEED + DELTA);
    }

    /** Should reset quiet at position. */
    @Test
    void shouldResetQuietAtPosition() {
        Ball ball = new Ball(500, 300);
        ball.kick(300, -300);
        ball.resetAt(10, 20);
        assertEquals(10, ball.getX(), DELTA);
        assertEquals(20, ball.getY(), DELTA);
        assertEquals(0, ball.getVx(), DELTA);
        assertEquals(0, ball.getVy(), DELTA);
    }
}
