package com.doposoccer.domain.enums;
import com.doposoccer.domain.match.MatchClock;
import com.doposoccer.domain.match.Scoreboard;

/**
 * Type of match. Each type knows its target, how to create its clock
 * and how to decide whether the match is over
 */
public enum MatchType {
    // Whoever has more goals when time runs out wins. If it is tied, the next goal wins
    TIMED("Por tiempo", "Minutos", 3) {
        @Override
        public MatchClock createClock(int target) {
            return MatchClock.countdown(target * SECONDS_PER_MINUTE);
        }

        @Override
        public boolean isFinished(Scoreboard scoreboard, MatchClock clock, int target) {
            return clock.isExpired() && !scoreboard.isTied();
        }

        @Override
        public boolean isSuddenDeath(Scoreboard scoreboard, MatchClock clock) {
            return clock.isExpired() && scoreboard.isTied();
        }
    },
    // The first to reach the goals wins. The clock only counts up
    GOALS("Por goles", "Goles para ganar", 5) {
        @Override
        public MatchClock createClock(int target) {
            return MatchClock.stopwatch();
        }

        @Override
        public boolean isFinished(Scoreboard scoreboard, MatchClock clock, int target) {
            return scoreboard.getMaxGoals() >= target;
        }

        @Override
        public boolean isSuddenDeath(Scoreboard scoreboard, MatchClock clock) {
            return false;
        }
    };

    /** Smallest target allowed by every type. */
    public static final int MIN_TARGET = 1;

    /** Largest target allowed by every type. */
    public static final int MAX_TARGET = 10;

    private static final int SECONDS_PER_MINUTE = 60;

    private final String displayName;
    private final String targetLabel;
    private final int defaultTarget;

    MatchType(String displayName, String targetLabel, int defaultTarget) {
        this.displayName = displayName;
        this.targetLabel = targetLabel;
        this.defaultTarget = defaultTarget;
    }

    /**
     * Creates the clock of a match of this type
     * @param target minutes or goals chosen by the users
     * @return a new clock
     */
    public abstract MatchClock createClock(int target);

    /**
     * Tells whether the match reached its end condition
     * @param scoreboard current score
     * @param clock match clock
     * @param target minutes or goals chosen by the users
     * @return true if the match is over
     */
    public abstract boolean isFinished(Scoreboard scoreboard, MatchClock clock, int target);

    /**
     * Tells whether the match is in sudden death: the next goal wins
     * @param scoreboard current score
     * @param clock match clock
     * @return true while in sudden death
     */
    public abstract boolean isSuddenDeath(Scoreboard scoreboard, MatchClock clock);

    /**
     * Returns the name shown to the users
     * @return the display name
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Returns what the target means
     * @return the label of the target
     */
    public String getTargetLabel() {
        return targetLabel;
    }

    /**
     * Returns the target proposed when this type is chosen
     * @return the default target
     */
    public int getDefaultTarget() {
        return defaultTarget;
    }

    /**
     * Tells whether a target is inside the allowed range
     * @param target target to check
     * @return true if it is allowed
     */
    public boolean isValidTarget(int target) {
        return target >= MIN_TARGET && target <= MAX_TARGET;
    }
}
