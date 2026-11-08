package com.doposoccer.domain.model;

import com.doposoccer.domain.enums.Figure;

/**
 * Everything the presentation needs to draw a game object. Thanks to this
 * record the domain does not depend on java.awt: each object describes how it
 * looks and the view paints it.
 *
 * @param figure    kind of figure to paint
 * @param spriteKey key of the sprite (it may be missing: the view then draws a default shape)
 * @param label     associated text (for example, the country name)
 * @param x         center x in the logical world
 * @param y         center y in the logical world
 * @param width     width in the logical world
 * @param height    height in the logical world
 * @param angle     angle in degrees (ball rotation or foot angle while kicking)
 * @param flipped   true if it must be drawn mirrored (facing left)
 * @param color     color in 0xRRGGBB format
 */
public record RenderState(Figure figure, String spriteKey, String label,
                          double x, double y, double width, double height,
                          double angle, boolean flipped, int color) {
}
