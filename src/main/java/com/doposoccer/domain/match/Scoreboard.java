package com.doposoccer.domain.match;
import com.doposoccer.domain.enums.TeamSide;
import java.util.HashMap;
import java.util.Map;

/**
 * Score of the match. It always starts 0 - 0
 */
public final class Scoreboard {

    private final Map<TeamSide, Integer> goals = new HashMap<>();

    public Scoreboard() {
        TeamSide[] sides = TeamSide.values();
        for (int i = 0; i < sides.length; i++) {
            goals.put(sides[i], 0);
        }
    }

    /**
     * Adds one goal
     * @param scorer side of the player who scored
     */
    public void addGoal(TeamSide scorer) {
        goals.put(scorer, goals.get(scorer) + 1);
    }

    /**
     * Returns the goals scored by one side
     * @param side side to query
     * @return goals scored by that side
     */
    public int getGoals(TeamSide side) {
        return goals.get(side);
    }

    /**
     * Tells whether both sides have the same number of goals
     * @return true if both sides have the same number of goals
     */
    public boolean isTied() {
        return getGoals(TeamSide.LEFT) == getGoals(TeamSide.RIGHT);
    }

    /**
     * @return the highest number of goals among both sides
     */
    public int getMaxGoals() {
        return Math.max(getGoals(TeamSide.LEFT), getGoals(TeamSide.RIGHT));
    }

    /**
     * @return the side that is winning; only meaningful when the score is not tied
     */
    public TeamSide getLeader() {
        if (getGoals(TeamSide.LEFT) > getGoals(TeamSide.RIGHT)) {
            return TeamSide.LEFT;
        }
        return TeamSide.RIGHT;
    }

    /**
     * Returns a copy of the goals of each side
     * @return goals indexed by side
     */
    public Map<TeamSide, Integer> copyOfGoals() {
        return new HashMap<>(goals);
    }
}
