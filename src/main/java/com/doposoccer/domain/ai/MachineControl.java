package com.doposoccer.domain.ai;
import com.doposoccer.domain.control.ControlStrategy;
import com.doposoccer.domain.model.Ball;
import com.doposoccer.domain.model.Field;
import com.doposoccer.domain.model.Player;

/**
 * Base of the strategies of the machine: the moves that every profile combines in its own way
 */
public abstract class MachineControl implements ControlStrategy {

    // Horizontal distance at which the machine can hit the ball
    protected static final double REACH = 75;

    // Distance the machine keeps behind the ball, so its kick goes to the rival goal
    protected static final double BEHIND = 35;

    // Distance from its own goal line where the machine waits when it defends
    protected static final double HOME_DISTANCE = 110;

    private static final double TOLERANCE = 8;  // closer than this the machine stops walking, so it does not shake

    /**
     * Walks towards a point of the field
     * @param player the machine player
     * @param targetX x where it wants to be
     */
    protected void goTo(Player player, double targetX) {
        if (targetX > player.getX() + TOLERANCE) {
            player.move(1);
        } else if (targetX < player.getX() - TOLERANCE) {
            player.move(-1);
        }
    }

    /**
     * Goes behind the ball and hits it towards the rival goal
     * @param player the machine player
     * @param ball the ball of the match
     */
    protected void attack(Player player, Ball ball) {
        goTo(player, ball.getX() - player.getSide().getDirection() * BEHIND);
        strike(player, ball);
    }

    /**
     * Kicks a low ball in front of the player and jumps to head a high one
     * @param player the machine player
     * @param ball the ball of the match
     */
    protected void strike(Player player, Ball ball) {
        if (distanceAhead(player, ball) > 0 && distanceAhead(player, ball) < REACH && ball.getY() > player.getY()) {
            player.kick();
        }
        if (Math.abs(ball.getX() - player.getX()) < REACH && ball.getBottom() < player.getTop()) {
            player.jump();
        }
    }

    /**
     * Waits near its own goal
     * @param player the machine player
     */
    protected void goHome(Player player) {
        goTo(player, player.getSide().goalLineX() + player.getSide().getDirection() * HOME_DISTANCE);
    }

    /**
     * Returns how far the ball is in front of the player; negative if the ball is behind it
     * @param player the machine player
     * @param ball the ball of the match
     * @return distance towards the rival goal
     */
    protected double distanceAhead(Player player, Ball ball) {
        return (ball.getX() - player.getX()) * player.getSide().getDirection();
    }

    /**
     * Tells whether the ball is in the half of the field that the player defends
     * @param player the machine player
     * @param ball the ball of the match
     * @return true if the ball is in its own half
     */
    protected boolean inOwnHalf(Player player, Ball ball) {
        return (ball.getX() - Field.centerX()) * player.getSide().getDirection() < 0;
    }

    /**
     * Tells whether the ball flies towards the goal that the player defends
     * @param player the machine player
     * @param ball the ball of the match
     * @return true if the ball comes to its own goal
     */
    protected boolean isComing(Player player, Ball ball) {
        return ball.getVx() * player.getSide().getDirection() < 0;
    }
}
