package com.doposoccer.presentation.input;
import com.doposoccer.domain.enums.PlayerAction;
import java.awt.event.KeyEvent;

/**
 * Keys of player 1: W jump, A/D move, S kick, Q power
 */
public class PlayerOneKeys extends PlayerKeys {

    /** Binds the keys of player 1. */
    public PlayerOneKeys() {
        bind(standard(KeyEvent.VK_A), PlayerAction.MOVE_LEFT);
        bind(standard(KeyEvent.VK_D), PlayerAction.MOVE_RIGHT);
        bind(standard(KeyEvent.VK_W), PlayerAction.JUMP);
        bind(standard(KeyEvent.VK_S), PlayerAction.KICK);
        bind(standard(KeyEvent.VK_Q), PlayerAction.POWER);
    }

    @Override
    public String getHint() {
        return "W saltar · A/D mover · S patear · Q poder";
    }
}
