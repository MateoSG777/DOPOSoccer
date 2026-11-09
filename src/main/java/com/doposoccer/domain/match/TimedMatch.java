package com.doposoccer.domain.match;

/**
 * Timed match. Whoever has more goals when time runs out wins
 * If it is tied, it goes to sudden death: the next goal wins, no matter how long it takes
 */
public class TimedMatch extends MatchTypeInfo {

    private static final int SECONDS_PER_MINUTE = 60;

    @Override
    public String getDisplayName() {
        return "Por tiempo";
    }

    @Override
    public String getTargetLabel() {
        return "Minutos";
    }

    @Override
    public int getDefaultTarget() {
        return 3;
    }

    @Override
    public MatchClock createClock(int target) {
        return MatchClock.countdown(target * SECONDS_PER_MINUTE);
    }

    @Override
    public boolean isFinished(Scoreboard scoreboard, MatchClock clock, int target) {
        return clock.isExpired() && !scoreboard.isTied();
    }

    @Override
    public boolean isSuddenDeath(Scoreboard scoreboard, MatchClock clock) {
        return clock.isExpired() && scoreboard.isTied();
    }
}
