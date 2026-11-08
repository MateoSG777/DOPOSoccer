package com.doposoccer.domain.enums;

/**
 * Kind of figure the presentation must draw for a game object.
 * The view associates one painter with each figure (EnumMap), so it never
 * needs instanceof to know how to draw an object.
 */
public enum Figure {

    /** A player (head, body and foot). */
    PLAYER,

    /** The ball. */
    BALL,

    /** A goal (posts, crossbar and net). */
    GOAL
}
