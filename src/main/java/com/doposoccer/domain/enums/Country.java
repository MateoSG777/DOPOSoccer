package com.doposoccer.domain.enums;

/**
 * Playable characters (Annex 1). Statistics belong to the player, not to the
 * country, so a country only holds its visual identity and the name of its
 * power shot. Adding a character means adding one constant.
 */
public enum Country {

    /** South Korea: power shot "Aura Azul". */
    KOREA("Corea del Sur", "Aura Azul", "korea", 0xD32F2F),

    /** Cameroon: power shot "Rayo". */
    CAMEROON("Camerún", "Rayo", "cameroon", 0x2E7D32),

    /** Nigeria: power shot "Tornado". */
    NIGERIA("Nigeria", "Tornado", "nigeria", 0x43A047),

    /** United States: power shot "Ilusión". */
    USA("Estados Unidos", "Ilusión", "usa", 0x1565C0);

    /** Name shown to the users. */
    private final String displayName;

    /** Name of the unique power shot. */
    private final String powerName;

    /** Key of the head sprite in the presentation sprite cache. */
    private final String spriteKey;

    /** Representative color in 0xRRGGBB format (no java.awt dependency). */
    private final int color;

    /**
     * Creates the constant with its values.
     *
     * @param displayName name shown to the users
     * @param powerName   name of the unique power shot
     * @param spriteKey   key of the head sprite
     * @param color       representative color in 0xRRGGBB format
     */
    Country(String displayName, String powerName, String spriteKey, int color) {
        this.displayName = displayName;
        this.powerName = powerName;
        this.spriteKey = spriteKey;
        this.color = color;
    }

    /**
     * Returns the name shown to the users.
     *
     * @return name shown to the users
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Returns the name of the unique power shot.
     *
     * @return name of the unique power shot
     */
    public String getPowerName() {
        return powerName;
    }

    /**
     * Returns the key of the head sprite in the presentation sprite cache.
     *
     * @return key of the head sprite in the presentation sprite cache
     */
    public String getSpriteKey() {
        return spriteKey;
    }

    /**
     * Returns the representative color in 0xRRGGBB format.
     *
     * @return representative color in 0xRRGGBB format
     */
    public int getColor() {
        return color;
    }

    /**
     * Returns the display name, used by Swing combo boxes.
     *
     * @return the display name, used by Swing combo boxes
     */
    @Override
    public String toString() {
        return displayName;
    }
}
