package com.doposoccer.presentation.input;
import com.doposoccer.domain.control.ActionSource;
import com.doposoccer.domain.enums.PlayerAction;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Tells the domain which actions a player requests: an action is active while its key is held down
 */
public class KeyboardActionSource implements ActionSource {

    private final Map<Integer, PlayerAction> bindings;
    private final KeyInputHandler input;

    /**
     * Creates the action source of one player
     * @param keys  keys of the player
     * @param input shared set of pressed keys
     */
    public KeyboardActionSource(PlayerKeys keys, KeyInputHandler input) {
        this.bindings = keys.getBindings();
        this.input = input;
    }

    /**
     * Returns the actions whose key is held down
     * @return the active actions, possibly empty
     */
    @Override
    public List<PlayerAction> getActiveActions() {
        List<PlayerAction> active = new ArrayList<>();
        for (Map.Entry<Integer, PlayerAction> binding : bindings.entrySet()) {
            if (input.isPressed(binding.getKey())) {
                active.add(binding.getValue());
            }
        }
        return active;
    }
}