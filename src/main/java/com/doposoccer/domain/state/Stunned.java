package com.doposoccer.domain.state;

/**
 * Stunned player: cannot do anything until the stun ends
 */
public class Stunned extends PlayerStateBehavior {

    @Override
    public String getName() {
        return "Aturdido";
    }

    @Override
    public boolean canMove() {
        return false;
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
