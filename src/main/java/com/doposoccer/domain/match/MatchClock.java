package com.doposoccer.domain.match;

/**
 * Match clock. A countdown for timed matches or a stopwatch for goals matches
 * A stopwatch is a countdown with an infinite duration, so it never expires
 */
public final class MatchClock {

    private static final int SECONDS_PER_MINUTE = 60;
    private final double durationSeconds;
    private double elapsedSeconds;

    private MatchClock(double durationSeconds) {
        this.durationSeconds = durationSeconds;
    }

    /**
     * Creates a countdown clock
     * @param durationSeconds total duration in seconds
     * @return a clock that expires after the given duration
     */
    public static MatchClock countdown(double durationSeconds) {
        return new MatchClock(durationSeconds);
    }

    /**
     * Creates a stopwatch that never expires
     * @return a clock without time limit
     */
    public static MatchClock stopwatch() {
        return new MatchClock(Double.POSITIVE_INFINITY);
    }

    /**
     * Advances the clock. A countdown never goes beyond its duration
     * @param dt seconds elapsed since the last frame
     */
    public void tick(double dt) {
        elapsedSeconds = Math.min(elapsedSeconds + dt, durationSeconds);
    }

    /**
     * Tells whether this clock is a countdown
     * @return true if this clock is a countdown
     */
    public boolean hasLimit() {
        return !Double.isInfinite(durationSeconds);
    }

    /**
     * Tells whether this is a countdown that reached zero
     * @return true if the time is up
     */
    public boolean isExpired() {
        return elapsedSeconds >= durationSeconds;
    }

    /**
     * Returns the displayed time in mm:ss format. remaining time for a countdown,
     * elapsed time for a stopwatch
     * @return the displayed time in mm:ss format
     */
    public String format() {
        int total = (int) Math.floor(elapsedSeconds);
        if (hasLimit()) {
            total = (int) Math.ceil(durationSeconds - elapsedSeconds);
        }
        return String.format("%02d:%02d", total / SECONDS_PER_MINUTE, total % SECONDS_PER_MINUTE);
    }
}