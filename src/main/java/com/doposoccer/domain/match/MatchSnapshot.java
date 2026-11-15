package com.doposoccer.domain.match;
import com.doposoccer.domain.enums.MatchStatus;
import com.doposoccer.domain.enums.TeamSide;
import com.doposoccer.domain.model.RenderState;
import java.util.List;
import java.util.Map;

/**
 * Picture of a match at one moment. Everything the screen needs to draw a frame
 * It is taken while the match is locked, so the screen can read it without
 * racing against the game loop
 */
public final class MatchSnapshot {

    private final Map<TeamSide, Integer> score;
    private final String clockText;
    private final MatchStatus status;
    private final boolean suddenDeath;
    private final List<RenderState> renderStates;
    private final Map<TeamSide, Double> life;
    private final Map<TeamSide, Double> power;

    /**
     * Creates a snapshot
     * @param score goals of each side
     * @param clockText clock in mm:ss format
     * @param status status of the match
     * @param suddenDeath  true if the next goal wins
     * @param renderStates what must be drawn, in drawing order
     * @param life fraction of life of each side, between 0 and 1
     * @param power fraction of the power bar of each side, between 0 and 1
     */
    public MatchSnapshot(Map<TeamSide, Integer> score, String clockText, MatchStatus status,
                         boolean suddenDeath, List<RenderState> renderStates,
                         Map<TeamSide, Double> life, Map<TeamSide, Double> power) {
        this.score = score;
        this.clockText = clockText;
        this.status = status;
        this.suddenDeath = suddenDeath;
        this.renderStates = renderStates;
        this.life = life;
        this.power = power;
    }

    /**
     * Returns the life of one side
     * @param side side to query
     * @return a value between 0 and 1
     */
    public double getLife(TeamSide side) {
        return life.get(side);
    }

    /**
     * Returns the charge of the power bar of one side
     * @param side side to query
     * @return a value between 0 and 1
     */
    public double getPower(TeamSide side) {
        return power.get(side);
    }

    /**
     * Returns the goals of one side
     * @param side side to query
     * @return goals scored by that side
     */
    public int getGoals(TeamSide side) {
        return score.get(side);
    }

    /**
     * Returns the clock in mm:ss format
     * @return the clock text
     */
    public String getClockText() {
        return clockText;
    }

    /**
     * Returns the status of the match
     * @return the status
     */
    public MatchStatus getStatus() {
        return status;
    }

    /**
     * Tells whether the next goal wins
     * @return true in sudden death
     */
    public boolean isSuddenDeath() {
        return suddenDeath;
    }

    /**
     * Returns what must be drawn, in drawing order
     * @return the render states
     */
    public List<RenderState> getRenderStates() {
        return renderStates;
    }
}
