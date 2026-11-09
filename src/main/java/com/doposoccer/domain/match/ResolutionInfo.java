package com.doposoccer.domain.match;

import com.doposoccer.domain.enums.Resolution;

import java.util.EnumMap;
import java.util.Map;

/**
 * Size in pixels of a window resolution. The domain always simulates the same field size and
 * the presentation scales the drawing, so the game behaves the same in every resolution
 * The instance of each resolution is found with {@link #get}.
 */
public class ResolutionInfo {
    private static final Map<Resolution, ResolutionInfo> BY_RESOLUTION = new EnumMap<>(Resolution.class);
    static {
        BY_RESOLUTION.put(Resolution.SMALL, new ResolutionInfo(800, 500));
        BY_RESOLUTION.put(Resolution.MEDIUM, new ResolutionInfo(1024, 640));
        BY_RESOLUTION.put(Resolution.LARGE, new ResolutionInfo(1280, 800));
    }

    private final int width;
    private final int height;

    private ResolutionInfo(int width, int height) {
        this.width = width;
        this.height = height;
    }

    /**
     * Returns the size of a resolution
     * @param resolution the resolution
     * @return its size
     */
    public static ResolutionInfo get(Resolution resolution) {
        return BY_RESOLUTION.get(resolution);
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
