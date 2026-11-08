package com.doposoccer.domain.control;

import com.doposoccer.domain.enums.PlayerAction;

/**
 * Source of actions of a human player. The presentation implements it by
 * reading the keyboard, so the domain knows nothing about keys or java.awt.
 */
public interface ActionSource {

    /**
     * Tells whether the player is requesting an action.
     *
     * @param action action to query
     * @return true if the player is requesting that action right now
     */
    boolean isActive(PlayerAction action);
}
