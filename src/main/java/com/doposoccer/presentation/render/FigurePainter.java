package com.doposoccer.presentation.render;

import com.doposoccer.domain.model.RenderState;

import java.awt.Graphics2D;

/**
 * Knows how to draw one kind of figure. Drawing happens in three layers so
 * that, for example, the ball can be seen inside the net of a goal:
 * back layer, main layer and front layer.
 */
public interface FigurePainter {

    /**
     * Draws what goes behind every figure (for example, the back post of a goal).
     *
     * @param g     graphics in field coordinates
     * @param state what to draw
     */
    default void paintBack(Graphics2D g, RenderState state) {
        // Most figures have nothing behind.
    }

    /**
     * Draws the figure itself.
     *
     * @param g     graphics in field coordinates
     * @param state what to draw
     */
    default void paint(Graphics2D g, RenderState state) {
        // Some figures only have back and front layers.
    }

    /**
     * Draws what goes in front of every figure (for example, the net of a goal).
     *
     * @param g     graphics in field coordinates
     * @param state what to draw
     */
    default void paintFront(Graphics2D g, RenderState state) {
        // Most figures have nothing in front.
    }
}
