package com.doposoccer.domain.control;
import com.doposoccer.domain.model.Ball;
import com.doposoccer.domain.model.Player;

/**
 * Control of a player that does nothing
 */
public class NoControl implements ControlStrategy {

    /**
     * Does nothing. Is importatne to set a default state, otherwise will throw a NullPointerException
     *
     * @param player the player being driven
     * @param ball the ball of the match
     * @param dt seconds elapsed since the last frame
     */
    @Override
    public void update(Player player, Ball ball, double dt) {
    }
}
