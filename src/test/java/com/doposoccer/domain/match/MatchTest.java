package com.doposoccer.domain.match;

import com.doposoccer.domain.DOPOSoccerException;
import com.doposoccer.domain.enums.Country;
import com.doposoccer.domain.enums.GameMode;
import com.doposoccer.domain.enums.MatchStatus;
import com.doposoccer.domain.enums.MatchType;
import com.doposoccer.domain.enums.Resolution;
import com.doposoccer.domain.enums.TeamSide;
import com.doposoccer.domain.model.Ball;
import com.doposoccer.domain.model.Field;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Unit tests of Match.
 */
class MatchTest {

    /** Duration of one frame at 60 FPS. */
    private static final double FRAME = 1.0 / 60;
    /** Tolerance to compare doubles. */
    private static final double DELTA = 1e-6;

    /**
     * Creates a Player vs Player match (Korea vs Cameroon).
     *
     * @param type   type of match
     * @param target minutes or goals to win
     * @return the new match
     * @throws DOPOSoccerException if the configuration is not valid
     */
    private Match newMatch(MatchType type, int target) throws DOPOSoccerException {
        return new Match(new MatchConfig(GameMode.PLAYER_VS_PLAYER, Resolution.MEDIUM,
                Country.KOREA, Country.CAMEROON, type, target));
    }

    /**
     * Plays the match frame by frame during the given time.
     *
     * @param match   match to play
     * @param seconds simulated seconds
     */
    private void play(Match match, double seconds) {
        int frames = (int) Math.round(seconds / FRAME);
        for (int i = 0; i < frames; i++) {
            match.update(FRAME);
        }
    }

    /** Should start zero zero and playing. */
    @Test
    void shouldStartZeroZeroAndPlaying() throws DOPOSoccerException {
        Match match = newMatch(MatchType.BY_GOALS, 3);
        assertEquals(0, match.getGoals(TeamSide.LEFT));
        assertEquals(0, match.getGoals(TeamSide.RIGHT));
        assertEquals(MatchStatus.PLAYING, match.getStatus());
        assertEquals(5, match.getRenderStates().size());
    }

    /** Ball entering left goal should score for right and restart from center. */
    @Test
    void ballEnteringLeftGoalShouldScoreForRightAndRestartFromCenter() throws DOPOSoccerException {
        Match match = newMatch(MatchType.BY_GOALS, 3);
        match.getBall().resetAt(Field.GOAL_DEPTH / 2, Field.GROUND_Y - Ball.RADIUS);
        match.update(FRAME);
        assertEquals(1, match.getGoals(TeamSide.RIGHT));
        assertEquals(Field.centerX(), match.getBall().getX(), DELTA);
        assertEquals(Field.KICKOFF_Y, match.getBall().getY(), DELTA);
    }

    /** By goals should finish and give points. */
    @Test
    void byGoalsShouldFinishAndGivePoints() throws DOPOSoccerException {
        Match match = newMatch(MatchType.BY_GOALS, 1);
        match.goalScored(TeamSide.LEFT);
        assertEquals(MatchStatus.FINISHED, match.getStatus());
        assertEquals(TeamSide.LEFT, match.getWinner());
        assertEquals(Match.WINNER_POINTS, match.getPointsEarned(TeamSide.LEFT));
        assertEquals(Match.LOSER_POINTS, match.getPointsEarned(TeamSide.RIGHT));
    }

    /** By time tied should go to sudden death and next goal wins. */
    @Test
    void byTimeTiedShouldGoToSuddenDeathAndNextGoalWins() throws DOPOSoccerException {
        Match match = newMatch(MatchType.BY_TIME, 1);
        play(match, 61);
        assertEquals(MatchStatus.PLAYING, match.getStatus());
        assertTrue(match.isSuddenDeath());
        match.goalScored(TeamSide.RIGHT);
        assertEquals(MatchStatus.FINISHED, match.getStatus());
        assertEquals(TeamSide.RIGHT, match.getWinner());
    }

    /** By time should finish when time runs out with a leader. */
    @Test
    void byTimeShouldFinishWhenTimeRunsOutWithALeader() throws DOPOSoccerException {
        Match match = newMatch(MatchType.BY_TIME, 1);
        match.goalScored(TeamSide.LEFT);
        play(match, 61);
        assertEquals(MatchStatus.FINISHED, match.getStatus());
        assertEquals(TeamSide.LEFT, match.getWinner());
    }

    /** Pause should stop the clock. */
    @Test
    void pauseShouldStopTheClock() throws DOPOSoccerException {
        Match match = newMatch(MatchType.BY_TIME, 2);
        play(match, 1);
        match.togglePause();
        String clockText = match.getClockText();
        play(match, 5);
        assertEquals(MatchStatus.PAUSED, match.getStatus());
        assertEquals(clockText, match.getClockText());
        match.togglePause();
        assertEquals(MatchStatus.PLAYING, match.getStatus());
    }

    /** Should not pause a finished match. */
    @Test
    void shouldNotPauseAFinishedMatch() throws DOPOSoccerException {
        Match match = newMatch(MatchType.BY_GOALS, 1);
        match.end();
        try {
            match.togglePause();
            fail("DOPOSoccerException was expected");
        } catch (DOPOSoccerException e) {
            assertEquals(DOPOSoccerException.MATCH_FINISHED, e.getMessage());
        }
    }

    /** Ending the match should give no winner and no points. */
    @Test
    void endingTheMatchShouldGiveNoWinnerAndNoPoints() throws DOPOSoccerException {
        Match match = newMatch(MatchType.BY_GOALS, 3);
        match.goalScored(TeamSide.LEFT);
        match.end();
        assertEquals(MatchStatus.ABANDONED, match.getStatus());
        assertFalse(match.hasWinner());
        assertEquals(0, match.getPointsEarned(TeamSide.LEFT));
        assertEquals(0, match.getPointsEarned(TeamSide.RIGHT));
        try {
            match.getWinner();
            fail("DOPOSoccerException was expected");
        } catch (DOPOSoccerException e) {
            assertEquals(DOPOSoccerException.MATCH_ABANDONED, e.getMessage());
        }
    }

    /** Should not end a match that is already over. */
    @Test
    void shouldNotEndAMatchThatIsAlreadyOver() throws DOPOSoccerException {
        Match match = newMatch(MatchType.BY_GOALS, 1);
        match.goalScored(TeamSide.RIGHT);
        try {
            match.end();
            fail("DOPOSoccerException was expected");
        } catch (DOPOSoccerException e) {
            assertEquals(DOPOSoccerException.MATCH_FINISHED, e.getMessage());
        }
    }

    /** Should not give winner before the end. */
    @Test
    void shouldNotGiveWinnerBeforeTheEnd() throws DOPOSoccerException {
        Match match = newMatch(MatchType.BY_GOALS, 3);
        try {
            match.getWinner();
            fail("DOPOSoccerException was expected");
        } catch (DOPOSoccerException e) {
            assertEquals(DOPOSoccerException.MATCH_NOT_FINISHED, e.getMessage());
        }
    }

    /** Finished match should not advance. */
    @Test
    void finishedMatchShouldNotAdvance() throws DOPOSoccerException {
        Match match = newMatch(MatchType.BY_TIME, 1);
        match.end();
        String clockText = match.getClockText();
        play(match, 2);
        assertEquals(clockText, match.getClockText());
    }

    /** Ball dropped at kickoff should stay inside the field. */
    @Test
    void ballDroppedAtKickoffShouldStayInsideTheField() throws DOPOSoccerException {
        Match match = newMatch(MatchType.BY_GOALS, 3);
        play(match, 5);
        Ball ball = match.getBall();
        assertTrue(ball.getBottom() <= Field.GROUND_Y + DELTA);
        assertEquals(0, match.getGoals(TeamSide.LEFT));
        assertEquals(0, match.getGoals(TeamSide.RIGHT));
    }
}
