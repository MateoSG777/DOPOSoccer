package com.doposoccer.domain.model;

import com.doposoccer.domain.enums.TeamSide;
import com.doposoccer.domain.match.GoalListener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests of Goal.
 */
class GoalTest {

    /** Listener that records the goals. */
    private RecordingListener listener;
    /** Goal defended by player 1. */
    private Goal leftGoal;
    /** Goal defended by player 2. */
    private Goal rightGoal;

    /** Creates fresh objects before each test. */
    @BeforeEach
    void setUp() {
        listener = new RecordingListener();
        leftGoal = new Goal(TeamSide.LEFT, listener);
        rightGoal = new Goal(TeamSide.RIGHT, listener);
    }

    /** Ball inside left goal should be a goal for the right player. */
    @Test
    void ballInsideLeftGoalShouldBeAGoalForTheRightPlayer() {
        Ball ball = new Ball(Field.GOAL_DEPTH / 2, Field.GROUND_Y - Ball.RADIUS);
        leftGoal.checkCollision(ball);
        assertEquals(1, listener.goals);
        assertEquals(TeamSide.RIGHT, listener.lastScorer);
    }

    /** Ball inside right goal should be a goal for the left player. */
    @Test
    void ballInsideRightGoalShouldBeAGoalForTheLeftPlayer() {
        Ball ball = new Ball(Field.WIDTH - Field.GOAL_DEPTH / 2, Field.GROUND_Y - Ball.RADIUS);
        rightGoal.checkCollision(ball);
        assertEquals(1, listener.goals);
        assertEquals(TeamSide.LEFT, listener.lastScorer);
    }

    /** Ball on the line should not be a goal. */
    @Test
    void ballOnTheLineShouldNotBeAGoal() {
        Ball ball = new Ball(Field.GOAL_DEPTH, Field.GROUND_Y - Ball.RADIUS);
        leftGoal.checkCollision(ball);
        assertEquals(0, listener.goals);
    }

    /** Ball above crossbar should not be a goal. */
    @Test
    void ballAboveCrossbarShouldNotBeAGoal() {
        Ball ball = new Ball(Field.GOAL_DEPTH / 2, Field.crossbarY() - 60);
        leftGoal.checkCollision(ball);
        assertEquals(0, listener.goals);
    }

    /** Ball touching the wall above the goal should not be a goal. */
    @Test
    void ballTouchingTheWallAboveTheGoalShouldNotBeAGoal() {
        Ball ball = new Ball(Ball.RADIUS, Field.crossbarY() - Field.CROSSBAR_THICKNESS - 80);
        leftGoal.checkCollision(ball);
        rightGoal.checkCollision(ball);
        assertEquals(0, listener.goals);
    }

    /** Ball touching the net should be a goal. */
    @Test
    void ballTouchingTheNetShouldBeAGoal() {
        Ball ball = new Ball(Ball.RADIUS, Field.crossbarY() + Ball.RADIUS + 1);
        leftGoal.checkCollision(ball);
        assertEquals(1, listener.goals);
    }

    /** Ball should bounce on crossbar. */
    @Test
    void ballShouldBounceOnCrossbar() {
        double crossbarTop = Field.crossbarY() - Field.CROSSBAR_THICKNESS;
        Ball ball = new Ball(Field.GOAL_DEPTH / 2, crossbarTop - Ball.RADIUS + 3);
        ball.kick(0, 300);
        leftGoal.checkCollision(ball);
        assertTrue(ball.getVy() < 0);
        assertEquals(0, listener.goals);
    }

    /** Test listener that remembers the goals it received. */
    private static class RecordingListener implements GoalListener {
        /** Number of goals received. */
        private int goals;
        /** Side of the last scorer. */
        private TeamSide lastScorer;

        /** Records the goal. */
        @Override
        public void goalScored(TeamSide scorer) {
            goals++;
            lastScorer = scorer;
        }
    }
}
