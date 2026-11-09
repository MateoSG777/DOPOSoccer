package com.doposoccer.domain.state;

/**
 * Player kicking: can move, but cannot jump or start another kick
 */
public class Kicking extends PlayerStateBehavior {

    @Override
    public String getName() {
        return "Pateando";
    }

    @Override
    public boolean canJump() {
        return false;
    }

    @Override
    public boolean canKick() {
        return false;
    }
}
