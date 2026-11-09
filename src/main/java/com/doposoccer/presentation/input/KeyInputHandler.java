package com.doposoccer.presentation.input;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Keeps the set of keys that are pressed right now, so both players can press
 * several keys at the same time without blocking each other
 * Swing writes the set and the game loop reads it from another thread, so it is synchronized
 * Each key is identified by its code and its location, because the right
 * Control key has the same code as the left one and only differs in location
 */
public class KeyInputHandler extends KeyAdapter {

    /** Number of possible key locations*/
    public static final int LOCATION_SLOTS = 10;

    private final Set<Integer> pressed = Collections.synchronizedSet(new HashSet<Integer>());

    /**
     * Builds the identifier of a key from its code and location
     * @param keyCode  KeyEvent.VK_* code of the key
     * @param location KeyEvent.KEY_LOCATION_* of the key
     * @return a number that identifies that physical key
     */
    public static int keyId(int keyCode, int location) {
        return keyCode * LOCATION_SLOTS + location;
    }

    /**
     * Tells whether a key is being held down
     * @param keyId identifier built with keyId
     * @return true if that key is pressed
     */
    public boolean isPressed(int keyId) {
        return pressed.contains(keyId);
    }

    /**
     * Registers a key that went down
     * @param e key event
     */
    @Override
    public void keyPressed(KeyEvent e) {
        pressed.add(keyId(e.getKeyCode(), e.getKeyLocation()));
    }

    /**
     * Forgets a key that went up
     * @param e key event
     */
    @Override
    public void keyReleased(KeyEvent e) {
        pressed.remove(keyId(e.getKeyCode(), e.getKeyLocation()));
    }

    /** Releases every key, for example when the window loses the focus, so no player keeps walking alone*/
    public void releaseAll() {
        pressed.clear();
    }
}
