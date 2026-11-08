package com.doposoccer.domain.enums;

import com.doposoccer.domain.match.MatchClock;
import com.doposoccer.domain.match.Scoreboard;

/**
 * Type of match. Each constant knows how to validate its target, how to
 * create its clock and how to decide whether the match is over
 * (constant-specific polymorphism, no switch needed).
 */
public enum MatchType {

    /**
     * Timed match: whoever has more goals when time runs out wins.
     * If it is tied, it goes to sudden death: the next goal wins, no matter how long it takes.
     */
    BY_TIME("Por tiempo", "Minutos", 1, 10, 3) {
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

    /** Goals match: the first player who reaches the target number of goals wins. No time limit. */
    BY_GOALS("Por goles", "Goles para ganar", 1, 10, 5) {
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

    /** Seconds in one minute. */
    public static final int SECONDS_PER_MINUTE = 60;

    /** Name shown in the menu. */
    private final String displayName;

    /** Name of the target value shown in the menu (minutes or goals). */
    private final String targetLabel;

    /** Smallest valid target. */
    private final int minTarget;

    /** Largest valid target. */
    private final int maxTarget;

    /** Target suggested in the menu. */
    private final int defaultTarget;

    /**
     * Creates the constant with its values
     * @param displayName name shown in the menu
     * @param targetLabel name of the target value
     * @param minTarget smallest valid target
     * @param maxTarget largest valid target
     * @param defaultTarget target suggested in the menu
     */
    MatchType(String displayName, String targetLabel, int minTarget, int maxTarget, int defaultTarget) {
        this.displayName = displayName;
        this.targetLabel = targetLabel;
        this.minTarget = minTarget;
        this.maxTarget = maxTarget;
        this.defaultTarget = defaultTarget;
    }

    /**
     * Creates the clock this type of match needs.
     * @param target minutes (by time) or goals to win (by goals)
     * @return a countdown for timed matches, a stopwatch otherwise
     */
    public abstract MatchClock createClock(int target);

    /**
     * Decides whether the match reached its end condition.
     * @param scoreboard current score
     * @param clock match clock
     * @param target minutes or goals to win
     * @return true if the match must finish with a winner
     */
    public abstract boolean isFinished(Scoreboard scoreboard, MatchClock clock, int target);

    /**
     * Tells whether the match is in sudden death (time is up and the score is tied).
     * @param scoreboard current score
     * @param clock match clock
     * @return true if the next goal wins the match
     */
    public abstract boolean isSuddenDeath(Scoreboard scoreboard, MatchClock clock);

    /**
     * Tells whether a target is within the allowed range.
     * @param target minutes or goals proposed by the users
     * @return true if the target is within [minTarget, maxTarget]
     */
    public boolean isValidTarget(int target) {
        return target >= minTarget && target <= maxTarget;
    }

    /**
     * Returns the name shown in the menu.
     * @return name shown in the menu
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Returns the name of the target value (minutes or goals).
     * @return name of the target value (minutes or goals)
     */
    public String getTargetLabel() {
        return targetLabel;
    }

    /**
     * Returns the smallest valid target.
     * @return smallest valid target
     */
    public int getMinTarget() {
        return minTarget;
    }

    /**
     * Returns the largest valid target.
     * @return largest valid target
     */
    public int getMaxTarget() {
        return maxTarget;
    }

    /**
     * Returns the target suggested in the menu.
     * @return target suggested in the menu
     */
    public int getDefaultTarget() {
        return defaultTarget;
    }

    /**
     * Returns the display name, used by Swing combo boxes.
     * @return the display name, used by Swing combo boxes
     */
    @Override
    public String toString() {
        return displayName;
    }
}
