package com.doposoccer.domain.model;

import com.doposoccer.domain.enums.Figure;

/**
 * Everything the presentation needs to draw a game object.Each object describes how it
 * looks and the view paints it. It cannot change after it is created
 */
public final class RenderState {

    private final Figure figure;
    private final String spriteKey;
    private final double x;
    private final double y;
    private final double width;
    private final double height;
    private final double angle;
    private final boolean flipped;

    /**
     * Creates the description of a figure.
     *
     * @param figure kind of figure to paint
     * @param spriteKey key of the sprite
     * @param x center x in the logical world
     * @param y center y in the logical world
     * @param width width in the logical world
     * @param height height in the logical world
     * @param angle angle in degrees
     * @param flipped true if it must be drawn mirrored
     */
    public RenderState(Figure figure, String spriteKey, double x, double y, double width, double height,
                       double angle, boolean flipped) {
        this.figure = figure;
        this.spriteKey = spriteKey;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.angle = angle;
        this.flipped = flipped;
    }

    /**
     * Returns the kind of figure to paint
     * @return the figure
     */
    public Figure getFigure() {
        return figure;
    }

    /**
     * Returns the key of the sprite
     * @return the sprite key
     */
    public String getSpriteKey() {
        return spriteKey;
    }

    /**
     * Returns the center x in the logical world
     * @return center x
     */
    public double getX() {
        return x;
    }

    /**
     * Returns the center y in the logical world
     * @return center y
     */
    public double getY() {
        return y;
    }

    /**
     * Returns the width in the logical world
     * @return width
     */
    public double getWidth() {
        return width;
    }

    /**
     * Returns the height in the logical world
     * @return height
     */
    public double getHeight() {
        return height;
    }

    /**
     * Returns the angle in degrees
     * @return angle
     */
    public double getAngle() {
        return angle;
    }

    /**
     * Tells whether the figure must be drawn mirrored
     * @return true if mirrored
     */
    public boolean isFlipped() {
        return flipped;
    }
}
