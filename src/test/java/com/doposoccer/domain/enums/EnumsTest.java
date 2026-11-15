package com.doposoccer.domain.enums;

import com.doposoccer.domain.DOPOSoccerException;
import com.doposoccer.domain.model.Field;
import com.doposoccer.domain.model.Player;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Unit tests of the enums that carry data and behavior.
 */
class EnumsTest {

    /** A stunned player should not be able to do anything. */
    @Test
    void stunnedShouldForbidEverything() {
        assertFalse(PlayerState.STUNNED.canMove());
        assertFalse(PlayerState.STUNNED.canJump());
        assertFalse(PlayerState.STUNNED.canKick());
        assertTrue(PlayerState.JUMPING.canKick());
        assertFalse(PlayerState.JUMPING.canJump());
    }

    /** Pause should switch between playing and paused. */
    @Test
    void pauseShouldSwitchBetweenPlayingAndPaused() throws DOPOSoccerException {
        assertEquals(MatchStatus.PAUSED, MatchStatus.PLAYING.togglePause());
        assertEquals(MatchStatus.PLAYING, MatchStatus.PAUSED.togglePause());
        assertEquals(MatchStatus.ABANDONED, MatchStatus.PAUSED.end());
    }

    /** A match that is over should not change its status. */
    @Test
    void overStatusShouldNotChange() {
        try {
            MatchStatus.FINISHED.end();
            fail("DOPOSoccerException was expected");
        } catch (DOPOSoccerException e) {
            assertEquals(DOPOSoccerException.MATCH_FINISHED, e.getMessage());
        }
        assertTrue(MatchStatus.FINISHED.hasResult());
        assertFalse(MatchStatus.ABANDONED.hasResult());
        assertTrue(MatchStatus.ABANDONED.isOver());
    }

    /** Each action should give its order to the player. */
    @Test
    void actionShouldGiveItsOrderToThePlayer() {
        Player player = new Player(Country.KOREA, TeamSide.LEFT, 300, Field.GROUND_Y - Player.HEIGHT / 2);
        player.update(0.01);
        PlayerAction.KICK.apply(player);
        assertTrue(player.isKicking());
        PlayerAction.MOVE_RIGHT.apply(player);
        player.update(0.1);
        assertTrue(player.getX() > 300);
    }

    /** The cost of an upgrade should double at every level. */
    @Test
    void upgradeCostShouldDoubleAtEveryLevel() {
        assertEquals(500, Stat.cost(1));
        assertEquals(1000, Stat.cost(2));
        assertEquals(2000, Stat.cost(3));
        assertEquals(1.0, Stat.multiplier(Stat.MIN_LEVEL), 1e-9);
        assertEquals(1.4, Stat.multiplier(5), 1e-9);
        assertEquals(1.9, Stat.multiplier(Stat.MAX_LEVEL), 1e-9);
    }

    /** A harder machine should have stronger stats. */
    @Test
    void harderMachineShouldHaveStrongerStats() {
        assertEquals(1, MachineProfile.CLUMSY.getStatLevel());
        assertTrue(MachineProfile.OFFENSIVE.getStatLevel() > MachineProfile.CLUMSY.getStatLevel());
        assertTrue(MachineProfile.EXPERT.getStatLevel() > MachineProfile.DEFENSIVE.getStatLevel());
        assertEquals(MachineProfile.EXPERT.getStatLevel(), MachineProfile.EXPERT.createProfile().getLevel(Stat.KICK));
    }

    /** The saved matches should show a short name of the mode. */
    @Test
    void modeShouldHaveAShortName() {
        assertEquals("JvJ", GameMode.PLAYER_VS_PLAYER.shortName(MachineProfile.EXPERT));
        assertEquals("JvM (Experta)", GameMode.PLAYER_VS_MACHINE.shortName(MachineProfile.EXPERT));
    }

    /** Every profile of the machine should create its own strategy. */
    @Test
    void everyMachineProfileShouldCreateItsStrategy() {
        MachineProfile[] profiles = MachineProfile.values();
        for (int i = 0; i < profiles.length; i++) {
            assertNotNull(profiles[i].createControl(), profiles[i].getDisplayName());
        }
    }

    /** Every country should have its name, its sprites and its power. */
    @Test
    void everyCountryShouldHaveItsData() {
        Country[] countries = Country.values();
        for (int i = 0; i < countries.length; i++) {
            assertFalse(countries[i].getDisplayName().isEmpty());
            assertFalse(countries[i].getSpriteKey().isEmpty());
            assertFalse(countries[i].getPowerName().isEmpty());
            assertTrue(countries[i].getBaseDamage() > 0);
        }
    }

    /** Resolutions should describe their size. */
    @Test
    void resolutionShouldDescribeItsSize() {
        assertEquals("1024 x 640", Resolution.MEDIUM.getDisplayName());
        assertTrue(GameMode.PLAYER_VS_MACHINE.hasMachine());
        assertFalse(GameMode.PLAYER_VS_PLAYER.hasMachine());
    }
}
