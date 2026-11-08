package com.doposoccer.domain.match;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests of MatchClock.
 */
class MatchClockTest {

    /** Countdown should show remaining time. */
    @Test
    void countdownShouldShowRemainingTime() {
        MatchClock clock = MatchClock.countdown(180);
        assertEquals("03:00", clock.format());
        clock.tick(30.5);
        assertEquals("02:30", clock.format());
    }

    /** Countdown should expire and not go below zero. */
    @Test
    void countdownShouldExpireAndNotGoBelowZero() {
        MatchClock clock = MatchClock.countdown(60);
        clock.tick(75);
        assertTrue(clock.isExpired());
        assertEquals("00:00", clock.format());
    }

    /** Stopwatch should never expire. */
    @Test
    void stopwatchShouldNeverExpire() {
        MatchClock clock = MatchClock.stopwatch();
        clock.tick(5000);
        assertFalse(clock.isExpired());
        assertFalse(clock.hasLimit());
    }

    /** Stopwatch should show elapsed time. */
    @Test
    void stopwatchShouldShowElapsedTime() {
        MatchClock clock = MatchClock.stopwatch();
        clock.tick(65.9);
        assertEquals("01:05", clock.format());
    }
}
