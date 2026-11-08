package com.doposoccer.domain.model;

import com.doposoccer.domain.enums.Country;
import com.doposoccer.domain.enums.PlayerState;
import com.doposoccer.domain.enums.TeamSide;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests of Player.
 */
class PlayerTest {

    /** Tolerance to compare doubles. */
    private static final double DELTA = 1e-6;
    /** y of a player standing on the grass. */
    private static final double STANDING_Y = Field.GROUND_Y - Player.HEIGHT / 2;

    /** Player 1 standing on the ground. */
    private Player left;
    /** Player 2 standing on the ground. */
    private Player right;

    /** Creates fresh objects before each test. */
    @BeforeEach
    void setUp() {
        left = new Player(Country.KOREA, TeamSide.LEFT, 300, STANDING_Y);
        right = new Player(Country.USA, TeamSide.RIGHT, 700, STANDING_Y);
        left.update(0.01);
        right.update(0.01);
    }

    /** Should stand on ground. */
    @Test
    void shouldStandOnGround() {
        assertTrue(left.isOnGround());
        assertEquals(Field.GROUND_Y, left.getBottom(), DELTA);
        assertEquals(PlayerState.IDLE, left.getState());
    }

    /** Should move right. */
    @Test
    void shouldMoveRight() {
        left.moveRight();
        left.update(0.1);
        assertEquals(300 + Player.BASE_SPEED * 0.1, left.getX(), DELTA);
        assertEquals(PlayerState.WALKING, left.getState());
    }

    /** Should move left. */
    @Test
    void shouldMoveLeft() {
        left.moveLeft();
        left.update(0.1);
        assertEquals(300 - Player.BASE_SPEED * 0.1, left.getX(), DELTA);
    }

    /** Should not leave the field. */
    @Test
    void shouldNotLeaveTheField() {
        for (int i = 0; i < 300; i++) {
            left.moveLeft();
            left.update(0.02);
        }
        assertEquals(0, left.getLeft(), DELTA);
    }

    /** Should jump from ground. */
    @Test
    void shouldJumpFromGround() {
        left.jump();
        left.update(0.01);
        assertFalse(left.isOnGround());
        assertTrue(left.getVy() < 0);
        assertEquals(PlayerState.JUMPING, left.getState());
    }

    /** Should not jump twice in the air. */
    @Test
    void shouldNotJumpTwiceInTheAir() {
        left.jump();
        left.update(0.01);
        double vyInAir = left.getVy();
        left.jump();
        left.update(0.0);
        assertEquals(vyInAir, left.getVy(), DELTA);
    }

    /** Should land after jumping. */
    @Test
    void shouldLandAfterJumping() {
        left.jump();
        for (int i = 0; i < 120; i++) {
            left.update(1.0 / 60);
        }
        assertTrue(left.isOnGround());
        assertEquals(PlayerState.IDLE, left.getState());
    }

    /** Should finish kick after its duration. */
    @Test
    void shouldFinishKickAfterItsDuration() {
        left.kick();
        left.update(0.01);
        assertEquals(PlayerState.KICKING, left.getState());
        left.update(Player.KICK_DURATION);
        assertEquals(PlayerState.IDLE, left.getState());
    }

    /** Left player should kick ball towards the right. */
    @Test
    void leftPlayerShouldKickBallTowardsTheRight() {
        Ball ball = new Ball(left.getFootX(), left.getFootY());
        left.kick();
        left.checkCollision(ball);
        assertEquals(Player.KICK_SPEED_X, ball.getVx(), DELTA);
        assertEquals(Player.KICK_SPEED_Y, ball.getVy(), DELTA);
    }

    /** Right player should kick ball towards the left. */
    @Test
    void rightPlayerShouldKickBallTowardsTheLeft() {
        Ball ball = new Ball(right.getFootX(), right.getFootY());
        right.kick();
        right.checkCollision(ball);
        assertEquals(-Player.KICK_SPEED_X, ball.getVx(), DELTA);
    }

    /** Should not kick ball out of reach. */
    @Test
    void shouldNotKickBallOutOfReach() {
        Ball ball = new Ball(left.getX() + 300, Field.GROUND_Y - Ball.RADIUS);
        left.kick();
        left.checkCollision(ball);
        assertEquals(0, ball.getVx(), DELTA);
    }

    /** Should apply kick only once per kick. */
    @Test
    void shouldApplyKickOnlyOncePerKick() {
        Ball ball = new Ball(left.getFootX(), left.getFootY());
        left.kick();
        left.checkCollision(ball);
        ball.resetAt(left.getFootX(), left.getFootY());
        left.checkCollision(ball);
        assertTrue(ball.getVx() != Player.KICK_SPEED_X || ball.getVy() != Player.KICK_SPEED_Y);
    }

    /** The kicking foot should swing forward and up, reaching a ball in front of the face. */
    @Test
    void kickShouldSwingTheFootForwardAndUp() {
        double restX = left.getFootX();
        double restY = left.getFootY();
        left.kick();
        left.update(Player.KICK_DURATION / 2);
        assertEquals(Player.KICK_FOOT_ANGLE, left.getLegAngle(), 1e-6);
        assertTrue(left.getFootX() > restX);
        assertTrue(left.getFootY() < restY);
    }

    /** A kick should hit a ball floating in front of the player during the swing. */
    @Test
    void kickShouldHitABallInFrontDuringTheSwing() {
        Ball ball = new Ball(left.getX() + Player.HIP_OFFSET_X + Player.LEG_LENGTH + Ball.RADIUS,
                left.getY() - 5);
        left.kick();
        boolean hit = false;
        for (int i = 0; i < 20 && !hit; i++) {
            left.update(1.0 / 60);
            left.checkCollision(ball);
            hit = ball.getVx() == Player.KICK_SPEED_X;
        }
        assertTrue(hit);
    }

    /** Should bounce ball on head. */
    @Test
    void shouldBounceBallOnHead() {
        Ball ball = new Ball(left.getHeadX(), left.getHeadY() - Player.HEAD_RADIUS - Ball.RADIUS + 4);
        ball.kick(0, 300);
        left.checkCollision(ball);
        assertTrue(ball.getVy() < 0);
    }

    /** Players should not overlap. */
    @Test
    void playersShouldNotOverlap() {
        Player other = new Player(Country.NIGERIA, TeamSide.RIGHT, 320, STANDING_Y);
        other.update(0.01);
        left.separateFrom(other);
        assertTrue(left.getRight() <= other.getLeft() + DELTA);
    }
}
