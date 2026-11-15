package com.doposoccer.domain.enums;

/**
 * Size of the window in pixels. The domain always simulates the same field size and
 * the presentation scales the drawing, so the game behaves the same in every resolution
 */
public enum Resolution {
    SMALL(800, 500),
    MEDIUM(1024, 640),
    LARGE(1280, 800);

    private final int width;
    private final int height;

    Resolution(int width, int height) {
        this.width = width;
        this.height = height;
    }

    /**
     * Returns the width in pixels
     * @return width in pixels
     */
    public int getWidth() {
        return width;
    }

    /**
     * Returns the height in pixels
     * @return height in pixels
     */
    public int getHeight() {
        return height;
    }

    /**
     * Returns a text such as "1024 x 640"
     * @return the text shown to the users
     */
    public String getDisplayName() {
        return width + " x " + height;
    }
}
