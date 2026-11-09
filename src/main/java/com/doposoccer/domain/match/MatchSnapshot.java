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

    /**
     * Creates a snapshot
     * @param score goals of each side
     * @param clockText clock in mm:ss format
     * @param status status of the match
     * @param suddenDeath  true if the next goal wins
     * @param renderStates what must be drawn, in drawing order
     */
    public MatchSnapshot(Map<TeamSide, Integer> score, String clockText, MatchStatus status,
                         boolean suddenDeath, List<RenderState> renderStates) {
        this.score = score;
        this.clockText = clockText;
        this.status = status;
        this.suddenDeath = suddenDeath;
        this.renderStates = renderStates;
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
