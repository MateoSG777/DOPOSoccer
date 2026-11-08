package com.doposoccer.domain.control;

import com.doposoccer.domain.model.Ball;
import com.doposoccer.domain.model.Player;

/**
 * Null object: a player without control does nothing.
 * It avoids checking for null in every frame.
 */
public class NoControl implements ControlStrategy {

    /** Creates a control that never orders anything. */
    public NoControl() {
        // Nothing to initialize.
    }

    /**
     * Does nothing.
     *
     * @param player the player being driven
     * @param ball   the ball of the match
     * @param dt     seconds elapsed since the last frame
     */
    @Override
    public void update(Player player, Ball ball, double dt) {
        // No actions.
    }
}
