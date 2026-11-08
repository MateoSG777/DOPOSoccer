package com.doposoccer.domain.enums;

/**
 * Actions a player can request, independent of the input device.
 */
public enum PlayerAction {

    /** Walk to the left. */
    MOVE_LEFT,

    /** Walk to the right. */
    MOVE_RIGHT,

    /** Jump. */
    JUMP,

    /** Kick. */
    KICK,

    /** Activate the power shot (used from version two). */
    POWER
}
