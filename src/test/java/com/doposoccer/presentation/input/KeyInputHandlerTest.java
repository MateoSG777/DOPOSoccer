package com.doposoccer.presentation.input;

import com.doposoccer.domain.enums.PlayerAction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.JPanel;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests of KeyInputHandler, PlayerKeys and KeyboardActionSource.
 */
class KeyInputHandlerTest {

    /** Component used as the source of the fake key events. */
    private JPanel source;

    /** Handler under test. */
    private KeyInputHandler input;

    /** Creates fresh objects before each test. */
    @BeforeEach
    void setUp() {
        source = new JPanel();
        input = new KeyInputHandler();
    }

    /**
     * Builds a fake key event.
     *
     * @param id       KeyEvent.KEY_PRESSED or KeyEvent.KEY_RELEASED
     * @param keyCode  KeyEvent.VK_* code
     * @param location KeyEvent.KEY_LOCATION_* location
     * @return the event
     */
    private KeyEvent key(int id, int keyCode, int location) {
        return new KeyEvent(source, id, 0, 0, keyCode, KeyEvent.CHAR_UNDEFINED, location);
    }

    /** Should remember several keys pressed at the same time. */
    @Test
    void shouldRememberSeveralKeysAtTheSameTime() {
        input.keyPressed(key(KeyEvent.KEY_PRESSED, KeyEvent.VK_A, KeyEvent.KEY_LOCATION_STANDARD));
        input.keyPressed(key(KeyEvent.KEY_PRESSED, KeyEvent.VK_W, KeyEvent.KEY_LOCATION_STANDARD));
        input.keyPressed(key(KeyEvent.KEY_PRESSED, KeyEvent.VK_UP, KeyEvent.KEY_LOCATION_STANDARD));
        assertTrue(input.isPressed(PlayerKeys.PLAYER_ONE.keyFor(PlayerAction.MOVE_LEFT)));
        assertTrue(input.isPressed(PlayerKeys.PLAYER_ONE.keyFor(PlayerAction.JUMP)));
        assertTrue(input.isPressed(PlayerKeys.PLAYER_TWO.keyFor(PlayerAction.JUMP)));
    }

    /** Should forget a key when it is released. */
    @Test
    void shouldForgetReleasedKeys() {
        input.keyPressed(key(KeyEvent.KEY_PRESSED, KeyEvent.VK_S, KeyEvent.KEY_LOCATION_STANDARD));
        input.keyReleased(key(KeyEvent.KEY_RELEASED, KeyEvent.VK_S, KeyEvent.KEY_LOCATION_STANDARD));
        assertFalse(input.isPressed(PlayerKeys.PLAYER_ONE.keyFor(PlayerAction.KICK)));
    }

    /** Only the right control key should activate the power of player 2. */
    @Test
    void onlyRightControlShouldActivateThePowerOfPlayerTwo() {
        KeyboardActionSource playerTwo = new KeyboardActionSource(PlayerKeys.PLAYER_TWO, input);
        input.keyPressed(key(KeyEvent.KEY_PRESSED, KeyEvent.VK_CONTROL, KeyEvent.KEY_LOCATION_LEFT));
        assertFalse(playerTwo.isActive(PlayerAction.POWER));
        input.keyPressed(key(KeyEvent.KEY_PRESSED, KeyEvent.VK_CONTROL, KeyEvent.KEY_LOCATION_RIGHT));
        assertTrue(playerTwo.isActive(PlayerAction.POWER));
    }

    /** Keys of one player should not move the other player. */
    @Test
    void keysOfOnePlayerShouldNotMoveTheOther() {
        KeyboardActionSource playerOne = new KeyboardActionSource(PlayerKeys.PLAYER_ONE, input);
        input.keyPressed(key(KeyEvent.KEY_PRESSED, KeyEvent.VK_LEFT, KeyEvent.KEY_LOCATION_STANDARD));
        assertFalse(playerOne.isActive(PlayerAction.MOVE_LEFT));
    }

    /** Should release every key when the window loses the focus. */
    @Test
    void shouldReleaseEveryKeyWhenTheFocusIsLost() {
        input.keyPressed(key(KeyEvent.KEY_PRESSED, KeyEvent.VK_D, KeyEvent.KEY_LOCATION_STANDARD));
        input.focusLost(new FocusEvent(source, FocusEvent.FOCUS_LOST));
        assertFalse(input.isPressed(PlayerKeys.PLAYER_ONE.keyFor(PlayerAction.MOVE_RIGHT)));
    }
}
