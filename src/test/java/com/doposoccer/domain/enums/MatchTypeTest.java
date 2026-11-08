package com.doposoccer.domain.enums;

import com.doposoccer.domain.match.MatchClock;
import com.doposoccer.domain.match.Scoreboard;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests of MatchType.
 */
class MatchTypeTest {

    /** By goals should finish when someone reaches the target. */
    @Test
    void byGoalsShouldFinishWhenSomeoneReachesTheTarget() {
        Scoreboard scoreboard = new Scoreboard();
        MatchClock clock = MatchType.BY_GOALS.createClock(2);
        scoreboard.addGoal(TeamSide.LEFT);
        assertFalse(MatchType.BY_GOALS.isFinished(scoreboard, clock, 2));
        scoreboard.addGoal(TeamSide.LEFT);
        assertTrue(MatchType.BY_GOALS.isFinished(scoreboard, clock, 2));
    }

    /** By time should not finish before time. */
    @Test
    void byTimeShouldNotFinishBeforeTime() {
        Scoreboard scoreboard = new Scoreboard();
        MatchClock clock = MatchType.BY_TIME.createClock(1);
        scoreboard.addGoal(TeamSide.LEFT);
        clock.tick(59);
        assertFalse(MatchType.BY_TIME.isFinished(scoreboard, clock, 1));
    }

    /** By time should finish when time is up and there is a leader. */
    @Test
    void byTimeShouldFinishWhenTimeIsUpAndThereIsALeader() {
        Scoreboard scoreboard = new Scoreboard();
        MatchClock clock = MatchType.BY_TIME.createClock(1);
        scoreboard.addGoal(TeamSide.RIGHT);
        clock.tick(60);
        assertTrue(MatchType.BY_TIME.isFinished(scoreboard, clock, 1));
    }

    /** By time should go to sudden death when tied. */
    @Test
    void byTimeShouldGoToSuddenDeathWhenTied() {
        Scoreboard scoreboard = new Scoreboard();
        MatchClock clock = MatchType.BY_TIME.createClock(1);
        clock.tick(60);
        assertFalse(MatchType.BY_TIME.isFinished(scoreboard, clock, 1));
        assertTrue(MatchType.BY_TIME.isSuddenDeath(scoreboard, clock));
    }

    /** Sudden death should last until someone scores. */
    @Test
    void suddenDeathShouldLastUntilSomeoneScores() {
        Scoreboard scoreboard = new Scoreboard();
        MatchClock clock = MatchType.BY_TIME.createClock(1);
        clock.tick(60 + 3600 * 1000);
        assertFalse(MatchType.BY_TIME.isFinished(scoreboard, clock, 1));
        scoreboard.addGoal(TeamSide.LEFT);
        assertTrue(MatchType.BY_TIME.isFinished(scoreboard, clock, 1));
    }

    /** By goals should default to five goals. */
    @Test
    void byGoalsShouldDefaultToFiveGoals() {
        assertEquals(5, MatchType.BY_GOALS.getDefaultTarget());
    }

    /** Should validate target range. */
    @Test
    void shouldValidateTargetRange() {
        assertTrue(MatchType.BY_TIME.isValidTarget(1));
        assertTrue(MatchType.BY_GOALS.isValidTarget(10));
        assertFalse(MatchType.BY_TIME.isValidTarget(0));
        assertFalse(MatchType.BY_GOALS.isValidTarget(11));
    }
}
