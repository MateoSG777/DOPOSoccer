package com.doposoccer.presentation.input;

import com.doposoccer.domain.control.ActionSource;
import com.doposoccer.domain.enums.PlayerAction;

/**
 * Bridge between the keyboard (presentation) and the KeyboardControl strategy
 * (domain): an action is active while its key is held down.
 */
public class KeyboardActionSource implements ActionSource {

    /** Keys of the player. */
    private final PlayerKeys keys;

    /** Shared set of pressed keys. */
    private final KeyInputHandler input;

    /**
     * Creates the action source of one player.
     *
     * @param keys  keys of the player
     * @param input shared set of pressed keys
     */
    public KeyboardActionSource(PlayerKeys keys, KeyInputHandler input) {
        this.keys = keys;
        this.input = input;
    }

    /**
     * Tells whether the player is holding the key of an action.
     *
     * @param action action to query
     * @return true if its key is pressed
     */
    @Override
    public boolean isActive(PlayerAction action) {
        return input.isPressed(keys.keyFor(action));
    }
}
