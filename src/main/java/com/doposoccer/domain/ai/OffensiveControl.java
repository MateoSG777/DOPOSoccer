package com.doposoccer.domain.ai;
import com.doposoccer.domain.model.Ball;
import com.doposoccer.domain.model.Player;

/**
 * Machine whose priority is to score
 */
public class OffensiveControl extends MachineControl {

    /**
     * Always chases the ball, hits it towards the rival goal and uses its power as soon as it is ready
     * @param player the player being driven
     * @param ball the ball of the match
     * @param dt seconds elapsed since the last frame
     */
    @Override
    public void update(Player player, Ball ball, double dt) {
        player.activatePower();
        attack(player, ball);
    }
}
