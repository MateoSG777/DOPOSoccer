package com.doposoccer.domain.action;
import com.doposoccer.domain.model.Player;

/**
 * Jump
 */
public class Jump extends PlayerActionBehavior {
    @Override
    public void apply(Player player) {
        player.jump();
    }
}
