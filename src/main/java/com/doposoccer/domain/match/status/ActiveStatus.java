package com.doposoccer.domain.match.status;
import com.doposoccer.domain.enums.MatchStatus;

/**
 * Status of a match that is not over yet. It can be paused, resumed or ended by the users
 */
public abstract class ActiveStatus extends MatchStatusBehavior {

    @Override
    public boolean isOver() {
        return false;
    }

    @Override
    public MatchStatus end() {
        return MatchStatus.ABANDONED;
    }
}
