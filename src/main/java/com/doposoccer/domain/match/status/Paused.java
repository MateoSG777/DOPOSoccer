package com.doposoccer.domain.match.status;
import com.doposoccer.domain.enums.MatchStatus;

/**
 * The match is paused. So physics and clock are stopped
 */
public class Paused extends ActiveStatus {

    @Override
    public String getLabel() {
        return "Pausa";
    }

    @Override
    public boolean isPaused() {
        return true;
    }

    @Override
    public MatchStatus togglePause() {
        return MatchStatus.PLAYING;
    }
}
