package com.doposoccer.domain.control;
import com.doposoccer.domain.enums.PlayerAction;
import java.util.List;

/**
 * Source of actions of a human player. The presentation implements it by
 * reading the keyboard
 */
public interface ActionSource {

    /**
     * Returns the actions the player is requesting at the moment
     * @return the active actions, possibly empty
     */
    List<PlayerAction> getActiveActions();
}
