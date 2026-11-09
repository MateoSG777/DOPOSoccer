package com.doposoccer.domain.state;

/**
 * Player in the air: can move and kick, but cannot jump again
 */
public class Jumping extends PlayerStateBehavior {

    @Override
    public String getName() {
        return "Saltando";
    }

    @Override
    public boolean canJump() {
        return false;
    }
}
