package com.doposoccer.domain.match;
import com.doposoccer.domain.enums.MatchType;
import java.util.EnumMap;
import java.util.Map;

/**
 * Rules of a type of match. Each concrete type knows its target, how to create its clock
 * and how to decide whether the match is over. The instance of each type is found with {@link #get}
 */
public abstract class MatchTypeInfo {

    /** Smallest target allowed by every type. */
    public static final int MIN_TARGET = 1;

    /** Largest target allowed by every type. */
    public static final int MAX_TARGET = 10;

    private static final Map<MatchType, MatchTypeInfo> BY_TYPE = new EnumMap<>(MatchType.class);
    static {
        BY_TYPE.put(MatchType.TIMED, new TimedMatch());
        BY_TYPE.put(MatchType.GOALS, new GoalsMatch());
    }

    /**
     * Returns the rules of a type
     * @param type the type
     * @return its rules
     */
    public static MatchTypeInfo of(MatchType type) {
        return BY_TYPE.get(type);
    }

    /**
     * Returns the name shown to the users
     * @return the display name
     */
    public abstract String getDisplayName();

    /**
     * Returns what the target meanss
     * @return the label of the target
     */
    public abstract String getTargetLabel();

    /**
     * Returns the target proposed when this type is chosen
     * @return the default target
     */
    public abstract int getDefaultTarget();

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
     * Returns the smallest target allowed
     * @return the minimum target
     */
    public int getMinTarget() {
        return MIN_TARGET;
    }

    /**
     * Returns the largest target allowed
     * @return the maximum target
     */
    public int getMaxTarget() {
        return MAX_TARGET;
    }

    /**
     * Tells whether a target is inside the allowed range
     * @param target target to check
     * @return true if it is allowed
     */
    public boolean isValidTarget(int target) {
        return target >= getMinTarget() && target <= getMaxTarget();
    }
}
