package com.doposoccer.presentation;

import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests of SpriteCache (Flyweight).
 */
class SpriteCacheTest {

    /** The same key should always return the same image object (loaded only once). */
    @Test
    void shouldReturnTheSameImageEveryTime() {
        SpriteCache cache = new SpriteCache();
        BufferedImage first = cache.get("ball");
        BufferedImage second = cache.get("ball");
        assertTrue(first != null);
        assertTrue(first == second);
    }

    /** A missing image should give null instead of an error. */
    @Test
    void missingImageShouldGiveNull() {
        SpriteCache cache = new SpriteCache();
        assertTrue(cache.get("this_sprite_does_not_exist") == null);
    }

    /** The gray version should keep the size of the original image. */
    @Test
    void grayVersionShouldKeepTheSize() {
        SpriteCache cache = new SpriteCache();
        BufferedImage original = cache.get("btn");
        BufferedImage gray = cache.getGray("btn");
        assertEquals(original.getWidth(), gray.getWidth());
        assertEquals(original.getHeight(), gray.getHeight());
    }
}
