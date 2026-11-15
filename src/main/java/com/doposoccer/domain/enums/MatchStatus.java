package com.doposoccer.domain.enums;
import com.doposoccer.domain.DOPOSoccerException;

/**
 * Status of a match and what each one allows
 */
public enum MatchStatus {
    PLAYING(true, false, false),
    PAUSED(false, true, false),
    FINISHED(false, false, true),
    // The players ended the match
    ABANDONED(false, false, false);

    private final boolean running;
    private final boolean paused;
    private final boolean result;

    MatchStatus(boolean running, boolean paused, boolean result) {
        this.running = running;
        this.paused = paused;
        this.result = result;
    }

    /**
     * Pauses a running match or resumes a paused one
     * @return the new status
     * @throws DOPOSoccerException if the match is already over
     */
    public MatchStatus togglePause() throws DOPOSoccerException {
        checkNotOver();
        return running ? PAUSED : PLAYING;
    }

    /**
     * Ends the match by decision of the players
     * @return the new status
     * @throws DOPOSoccerException if the match is already over
     */
    public MatchStatus end() throws DOPOSoccerException {
        checkNotOver();
        return ABANDONED;
    }

    private void checkNotOver() throws DOPOSoccerException {
        if (isOver()) {
            throw new DOPOSoccerException(DOPOSoccerException.MATCH_FINISHED);
        }
    }

    /**
     * Tells whether the match advances in this status
     * @return true only while playing
     */
    public boolean advancesTime() {
        return running;
    }

    /**
     * Tells whether the match is paused
     * @return true only while paused
     */
    public boolean isPaused() {
        return paused;
    }

    /**
     * Tells whether the match is over
     * @return true if it finished or was abandoned
     */
    public boolean isOver() {
        return !running && !paused;
    }

    /**
     * Tells whether the match finished by its rules, so it can have a winner
     * @return true only when finished
     */
    public boolean hasResult() {
        return result;
    }
}
