package com.doposoccer.presentation.input;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.swing.JPanel;
import java.awt.event.KeyEvent;

/**
 * Unit tests of KeyInputHandler, PlayerKeys and KeyboardActionSource.
 */
class KeyInputHandlerTest {

    private JPanel source;
    private KeyInputHandler input;

    /** Creates fresh objects before each test. */
    @BeforeEach
    void setUp() {
        source = new JPanel();
        input = new KeyInputHandler();
    }

    private int standard(int keyCode) {
        return KeyInputHandler.keyId(keyCode, KeyEvent.KEY_LOCATION_STANDARD);
    }

    private KeyEvent key(int id, int keyCode, int location) {
        return new KeyEvent(source, id, 0, 0, keyCode, KeyEvent.CHAR_UNDEFINED, location);
    }

    /** Should remember several keys pressed at the same time. */
    @Test
    void shouldRememberSeveralKeysAtTheSameTime() {
        input.keyPressed(key(KeyEvent.KEY_PRESSED, KeyEvent.VK_A, KeyEvent.KEY_LOCATION_STANDARD));
        input.keyPressed(key(KeyEvent.KEY_PRESSED, KeyEvent.VK_W, KeyEvent.KEY_LOCATION_STANDARD));
        input.keyPressed(key(KeyEvent.KEY_PRESSED, KeyEvent.VK_UP, KeyEvent.KEY_LOCATION_STANDARD));
        assertTrue(input.isPressed(standard(KeyEvent.VK_A)));
        assertTrue(input.isPressed(standard(KeyEvent.VK_W)));
        assertTrue(input.isPressed(standard(KeyEvent.VK_UP)));
    }

    /** Should forget a key when it is released. */
    @Test
    void shouldForgetReleasedKeys() {
        input.keyPressed(key(KeyEvent.KEY_PRESSED, KeyEvent.VK_S, KeyEvent.KEY_LOCATION_STANDARD));
        input.keyReleased(key(KeyEvent.KEY_RELEASED, KeyEvent.VK_S, KeyEvent.KEY_LOCATION_STANDARD));
        assertFalse(input.isPressed(standard(KeyEvent.VK_S)));
    }

    /** Only the right control key should activate the power of player 2. */
    @Test
    void onlyRightControlShouldActivateThePowerOfPlayerTwo() {
        KeyboardActionSource playerTwo = new KeyboardActionSource(new PlayerTwoKeys(), input);
        input.keyPressed(key(KeyEvent.KEY_PRESSED, KeyEvent.VK_CONTROL, KeyEvent.KEY_LOCATION_LEFT));
        assertTrue(playerTwo.getActiveActions().isEmpty());
        input.keyPressed(key(KeyEvent.KEY_PRESSED, KeyEvent.VK_CONTROL, KeyEvent.KEY_LOCATION_RIGHT));
        assertEquals(1, playerTwo.getActiveActions().size());
    }

    /** Keys of one player should not move the other player. */
    @Test
    void keysOfOnePlayerShouldNotMoveTheOther() {
        KeyboardActionSource playerOne = new KeyboardActionSource(new PlayerOneKeys(), input);
        input.keyPressed(key(KeyEvent.KEY_PRESSED, KeyEvent.VK_LEFT, KeyEvent.KEY_LOCATION_STANDARD));
        assertTrue(playerOne.getActiveActions().isEmpty());
    }

    /** Should release every key when the window loses the focus. */
    @Test
    void shouldReleaseEveryKeyWhenTheFocusIsLost() {
        input.keyPressed(key(KeyEvent.KEY_PRESSED, KeyEvent.VK_D, KeyEvent.KEY_LOCATION_STANDARD));
        input.releaseAll();
        assertFalse(input.isPressed(standard(KeyEvent.VK_D)));
    }
}
