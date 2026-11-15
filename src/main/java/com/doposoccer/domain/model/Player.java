package com.doposoccer.domain.model;
import com.doposoccer.domain.control.ControlStrategy;
import com.doposoccer.domain.control.NoControl;
import com.doposoccer.domain.enums.Country;
import com.doposoccer.domain.enums.Figure;
import com.doposoccer.domain.enums.PlayerState;
import com.doposoccer.domain.enums.Stat;
import com.doposoccer.domain.enums.TeamSide;
import com.doposoccer.domain.power.Hit;
import com.doposoccer.domain.power.ImpactHandler;
import com.doposoccer.domain.shop.PlayerProfile;

/**
 * A player on the field, driven by its control strategy. It always faces the rival goal.
 * Its control is not saved with the match: it is attached again when a saved match is opened
 */
public class Player extends GameObject {

    private static final long serialVersionUID = 1L;

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

    /** Life of a player without upgrades. */
    public static final double BASE_LIFE = 100;

    /** Seconds that the power bar needs to fill by itself. */
    public static final double POWER_CHARGE_SECONDS = 10;

    /** Fraction of the power bar won when the rival scores. */
    public static final double GOAL_POWER_BONUS = 0.25;

    /** Seconds that a player without life stays stunned. */
    public static final double KNOCKOUT_SECONDS = 3;

    /** Damage of a normal kick to the rival before the kick multiplier. */
    public static final double KICK_DAMAGE = 10;

    /** Fraction of the walking speed kept by a slowed player. */
    public static final double SLOW_FACTOR = 0.5;

    /** Horizontal speed given by a push. */
    public static final double PUSH_SPEED = 650;

    /** Fraction of the push speed lost per second. */
    public static final double PUSH_FRICTION = 2;

    /** Vertical speed of a player thrown into the air. */
    public static final double LAUNCH_SPEED = 900;

    private final Country country;
    private final TeamSide side;
    private transient ControlStrategy control;
    private PlayerState state;
    private double vx;
    private double vy;
    private int moveIntent;
    private boolean onGround;
    private double kickTimer;
    private boolean kickApplied;
    private double speedFactor = 1;
    private double jumpFactor = 1;
    private double kickFactor = 1;
    private double maxLife = BASE_LIFE;
    private double life = BASE_LIFE;
    private double power;
    private boolean aura;
    private double pushVx;
    private double slowTimer;
    private double dazeTimer;
    private double knockTimer;

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
     * Applies the upgrades bought in the store: speed, jump, kick and maximum life
     * The height of a jump grows with the square of its speed, so the speed grows with the square root
     * @param profile progress of the player who drives this character
     */
    public void applyStats(PlayerProfile profile) {
        speedFactor = profile.multiplier(Stat.SPEED);
        jumpFactor = Math.sqrt(profile.multiplier(Stat.JUMP));
        kickFactor = profile.multiplier(Stat.KICK);
        maxLife = BASE_LIFE * profile.multiplier(Stat.SURVIVAL);
        life = maxLife;
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
        if (state.canMove()) {
            moveIntent = direction;
        }
    }

    /** Starts a jump if the player is on the ground and the state allows it */
    public void jump() {
        if (onGround && state.canJump() && slowTimer <= 0) {
            vy = -BASE_JUMP_SPEED * jumpFactor;
            onGround = false;
        }
    }

    /** Starts a kick if the state allows it */
    public void kick() {
        if (state.canKick()) {
            kickTimer = KICK_DURATION;
            kickApplied = false;
            state = PlayerState.KICKING;
        }
    }

    /** Spends a full power bar to get the aura: the next touch of the ball is a power shot */
    public void activatePower() {
        if (power >= 1 && !aura && state.canMove()) {
            aura = true;
            power = 0;
        }
    }

    /**
     * Moves the player, applies gravity, keeps it inside the field and updates its state
     * @param dt seconds elapsed since the last frame
     */
    @Override
    public void update(double dt) {
        vx = moveIntent * BASE_SPEED * speedFactor;
        if (slowTimer > 0) {
            vx *= SLOW_FACTOR;
        }
        x += (vx + pushVx) * dt;
        pushVx -= pushVx * PUSH_FRICTION * dt;
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
        slowTimer = Math.max(0, slowTimer - dt);
        dazeTimer = Math.max(0, dazeTimer - dt);
        if (knockTimer > 0) {
            knockTimer = Math.max(0, knockTimer - dt);
            if (knockTimer <= 0) {
                life = maxLife;
            }
        }
        power = Math.min(1, power + dt / POWER_CHARGE_SECONDS);
        state = nextState();
    }

    private PlayerState nextState() {
        if (dazeTimer > 0 || knockTimer > 0) {
            return PlayerState.STUNNED;
        }
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
     * Every touch is reported to the ball, which decides whether a power shot starts or hits
     * The power shot of this player passes through it
     * @param ball the ball of the match
     */
    @Override
    public void checkCollision(Ball ball) {
        if (ball.isShotBy(this)) {
            return;
        }
        if (isKicking() && !kickApplied && footReaches(ball)) {
            ball.kick(side.getDirection() * KICK_SPEED_X, KICK_SPEED_Y);
            kickApplied = true;
            ball.touchedBy(this);
            return;
        }
        boolean head = ball.bounceOffCircle(getHeadX(), getHeadY(), HEAD_RADIUS, vx, vy, HEADER_MIN_SPEED);
        boolean body = ball.bounceOffRect(x - BODY_HALF_WIDTH, getHeadY() + HEAD_RADIUS, x + BODY_HALF_WIDTH, getBottom());
        if (head || body) {
            ball.touchedBy(this);
        }
    }

    /**
     * Takes life away. The life never goes below zero
     * @param amount life to take
     */
    public void damage(double amount) {
        life = Math.max(0, life - amount);
    }

    /**
     * A kick hurts the rival only if the foot reaches it and the kick did not touch the ball first
     * @param rival the other player
     */
    public void checkKick(Player rival) {
        if (isKicking() && !kickApplied && footReaches(rival)) {
            kickApplied = true;
            ImpactHandler.KICK.handle(new Hit(this, rival, KICK_DAMAGE * kickFactor));
        }
    }

    /** A player without life is stunned for a while; when the stun ends it gets all its life back */
    public void knockOut() {
        if (knockTimer <= 0) {
            knockTimer = KNOCKOUT_SECONDS;
            stop();
        }
    }

    /**
     * Throws the player towards a side; it cannot act while it flies
     * @param direction 1 to push it to the right, -1 to the left
     * @param seconds duration of the effect
     */
    public void push(int direction, double seconds) {
        pushVx = direction * PUSH_SPEED;
        daze(seconds);
    }

    /**
     * Makes the player walk slower and unable to jump for a while
     * @param seconds duration of the effect
     */
    public void slow(double seconds) {
        slowTimer = seconds;
    }

    /** Throws the player into the air */
    public void launch() {
        vy = -LAUNCH_SPEED;
        onGround = false;
    }

    /**
     * Leaves the player unable to move, jump or kick for a while
     * @param seconds duration of the effect
     */
    public void daze(double seconds) {
        dazeTimer = seconds;
        stop();
    }

    /** Removes the effect of a power: effects do not stack, the new one replaces the old one */
    public void clearEffects() {
        slowTimer = 0;
        dazeTimer = 0;
        pushVx = 0;
    }

    private void stop() {
        kickTimer = 0;
        moveIntent = 0;
        state = PlayerState.STUNNED;
    }

    /**
     * Adds charge to the power bar, up to a full bar
     * @param fraction part of the bar to add, between 0 and 1
     */
    public void chargePower(double fraction) {
        power = Math.min(1, power + fraction);
    }

    /** Loses the aura because the power shot went out */
    public void releaseAura() {
        aura = false;
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
     * Tells whether the foot can touch the body of the rival
     * @param rival the other player
     * @return true if the rival is within reach of the foot
     */
    public boolean footReaches(Player rival) {
        double closestX = Math.max(rival.getLeft(), Math.min(getFootX(), rival.getRight()));
        double closestY = Math.max(rival.getTop(), Math.min(getFootY(), rival.getBottom()));
        return Math.hypot(getFootX() - closestX, getFootY() - closestY) <= FOOT_REACH;
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
        String glow = aura ? country.getSpriteKey() : null;
        return new RenderState(Figure.PLAYER, country.getSpriteKey(), x, y, width, height,
                getLegAngle(), side.getDirection() < 0, glow, isAffected() || isKnockedOut());
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
        return x + side.getDirection() * footLocalX(getLegAngle());
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
     * Tells whether the player suffers the effect of a power
     * @return true while the player is pushed, slowed or dazed
     */
    public boolean isAffected() {
        return slowTimer > 0 || dazeTimer > 0;
    }

    /**
     * Tells whether the player ran out of life and is stunned
     * @return true while the knockout lasts
     */
    public boolean isKnockedOut() {
        return knockTimer > 0;
    }

    /**
     * Tells whether the next touch of the ball is a power shot
     * @return true while the player has the aura
     */
    public boolean hasAura() {
        return aura;
    }

    /**
     * Returns the charge of the power bar
     * @return a value between 0 and 1
     */
    public double getPower() {
        return power;
    }

    /**
     * Returns the current life
     * @return current life
     */
    public double getLife() {
        return life;
    }

    /**
     * Returns the life of the player when it is not hurt
     * @return maximum life
     */
    public double getMaxLife() {
        return maxLife;
    }

    /**
     * Returns multiplicadorKick, which multiplies the damage of kicks and power shots
     * @return kick multiplier
     */
    public double getKickFactor() {
        return kickFactor;
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
