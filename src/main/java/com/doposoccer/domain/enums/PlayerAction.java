package com.doposoccer.domain.enums;
import com.doposoccer.domain.model.Player;
import java.util.function.Consumer;

/**
 * Action a player can request and the order it gives to the player
 */
public enum PlayerAction {
    MOVE_LEFT(player -> player.move(-1)),
    MOVE_RIGHT(player -> player.move(1)),
    JUMP(Player::jump),
    KICK(Player::kick),
    POWER(Player::activatePower);

    private final Consumer<Player> order;

    PlayerAction(Consumer<Player> order) {
        this.order = order;
    }

    /**
     * Gives the order of this action to a player
     * @param player the player that requested the action
     */
    public void apply(Player player) {
        order.accept(player);
    }
}
