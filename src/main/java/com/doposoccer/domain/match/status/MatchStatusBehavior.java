package com.doposoccer.domain.match.status;
import com.doposoccer.domain.DOPOSoccerException;
import com.doposoccer.domain.enums.MatchStatus;
import java.util.EnumMap;
import java.util.Map;

/**
 *
 * What a match status allows: whether time advances, whether the match is over,
 * whether the score counts and which status comes after a pause or an end request
 * The instance of each status is found with {@link #get}.
 */
public abstract class MatchStatusBehavior {
    private static final Map<MatchStatus, MatchStatusBehavior> BY_STATUS = new EnumMap<>(MatchStatus.class);
    static {
        BY_STATUS.put(MatchStatus.PLAYING, new Playing());
        BY_STATUS.put(MatchStatus.PAUSED, new Paused());
        BY_STATUS.put(MatchStatus.FINISHED, new Finished());
        BY_STATUS.put(MatchStatus.ABANDONED, new Abandoned());
    }

    /**
     * Returns the behavior of a status
     * @param status the status
     * @return its behavior
     */
    public static MatchStatusBehavior get(MatchStatus status) {
        return BY_STATUS.get(status);
    }

    /**
     * Returns the text shown to the users
     * @return the label
     */
    public abstract String getLabel();

    /**
     * Tells whether the match is over
     * @return true if the match is over
     */
    public abstract boolean isOver();

    /**
     * Returns the status after the users press pause
     * @return the next status
     * @throws DOPOSoccerException if the match is already over
     */
    public abstract MatchStatus togglePause() throws DOPOSoccerException;

    /**
     * Returns the status after the users end the match
     * @return the next status
     * @throws DOPOSoccerException if the match is already over
     */
    public abstract MatchStatus end() throws DOPOSoccerException;

    /**
     * Tells whether physics and clock advance in this status
     * @return false by default
     */
    public boolean advancesTime() {
        return false;
    }

    /**
     * Tells whether the match is waiting for the users to resume it
     * @return false by default
     */
    public boolean isPaused() {
        return false;
    }

    /**
     * Tells whether the final score counts, so there is a winner and points
     * @return false by default
     */
    public boolean hasResult() {
        return false;
    }
}
