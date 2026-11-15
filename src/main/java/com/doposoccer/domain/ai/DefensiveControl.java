package com.doposoccer.domain.ai;
import com.doposoccer.domain.model.Ball;
import com.doposoccer.domain.model.Player;

/**
 * Machine whose priority is to defend its goal
 */
public class DefensiveControl extends MachineControl {

    /**
     * Waits near its own goal and only goes for the ball to clear it from its own half
     * @param player the player being driven
     * @param ball the ball of the match
     * @param dt seconds elapsed since the last frame
     */
    @Override
    public void update(Player player, Ball ball, double dt) {
        if (inOwnHalf(player, ball)) {
            attack(player, ball);
        } else {
            goHome(player);
        }
    }
}
