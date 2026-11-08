package com.doposoccer.domain.enums;

/**
 * Available field resolutions. The domain always simulates in a fixed logical
 * world (Field.WIDTH x Field.HEIGHT); the presentation scales the drawing to
 * the chosen resolution, so the game behaves the same in all of them.
 */
public enum Resolution {

    /** 800 x 500 pixels. */
    SMALL(800, 500),

    /** 1024 x 640 pixels (same size as the logical world). */
    MEDIUM(1024, 640),

    /** 1280 x 800 pixels. */
    LARGE(1280, 800);

    /** Width in pixels. */
    private final int width;

    /** Height in pixels. */
    private final int height;

    /**
     * Creates the constant with its values.
     *
     * @param width  width in pixels
     * @param height height in pixels
     */
    Resolution(int width, int height) {
        this.width = width;
        this.height = height;
    }

    /**
     * Returns the width in pixels.
     *
     * @return width in pixels
     */
    public int getWidth() {
        return width;
    }

    /**
     * Returns the height in pixels.
     *
     * @return height in pixels
     */
    public int getHeight() {
        return height;
    }

    /**
     * Returns the text such as "1024 x 640", used by Swing combo boxes.
     *
     * @return text such as "1024 x 640", used by Swing combo boxes
     */
    @Override
    public String toString() {
        return width + " x " + height;
    }
}
