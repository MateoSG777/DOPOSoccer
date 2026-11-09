package com.doposoccer.domain.match;
import com.doposoccer.domain.enums.TeamSide;

/**
 * Implemented by whoever needs to know when a goal is scored
 */
public interface GoalListener {

    /**
     * Called by a goal when the ball gets in
     * @param scorer side of the player who scored
     */
    void goalScored(TeamSide scorer);
}