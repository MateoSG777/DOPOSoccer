package com.doposoccer.domain.match;

/**
 * Two humans on the same keyboard
 */
public class PlayerVsPlayer extends GameModeInfo {

    @Override
    public String getDisplayName() {
        return "Jugador vs Jugador";
    }

    @Override
    public boolean isAvailable() {
        return true;
    }
}
