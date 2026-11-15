package com.doposoccer.domain.enums;

/**
 * State of a player and what it allows: move, jump and kick
 */
public enum PlayerState {
    IDLE("Quieto", true, true, true),
    WALKING("Caminando", true, true, true),
    // In the air: cannot jump again
    JUMPING("Saltando", true, false, true),
    // Cannot jump or start another kick
    KICKING("Pateando", true, false, false),
    // Cannot do anything until the stun ends
    STUNNED("Aturdido", false, false, false);

    private final String name;
    private final boolean move;
    private final boolean jump;
    private final boolean kick;

    PlayerState(String name, boolean move, boolean jump, boolean kick) {
        this.name = name;
        this.move = move;
        this.jump = jump;
        this.kick = kick;
    }

    /**
     * Returns the name of the state
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Tells whether the player can walk in this state
     * @return true if it can walk
     */
    public boolean canMove() {
        return move;
    }

    /**
     * Tells whether the player can start a jump in this state
     * @return true if it can jump
     */
    public boolean canJump() {
        return jump;
    }

    /**
     * Tells whether the player can start a kick in this state
     * @return true if it can kick
     */
    public boolean canKick() {
        return kick;
    }
}
