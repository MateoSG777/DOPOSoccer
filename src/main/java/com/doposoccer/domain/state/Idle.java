package com.doposoccer.domain.state;

/**
 * Player standing still: can do everything
 */
public class Idle extends PlayerStateBehavior {

    @Override
    public String getName() {
        return "Quieto";
    }
}
