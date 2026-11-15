package com.doposoccer.domain.match;

import com.doposoccer.domain.enums.MatchType;
import com.doposoccer.domain.enums.TeamSide;
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
        MatchClock clock = MatchType.GOALS.createClock(2);
        scoreboard.addGoal(TeamSide.LEFT);
        assertFalse(MatchType.GOALS.isFinished(scoreboard, clock, 2));
        scoreboard.addGoal(TeamSide.LEFT);
        assertTrue(MatchType.GOALS.isFinished(scoreboard, clock, 2));
    }

    /** By time should not finish before time. */
    @Test
    void byTimeShouldNotFinishBeforeTime() {
        Scoreboard scoreboard = new Scoreboard();
        MatchClock clock = MatchType.TIMED.createClock(1);
        scoreboard.addGoal(TeamSide.LEFT);
        clock.tick(59);
        assertFalse(MatchType.TIMED.isFinished(scoreboard, clock, 1));
    }

    /** By time should finish when time is up and there is a leader. */
    @Test
    void byTimeShouldFinishWhenTimeIsUpAndThereIsALeader() {
        Scoreboard scoreboard = new Scoreboard();
        MatchClock clock = MatchType.TIMED.createClock(1);
        scoreboard.addGoal(TeamSide.RIGHT);
        clock.tick(60);
        assertTrue(MatchType.TIMED.isFinished(scoreboard, clock, 1));
    }

    /** By time should go to sudden death when tied. */
    @Test
    void byTimeShouldGoToSuddenDeathWhenTied() {
        Scoreboard scoreboard = new Scoreboard();
        MatchClock clock = MatchType.TIMED.createClock(1);
        clock.tick(60);
        assertFalse(MatchType.TIMED.isFinished(scoreboard, clock, 1));
        assertTrue(MatchType.TIMED.isSuddenDeath(scoreboard, clock));
    }

    /** Sudden death should last until someone scores. */
    @Test
    void suddenDeathShouldLastUntilSomeoneScores() {
        Scoreboard scoreboard = new Scoreboard();
        MatchClock clock = MatchType.TIMED.createClock(1);
        clock.tick(60 + 3600 * 1000);
        assertFalse(MatchType.TIMED.isFinished(scoreboard, clock, 1));
        scoreboard.addGoal(TeamSide.LEFT);
        assertTrue(MatchType.TIMED.isFinished(scoreboard, clock, 1));
    }

    /** By goals should default to five goals. */
    @Test
    void byGoalsShouldDefaultToFiveGoals() {
        assertEquals(5, MatchType.GOALS.getDefaultTarget());
    }

    /** Should validate target range. */
    @Test
    void shouldValidateTargetRange() {
        assertTrue(MatchType.TIMED.isValidTarget(1));
        assertTrue(MatchType.GOALS.isValidTarget(10));
        assertFalse(MatchType.TIMED.isValidTarget(0));
        assertFalse(MatchType.GOALS.isValidTarget(11));
    }
}
