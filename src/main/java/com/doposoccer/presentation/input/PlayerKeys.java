package com.doposoccer.presentation.input;

import com.doposoccer.domain.enums.PlayerAction;
import com.doposoccer.domain.enums.TeamSide;

import java.awt.event.KeyEvent;
import java.util.EnumMap;
import java.util.Map;

/**
 * Keys of each player.
 * Player 1: W jump, A/D move, S kick, Q power.
 * Player 2: arrows to jump and move, down arrow to kick, right Control for the power.
 */
public enum PlayerKeys {

    /** Keys of player 1 (left side). */
    PLAYER_ONE(TeamSide.LEFT,
            standard(KeyEvent.VK_A), standard(KeyEvent.VK_D), standard(KeyEvent.VK_W),
            standard(KeyEvent.VK_S), standard(KeyEvent.VK_Q),
            "W saltar · A/D mover · S patear · Q poder"),

    /** Keys of player 2 (right side). */
    PLAYER_TWO(TeamSide.RIGHT,
            standard(KeyEvent.VK_LEFT), standard(KeyEvent.VK_RIGHT), standard(KeyEvent.VK_UP),
            standard(KeyEvent.VK_DOWN), KeyInputHandler.keyId(KeyEvent.VK_CONTROL, KeyEvent.KEY_LOCATION_RIGHT),
            "↑ saltar · ←/→ mover · ↓ patear · Ctrl der. poder");

    /** Side of the player that uses these keys. */
    private final TeamSide side;

    /** Key identifier of each action. */
    private final Map<PlayerAction, Integer> keys = new EnumMap<>(PlayerAction.class);

    /** Short text that explains the keys to the users. */
    private final String hint;

    /**
     * Creates the constant with its keys.
     *
     * @param side  side of the player
     * @param left  key to move left
     * @param right key to move right
     * @param jump  key to jump
     * @param kick  key to kick
     * @param power key to activate the power
     * @param hint  short explanation of the keys
     */
    PlayerKeys(TeamSide side, int left, int right, int jump, int kick, int power, String hint) {
        this.side = side;
        this.hint = hint;
        keys.put(PlayerAction.MOVE_LEFT, left);
        keys.put(PlayerAction.MOVE_RIGHT, right);
        keys.put(PlayerAction.JUMP, jump);
        keys.put(PlayerAction.KICK, kick);
        keys.put(PlayerAction.POWER, power);
    }

    /**
     * Builds the identifier of a key that has a single location on the keyboard.
     *
     * @param keyCode KeyEvent.VK_* code
     * @return the key identifier
     */
    private static int standard(int keyCode) {
        return KeyInputHandler.keyId(keyCode, KeyEvent.KEY_LOCATION_STANDARD);
    }

    /**
     * Returns the key identifier of an action.
     *
     * @param action action to query
     * @return the key identifier bound to that action
     */
    public int keyFor(PlayerAction action) {
        return keys.get(action);
    }

    /**
     * Returns the short explanation of the keys.
     *
     * @return the hint shown to the users
     */
    public String getHint() {
        return hint;
    }

    /**
     * Returns the side of the player that uses these keys.
     *
     * @return the side of the player
     */
    public TeamSide getSide() {
        return side;
    }

    /**
     * Finds the keys of the player on a side.
     *
     * @param side side of the player
     * @return the keys of that player
     */
    public static PlayerKeys of(TeamSide side) {
        PlayerKeys[] all = values();
        for (int i = 0; i < all.length; i++) {
            if (all[i].side == side) {
                return all[i];
            }
        }
        return PLAYER_ONE;
    }
}
