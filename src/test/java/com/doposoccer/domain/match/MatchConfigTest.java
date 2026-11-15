package com.doposoccer.domain.match;

import com.doposoccer.domain.DOPOSoccerException;
import com.doposoccer.domain.enums.Country;
import com.doposoccer.domain.enums.GameMode;
import com.doposoccer.domain.enums.MachineProfile;
import com.doposoccer.domain.enums.MatchType;
import com.doposoccer.domain.enums.Resolution;
import com.doposoccer.domain.enums.TeamSide;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Unit tests of MatchConfig.
 */
class MatchConfigTest {

    /** Should create valid configuration. */
    @Test
    void shouldCreateValidConfiguration() throws DOPOSoccerException {
        Country korea = Country.KOREA;
        Country nigeria = Country.NIGERIA;
        MatchConfig config = new MatchConfig(GameMode.PLAYER_VS_PLAYER, Resolution.MEDIUM,
                korea, nigeria, MatchType.GOALS, 3, MachineProfile.CLUMSY);
        assertSame(korea, config.getCountry(TeamSide.LEFT));
        assertSame(nigeria, config.getCountry(TeamSide.RIGHT));
        assertEquals(3, config.getTarget());
    }

    /** In player vs machine only the player on the left should be human. */
    @Test
    void machineShouldDriveTheRightSideInPlayerVsMachine() throws DOPOSoccerException {
        MatchConfig config = new MatchConfig(GameMode.PLAYER_VS_MACHINE, Resolution.SMALL,
                Country.KOREA, Country.USA, MatchType.TIMED, 2, MachineProfile.EXPERT);
        assertTrue(config.isHuman(TeamSide.LEFT));
        assertFalse(config.isHuman(TeamSide.RIGHT));
        assertEquals(MachineProfile.EXPERT, config.getMachine());
        assertEquals("Jugador 1", config.getPlayerName(TeamSide.LEFT));
        assertEquals("Máquina (Exp)", config.getPlayerName(TeamSide.RIGHT));
    }

    /** Should reject missing data. */
    @Test
    void shouldRejectMissingData() {
        try {
            new MatchConfig(GameMode.PLAYER_VS_PLAYER, Resolution.SMALL,
                    null, Country.USA, MatchType.TIMED, 2, MachineProfile.CLUMSY);
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
                    Country.KOREA, Country.USA, MatchType.GOALS, 0, MachineProfile.CLUMSY);
            fail("DOPOSoccerException was expected");
        } catch (DOPOSoccerException e) {
            assertTrue(e.getMessage().startsWith(DOPOSoccerException.INVALID_TARGET));
        }
    }
}
