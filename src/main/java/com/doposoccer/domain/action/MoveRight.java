package com.doposoccer.domain.action;
import com.doposoccer.domain.model.Player;
/**
 * Walk to the right
 */
public class MoveRight extends PlayerActionBehavior {
    @Override
    public void apply(Player player) {
        player.move(1);
    }
}
