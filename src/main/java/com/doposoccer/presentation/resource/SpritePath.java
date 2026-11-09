package com.doposoccer.presentation.resource;

/**
 * Builds the key of each sprite from its folder inside "sprites". The rest of the
 * presentation asks for a sprite by its role, so only this class knows how the folders are organized.
 */
public final class SpritePath {

    private static final String BACKGROUNDS = "backgrounds/";
    private static final String BUTTONS = "buttons/";
    private static final String CHARACTERS = "characters/";
    private static final String FLAGS = "flags/";
    private static final String MATCH = "match/";
    private static final String UI = "ui/";
    private static final String FLAG_PREFIX = "flag_";

    private SpritePath() {
    }

    /**
     * Returns the key of a background or a scenery layer.
     *
     * @param name name of the file without extension, such as "menu"
     * @return the key to give to the sprite cache
     */
    public static String background(String name) {
        return BACKGROUNDS + name;
    }

    /**
     * Returns the key of a button image
     * @param name name of the file without extension, such as "btn"
     * @return the key to give to the sprite cache
     */
    public static String button(String name) {
        return BUTTONS + name;
    }

    /**
     * Returns the key of a piece of a character: its head or the parts of its body.
     * @param name sprite key of the country or name of the body part, such as "korea" or "foot"
     * @return the key to give to the sprite cache
     */
    public static String character(String name) {
        return CHARACTERS + name;
    }

    /**
     * Returns the key of the flag of a country
     * @param spriteKey sprite key of the country, such as "korea"
     * @return the key to give to the sprite cache
     */
    public static String flag(String spriteKey) {
        return FLAGS + FLAG_PREFIX + spriteKey;
    }

    /**
     * Returns the key of an object of the match, such as the ball or the goal posts
     * @param name name of the file without extension, such as "ball"
     * @return the key to give to the sprite cache
     */
    public static String match(String name) {
        return MATCH + name;
    }

    /**
     * Returns the key of an image of the menu, such as the logo
     * @param name name of the file without extension, such as "logo"
     * @return the key to give to the sprite cache
     */
    public static String ui(String name) {
        return UI + name;
    }
}
