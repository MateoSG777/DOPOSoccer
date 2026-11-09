package com.doposoccer.domain.match;
import com.doposoccer.domain.DOPOSoccerException;
import com.doposoccer.domain.control.ControlStrategy;
import com.doposoccer.domain.enums.MatchStatus;
import com.doposoccer.domain.enums.TeamSide;
import com.doposoccer.domain.match.status.MatchStatusBehavior;
import com.doposoccer.domain.model.Ball;
import com.doposoccer.domain.model.Field;
import com.doposoccer.domain.model.GameObject;
import com.doposoccer.domain.model.Goal;
import com.doposoccer.domain.model.Player;
import com.doposoccer.domain.model.RenderState;
import com.doposoccer.domain.model.TeamSideInfo;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A DOPOSoccer match contains field objects, score and clock. It is the facade of the
 * domain for the presentation layer. Its public methods are synchronized
 * because the game loop thread and the Swing thread use the match at the same time
 */
public class Match implements GoalListener {

    /** Points earned by the winner of a match. */
    public static final int WINNER_POINTS = 100;

    /** Points earned by the loser of a match. */
    public static final int LOSER_POINTS = 25;

    private final MatchConfig config;
    private final Scoreboard scoreboard;
    private final MatchClock clock;
    private final Ball ball;
    private final Map<TeamSide, Player> players = new HashMap<>();
    private final List<GameObject> entities = new ArrayList<>();
    private MatchStatus status;

    /**
     * Creates the match: players, goals and ball, all dropping from the center
     * @param config validated configuration
     */
    public Match(MatchConfig config) {
        this.config = config;
        this.scoreboard = new Scoreboard();
        this.clock = MatchTypeInfo.of(config.getMatchType()).createClock(config.getTarget());
        this.ball = new Ball(Field.centerX(), Field.KICKOFF_Y);

        TeamSide[] sides = TeamSide.values();
        for (int i = 0; i < sides.length; i++) {
            double spawnX = TeamSideInfo.of(sides[i]).spawnX();
            Player player = new Player(config.getCountry(sides[i]), sides[i], spawnX, Field.KICKOFF_Y);
            players.put(sides[i], player);
            entities.add(player);
            entities.add(new Goal(sides[i], this));
        }
        entities.add(ball);
        status = MatchStatus.PLAYING;
        kickoff();
    }

    /**
     * Assigns who controls a player.
     * @param side side of the player
     * @param control control strategy
     */
    public void setControl(TeamSide side, ControlStrategy control) {
        players.get(side).setControl(control);
    }

    /**
     * Advances the match: controls, physics, clock, collisions and end condition
     * Does nothing if the match is paused or over
     * @param dt seconds elapsed since the last frame
     */
    public synchronized void update(double dt) {
        if (!MatchStatusBehavior.get(status).advancesTime()) {
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
        clock.tick(dt);
        for (int i = 0; i < entities.size(); i++) {
            entities.get(i).checkCollision(ball);
        }
        checkEnd();
    }

    /**
     * A goal reports that the ball got in, hence the score is updated and play restarts from the center
     * @param scorer side of the player who scored
     */
    @Override
    public void goalScored(TeamSide scorer) {
        scoreboard.addGoal(scorer);
        kickoff();
        checkEnd();
    }

    private void kickoff() {
        ball.resetAt(Field.centerX(), Field.KICKOFF_Y);
        TeamSide[] sides = TeamSide.values();
        for (int i = 0; i < sides.length; i++) {
            players.get(sides[i]).resetAt(TeamSideInfo.of(sides[i]).spawnX(), Field.KICKOFF_Y);
        }
    }

    private void checkEnd() {
        if (MatchTypeInfo.of(config.getMatchType()).isFinished(scoreboard, clock, config.getTarget())) {
            status = MatchStatus.FINISHED;
        }
    }

    /**
     * Pauses a running match or resumes a paused one
     * @throws DOPOSoccerException if the match is already over
     */
    public synchronized void togglePause() throws DOPOSoccerException {
        status = MatchStatusBehavior.get(status).togglePause();
    }

    /**
     * The players end the match at any moment. Nobody wins and nobody earns
     * points, so ending early cannot be used to farm points
     * @throws DOPOSoccerException if the match is already over
     */
    public synchronized void end() throws DOPOSoccerException {
        status = MatchStatusBehavior.get(status).end();
    }

    /**
     * Tells whether the match finished by its rules and there is a winner
     * @return true if the match finished by its rules and there is a winner
     */
    public synchronized boolean hasWinner() {
        return MatchStatusBehavior.get(status).hasResult() && !scoreboard.isTied();
    }

    /**
     * Returns the winner of a match that finished by its rules
     * @return side of the winner
     * @throws DOPOSoccerException if the match is not over or was ended by the players
     */
    public synchronized TeamSide getWinner() throws DOPOSoccerException {
        if (!MatchStatusBehavior.get(status).isOver()) {
            throw new DOPOSoccerException(DOPOSoccerException.MATCH_NOT_FINISHED);
        }
        if (!MatchStatusBehavior.get(status).hasResult()) {
            throw new DOPOSoccerException(DOPOSoccerException.MATCH_ABANDONED);
        }
        return scoreboard.getLeader();
    }

    /**
     * Points earned in this match: 100 for the winner and 25 for the loser
     * A match ended by the players gives no points
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
     * Takes a picture of everything the screen needs to draw one frame, in a single synchronized call
     * @return the current state of the match, safe to use from the Swing thread
     */
    public synchronized MatchSnapshot snapshot() {
        List<RenderState> states = new ArrayList<>(entities.size());
        for (int i = 0; i < entities.size(); i++) {
            states.add(entities.get(i).getRenderState());
        }
        return new MatchSnapshot(scoreboard.copyOfGoals(), clock.format(), status,
                MatchTypeInfo.of(config.getMatchType()).isSuddenDeath(scoreboard, clock), states);
    }

    /**
     * Returns the current status of the match
     * @return current status of the match
     */
    public synchronized MatchStatus getStatus() {
        return status;
    }

    /**
     * Returns the configuration chosen by the users
     * @return configuration chosen by the users
     */
    public MatchConfig getConfig() {
        return config;
    }

    /**
     * Returns the player of one side
     * @param side side of the player
     * @return the player on that side
     */
    public Player getPlayer(TeamSide side) {
        return players.get(side);
    }

    /**
     * Returns the ball
     * @return the ball
     */
    public Ball getBall() {
        return ball;
    }
}