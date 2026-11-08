package com.doposoccer.domain.enums;

/**
 * Status of a match. Each status knows whether time advances, whether the
 * match is over and whether the final score counts (winner and points).
 */
public enum MatchStatus {

    /** The match is being played. */
    PLAYING(true, false, false, "En juego"),

    /** The match is paused: physics and clock are stopped. */
    PAUSED(false, false, false, "Pausa"),

    /** The match reached its end condition: there is a winner. */
    FINISHED(false, true, true, "Partido terminado"),

    /** The players ended the match: there is no winner and no points. */
    ABANDONED(false, true, false, "Partido terminado por los jugadores");

    /** True if physics and clock advance in this status. */
    private final boolean advancesTime;

    /** True if the match is over. */
    private final boolean over;

    /** True if the final score counts (winner and points). */
    private final boolean hasResult;

    /** Text shown to the users. */
    private final String label;

    /**
     * Creates the constant with its values.
     *
     * @param advancesTime true if physics and clock advance
     * @param over         true if the match is over
     * @param hasResult    true if the final score counts
     * @param label        text shown to the users
     */
    MatchStatus(boolean advancesTime, boolean over, boolean hasResult, String label) {
        this.advancesTime = advancesTime;
        this.over = over;
        this.hasResult = hasResult;
        this.label = label;
    }

    /**
     * Tells whether physics and clock advance in this status.
     *
     * @return true if physics and clock advance in this status
     */
    public boolean advancesTime() {
        return advancesTime;
    }

    /**
     * Tells whether the match is over.
     *
     * @return true if the match is over
     */
    public boolean isOver() {
        return over;
    }

    /**
     * Tells whether the final score counts (winner and points).
     *
     * @return true if the final score counts (winner and points)
     */
    public boolean hasResult() {
        return hasResult;
    }

    /**
     * Returns the text shown to the users.
     *
     * @return text shown to the users
     */
    public String getLabel() {
        return label;
    }
}
