package com.doposoccer.domain.state;
import com.doposoccer.domain.enums.PlayerState;
import java.util.EnumMap;
import java.util.Map;

/**
 * What a player can do in each state. By default a player can move, jump and kick; each
 * concrete state overrides only what it forbids. The instance of each state is found with {@link #get}
 */
public abstract class PlayerStateBehavior {

    private static final Map<PlayerState, PlayerStateBehavior> BY_STATE = new EnumMap<>(PlayerState.class);

    static {
        BY_STATE.put(PlayerState.IDLE, new Idle());
        BY_STATE.put(PlayerState.WALKING, new Walking());
        BY_STATE.put(PlayerState.JUMPING, new Jumping());
        BY_STATE.put(PlayerState.KICKING, new Kicking());
        BY_STATE.put(PlayerState.STUNNED, new Stunned());
    }

    /**
     * Returns the behavior of a state
     * @param state the state
     * @return its behavior
     */
    public static PlayerStateBehavior of(PlayerState state) {
        return BY_STATE.get(state);
    }

    /**
     * Returns the name of the state
     * @return the name
     */
    public abstract String getName();

    /**
     * Tells whether the player can walk in this state
     * @return true by default
     */
    public boolean canMove() {
        return true;
    }

    /**
     * Tells whether the player can start a jump in this state
     * @return true by default
     */
    public boolean canJump() {
        return true;
    }

    /**
     * Tells whether the player can start a kick in this state
     * @return true by default
     */
    public boolean canKick() {
        return true;
    }
}
