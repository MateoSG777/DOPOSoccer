package com.doposoccer.domain.ai;
import com.doposoccer.domain.model.Ball;
import com.doposoccer.domain.model.Player;

/**
 * Machine that chooses its action from the state of the match
 */
public class ExpertControl extends MachineControl {

    private static final double POWER_RANGE = 160;  // the power is only spent when the ball is close enough to be the next touch

    /**
     * Clears the ball from its own half, pushes forward while the ball goes away and
     * falls back to its goal when the ball comes from the rival half
     * It keeps its power until the ball is close and in front
     * @param player the player being driven
     * @param ball the ball of the match
     * @param dt seconds elapsed since the last frame
     */
    @Override
    public void update(Player player, Ball ball, double dt) {
        if (inOwnHalf(player, ball) || !isComing(player, ball)) {
            attack(player, ball);
        } else {
            goHome(player);
            strike(player, ball);
        }
        if (distanceAhead(player, ball) > 0 && distanceAhead(player, ball) < POWER_RANGE) {
            player.activatePower();
        }
    }
}
