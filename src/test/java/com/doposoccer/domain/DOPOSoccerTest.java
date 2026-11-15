package com.doposoccer.domain;

import com.doposoccer.domain.enums.Country;
import com.doposoccer.domain.enums.GameMode;
import com.doposoccer.domain.enums.MachineProfile;
import com.doposoccer.domain.enums.MatchType;
import com.doposoccer.domain.enums.Resolution;
import com.doposoccer.domain.enums.Stat;
import com.doposoccer.domain.enums.TeamSide;
import com.doposoccer.domain.match.Match;
import com.doposoccer.domain.match.MatchBuilder;
import com.doposoccer.domain.model.Player;
import com.doposoccer.persistence.MatchSlots;
import com.doposoccer.persistence.ProfileStore;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

/**
 * Unit tests of the facade of the domain and of the builder of matches.
 * They use a temporary file, so the real progress is never touched.
 */
class DOPOSoccerTest {

    private static final double FRAME = 1.0 / 60;

    private File file;
    private File slotsFile;
    private DOPOSoccer game;

    /** Points the store to a new temporary file and creates the facade. */
    @BeforeEach
    void setUp() throws IOException {
        file = File.createTempFile("doposoccer", ".dat");
        file.delete();
        file.deleteOnExit();
        ProfileStore.getInstance().useFile(file);
        slotsFile = File.createTempFile("doposoccer-partidas", ".dat");
        slotsFile.delete();
        slotsFile.deleteOnExit();
        game = new DOPOSoccer(new MatchSlots(slotsFile));
    }

    private MatchBuilder builder(GameMode mode) {
        return new MatchBuilder().mode(mode).resolution(Resolution.MEDIUM).rules(MatchType.GOALS, 1)
                .machine(MachineProfile.DEFENSIVE)
                .country(TeamSide.LEFT, Country.KOREA).country(TeamSide.RIGHT, Country.USA);
    }

    /** The builder should reject choices that are missing. */
    @Test
    void builderShouldRejectMissingChoices() {
        try {
            new MatchBuilder().mode(GameMode.PLAYER_VS_PLAYER).build();
            fail("DOPOSoccerException was expected");
        } catch (DOPOSoccerException e) {
            assertEquals(DOPOSoccerException.NULL_CONFIGURATION, e.getMessage());
        }
    }

    /** The builder should give the upgrades of the profile to the player. */
    @Test
    void builderShouldGiveTheUpgradesToThePlayer() throws DOPOSoccerException {
        game.getProfile(TeamSide.LEFT).addPoints(500);
        game.upgrade(TeamSide.LEFT, Stat.SURVIVAL);
        Match match = builder(GameMode.PLAYER_VS_PLAYER).profile(TeamSide.LEFT, game.getProfile(TeamSide.LEFT)).build();
        assertTrue(match.getPlayer(TeamSide.LEFT).getMaxLife() > Player.BASE_LIFE);
        assertEquals(Player.BASE_LIFE, match.getPlayer(TeamSide.RIGHT).getMaxLife(), 1e-9);
    }

    /** A new match should start playing with full life and an empty power bar. */
    @Test
    void newMatchShouldStartWithFullLifeAndNoPower() throws DOPOSoccerException {
        game.startMatch(builder(GameMode.PLAYER_VS_PLAYER));
        assertFalse(game.isOver());
        assertFalse(game.isPaused());
        assertEquals(1, game.snapshot().getLife(TeamSide.LEFT), 1e-9);
        assertEquals(0, game.snapshot().getPower(TeamSide.RIGHT), 1e-9);
    }

    /** The player that receives a goal should win a quarter of its power bar. */
    @Test
    void receivingAGoalShouldChargeThePowerBar() throws DOPOSoccerException {
        Match match = builder(GameMode.PLAYER_VS_PLAYER).rules(MatchType.GOALS, 3).build();
        match.goalScored(TeamSide.LEFT);
        assertEquals(Player.GOAL_POWER_BONUS, match.snapshot().getPower(TeamSide.RIGHT), 1e-9);
        assertEquals(0, match.snapshot().getPower(TeamSide.LEFT), 1e-9);
    }

    /** Pausing through the facade should stop the match. */
    @Test
    void shouldPauseAndEndTheMatch() throws DOPOSoccerException {
        game.startMatch(builder(GameMode.PLAYER_VS_PLAYER));
        game.togglePause();
        assertTrue(game.isPaused());
        game.endMatch();
        assertTrue(game.isOver());
        assertFalse(game.hasWinner());
    }

    /** The store should reject an upgrade that the player cannot pay. */
    @Test
    void shouldRejectAnUpgradeWithoutPoints() {
        try {
            game.upgrade(TeamSide.RIGHT, Stat.KICK);
            fail("DOPOSoccerException was expected");
        } catch (DOPOSoccerException e) {
            assertEquals(DOPOSoccerException.NOT_ENOUGH_POINTS, e.getMessage());
        }
    }

    /** The machine should drive the right player and move it without a keyboard. */
    @Test
    void machineShouldDriveTheRightPlayer() throws DOPOSoccerException {
        Match match = builder(GameMode.PLAYER_VS_MACHINE).rules(MatchType.GOALS, 5).build();
        double startX = match.getPlayer(TeamSide.RIGHT).getX();
        for (int i = 0; i < 180; i++) {
            match.update(FRAME);
        }
        assertTrue(match.getPlayer(TeamSide.RIGHT).getX() != startX);
        assertEquals(TeamSide.LEFT.spawnX(), match.getPlayer(TeamSide.LEFT).getX(), 1e-6);
    }

    /** The points of a finished match should be given once and saved to disk. */
    @Test
    void finishedMatchShouldGiveAndSaveThePointsOnce() throws DOPOSoccerException {
        game.startMatch(builder(GameMode.PLAYER_VS_PLAYER));
        game.getMatch().goalScored(TeamSide.LEFT);
        game.update(FRAME);
        game.update(FRAME);
        assertEquals(TeamSide.LEFT, game.getWinner());
        assertEquals(Match.WINNER_POINTS, game.getProfile(TeamSide.LEFT).getPoints());
        assertEquals(Match.LOSER_POINTS, game.getProfile(TeamSide.RIGHT).getPoints());
        ProfileStore.getInstance().useFile(file);
        assertEquals(Match.WINNER_POINTS, game.getProfile(TeamSide.LEFT).getPoints());
    }

    /** The machine should not earn points for the profile of player 2. */
    @Test
    void machineShouldNotEarnPoints() throws DOPOSoccerException {
        game.startMatch(builder(GameMode.PLAYER_VS_MACHINE));
        game.getMatch().goalScored(TeamSide.RIGHT);
        game.update(FRAME);
        assertEquals(Match.LOSER_POINTS, game.getProfile(TeamSide.LEFT).getPoints());
        assertEquals(0, game.getProfile(TeamSide.RIGHT).getPoints());
    }

    /** A match ended by the players should give no points. */
    @Test
    void abandonedMatchShouldGiveNoPoints() throws DOPOSoccerException {
        game.startMatch(builder(GameMode.PLAYER_VS_PLAYER));
        game.endMatch();
        game.update(FRAME);
        assertEquals(0, game.getProfile(TeamSide.LEFT).getPoints());
        assertEquals(0, game.getPointsEarned(TeamSide.LEFT));
    }

    /** The machine should get the stats of its profile, the human those of the store. */
    @Test
    void machineShouldGetTheStatsOfItsProfile() throws DOPOSoccerException {
        Match match = builder(GameMode.PLAYER_VS_MACHINE).machine(MachineProfile.EXPERT).build();
        double expected = Player.BASE_LIFE * Stat.multiplier(MachineProfile.EXPERT.getStatLevel());
        assertEquals(expected, match.getPlayer(TeamSide.RIGHT).getMaxLife(), 1e-9);
        assertEquals(Player.BASE_LIFE, match.getPlayer(TeamSide.LEFT).getMaxLife(), 1e-9);
    }

    /** Only a paused match should be saved. */
    @Test
    void shouldSaveOnlyAPausedMatch() throws DOPOSoccerException {
        game.startMatch(builder(GameMode.PLAYER_VS_PLAYER));
        try {
            game.saveMatch(1);
            fail("DOPOSoccerException was expected");
        } catch (DOPOSoccerException e) {
            assertEquals(DOPOSoccerException.SAVE_ONLY_PAUSED, e.getMessage());
        }
        assertFalse(game.hasSavedMatch(1));
    }

    /** A saved match should open again, paused, with its score, even after closing the game. */
    @Test
    void savedMatchShouldOpenAgainAfterClosingTheGame() throws DOPOSoccerException {
        game.startMatch(builder(GameMode.PLAYER_VS_MACHINE).rules(MatchType.GOALS, 5));
        game.getMatch().goalScored(TeamSide.LEFT);
        game.togglePause();
        game.saveMatch(2);
        DOPOSoccer reopened = new DOPOSoccer(new MatchSlots(slotsFile));
        assertTrue(reopened.hasSavedMatch(2));
        assertFalse(reopened.hasSavedMatch(1));
        assertEquals("JvM (Defensiva) · Por goles · 1 - 0", reopened.getSavedMatchLabel(2));
        reopened.loadMatch(2, new HashMap<>());
        assertTrue(reopened.isPaused());
        assertEquals(1, reopened.snapshot().getGoals(TeamSide.LEFT));
        assertEquals(Country.USA, reopened.getConfig().getCountry(TeamSide.RIGHT));
    }

    /** The machine of an opened match should play again. */
    @Test
    void machineOfAnOpenedMatchShouldPlay() throws DOPOSoccerException {
        game.startMatch(builder(GameMode.PLAYER_VS_MACHINE).rules(MatchType.GOALS, 5));
        game.togglePause();
        game.saveMatch(3);
        game.loadMatch(3, new HashMap<>());
        game.togglePause();
        double startX = game.getMatch().getPlayer(TeamSide.RIGHT).getX();
        for (int i = 0; i < 180; i++) {
            game.update(FRAME);
        }
        assertTrue(game.getMatch().getPlayer(TeamSide.RIGHT).getX() != startX);
    }

    /** Playing an opened match should not change the copy kept in the slot. */
    @Test
    void slotShouldKeepItsOwnCopy() throws DOPOSoccerException {
        game.startMatch(builder(GameMode.PLAYER_VS_PLAYER).rules(MatchType.GOALS, 5));
        game.togglePause();
        game.saveMatch(1);
        game.loadMatch(1, new HashMap<>());
        game.getMatch().goalScored(TeamSide.RIGHT);
        game.loadMatch(1, new HashMap<>());
        assertEquals(0, game.snapshot().getGoals(TeamSide.RIGHT));
    }

    /** Opening an empty or a missing slot should give a clear message. */
    @Test
    void shouldRejectEmptyAndMissingSlots() {
        try {
            game.loadMatch(1, new HashMap<>());
            fail("DOPOSoccerException was expected");
        } catch (DOPOSoccerException e) {
            assertEquals(DOPOSoccerException.EMPTY_SLOT, e.getMessage());
        }
        try {
            game.loadMatch(4, new HashMap<>());
            fail("DOPOSoccerException was expected");
        } catch (DOPOSoccerException e) {
            assertEquals(DOPOSoccerException.INVALID_SLOT, e.getMessage());
        }
    }

    /** A rematch should repeat the options of the match that ended. */
    @Test
    void rematchShouldRepeatTheOptions() throws DOPOSoccerException {
        game.startMatch(builder(GameMode.PLAYER_VS_MACHINE).machine(MachineProfile.EXPERT));
        game.startMatch(new MatchBuilder().copy(game.getConfig()));
        assertEquals(MachineProfile.EXPERT, game.getConfig().getMachine());
        assertEquals(Country.USA, game.getConfig().getCountry(TeamSide.RIGHT));
    }

    /** A saved match that finishes should leave its slots, so its end cannot give points twice. */
    @Test
    void finishedMatchShouldLeaveItsSlots() throws DOPOSoccerException {
        game.startMatch(builder(GameMode.PLAYER_VS_PLAYER));
        game.togglePause();
        game.saveMatch(1);
        game.saveMatch(3);
        game.loadMatch(1, new HashMap<>());
        game.togglePause();
        game.getMatch().goalScored(TeamSide.LEFT);
        game.update(FRAME);
        assertTrue(game.hasWinner());
        assertFalse(game.hasSavedMatch(1));
        assertFalse(game.hasSavedMatch(3));
        assertFalse(new DOPOSoccer(new MatchSlots(slotsFile)).hasSavedMatch(1));
    }

    /** A saved match that the players end should stay saved, because it gives no points. */
    @Test
    void abandonedMatchShouldStaySaved() throws DOPOSoccerException {
        game.startMatch(builder(GameMode.PLAYER_VS_PLAYER));
        game.togglePause();
        game.saveMatch(2);
        game.endMatch();
        game.update(FRAME);
        assertTrue(game.hasSavedMatch(2));
    }
}
