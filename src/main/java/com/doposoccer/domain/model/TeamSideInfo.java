package com.doposoccer.domain.model;

import com.doposoccer.domain.enums.TeamSide;

import java.util.EnumMap;
import java.util.Map;

/**
 * Geometry and label of a side of the field. Each concrete side knows its own direction,
 * so the rest of the game asks the side instead of checking which one it is
 * The instance of each side is found with {@link #get}
 */
public abstract class TeamSideInfo {
    private static final Map<TeamSide, TeamSideInfo> BY_SIDE = new EnumMap<>(TeamSide.class);
    static {
        BY_SIDE.put(TeamSide.LEFT, new LeftSide());
        BY_SIDE.put(TeamSide.RIGHT, new RightSide());
    }

    /**
     * Returns the information of a side
     * @param side the side
     * @return its information
     */
    public static TeamSideInfo of(TeamSide side) {
        return BY_SIDE.get(side);
    }

    /**
     * Returns the name shown to the users
     * @return the label of the side
     */
    public abstract String getLabel();

    /**
     * Returns the direction in which this side attacks: 1 to the right, -1 to the left
     * @return the attack direction
     */
    public abstract int getDirection();

    /**
     * Returns the rival side.
     * @return the opponent
     */
    public abstract TeamSide opponent();

    /**
     * Returns the fraction of the field width where the player of this side starts
     * @return a value between 0 and 1
     */
    protected abstract double spawnFraction();

    /**
     * Returns the x where the player of this side starts
     * @return starting x
     */
    public double spawnX() {
        return Field.WIDTH * spawnFraction();
    }

    /**
     * Returns the x of the border behind the goal of this side
     * @return x of the border
     */
    public double borderX() {
        return Field.WIDTH * (1 - getDirection()) / 2;
    }

    /**
     * Returns the x of the goal line, one goal depth inside the field
     * @return x of the goal line
     */
    public double goalLineX() {
        return borderX() + getDirection() * Field.GOAL_DEPTH;
    }
}
