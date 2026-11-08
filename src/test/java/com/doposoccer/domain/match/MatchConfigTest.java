package com.doposoccer.domain.match;

import com.doposoccer.domain.DOPOSoccerException;
import com.doposoccer.domain.enums.Country;
import com.doposoccer.domain.enums.GameMode;
import com.doposoccer.domain.enums.MatchType;
import com.doposoccer.domain.enums.Resolution;
import com.doposoccer.domain.enums.TeamSide;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Unit tests of MatchConfig.
 */
class MatchConfigTest {

    /** Should create valid configuration. */
    @Test
    void shouldCreateValidConfiguration() throws DOPOSoccerException {
        MatchConfig config = new MatchConfig(GameMode.PLAYER_VS_PLAYER, Resolution.MEDIUM,
                Country.KOREA, Country.NIGERIA, MatchType.BY_GOALS, 3);
        assertEquals(Country.KOREA, config.getCountry(TeamSide.LEFT));
        assertEquals(Country.NIGERIA, config.getCountry(TeamSide.RIGHT));
        assertEquals(3, config.getTarget());
    }

    /** Should reject player vs machine in this version. */
    @Test
    void shouldRejectPlayerVsMachineInThisVersion() {
        try {
            new MatchConfig(GameMode.PLAYER_VS_MACHINE, Resolution.SMALL,
                    Country.KOREA, Country.USA, MatchType.BY_TIME, 2);
            fail("DOPOSoccerException was expected");
        } catch (DOPOSoccerException e) {
            assertEquals(DOPOSoccerException.MODE_NOT_AVAILABLE, e.getMessage());
        }
    }

    /** Should reject missing data. */
    @Test
    void shouldRejectMissingData() {
        try {
            new MatchConfig(GameMode.PLAYER_VS_PLAYER, Resolution.SMALL,
                    null, Country.USA, MatchType.BY_TIME, 2);
            fail("DOPOSoccerException was expected");
        } catch (DOPOSoccerException e) {
            assertEquals(DOPOSoccerException.NULL_CONFIGURATION, e.getMessage());
        }
    }

    /** Should reject target out of range. */
    @Test
    void shouldRejectTargetOutOfRange() {
        try {
            new MatchConfig(GameMode.PLAYER_VS_PLAYER, Resolution.LARGE,
                    Country.KOREA, Country.USA, MatchType.BY_GOALS, 0);
            fail("DOPOSoccerException was expected");
        } catch (DOPOSoccerException e) {
            assertTrue(e.getMessage().startsWith(DOPOSoccerException.INVALID_TARGET));
        }
    }
}
