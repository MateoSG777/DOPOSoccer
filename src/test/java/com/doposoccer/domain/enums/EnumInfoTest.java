package com.doposoccer.domain.enums;

import com.doposoccer.domain.action.PlayerActionBehavior;
import com.doposoccer.domain.ai.MachineProfileBehavior;
import com.doposoccer.domain.country.CountryInfo;
import com.doposoccer.domain.match.GameModeInfo;
import com.doposoccer.domain.match.MatchTypeInfo;
import com.doposoccer.domain.match.ResolutionInfo;
import com.doposoccer.domain.match.status.MatchStatusBehavior;
import com.doposoccer.domain.model.TeamSideInfo;
import com.doposoccer.domain.state.PlayerStateBehavior;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Checks that every constant of the enums has the class that gives it data or behavior,
 * so no lookup returns null.
 */
public class EnumInfoTest {

    /** Every country has its data. */
    @Test
    public void everyCountryHasInfo() {
        for (Country country : Country.values()) {
            assertNotNull(CountryInfo.of(country));
        }
    }

    /** Every game mode has its data. */
    @Test
    public void everyGameModeHasInfo() {
        for (GameMode mode : GameMode.values()) {
            assertNotNull(GameModeInfo.of(mode));
        }
    }

    /** Every machine profile has its behavior. */
    @Test
    public void everyMachineProfileHasBehavior() {
        for (MachineProfile profile : MachineProfile.values()) {
            assertNotNull(MachineProfileBehavior.of(profile));
        }
    }

    /** Every match status has its behavior. */
    @Test
    public void everyMatchStatusHasBehavior() {
        for (MatchStatus status : MatchStatus.values()) {
            assertNotNull(MatchStatusBehavior.of(status));
        }
    }

    /** Every match type has its rules. */
    @Test
    public void everyMatchTypeHasInfo() {
        for (MatchType type : MatchType.values()) {
            assertNotNull(MatchTypeInfo.of(type));
        }
    }

    /** Every player action has its behavior. */
    @Test
    public void everyPlayerActionHasBehavior() {
        for (PlayerAction action : PlayerAction.values()) {
            assertNotNull(PlayerActionBehavior.get(action));
        }
    }

    /** Every player state has its behavior. */
    @Test
    public void everyPlayerStateHasBehavior() {
        for (PlayerState state : PlayerState.values()) {
            assertNotNull(PlayerStateBehavior.of(state));
        }
    }

    /** Every resolution has its size. */
    @Test
    public void everyResolutionHasInfo() {
        for (Resolution resolution : Resolution.values()) {
            assertNotNull(ResolutionInfo.of(resolution));
        }
    }

    /** Every side has its geometry. */
    @Test
    public void everyTeamSideHasInfo() {
        for (TeamSide side : TeamSide.values()) {
            assertNotNull(TeamSideInfo.of(side));
        }
    }
}
