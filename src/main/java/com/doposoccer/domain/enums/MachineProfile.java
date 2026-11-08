package com.doposoccer.domain.enums;

/**
 * Behaviour profiles of the machine player. In version one they are only
 * shown (disabled) in the configuration screen; their behaviour (AIControl)
 * arrives with the Player vs Machine mode.
 */
public enum MachineProfile {

    /** Chooses its movements at random. */
    CLUMSY("Torpe", "Escoge al azar sus movimientos"),

    /** Its priority is scoring. */
    OFFENSIVE("Ofensiva", "Su prioridad es anotar"),

    /** Its priority is defending its goal. */
    DEFENSIVE("Defensiva", "Su prioridad es defender el arco"),

    /** Decides according to the state of the match. */
    EXPERT("Experta", "Decide la acción según el estado del partido");

    /** Name shown in the menu. */
    private final String displayName;

    /** Short explanation shown in the menu. */
    private final String description;

    /**
     * Creates the constant with its values.
     *
     * @param displayName name shown in the menu
     * @param description short explanation shown in the menu
     */
    MachineProfile(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
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
     * Returns the short explanation shown in the menu.
     *
     * @return short explanation shown in the menu
     */
    public String getDescription() {
        return description;
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
