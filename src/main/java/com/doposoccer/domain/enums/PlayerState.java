package com.doposoccer.domain.enums;

/**
 * State of a player. Each state knows which actions it allows,
 * so Player does not need several loose boolean flags.
 */
public enum PlayerState {

    /** Standing still on the ground. */
    IDLE(true, true, true),

    /** Walking on the ground. */
    WALKING(true, true, true),

    /** In the air: can move and kick, but cannot jump again. */
    JUMPING(true, false, true),

    /** Kick animation in progress: can move, cannot jump or kick again. */
    KICKING(true, false, false),

    /** Stunned (life reached zero): cannot do anything. Used from version two. */
    STUNNED(false, false, false);

    /** True if the player can walk in this state. */
    private final boolean canMove;

    /** True if the player can start a jump in this state. */
    private final boolean canJump;

    /** True if the player can start a kick in this state. */
    private final boolean canKick;

    /**
     * Creates the constant with its values.
     *
     * @param canMove true if walking is allowed
     * @param canJump true if jumping is allowed
     * @param canKick true if kicking is allowed
     */
    PlayerState(boolean canMove, boolean canJump, boolean canKick) {
        this.canMove = canMove;
        this.canJump = canJump;
        this.canKick = canKick;
    }

    /**
     * Tells whether the player can walk in this state.
     *
     * @return true if the player can walk in this state
     */
    public boolean canMove() {
        return canMove;
    }

    /**
     * Tells whether the player can start a jump in this state.
     *
     * @return true if the player can start a jump in this state
     */
    public boolean canJump() {
        return canJump;
    }

    /**
     * Tells whether the player can start a kick in this state.
     *
     * @return true if the player can start a kick in this state
     */
    public boolean canKick() {
        return canKick;
    }
}
