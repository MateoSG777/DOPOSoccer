package com.doposoccer.domain.enums;
import com.doposoccer.domain.model.Field;

/**
 * Side of the field where a player starts and whose goal it defends
 * Each side knows its own direction, so the rest of the game asks the side instead of checking which one it is
 */
public enum TeamSide {
    // Player one, attacking to the right
    LEFT("Jugador 1", 1, 0.25) {
        @Override
        public TeamSide opponent() {
            return RIGHT;
        }
    },
    // Player two, attacking to the left
    RIGHT("Jugador 2", -1, 0.75) {
        @Override
        public TeamSide opponent() {
            return LEFT;
        }
    };

    private final String label;
    private final int direction;
    private final double spawnFraction;

    TeamSide(String label, int direction, double spawnFraction) {
        this.label = label;
        this.direction = direction;
        this.spawnFraction = spawnFraction;
    }

    /**
     * Returns the rival side.
     * @return the opponent
     */
    public abstract TeamSide opponent();

    /**
     * Returns the name shown to the users
     * @return the label of the side
     */
    public String getLabel() {
        return label;
    }

    /**
     * Returns the direction in which this side attacks: 1 to the right, -1 to the left
     * @return the attack direction
     */
    public int getDirection() {
        return direction;
    }

    /**
     * Returns the x where the player of this side starts
     * @return starting x
     */
    public double spawnX() {
        return Field.WIDTH * spawnFraction;
    }

    /**
     * Returns the x of the border behind the goal of this side
     * @return x of the border
     */
    public double borderX() {
        return Field.WIDTH * (1 - direction) / 2;
    }

    /**
     * Returns the x of the goal line, one goal depth inside the field
     * @return x of the goal line
     */
    public double goalLineX() {
        return borderX() + direction * Field.GOAL_DEPTH;
    }
}
