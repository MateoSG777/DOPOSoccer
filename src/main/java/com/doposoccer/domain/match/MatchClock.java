package com.doposoccer.domain.match;

/**
 * Match clock. It is either a countdown (timed matches) or a stopwatch
 * without limit (goals matches).
 */
public final class MatchClock {

    /** Seconds in one minute, used to format the time. */
    private static final int SECONDS_PER_MINUTE = 60;

    /** Marker duration meaning "no time limit". */
    private static final double NO_LIMIT = -1;

    /** Total duration in seconds, or NO_LIMIT for a stopwatch. */
    private final double durationSeconds;

    /** Seconds played so far. */
    private double elapsedSeconds;

    /**
     * Creates a clock with the given duration.
     *
     * @param durationSeconds total duration in seconds, or NO_LIMIT
     */
    private MatchClock(double durationSeconds) {
        this.durationSeconds = durationSeconds;
        this.elapsedSeconds = 0;
    }

    /**
     * Creates a countdown clock.
     *
     * @param durationSeconds total duration in seconds
     * @return a clock that expires after the given duration
     */
    public static MatchClock countdown(double durationSeconds) {
        return new MatchClock(durationSeconds);
    }

    /**
     * Creates a stopwatch that never expires.
     *
     * @return a clock without time limit
     */
    public static MatchClock stopwatch() {
        return new MatchClock(NO_LIMIT);
    }

    /**
     * Advances the clock. A countdown never goes beyond its duration.
     *
     * @param dt seconds elapsed since the last frame
     */
    public void tick(double dt) {
        elapsedSeconds += dt;
        if (hasLimit() && elapsedSeconds > durationSeconds) {
            elapsedSeconds = durationSeconds;
        }
    }

    /**
     * Tells whether this clock is a countdown.
     *
     * @return true if this clock is a countdown
     */
    public boolean hasLimit() {
        return durationSeconds != NO_LIMIT;
    }

    /**
     * Tells whether this is a countdown that reached zero.
     *
     * @return true if this is a countdown that reached zero
     */
    public boolean isExpired() {
        return hasLimit() && elapsedSeconds >= durationSeconds;
    }

    /**
     * Returns the seconds played so far.
     *
     * @return seconds played so far
     */
    public double getElapsedSeconds() {
        return elapsedSeconds;
    }

    /**
     * Returns the remaining seconds for a countdown, elapsed seconds for a stopwatch.
     *
     * @return remaining seconds for a countdown, elapsed seconds for a stopwatch
     */
    public double getDisplaySeconds() {
        if (hasLimit()) {
            return durationSeconds - elapsedSeconds;
        }
        return elapsedSeconds;
    }

    /**
     * Returns the displayed time in mm:ss format.
     *
     * @return the displayed time in mm:ss format
     */
    public String format() {
        int total = (int) Math.floor(getDisplaySeconds());
        if (hasLimit()) {
            total = (int) Math.ceil(getDisplaySeconds());
        }
        return String.format("%02d:%02d", total / SECONDS_PER_MINUTE, total % SECONDS_PER_MINUTE);
    }
}
