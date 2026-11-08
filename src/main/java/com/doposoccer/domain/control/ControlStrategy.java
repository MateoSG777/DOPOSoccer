package com.doposoccer.domain.control;

import com.doposoccer.domain.model.Ball;
import com.doposoccer.domain.model.Player;

/**
 * Strategy that drives a player: keyboard now, machine in version two.
 */
public interface ControlStrategy {

    /**
     * Decides which actions the player performs in this frame and orders them.
     *
     * @param player the player being driven
     * @param ball   the ball of the match
     * @param dt     seconds elapsed since the last frame
     */
    void update(Player player, Ball ball, double dt);
}
