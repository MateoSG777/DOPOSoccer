package com.doposoccer.domain.enums;

/**
 * Side of the field defended by each player.
 * The left player attacks to the right and the right player attacks to the left.
 */
public enum TeamSide {

    /** Player 1: defends the left goal. */
    LEFT("Jugador 1", 1),

    /** Player 2: defends the right goal. */
    RIGHT("Jugador 2", -1);

    /** Name shown to the users. */
    private final String label;

    /** Horizontal attack direction: +1 to the right, -1 to the left. */
    private final int direction;

    /**
     * Creates the constant with its values.
     *
     * @param label     name shown to the users
     * @param direction horizontal attack direction (+1 or -1)
     */
    TeamSide(String label, int direction) {
        this.label = label;
        this.direction = direction;
    }

    /**
     * Returns the name shown to the users.
     *
     * @return name shown to the users
     */
    public String getLabel() {
        return label;
    }

    /**
     * Returns +1 if this side attacks to the right, -1 if it attacks to the left.
     *
     * @return +1 if this side attacks to the right, -1 if it attacks to the left
     */
    public int getDirection() {
        return direction;
    }

    /**
     * Returns the opposite side.
     *
     * @return the opposite side
     */
    public TeamSide opponent() {
        if (this == LEFT) {
            return RIGHT;
        }
        return LEFT;
    }
}
