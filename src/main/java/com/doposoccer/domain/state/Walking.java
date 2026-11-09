package com.doposoccer.domain.state;

/**
 * Player walking: can do everything
 */
public class Walking extends PlayerStateBehavior {

    @Override
    public String getName() {
        return "Caminando";
    }
}
