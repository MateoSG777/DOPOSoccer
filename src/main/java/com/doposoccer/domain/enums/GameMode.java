package com.doposoccer.domain.enums;

/**
 * Way of playing
 */
public enum GameMode {
    // Two humans on the same keyboard
    PLAYER_VS_PLAYER("Jugador vs Jugador", false) {
        @Override
        public String shortName(MachineProfile machine) {
            return "JvJ";
        }
    },
    // The machine drives the player on the right
    PLAYER_VS_MACHINE("Jugador vs Máquina", true) {
        @Override
        public String shortName(MachineProfile machine) {
            return "JvM (" + machine.getDisplayName() + ")";
        }
    };

    private final String displayName;
    private final boolean machine;

    GameMode(String displayName, boolean machine) {
        this.displayName = displayName;
        this.machine = machine;
    }

    /**
     * Returns a short name for the list of saved matches, such as "JvJ" or "JvM (Experta)"
     * @param machine profile of the machine, shown only when the rival is the machine
     * @return the short name
     */
    public abstract String shortName(MachineProfile machine);

    /**
     * Returns the name shown to the users
     * @return the display name
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Tells whether the machine drives the player on the right
     * @return true if the rival is the machine
     */
    public boolean hasMachine() {
        return machine;
    }
}
