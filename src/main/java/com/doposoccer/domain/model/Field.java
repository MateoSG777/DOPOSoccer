package com.doposoccer.domain.model;

/**
 * Measures of the logical field world, in game units
 * Physics is always computed at this size. Also the presentation scales it to the
 * chosen resolution, so the game behaves the same in all of them
 */
public final class Field {

    // Width of the logical world
    public static final double WIDTH = 1024;  // same width as the medium resolution, so it is drawn 1:1 there

    // Height of the logical world
    public static final double HEIGHT = 640;  // keeps the 1.6 ratio of the three resolutions

    // y coordinate of the grass line
    public static final double GROUND_Y = 540;  // grass line of field.png where the players stand

    // Horizontal depth of each goal
    public static final double GOAL_DEPTH = 60;  // width of the net image without its triangular back

    // Height of the goal mouth
    public static final double GOAL_HEIGHT = 190;  // about twice the player height, so a jumping player can defend it

    // Thickness of the crossbar
    public static final double CROSSBAR_THICKNESS = 10;  // with the ball radius, thicker than the longest step of the ball, so it cannot pass through

    // Gravity applied to the ball
    public static final double BALL_GRAVITY = 600;  // lighter than the players, so the ball floats a little and is easier to hit

    // Gravity applied to the players
    public static final double PLAYER_GRAVITY = 1800;  // quick jumps that do not feel floaty

    // Height from which ball and players drop after every goal
    public static final double KICKOFF_Y = 180;  // high enough that everything drops after a goal, below the scoreboard

    private Field() {
    }

    /**
     * Returns the x coordinate of the center of the field
     * @return x coordinate of the center of the field
     */
    public static double centerX() {
        return WIDTH / 2;
    }

    /**
     * Returns the y coordinate of the lower edge of the crossbar
     * @return y coordinate of the lower edge of the crossbar
     */
    public static double crossbarY() {
        return GROUND_Y - GOAL_HEIGHT;
    }
}
