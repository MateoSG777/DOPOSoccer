package com.doposoccer.domain.model;

import com.doposoccer.domain.enums.Figure;
import com.doposoccer.domain.enums.TeamSide;
import com.doposoccer.domain.match.GoalListener;

/**
 * Goal defended by one side. The ball bounces on its crossbar, and the goal
 * notifies the match when the ball gets in. Touching the screen border above
 * the crossbar is never a goal
 */
public class Goal extends GameObject {

    private final TeamSide defender;
    private final GoalListener listener;

    /**
     * Creates the goal of one side
     * @param defender side of the player who defends this goal
     * @param listener who is notified when a goal is scored
     */
    public Goal(TeamSide defender, GoalListener listener) {
        super((TeamSideInfo.of(defender).borderX() + TeamSideInfo.of(defender).goalLineX()) / 2, Field.GROUND_Y - Field.GOAL_HEIGHT / 2,
                Field.GOAL_DEPTH, Field.GOAL_HEIGHT);
        this.defender = defender;
        this.listener = listener;
    }

    /**
     * Goals do not move
     * @param dt seconds elapsed since the last frame
     */
    @Override
    public void update(double dt) {
    }

    /**
     * Bounces the ball on the crossbar and reports a goal if the ball got in
     * @param ball the ball of the match
     */
    @Override
    public void checkCollision(Ball ball) {
        ball.bounceOffRect(getLeft(), Field.crossbarY() - Field.CROSSBAR_THICKNESS, getRight(), Field.crossbarY());
        if (contains(ball)) {
            listener.goalScored(TeamSideInfo.of(defender).opponent());
        }
    }

    /**
     * A goal counts when the ball completely crossed the goal line below the crossbar
     * @param ball the ball of the match
     * @return true if the ball is inside this goal
     */
    public boolean contains(Ball ball) {
        boolean belowCrossbar = ball.getTop() >= Field.crossbarY();
        boolean crossedLine = TeamSideInfo.of(defender).getDirection() * (TeamSideInfo.of(defender).goalLineX() - ball.getX()) >= ball.getRadius();
        return belowCrossbar && crossedLine;
    }

    /**
     * Returns the description of the goal to draw it
     * @return description of the goal to draw it
     */
    @Override
    public RenderState getRenderState() {
        return new RenderState(Figure.GOAL, "goal", x, y, width, height, 0,
                TeamSideInfo.of(defender).getDirection() < 0);
    }

    /**
     * Returns the side of the player who defends this goal
     * @return side of the player who defends this goal
     */
    public TeamSide getDefender() {
        return defender;
    }
}
