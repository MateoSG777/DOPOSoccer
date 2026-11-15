package com.doposoccer.domain.shop;

import com.doposoccer.domain.DOPOSoccerException;
import com.doposoccer.domain.enums.Stat;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Unit tests of PlayerProfile.
 */
class PlayerProfileTest {

    /** A new profile should have no points and every stat at the first level. */
    @Test
    void newProfileShouldStartAtTheFirstLevel() {
        PlayerProfile profile = new PlayerProfile();
        assertEquals(0, profile.getPoints());
        assertEquals(Stat.MIN_LEVEL, profile.getLevel(Stat.SURVIVAL));
        assertEquals(1.0, profile.multiplier(Stat.KICK), 1e-9);
    }

    /** An upgrade should cost points and raise the level. */
    @Test
    void upgradeShouldCostPointsAndRaiseTheLevel() throws DOPOSoccerException {
        PlayerProfile profile = new PlayerProfile();
        profile.addPoints(1600);
        profile.upgrade(Stat.KICK);
        profile.upgrade(Stat.KICK);
        assertEquals(3, profile.getLevel(Stat.KICK));
        assertEquals(100, profile.getPoints());
        assertEquals(2000, profile.getCost(Stat.KICK));
        assertEquals(1.2, profile.multiplier(Stat.KICK), 1e-9);
    }

    /** Should reject an upgrade without enough points. */
    @Test
    void shouldRejectUpgradeWithoutEnoughPoints() {
        PlayerProfile profile = new PlayerProfile();
        profile.addPoints(499);
        try {
            profile.upgrade(Stat.SPEED);
            fail("DOPOSoccerException was expected");
        } catch (DOPOSoccerException e) {
            assertEquals(DOPOSoccerException.NOT_ENOUGH_POINTS, e.getMessage());
        }
        assertEquals(499, profile.getPoints());
    }

    /** Should reject an upgrade beyond the last level. */
    @Test
    void shouldRejectUpgradeBeyondTheLastLevel() throws DOPOSoccerException {
        PlayerProfile profile = new PlayerProfile();
        profile.addPoints(1000000);
        for (int i = Stat.MIN_LEVEL; i < Stat.MAX_LEVEL; i++) {
            profile.upgrade(Stat.JUMP);
        }
        assertTrue(profile.isMaxed(Stat.JUMP));
        try {
            profile.upgrade(Stat.JUMP);
            fail("DOPOSoccerException was expected");
        } catch (DOPOSoccerException e) {
            assertEquals(DOPOSoccerException.MAX_LEVEL, e.getMessage());
        }
    }
}
