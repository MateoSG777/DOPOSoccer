package com.doposoccer.domain.match;

/**
 * not available at this moment
 */
public class PlayerVsMachine extends GameModeInfo {

    @Override
    public String getDisplayName() {
        return "Jugador vs Máquina";
    }

    @Override
    public boolean isAvailable() {
        return false;
    }
}
