package com.doposoccer.domain.control;
import com.doposoccer.domain.action.PlayerActionBehavior;
import com.doposoccer.domain.enums.PlayerAction;
import com.doposoccer.domain.model.Ball;
import com.doposoccer.domain.model.Player;
import java.util.List;

/**
 * Turns the active actions into orders for the player
 */
public class KeyboardControl implements ControlStrategy {
    private final ActionSource source;

    /**
     * Creates a keyboard control
     * @param source where the active actions come from
     */
    public KeyboardControl(ActionSource source) {
        this.source = source;
    }

    /**
     * Orders the player every action that is active
     * @param player the player being driven
     * @param ball the ball of the match
     * @param dt seconds elapsed since the last frame
     */
    @Override
    public void update(Player player, Ball ball, double dt) {
        List<PlayerAction> actions = source.getActiveActions();
        for (int i = 0; i < actions.size(); i++) {
            PlayerActionBehavior.get(actions.get(i)).apply(player);
        }
    }
}
