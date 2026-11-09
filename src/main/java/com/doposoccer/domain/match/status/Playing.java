package com.doposoccer.domain.match.status;
import com.doposoccer.domain.enums.MatchStatus;

/**
 * The match is being played
 */
public class Playing extends ActiveStatus {

    @Override
    public String getLabel() {
        return "En juego";
    }

    @Override
    public boolean advancesTime() {
        return true;
    }

    @Override
    public MatchStatus togglePause() {
        return MatchStatus.PAUSED;
    }
}
