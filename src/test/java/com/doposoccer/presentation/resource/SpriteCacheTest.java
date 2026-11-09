package com.doposoccer.presentation.resource;

import com.doposoccer.domain.country.CountryInfo;
import com.doposoccer.domain.enums.Country;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.image.BufferedImage;

/**
 * Unit tests of SpriteCache.
 */
class SpriteCacheTest {

    /** The same key should always return the same image object. */
    @Test
    void shouldReturnTheSameImageEveryTime() {
        SpriteCache cache = new SpriteCache();
        BufferedImage first = cache.get(SpritePath.match("ball"));
        BufferedImage second = cache.get(SpritePath.match("ball"));
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
        BufferedImage original = cache.get(SpritePath.button("btn"));
        BufferedImage gray = cache.getGray(SpritePath.button("btn"));
        assertEquals(original.getWidth(), gray.getWidth());
        assertEquals(original.getHeight(), gray.getHeight());
    }

    /** Every sprite that the game draws should exist in its folder. */
    @Test
    void everySpriteOfTheGameShouldExist() {
        SpriteCache cache = new SpriteCache();
        String[] keys = {
            SpritePath.background("menu"), SpritePath.background("menu_vs"), SpritePath.background("bg_store"),
            SpritePath.background("field"), SpritePath.background("top_field"), SpritePath.background("people"),
            SpritePath.button("btn"), SpritePath.button("btn_pressed"),
            SpritePath.character("body_2"), SpritePath.character("foot"),
            SpritePath.match("ball"), SpritePath.match("goalpost1"), SpritePath.match("goalpost2"),
            SpritePath.ui("logo"), SpritePath.ui("menu_ch")
        };
        for (int i = 0; i < keys.length; i++) {
            assertNotNull(cache.get(keys[i]), keys[i]);
        }
    }

    /** Every country should have its head and its flag. */
    @Test
    void everyCountryShouldHaveHeadAndFlag() {
        SpriteCache cache = new SpriteCache();
        Country[] countries = Country.values();
        for (int i = 0; i < countries.length; i++) {
            String key = CountryInfo.of(countries[i]).getSpriteKey();
            assertNotNull(cache.get(SpritePath.character(key)), key);
            assertNotNull(cache.get(SpritePath.flag(key)), key);
        }
    }
}
