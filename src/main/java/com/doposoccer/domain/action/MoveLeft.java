package com.doposoccer.domain.action;
import com.doposoccer.domain.model.Player;
/**
 * Walk to the left
 */
public class MoveLeft extends PlayerActionBehavior {
    @Override
    public void apply(Player player) {
        player.move(-1);
    }
}
