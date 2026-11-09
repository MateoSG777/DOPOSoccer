package com.doposoccer.domain.model;

import com.doposoccer.domain.enums.TeamSide;
import com.doposoccer.domain.model.Field;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests of TeamSide.
 */
class TeamSideTest {

    /** Each side should know its opponent. */
    @Test
    void eachSideShouldKnowItsOpponent() {
        assertEquals(TeamSide.RIGHT, TeamSideInfo.of(TeamSide.LEFT).opponent());
        assertEquals(TeamSide.LEFT, TeamSideInfo.of(TeamSide.RIGHT).opponent());
    }

    /** Each side should defend the border behind its goal. */
    @Test
    void eachSideShouldDefendItsBorder() {
        assertEquals(0, TeamSideInfo.of(TeamSide.LEFT).borderX(), 1e-9);
        assertEquals(Field.WIDTH, TeamSideInfo.of(TeamSide.RIGHT).borderX(), 1e-9);
    }

    /** The goal line should be one goal depth inside the field. */
    @Test
    void goalLineShouldBeOneGoalDepthInside() {
        assertEquals(Field.GOAL_DEPTH, TeamSideInfo.of(TeamSide.LEFT).goalLineX(), 1e-9);
        assertEquals(Field.WIDTH - Field.GOAL_DEPTH, TeamSideInfo.of(TeamSide.RIGHT).goalLineX(), 1e-9);
    }

    /** Players should start in the middle of their own half. */
    @Test
    void playersShouldStartInTheirOwnHalf() {
        assertEquals(Field.WIDTH / 4, TeamSideInfo.of(TeamSide.LEFT).spawnX(), 1e-9);
        assertEquals(Field.WIDTH * 3 / 4, TeamSideInfo.of(TeamSide.RIGHT).spawnX(), 1e-9);
    }
}
