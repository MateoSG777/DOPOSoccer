package com.doposoccer.domain.model;

/**
 * Object on the field. Every collision revolves around the ball: each object
 * knows how to resolve its own collision with it (checkCollision), so the
 * match iterates the list of objects without asking their type (zero instanceof).
 */
public abstract class GameObject {

    /** x coordinate of the center. */
    protected double x;

    /** y coordinate of the center. */
    protected double y;

    /** Width of the bounding box. */
    protected double width;

    /** Height of the bounding box. */
    protected double height;

    /**
     * Creates an object on the field.
     *
     * @param x      x coordinate of the center
     * @param y      y coordinate of the center
     * @param width  width of the bounding box
     * @param height height of the bounding box
     */
    protected GameObject(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    /**
     * Advances the physics of the object.
     *
     * @param dt seconds elapsed since the last frame
     */
    public abstract void update(double dt);

    /**
     * Resolves the collision of this object with the ball.
     *
     * @param ball the ball of the match
     */
    public abstract void checkCollision(Ball ball);

    /**
     * Returns the description of how this object must be drawn.
     *
     * @return description of how this object must be drawn
     */
    public abstract RenderState getRenderState();

    /**
     * Returns the x coordinate of the center.
     *
     * @return x coordinate of the center
     */
    public double getX() {
        return x;
    }

    /**
     * Returns the y coordinate of the center.
     *
     * @return y coordinate of the center
     */
    public double getY() {
        return y;
    }

    /**
     * Returns the width of the bounding box.
     *
     * @return width of the bounding box
     */
    public double getWidth() {
        return width;
    }

    /**
     * Returns the height of the bounding box.
     *
     * @return height of the bounding box
     */
    public double getHeight() {
        return height;
    }

    /**
     * Returns the x coordinate of the left edge.
     *
     * @return x coordinate of the left edge
     */
    public double getLeft() {
        return x - width / 2;
    }

    /**
     * Returns the x coordinate of the right edge.
     *
     * @return x coordinate of the right edge
     */
    public double getRight() {
        return x + width / 2;
    }

    /**
     * Returns the y coordinate of the top edge.
     *
     * @return y coordinate of the top edge
     */
    public double getTop() {
        return y - height / 2;
    }

    /**
     * Returns the y coordinate of the bottom edge.
     *
     * @return y coordinate of the bottom edge
     */
    public double getBottom() {
        return y + height / 2;
    }
}
