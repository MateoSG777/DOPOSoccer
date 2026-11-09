package com.doposoccer.domain.model;
import com.doposoccer.domain.enums.TeamSide;

/**
 * Right side of the field: player two, attacking to the left
 */
class RightSide extends TeamSideInfo {

    @Override
    public String getLabel() {
        return "Jugador 2";
    }

    @Override
    public int getDirection() {
        return -1;
    }

    @Override
    public TeamSide opponent() {
        return TeamSide.LEFT;
    }

    @Override
    protected double spawnFraction() {
        return 0.75;
    }
}
