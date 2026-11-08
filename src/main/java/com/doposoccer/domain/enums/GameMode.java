package com.doposoccer.domain.enums;

/**
 * Game modes. A mode that is not available yet is shown disabled in the menu.
 */
public enum GameMode {

    /** Two humans on the same keyboard. */
    PLAYER_VS_PLAYER("Jugador vs Jugador", true),

    /** One human against the machine. Planned for version two. */
    PLAYER_VS_MACHINE("Jugador vs Máquina", false);

    /** Name shown in the menu. */
    private final String displayName;

    /** True if the mode can be played in this version. */
    private final boolean available;

    /**
     * Creates the constant with its values.
     *
     * @param displayName name shown in the menu
     * @param available   true if the mode can be played in this version
     */
    GameMode(String displayName, boolean available) {
        this.displayName = displayName;
        this.available = available;
    }

    /**
     * Returns the name shown in the menu.
     *
     * @return name shown in the menu
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Tells whether the mode can be played in this version.
     *
     * @return true if the mode can be played in this version
     */
    public boolean isAvailable() {
        return available;
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
