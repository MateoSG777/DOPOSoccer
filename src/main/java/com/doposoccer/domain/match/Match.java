package com.doposoccer.domain.match;

import com.doposoccer.domain.DOPOSoccerException;
import com.doposoccer.domain.control.ControlStrategy;
import com.doposoccer.domain.enums.MatchStatus;
import com.doposoccer.domain.enums.TeamSide;
import com.doposoccer.domain.model.Ball;
import com.doposoccer.domain.model.Field;
import com.doposoccer.domain.model.GameObject;
import com.doposoccer.domain.model.Goal;
import com.doposoccer.domain.model.Player;
import com.doposoccer.domain.model.RenderState;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * A DOPOSoccer match: field objects, score and clock. It is the facade of the
 * domain for the presentation layer. update and the query methods are
 * synchronized because the game loop thread and the Swing thread use the
 * match at the same time.
 */
public class Match implements GoalListener {

    /** Points earned by the winner of a match. */
    public static final int WINNER_POINTS = 100;

    /** Points earned by the loser of a match. */
    public static final int LOSER_POINTS = 25;

    /** Starting x of player 1. */
    public static final double LEFT_SPAWN_X = Field.WIDTH * 0.25;

    /** Starting x of player 2. */
    public static final double RIGHT_SPAWN_X = Field.WIDTH * 0.75;

    /** Configuration chosen by the users. */
    private final MatchConfig config;

    /** Current score. */
    private final Scoreboard scoreboard;

    /** Match clock (countdown or stopwatch depending on the match type). */
    private final MatchClock clock;

    /** The ball: every collision is resolved against it. */
    private final Ball ball;

    /** Players indexed by side. */
    private final Map<TeamSide, Player> players = new EnumMap<>(TeamSide.class);

    /** Every object on the field, iterated each frame to update, collide and draw. */
    private final List<GameObject> entities = new ArrayList<>();

    /** Current status of the match. */
    private MatchStatus status;

    /**
     * Creates the match: players, goals and ball, all dropping from the center.
     *
     * @param config validated configuration
     */
    public Match(MatchConfig config) {
        this.config = config;
        this.scoreboard = new Scoreboard();
        this.clock = config.getMatchType().createClock(config.getTarget());
        this.ball = new Ball(Field.centerX(), Field.KICKOFF_Y);

        TeamSide[] sides = TeamSide.values();
        for (int i = 0; i < sides.length; i++) {
            Player player = new Player(config.getCountry(sides[i]), sides[i], spawnX(sides[i]), Field.KICKOFF_Y);
            players.put(sides[i], player);
            entities.add(player);
            entities.add(new Goal(sides[i], this));
        }
        entities.add(ball);
        status = MatchStatus.PLAYING;
        kickoff();
    }

    /**
     * Computes where a player starts after every goal.
     *
     * @param side side of the player
     * @return x where that player starts after every goal
     */
    private static double spawnX(TeamSide side) {
        if (side == TeamSide.LEFT) {
            return LEFT_SPAWN_X;
        }
        return RIGHT_SPAWN_X;
    }

    /**
     * Assigns who controls a player (keyboard now, machine in version two).
     *
     * @param side    side of the player
     * @param control control strategy
     */
    public void setControl(TeamSide side, ControlStrategy control) {
        players.get(side).setControl(control);
    }

    /**
     * Advances the match: controls, physics, collisions, clock and end condition.
     * Does nothing if the match is paused or over.
     *
     * @param dt seconds elapsed since the last frame
     */
    public synchronized void update(double dt) {
        if (!status.advancesTime()) {
            return;
        }
        Player left = players.get(TeamSide.LEFT);
        Player right = players.get(TeamSide.RIGHT);
        left.updateControl(ball, dt);
        right.updateControl(ball, dt);

        for (int i = 0; i < entities.size(); i++) {
            entities.get(i).update(dt);
        }
        left.separateFrom(right);
        for (int i = 0; i < entities.size(); i++) {
            entities.get(i).checkCollision(ball);
        }
        if (status.advancesTime()) {
            clock.tick(dt);
            checkEnd();
        }
    }

    /**
     * A goal reports that the ball got in: the score is updated and play
     * restarts from the center.
     *
     * @param scorer side of the player who scored
     */
    @Override
    public void goalScored(TeamSide scorer) {
        scoreboard.addGoal(scorer);
        kickoff();
        checkEnd();
    }

    /** Ball and players drop from the center of the field. */
    private void kickoff() {
        ball.resetAt(Field.centerX(), Field.KICKOFF_Y);
        TeamSide[] sides = TeamSide.values();
        for (int i = 0; i < sides.length; i++) {
            players.get(sides[i]).resetAt(spawnX(sides[i]), Field.KICKOFF_Y);
        }
    }

    /** Finishes the match if its type says the end condition was reached. */
    private void checkEnd() {
        if (config.getMatchType().isFinished(scoreboard, clock, config.getTarget())) {
            status = MatchStatus.FINISHED;
        }
    }

    /**
     * Pauses a running match or resumes a paused one.
     *
     * @throws DOPOSoccerException if the match is already over
     */
    public synchronized void togglePause() throws DOPOSoccerException {
        if (status.isOver()) {
            throw new DOPOSoccerException(DOPOSoccerException.MATCH_FINISHED);
        }
        if (status == MatchStatus.PLAYING) {
            status = MatchStatus.PAUSED;
        } else {
            status = MatchStatus.PLAYING;
        }
    }

    /**
     * The players end the match at any moment. Nobody wins and nobody earns
     * points, so ending early cannot be used to farm points.
     *
     * @throws DOPOSoccerException if the match is already over
     */
    public synchronized void end() throws DOPOSoccerException {
        if (status.isOver()) {
            throw new DOPOSoccerException(DOPOSoccerException.MATCH_FINISHED);
        }
        status = MatchStatus.ABANDONED;
    }

    /**
     * Tells whether the match finished by its rules and there is a winner.
     *
     * @return true if the match finished by its rules and there is a winner
     */
    public synchronized boolean hasWinner() {
        return status.hasResult() && !scoreboard.isTied();
    }

    /**
     * Returns the winner of a match that finished by its rules.
     *
     * @return side of the winner
     * @throws DOPOSoccerException if the match is not over or was ended by the players
     */
    public synchronized TeamSide getWinner() throws DOPOSoccerException {
        if (!status.isOver()) {
            throw new DOPOSoccerException(DOPOSoccerException.MATCH_NOT_FINISHED);
        }
        if (!status.hasResult()) {
            throw new DOPOSoccerException(DOPOSoccerException.MATCH_ABANDONED);
        }
        return scoreboard.getLeader();
    }

    /**
     * Points earned in this match: 100 for the winner and 25 for the loser.
     * A match ended by the players gives no points.
     *
     * @param side side of the player
     * @return points earned by that player
     */
    public synchronized int getPointsEarned(TeamSide side) {
        if (!hasWinner()) {
            return 0;
        }
        if (scoreboard.getLeader() == side) {
            return WINNER_POINTS;
        }
        return LOSER_POINTS;
    }

    /**
     * Returns a copy of what must be drawn, safe to use from the Swing thread.
     *
     * @return a copy of what must be drawn, safe to use from the Swing thread
     */
    public synchronized List<RenderState> getRenderStates() {
        List<RenderState> states = new ArrayList<>(entities.size());
        for (int i = 0; i < entities.size(); i++) {
            states.add(entities.get(i).getRenderState());
        }
        return states;
    }

    /**
     * Tells whether time is up, the score is tied and the next goal wins.
     *
     * @return true if time is up, the score is tied and the next goal wins
     */
    public synchronized boolean isSuddenDeath() {
        return config.getMatchType().isSuddenDeath(scoreboard, clock);
    }

    /**
     * Returns the goals scored by one side.
     *
     * @param side side to query
     * @return goals scored by that side
     */
    public synchronized int getGoals(TeamSide side) {
        return scoreboard.getGoals(side);
    }

    /**
     * Returns the clock in mm:ss format.
     *
     * @return the clock in mm:ss format
     */
    public synchronized String getClockText() {
        return clock.format();
    }

    /**
     * Returns the current status of the match.
     *
     * @return current status of the match
     */
    public synchronized MatchStatus getStatus() {
        return status;
    }

    /**
     * Returns the configuration chosen by the users.
     *
     * @return configuration chosen by the users
     */
    public MatchConfig getConfig() {
        return config;
    }

    /**
     * Returns the player of one side.
     *
     * @param side side of the player
     * @return the player on that side
     */
    public Player getPlayer(TeamSide side) {
        return players.get(side);
    }

    /**
     * Returns the ball.
     *
     * @return the ball
     */
    public Ball getBall() {
        return ball;
    }
}
