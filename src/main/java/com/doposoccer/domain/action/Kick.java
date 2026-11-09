package com.doposoccer.domain.action;

import com.doposoccer.domain.model.Player;
/**
 * Kick
 */
public class Kick extends PlayerActionBehavior {
    @Override
    public void apply(Player player) {
        player.kick();
    }
}
