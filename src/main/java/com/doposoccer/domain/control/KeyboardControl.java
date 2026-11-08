package com.doposoccer.domain.control;

import com.doposoccer.domain.enums.PlayerAction;
import com.doposoccer.domain.model.Ball;
import com.doposoccer.domain.model.Player;

/**
 * Human control: turns the active actions into orders for the player.
 * Several actions can be active at the same time (for example jump and kick).
 * The POWER action will be handled when power shots exist (version two).
 */
public class KeyboardControl implements ControlStrategy {

    /** Where the active actions come from (the keyboard, through the presentation). */
    private final ActionSource source;

    /**
     * Creates a keyboard control.
     *
     * @param source where the active actions come from
     */
    public KeyboardControl(ActionSource source) {
        this.source = source;
    }

    /**
     * Orders the player every action that is active.
     *
     * @param player the player being driven
     * @param ball   the ball of the match (not used by a human control)
     * @param dt     seconds elapsed since the last frame
     */
    @Override
    public void update(Player player, Ball ball, double dt) {
        if (source.isActive(PlayerAction.MOVE_LEFT)) {
            player.moveLeft();
        }
        if (source.isActive(PlayerAction.MOVE_RIGHT)) {
            player.moveRight();
        }
        if (source.isActive(PlayerAction.JUMP)) {
            player.jump();
        }
        if (source.isActive(PlayerAction.KICK)) {
            player.kick();
        }
    }
}
