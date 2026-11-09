package com.doposoccer.domain.match;

import com.doposoccer.domain.enums.TeamSide;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests of Scoreboard.
 */
class ScoreboardTest {

    /** Should start zero zero. */
    @Test
    void shouldStartZeroZero() {
        Scoreboard scoreboard = new Scoreboard();
        assertEquals(0, scoreboard.getGoals(TeamSide.LEFT));
        assertEquals(0, scoreboard.getGoals(TeamSide.RIGHT));
        assertTrue(scoreboard.isTied());
    }

    /** Should count goals and know the leader. */
    @Test
    void shouldCountGoalsAndKnowTheLeader() {
        Scoreboard scoreboard = new Scoreboard();
        scoreboard.addGoal(TeamSide.RIGHT);
        scoreboard.addGoal(TeamSide.RIGHT);
        scoreboard.addGoal(TeamSide.LEFT);
        assertEquals(2, scoreboard.getGoals(TeamSide.RIGHT));
        assertEquals(2, scoreboard.getMaxGoals());
        assertFalse(scoreboard.isTied());
        assertEquals(TeamSide.RIGHT, scoreboard.getLeader());
    }
}
