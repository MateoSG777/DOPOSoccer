package com.doposoccer.presentation.input;
import com.doposoccer.domain.enums.PlayerAction;
import java.awt.event.KeyEvent;

/**
 * Keys of player 2: arrows to jump and move, down arrow to kick, right Control for the power
 */
public class PlayerTwoKeys extends PlayerKeys {

    /** Binds the keys of player 2*/
    public PlayerTwoKeys() {
        bind(standard(KeyEvent.VK_LEFT), PlayerAction.MOVE_LEFT);
        bind(standard(KeyEvent.VK_RIGHT), PlayerAction.MOVE_RIGHT);
        bind(standard(KeyEvent.VK_UP), PlayerAction.JUMP);
        bind(standard(KeyEvent.VK_DOWN), PlayerAction.KICK);
        bind(KeyInputHandler.keyId(KeyEvent.VK_CONTROL, KeyEvent.KEY_LOCATION_RIGHT), PlayerAction.POWER);
    }

    @Override
    public String getHint() {
        return "↑ saltar · ←/→ mover · ↓ patear · Ctrl der. poder";
    }
}
