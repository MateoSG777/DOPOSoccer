package com.doposoccer.domain.model;

/**
 * Measures of the logical field world, in game units.
 * Physics is always computed at this size; the presentation scales it to the
 * chosen resolution, so the game behaves the same in all of them.
 */
public final class Field {

    /** Width of the logical world. */
    public static final double WIDTH = 1024;

    /** Height of the logical world. */
    public static final double HEIGHT = 640;

    /** y coordinate of the grass line. */
    public static final double GROUND_Y = 540;

    /** Horizontal depth of each goal (from the screen border to the goal line). */
    public static final double GOAL_DEPTH = 60;

    /** Height of the goal mouth (from the ground to the crossbar). */
    public static final double GOAL_HEIGHT = 190;

    /** Thickness of the crossbar. */
    public static final double CROSSBAR_THICKNESS = 10;

    /** Gravity applied to the ball (units per second squared). */
    public static final double BALL_GRAVITY = 600;

    /** Gravity applied to the players (units per second squared). */
    public static final double PLAYER_GRAVITY = 1800;

    /** Height from which ball and players drop after every goal. */
    public static final double KICKOFF_Y = 180;

    /** Utility class: it must not be instantiated. */
    private Field() {
    }

    /**
     * Returns the x coordinate of the center of the field.
     *
     * @return x coordinate of the center of the field
     */
    public static double centerX() {
        return WIDTH / 2;
    }

    /**
     * Returns the y coordinate of the lower edge of the crossbar.
     *
     * @return y coordinate of the lower edge of the crossbar
     */
    public static double crossbarY() {
        return GROUND_Y - GOAL_HEIGHT;
    }
}
