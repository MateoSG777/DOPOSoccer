package com.doposoccer.domain.model;

/**
 * Pair of coordinates that cannot change, used for directions such as the
 * normal of a collision
 */
public final class Vector2D {

    private final double x;
    private final double y;

    /**
     * Creates a vector
     * @param x horizontal component
     * @param y vertical component
     */
    public Vector2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Returns the horizontal component
     * @return the x component
     */
    public double getX() {
        return x;
    }

    /**
     * Returns the vertical component
     * @return the y component
     */
    public double getY() {
        return y;
    }
}
