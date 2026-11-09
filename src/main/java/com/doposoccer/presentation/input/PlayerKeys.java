package com.doposoccer.presentation.input;
import com.doposoccer.domain.enums.PlayerAction;
import com.doposoccer.domain.enums.TeamSide;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Keys of one player: which action each key requests. Each concrete subclass
 * binds its own keys in its constructor
 */
public abstract class PlayerKeys {
    private static final Map<TeamSide, PlayerKeys> BY_SIDE = new HashMap<>();
    static {
        BY_SIDE.put(TeamSide.LEFT, new PlayerOneKeys());
        BY_SIDE.put(TeamSide.RIGHT, new PlayerTwoKeys());
    }

    private final Map<Integer, PlayerAction> bindings = new LinkedHashMap<>();

    /**
     * Finds the keys of the player on a side: player one on the left, player two on the right
     * @param side side of the player
     * @return the keys of that player
     */
    public static PlayerKeys of(TeamSide side) {
        return BY_SIDE.get(side);
    }

    /**
     * Returns the identifier of a key in its standard location of the keyboard
     * @param keyCode key code of java.awt.event.KeyEvent
     * @return the key identifier used by KeyInputHandler
     */
    protected static int standard(int keyCode) {
        return KeyInputHandler.keyId(keyCode, KeyEvent.KEY_LOCATION_STANDARD);
    }

    /**
     * Binds a key to an action.
     * @param keyId  key identifier, as given by KeyInputHandler.keyId
     * @param action action requested while the key is held down
     */
    protected void bind(int keyId, PlayerAction action) {
        bindings.put(keyId, action);
    }

    /**
     * Returns the key identifiers bound to actions, in the order they were bound.
     *
     * @return a new map from key identifier to action
     */
    public Map<Integer, PlayerAction> getBindings() {
        return new LinkedHashMap<>(bindings);
    }

    /**
     * Returns the short explanation of the keys shown to the users.
     * @return the hint
     */
    public abstract String getHint();
}
