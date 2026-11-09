package com.doposoccer.domain.action;
import com.doposoccer.domain.enums.PlayerAction;
import com.doposoccer.domain.model.Player;
import java.util.EnumMap;
import java.util.Map;

/**
 * Order that a player action gives to the player. The instance of each action is found with {@link #get}.
 */
public abstract class  PlayerActionBehavior {
    private static final Map<PlayerAction, PlayerActionBehavior> BY_ACTION = new EnumMap<>(PlayerAction.class);
    static {
        BY_ACTION.put(PlayerAction.MOVE_LEFT, new MoveLeft());
        BY_ACTION.put(PlayerAction.MOVE_RIGHT, new MoveRight());
        BY_ACTION.put(PlayerAction.JUMP, new Jump());
        BY_ACTION.put(PlayerAction.KICK, new Kick());
        BY_ACTION.put(PlayerAction.POWER, new Power());
    }

    /**
     * Returns the behavior of an action
     * @param action the action made
     * @return its behavior
     */
    public static PlayerActionBehavior get(PlayerAction action) {
        return BY_ACTION.get(action);
    }

    /**
     * Gives the order of this action to a player.
     * @param player the player that requested the action
     */
    public abstract void apply(Player player);
}
