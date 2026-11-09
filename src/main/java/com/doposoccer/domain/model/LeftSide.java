package com.doposoccer.domain.model;

import com.doposoccer.domain.enums.TeamSide;

/**
 * Left side of the field: player one, attacking to the right
 */
class LeftSide extends TeamSideInfo {

    @Override
    public String getLabel() {
        return "Jugador 1";
    }

    @Override
    public int getDirection() {
        return 1;
    }

    @Override
    public TeamSide opponent() {
        return TeamSide.RIGHT;
    }

    @Override
    protected double spawnFraction() {
        return 0.25;
    }
}
