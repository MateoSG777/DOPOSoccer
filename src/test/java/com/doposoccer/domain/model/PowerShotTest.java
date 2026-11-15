package com.doposoccer.domain.model;

import com.doposoccer.domain.enums.Country;
import com.doposoccer.domain.enums.TeamSide;
import com.doposoccer.domain.match.GoalListener;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests of the power bar, the aura, the power shot and the kicks to the rival.
 */
class PowerShotTest {

    private static final double DELTA = 1e-6;
    private static final double STANDING_Y = Field.GROUND_Y - Player.HEIGHT / 2;
    private static final double FRAME = 1.0 / 60;

    private Player left;
    private Player right;

    /** Creates two players standing on the grass. */
    @BeforeEach
    void setUp() {
        left = new Player(Country.USA, TeamSide.LEFT, 300, STANDING_Y);
        right = new Player(Country.KOREA, TeamSide.RIGHT, 700, STANDING_Y);
        left.update(0.01);
        right.update(0.01);
    }

    private Ball powerShotOf(Player shooter) {
        shooter.chargePower(1);
        shooter.activatePower();
        Ball ball = new Ball(shooter.getHeadX(), shooter.getHeadY() - Player.HEAD_RADIUS - Ball.RADIUS + 4);
        shooter.checkCollision(ball);
        return ball;
    }

    /** The power bar should fill by itself in ten seconds. */
    @Test
    void powerBarShouldFillByItself() {
        left.update(Player.POWER_CHARGE_SECONDS / 2);
        assertEquals(0.5, left.getPower(), 0.01);
        left.update(Player.POWER_CHARGE_SECONDS);
        assertEquals(1, left.getPower(), DELTA);
    }

    /** The power should not be activated before the bar is full. */
    @Test
    void shouldNotActivateWithoutAFullBar() {
        left.chargePower(0.5);
        left.activatePower();
        assertFalse(left.hasAura());
    }

    /** Activating the power should give the aura and restart the bar. */
    @Test
    void activatingShouldGiveTheAuraAndRestartTheBar() {
        left.chargePower(1);
        left.activatePower();
        assertTrue(left.hasAura());
        assertEquals(0, left.getPower(), DELTA);
        assertEquals("usa", left.getRenderState().getGlow());
    }

    /** The power shot should go straight to the rival goal, without gravity. */
    @Test
    void powerShotShouldFlyStraight() {
        Ball ball = powerShotOf(left);
        double startY = ball.getY();
        assertTrue(ball.isPowerShot());
        assertFalse(left.hasAura());
        ball.update(0.2);
        assertEquals(Ball.POWER_SHOT_SPEED, ball.getVx(), DELTA);
        assertEquals(startY, ball.getY(), DELTA);
    }

    /** The player on the right should shoot to the left. */
    @Test
    void playerOnTheRightShouldShootToTheLeft() {
        Ball ball = powerShotOf(right);
        assertEquals(-Ball.POWER_SHOT_SPEED, ball.getVx(), DELTA);
    }

    /** The power shot should pass through the player that launched it. */
    @Test
    void powerShotShouldPassThroughItsShooter() {
        Ball ball = powerShotOf(left);
        left.checkCollision(ball);
        assertTrue(ball.isPowerShot());
        assertEquals(Ball.POWER_SHOT_SPEED, ball.getVx(), DELTA);
    }

    /** A power shot that reaches a border should lose its power and fall again. */
    @Test
    void powerShotShouldEndAtTheBorder() {
        Ball ball = powerShotOf(left);
        for (int i = 0; i < 60 && ball.isPowerShot(); i++) {
            ball.update(FRAME);
        }
        assertFalse(ball.isPowerShot());
        assertTrue(ball.getVx() < 0);
        double y = ball.getY();
        ball.update(0.2);
        assertTrue(ball.getY() > y);
    }

    /** A power shot that bounces on a crossbar should lose its power. */
    @Test
    void powerShotShouldEndAtTheCrossbar() {
        GoalListener nobody = scorer -> { };
        Goal goal = new Goal(TeamSide.RIGHT, nobody);
        double ballY = Field.crossbarY() - Field.CROSSBAR_THICKNESS / 2;
        Player shooter = new Player(Country.USA, TeamSide.LEFT, goal.getLeft() - 60,
                ballY + Player.HEIGHT / 2 - Player.HEAD_RADIUS);
        Ball ball = powerShotOf(shooter);
        assertTrue(ball.isPowerShot());
        assertTrue(Math.abs(ball.getY() - ballY) < Ball.RADIUS);
        for (int i = 0; i < 4 && ball.isPowerShot(); i++) {
            ball.update(FRAME);
            goal.checkCollision(ball);
        }
        assertFalse(ball.isPowerShot());
    }

    /** The illusion should show seven fake balls for its 2.5 seconds. */
    @Test
    void illusionShouldShowSevenFakeBalls() {
        Ball ball = powerShotOf(left);
        assertEquals(Ball.FAKE_BALLS, ball.getFakeStates().size());
        ball.update(Country.USA.getEffectSeconds() + 0.1);
        assertEquals(0, ball.getFakeStates().size());
    }

    /** A power shot should hurt the rival it touches and then be a normal ball. */
    @Test
    void powerShotShouldHurtTheRivalItTouches() {
        Ball ball = powerShotOf(left);
        Player rival = new Player(Country.KOREA, TeamSide.RIGHT, ball.getX(),
                ball.getY() + Ball.RADIUS - 4 + Player.HEIGHT / 2);
        rival.checkCollision(ball);
        assertEquals(Player.BASE_LIFE - Country.USA.getBaseDamage(), rival.getLife(), DELTA);
        assertFalse(ball.isPowerShot());
    }

    /** A power shot that is placed again, as after a goal, should have no effect. */
    @Test
    void powerShotShouldEndWithoutEffectAfterAGoal() {
        Ball ball = powerShotOf(left);
        ball.resetAt(right.getHeadX(), right.getHeadY() - Player.HEAD_RADIUS - Ball.RADIUS + 4);
        right.checkCollision(ball);
        assertEquals(Player.BASE_LIFE, right.getLife(), DELTA);
    }

    /** A kick should hurt the rival within reach of the foot. */
    @Test
    void kickShouldHurtTheRivalWithinReach() {
        Player rival = new Player(Country.KOREA, TeamSide.RIGHT, left.getX() + Player.WIDTH, STANDING_Y);
        rival.update(0.01);
        left.kick();
        left.update(Player.KICK_DURATION / 2);
        left.checkKick(rival);
        left.checkKick(rival);
        assertEquals(Player.BASE_LIFE - Player.KICK_DAMAGE, rival.getLife(), DELTA);
    }

    /** A kick should not hurt a rival that is far. */
    @Test
    void kickShouldNotHurtAFarRival() {
        left.kick();
        left.update(Player.KICK_DURATION / 2);
        left.checkKick(right);
        assertEquals(Player.BASE_LIFE, right.getLife(), DELTA);
    }

    /** A kick that touched the ball should only move the ball. */
    @Test
    void kickThatTouchedTheBallShouldNotHurt() {
        Player rival = new Player(Country.KOREA, TeamSide.RIGHT, left.getX() + Player.WIDTH, STANDING_Y);
        left.kick();
        left.update(Player.KICK_DURATION / 2);
        Ball ball = new Ball(left.getFootX(), left.getFootY());
        left.checkCollision(ball);
        left.checkKick(rival);
        assertEquals(Player.BASE_LIFE, rival.getLife(), DELTA);
    }

    /** A rival with the aura should not be hurt and should send its own power shot back at once. */
    @Test
    void rivalWithTheAuraShouldSendThePowerShotBack() {
        Ball ball = powerShotOf(left);
        Player rival = new Player(Country.NIGERIA, TeamSide.RIGHT, ball.getX(),
                ball.getY() + Ball.RADIUS - 4 + Player.HEIGHT / 2);
        rival.chargePower(1);
        rival.activatePower();
        rival.checkCollision(ball);
        assertEquals(Player.BASE_LIFE, rival.getLife(), DELTA);
        assertFalse(rival.hasAura());
        assertTrue(ball.isShotBy(rival));
        assertEquals(-Ball.POWER_SHOT_SPEED, ball.getVx(), DELTA);
    }
}
