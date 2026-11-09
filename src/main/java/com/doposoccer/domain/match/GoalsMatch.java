package com.doposoccer.domain.match;

/**
 * Match by goals. The first player that reaches the target wins
 * The clock only counts up
 */
public class GoalsMatch extends MatchTypeInfo {

    @Override
    public String getDisplayName() {
        return "Por goles";
    }

    @Override
    public String getTargetLabel() {
        return "Goles para ganar";
    }

    @Override
    public int getDefaultTarget() {
        return 5;  // default target
    }

    @Override
    public MatchClock createClock(int target) {
        return MatchClock.stopwatch();
    }

    @Override
    public boolean isFinished(Scoreboard scoreboard, MatchClock clock, int target) {
        return scoreboard.getMaxGoals() >= target;
    }

    @Override
    public boolean isSuddenDeath(Scoreboard scoreboard, MatchClock clock) {
        return false;
    }
}