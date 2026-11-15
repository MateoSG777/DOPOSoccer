package com.doposoccer.domain.enums;

/**
 * Skill of a player that can be improved in the store. All of them start at level 1 and
 * every level above 1 adds 10 % to the base value
 */
public enum Stat {
    // Walking speed
    SPEED("Velocidad"),
    // Damage of kicks and power shots: multiplicadorKick = 1 + 0.10 * (Kick - 1)
    KICK("Patada"),
    // Height of the jump
    JUMP("Salto"),
    // Maximum life: vidaMax = 100 * (1 + 0.10 * (Survival - 1))
    SURVIVAL("Supervivencia");

    /** First level of every stat */
    public static final int MIN_LEVEL = 1;

    /** Last level of every stat */
    public static final int MAX_LEVEL = 10;

    /** Points that the first upgrade costs; every level costs twice the previous one */
    public static final int BASE_COST = 500;

    /** Fraction of the base value added by every level */
    public static final double STEP = 0.10;

    private final String displayName;

    Stat(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Returns the name shown to the users
     * @return the display name
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Returns how much the base value is multiplied at a level
     * @param level level of the stat
     * @return 1.0 at level 1, 1.1 at level 2 ... 1.9 at level 10
     */
    public static double multiplier(int level) {
        return 1 + STEP * (level - MIN_LEVEL);
    }

    /**
     * Returns the points needed to go from a level to the next one: 500, 1000, 2000...
     * @param level current level of the stat
     * @return the cost of the upgrade
     */
    public static int cost(int level) {
        return BASE_COST << (level - MIN_LEVEL);
    }
}
