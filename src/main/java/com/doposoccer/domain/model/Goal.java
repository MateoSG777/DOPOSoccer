package com.doposoccer.domain.model;

import com.doposoccer.domain.enums.Figure;
import com.doposoccer.domain.enums.TeamSide;
import com.doposoccer.domain.match.GoalListener;

/**
 * Goal defended by one side. The ball bounces on its crossbar, and the goal
 * notifies the match when the ball gets in. Touching the screen border above
 * the crossbar is never a goal.
 */
public class Goal extends GameObject {

    /** Color of the net. */
    private static final int NET_COLOR = 0xEEEEEE;

    /** Side of the player who defends this goal. */
    private final TeamSide defender;

    /** Who is notified when a goal is scored. */
    private final GoalListener listener;

    /**
     * Creates the goal of one side.
     *
     * @param defender side of the player who defends this goal
     * @param listener who is notified when a goal is scored
     */
    public Goal(TeamSide defender, GoalListener listener) {
        super(centerXFor(defender), Field.GROUND_Y - Field.GOAL_HEIGHT / 2, Field.GOAL_DEPTH, Field.GOAL_HEIGHT);
        this.defender = defender;
        this.listener = listener;
    }

    /**
     * Computes where the goal of a side is placed.
     *
     * @param defender side of the player who defends the goal
     * @return x of the goal center
     */
    private static double centerXFor(TeamSide defender) {
        if (defender == TeamSide.LEFT) {
            return Field.GOAL_DEPTH / 2;
        }
        return Field.WIDTH - Field.GOAL_DEPTH / 2;
    }

    /**
     * Goals do not move.
     *
     * @param dt seconds elapsed since the last frame
     */
    @Override
    public void update(double dt) {
        // A goal is static.
    }

    /**
     * Bounces the ball on the crossbar and reports a goal if the ball got in.
     *
     * @param ball the ball of the match
     */
    @Override
    public void checkCollision(Ball ball) {
        ball.bounceOffRect(getLeft(), Field.crossbarY() - Field.CROSSBAR_THICKNESS, getRight(), Field.crossbarY());
        if (contains(ball)) {
            listener.goalScored(defender.opponent());
        }
    }

    /**
     * A goal counts when the ball completely crossed the goal line below the crossbar.
     *
     * @param ball the ball of the match
     * @return true if the ball is inside this goal
     */
    public boolean contains(Ball ball) {
        boolean belowCrossbar = ball.getTop() >= Field.crossbarY();
        boolean insideLeftGoal = ball.getRight() <= getRight();
        boolean insideRightGoal = ball.getLeft() >= getLeft();
        if (defender == TeamSide.LEFT) {
            return belowCrossbar && insideLeftGoal;
        }
        return belowCrossbar && insideRightGoal;
    }

    /**
     * Returns the description of the goal to draw it (the right goal is mirrored).
     *
     * @return description of the goal to draw it (the right goal is mirrored)
     */
    @Override
    public RenderState getRenderState() {
        return new RenderState(Figure.GOAL, "goal", "", x, y, width, height, 0,
                defender == TeamSide.RIGHT, NET_COLOR);
    }

    /**
     * Returns the side of the player who defends this goal.
     *
     * @return side of the player who defends this goal
     */
    public TeamSide getDefender() {
        return defender;
    }
}
