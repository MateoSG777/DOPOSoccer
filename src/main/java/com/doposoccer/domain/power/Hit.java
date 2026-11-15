package com.doposoccer.domain.power;
import com.doposoccer.domain.model.Player;

/**
 * A hit to the rival, by a kick or by a power shot: who hit, who received it and the life it takes
 * It travels through the chain of impact
 */
public class Hit {

    private final Player attacker;
    private final Player rival;
    private final double damage;

    /**
     * Creates a hit
     * @param attacker the player that kicked or launched the power shot
     * @param rival the player that received it
     * @param damage life it takes, already multiplied by the kick of the attacker
     */
    public Hit(Player attacker, Player rival, double damage) {
        this.attacker = attacker;
        this.rival = rival;
        this.damage = damage;
    }

    /**
     * Returns the life this hit takes
     * @return the damage
     */
    public double getDamage() {
        return damage;
    }

    /**
     * Returns the direction of the hit: 1 to the right, -1 to the left
     * @return the direction
     */
    public int getDirection() {
        return attacker.getSide().getDirection();
    }

    /**
     * Returns the player that hit
     * @return the attacker
     */
    public Player getAttacker() {
        return attacker;
    }

    /**
     * Returns the player that received the hit
     * @return the rival
     */
    public Player getRival() {
        return rival;
    }
}
