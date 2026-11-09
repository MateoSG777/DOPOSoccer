package com.doposoccer.domain.match.status;
import com.doposoccer.domain.DOPOSoccerException;
import com.doposoccer.domain.enums.MatchStatus;

/**
 * Status of a match when it's over. So it cannot be paused or ended again
 */
public abstract class OverStatus extends MatchStatusBehavior {

    @Override
    public boolean isOver() {
        return true;
    }

    @Override
    public MatchStatus togglePause() throws DOPOSoccerException {
        throw new DOPOSoccerException(DOPOSoccerException.MATCH_FINISHED);
    }

    @Override
    public MatchStatus end() throws DOPOSoccerException {
        throw new DOPOSoccerException(DOPOSoccerException.MATCH_FINISHED);
    }
}
