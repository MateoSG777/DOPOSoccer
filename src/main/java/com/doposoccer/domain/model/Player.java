package com.doposoccer.domain.model;
import com.doposoccer.domain.control.ControlStrategy;
import com.doposoccer.domain.control.NoControl;
import com.doposoccer.domain.country.CountryInfo;
import com.doposoccer.domain.enums.Country;
import com.doposoccer.domain.enums.Figure;
import com.doposoccer.domain.enums.PlayerState;
import com.doposoccer.domain.enums.TeamSide;
import com.doposoccer.domain.state.PlayerStateBehavior;

/**
 * A player on the field, driven by its control strategy. It always faces the rival goal.
 */
public class Player extends GameObject {

    /** Width of the player's bounding box. */
    public static final double WIDTH = 64;

    /** Height of the player's bounding box. */
    public static final double HEIGHT = 90;

    /** Radius of the head, used to bounce the ball. */
    public static final double HEAD_RADIUS = 34;

    /** Half the width of the body, used to bounce the ball. */
    public static final double BODY_HALF_WIDTH = 18;

    /** Horizontal distance from the center to the hip of the kicking leg. */
    public static final double HIP_OFFSET_X = 26;

    /** Height of the foot center above the ground when the leg is at rest. */
    public static final double FOOT_REST_HEIGHT = 12;

    /** Distance from the hip to the foot center: the radius of the kick arc. */
    public static final double LEG_LENGTH = 32;

    /** How far the foot reaches when kicking. */
    public static final double FOOT_REACH = 14;

    /** Walking speed. */
    public static final double BASE_SPEED = 320;

    /** Initial vertical speed of a jump. */
    public static final double BASE_JUMP_SPEED = 650;

    /** Duration of a kick in seconds. */
    public static final double KICK_DURATION = 0.25;

    /** Horizontal speed given to the ball by a kick. */
    public static final double KICK_SPEED_X = 750;

    /** Vertical speed given to the ball by a kick. */
    public static final double KICK_SPEED_Y = -420;

    /** Minimum exit speed of the ball after a header. */
    public static final double HEADER_MIN_SPEED = 380;

    /** Highest angle of the kicking leg in degrees: 0 hangs down, 90 points straight at the rival. */
    public static final double KICK_FOOT_ANGLE = 90;

    private final Country country;
    private final TeamSide side;
    private ControlStrategy control;
    private PlayerState state;
    private double vx;
    private double vy;
    private int moveIntent;
    private boolean onGround;
    private double kickTimer;
    private boolean kickApplied;

    /**
     * Creates a player without control until one is assigned.
     *
     * @param country character chosen by the player
     * @param side side the player defends
     * @param x x coordinate of the center
     * @param y y coordinate of the center
     */
    public Player(Country country, TeamSide side, double x, double y) {
        super(x, y, WIDTH, HEIGHT);
        this.country = country;
        this.side = side;
        this.control = new NoControl();
        this.state = PlayerState.IDLE;
    }

    /**
     * Asks the control strategy which actions to perform in this frame
     * @param ball the ball of the match
     * @param dt   seconds elapsed since the last frame
     */
    public void updateControl(Ball ball, double dt) {
        moveIntent = 0;
        control.update(this, ball, dt);
    }

    /**
     * Requests walking in this frame, if the state allows it.
     * @param direction -1 to walk left, +1 to walk right
     */
    public void move(int direction) {
        if (PlayerStateBehavior.of(state).canMove()) {
            moveIntent = direction;
        }
    }

    /** Starts a jump if the player is on the ground and the state allows it */
    public void jump() {
        if (onGround && PlayerStateBehavior.of(state).canJump()) {
            vy = -BASE_JUMP_SPEED;
            onGround = false;
        }
    }

    /** Starts a kick if the state allows it */
    public void kick() {
        if (PlayerStateBehavior.of(state).canKick()) {
            kickTimer = KICK_DURATION;
            kickApplied = false;
            state = PlayerState.KICKING;
        }
    }

    /**
     * Moves the player, applies gravity, keeps it inside the field and updates its state
     * @param dt seconds elapsed since the last frame
     */
    @Override
    public void update(double dt) {
        vx = moveIntent * BASE_SPEED;
        x += vx * dt;
        x = Math.max(WIDTH / 2, Math.min(Field.WIDTH - WIDTH / 2, x));

        vy += Field.PLAYER_GRAVITY * dt;
        y += vy * dt;
        if (getBottom() >= Field.GROUND_Y) {
            y = Field.GROUND_Y - HEIGHT / 2;
            vy = 0;
            onGround = true;
        }

        if (kickTimer > 0) {
            kickTimer = Math.max(0, kickTimer - dt);
        }
        state = nextState();
    }

    private PlayerState nextState() {
        if (kickTimer > 0) {
            return PlayerState.KICKING;
        }
        if (!onGround) {
            return PlayerState.JUMPING;
        }
        if (moveIntent != 0) {
            return PlayerState.WALKING;
        }
        return PlayerState.IDLE;
    }

    /**
     * Kick: if the foot reaches the ball, it is pushed towards the rival goal
     * Otherwise the ball bounces on the head or the body
     * @param ball the ball of the match
     */
    @Override
    public void checkCollision(Ball ball) {
        if (isKicking() && !kickApplied && footReaches(ball)) {
            ball.kick(TeamSideInfo.of(side).getDirection() * KICK_SPEED_X, KICK_SPEED_Y);
            kickApplied = true;
            return;
        }
        ball.bounceOffCircle(getHeadX(), getHeadY(), HEAD_RADIUS, vx, vy, HEADER_MIN_SPEED);
        ball.bounceOffRect(x - BODY_HALF_WIDTH, getHeadY() + HEAD_RADIUS, x + BODY_HALF_WIDTH, getBottom());
    }

    /**
     * Prevents both players from overlapping: each one moves back half the overlap.
     * @param other the other player
     */
    public void separateFrom(Player other) {
        double overlapX = Math.min(getRight(), other.getRight()) - Math.max(getLeft(), other.getLeft());
        double overlapY = Math.min(getBottom(), other.getBottom()) - Math.max(getTop(), other.getTop());
        if (overlapX <= 0 || overlapY <= 0) {
            return;
        }
        double push = overlapX / 2;
        if (x <= other.x) {
            x -= push;
            other.x += push;
        } else {
            x += push;
            other.x -= push;
        }
    }

    /**
     * Tells whether the foot can touch the ball
     * @param ball the ball of the match
     * @return true if the ball is within reach of the foot
     */
    public boolean footReaches(Ball ball) {
        double distance = Math.hypot(ball.getX() - getFootX(), ball.getY() - getFootY());
        return distance <= FOOT_REACH + ball.getRadius();
    }

    /**
     * Places the player at rest in the air; it falls when the match continues
     * @param x new x coordinate of the center
     * @param y new y coordinate of the center
     */
    public void resetAt(double x, double y) {
        this.x = x;
        this.y = y;
        this.vx = 0;
        this.vy = 0;
        this.onGround = false;
        this.kickTimer = 0;
        this.moveIntent = 0;
        this.state = PlayerState.JUMPING;
    }

    /**
     * Returns the description of the player to draw it
     * @return description of the player to draw it
     */
    @Override
    public RenderState getRenderState() {
        return new RenderState(Figure.PLAYER, CountryInfo.get(country).getSpriteKey(), x, y, width, height,
                getLegAngle(), TeamSideInfo.of(side).getDirection() < 0);
    }

    /**
     * Returns the current angle of the kicking leg. During a kick the leg swings
     * up to KICK_FOOT_ANGLE and comes back down, following half a sine wave
     * @return angle in degrees
     */
    public double getLegAngle() {
        if (kickTimer <= 0) {
            return 0;
        }
        double progress = 1 - kickTimer / KICK_DURATION;
        return KICK_FOOT_ANGLE * Math.sin(Math.PI * progress);
    }

    /**
     * Computes the horizontal position of the foot center, relative to the player
     * center and facing right. The foot moves on an arc around the hip
     * @param legAngle angle of the leg in degrees
     * @return x of the foot center relative to the player center
     */
    public static double footLocalX(double legAngle) {
        return HIP_OFFSET_X + LEG_LENGTH * Math.sin(Math.toRadians(legAngle));
    }

    /**
     * Computes the vertical position of the foot center, relative to the player
     * center. The foot moves on an arc around the hip
     * @param legAngle angle of the leg in degrees
     * @return y of the foot center relative to the player center
     */
    public static double footLocalY(double legAngle) {
        double hipY = HEIGHT / 2 - FOOT_REST_HEIGHT - LEG_LENGTH;
        return hipY + LEG_LENGTH * Math.cos(Math.toRadians(legAngle));
    }

    /**
     * Returns the x of the head center
     * @return x of the head center
     */
    public double getHeadX() {
        return x;
    }

    /**
     * Returns the y of the head center
     * @return y of the head center
     */
    public double getHeadY() {
        return getTop() + HEAD_RADIUS;
    }

    /**
     * Returns the x of the foot
     * @return x of the foot
     */
    public double getFootX() {
        return x + TeamSideInfo.of(side).getDirection() * footLocalX(getLegAngle());
    }

    /**
     * Returns the y of the foot
     * @return y of the foot
     */
    public double getFootY() {
        return y + footLocalY(getLegAngle());
    }

    /**
     * Tells whether a kick is in progress
     * @return true while a kick is in progress
     */
    public boolean isKicking() {
        return kickTimer > 0;
    }

    /**
     * Tells whether the player stands on the grass
     * @return true while the player stands on the grass
     */
    public boolean isOnGround() {
        return onGround;
    }

    /**
     * Sets who drives this player
     * @param control new control strategy for this player
     */
    public void setControl(ControlStrategy control) {
        this.control = control;
    }

    /**
     * Returns the character chosen by the player
     * @return character chosen by the player
     */
    public Country getCountry() {
        return country;
    }

    /**
     * Returns the side the player defends
     * @return side the player defends
     */
    public TeamSide getSide() {
        return side;
    }

    /**
     * Returns the current state
     * @return current state
     */
    public PlayerState getState() {
        return state;
    }

    /**
     * Returns the horizontal speed
     * @return horizontal speed
     */
    public double getVx() {
        return vx;
    }

    /**
     * Returns the vertical speed
     * @return vertical speed
     */
    public double getVy() {
        return vy;
    }
}
