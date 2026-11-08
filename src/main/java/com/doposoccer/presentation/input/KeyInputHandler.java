package com.doposoccer.presentation.input;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Keeps the set of keys that are pressed right now, so both players can press
 * several keys at the same time without blocking each other.
 * Swing writes the set (event thread) and the game loop reads it (its own
 * thread), so the HashSet is wrapped in a synchronized set.
 * Each key is identified by its code and its location, because the right
 * Control key has the same code as the left one and only differs in location.
 */
public class KeyInputHandler implements KeyListener, FocusListener {

    /** Number of possible key locations (KeyEvent.KEY_LOCATION_* go from 0 to 4). */
    public static final int LOCATION_SLOTS = 10;

    /** Identifiers of the keys pressed right now (O(1) add, remove and search). */
    private final Set<Integer> pressed = Collections.synchronizedSet(new HashSet<Integer>());

    /** Creates a handler with no key pressed. */
    public KeyInputHandler() {
        // Nothing is pressed at the beginning.
    }

    /**
     * Builds the identifier of a key from its code and location.
     *
     * @param keyCode  KeyEvent.VK_* code of the key
     * @param location KeyEvent.KEY_LOCATION_* of the key
     * @return a number that identifies that physical key
     */
    public static int keyId(int keyCode, int location) {
        return keyCode * LOCATION_SLOTS + location;
    }

    /**
     * Tells whether a key is being held down.
     *
     * @param keyId identifier built with keyId
     * @return true if that key is pressed
     */
    public boolean isPressed(int keyId) {
        return pressed.contains(keyId);
    }

    /**
     * Registers a key that went down.
     *
     * @param e key event
     */
    @Override
    public void keyPressed(KeyEvent e) {
        pressed.add(keyId(e.getKeyCode(), e.getKeyLocation()));
    }

    /**
     * Forgets a key that went up.
     *
     * @param e key event
     */
    @Override
    public void keyReleased(KeyEvent e) {
        pressed.remove(keyId(e.getKeyCode(), e.getKeyLocation()));
    }

    /**
     * Typed characters are not used.
     *
     * @param e key event
     */
    @Override
    public void keyTyped(KeyEvent e) {
        // Not needed: the game reads pressed keys, not characters.
    }

    /**
     * Nothing to do when the game gets the focus.
     *
     * @param e focus event
     */
    @Override
    public void focusGained(FocusEvent e) {
        // Keys are registered as they are pressed.
    }

    /**
     * Releases every key when the window loses the focus, so no player keeps walking alone.
     *
     * @param e focus event
     */
    @Override
    public void focusLost(FocusEvent e) {
        pressed.clear();
    }
}
