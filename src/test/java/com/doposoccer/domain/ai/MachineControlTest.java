package com.doposoccer.domain.ai;

import com.doposoccer.domain.enums.Country;
import com.doposoccer.domain.enums.MachineProfile;
import com.doposoccer.domain.enums.TeamSide;
import com.doposoccer.domain.model.Ball;
import com.doposoccer.domain.model.Field;
import com.doposoccer.domain.model.Player;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests of the strategies of the machine. The machine plays on the right, attacking to the left.
 */
class MachineControlTest {

    private static final double FRAME = 1.0 / 60;
    private static final double STANDING_Y = Field.GROUND_Y - Player.HEIGHT / 2;
    private static final double RESTING_Y = Field.GROUND_Y - Ball.RADIUS;

    private Player machine;

    /** Creates the machine player standing in its own half. */
    @BeforeEach
    void setUp() {
        machine = new Player(Country.NIGERIA, TeamSide.RIGHT, 768, STANDING_Y);
        machine.update(0.01);
    }

    private void play(MachineProfile profile, Ball ball, double seconds) {
        machine.setControl(profile.createControl());
        int frames = (int) Math.round(seconds / FRAME);
        for (int i = 0; i < frames; i++) {
            machine.updateControl(ball, FRAME);
            machine.update(FRAME);
        }
    }

    /** The offensive machine should go for a ball in the rival half. */
    @Test
    void offensiveShouldChaseTheBallAnywhere() {
        Ball ball = new Ball(300, RESTING_Y);
        play(MachineProfile.OFFENSIVE, ball, 1);
        assertTrue(machine.getX() < 768);
    }

    /** The offensive machine should use its power as soon as it is ready. */
    @Test
    void offensiveShouldUseItsPowerAsSoonAsItIsReady() {
        machine.chargePower(1);
        play(MachineProfile.OFFENSIVE, new Ball(300, RESTING_Y), 0.1);
        assertTrue(machine.hasAura());
    }

    /** The defensive machine should wait near its goal while the ball is far. */
    @Test
    void defensiveShouldWaitNearItsGoal() {
        Ball ball = new Ball(200, RESTING_Y);
        play(MachineProfile.DEFENSIVE, ball, 3);
        assertEquals(TeamSide.RIGHT.goalLineX() - MachineControl.HOME_DISTANCE, machine.getX(), 10);
    }

    /** The defensive machine should go for a ball in its own half. */
    @Test
    void defensiveShouldClearTheBallFromItsHalf() {
        Ball ball = new Ball(600, RESTING_Y);
        play(MachineProfile.DEFENSIVE, ball, 0.3);
        assertTrue(machine.getX() < 768);
    }

    /** The expert machine should fall back when the ball comes from the rival half. */
    @Test
    void expertShouldFallBackWhenTheBallComes() {
        Ball ball = new Ball(300, RESTING_Y);
        ball.kick(50, 0);
        play(MachineProfile.EXPERT, ball, 0.3);
        assertTrue(machine.getX() > 768);
    }

    /** The expert machine should keep its power while the ball is far. */
    @Test
    void expertShouldKeepItsPowerWhileTheBallIsFar() {
        machine.chargePower(1);
        play(MachineProfile.EXPERT, new Ball(200, RESTING_Y), 0.2);
        assertFalse(machine.hasAura());
    }

    /** The expert machine should spend its power when the ball is close and in front. */
    @Test
    void expertShouldUseItsPowerNearTheBall() {
        machine.chargePower(1);
        play(MachineProfile.EXPERT, new Ball(700, RESTING_Y), 0.1);
        assertTrue(machine.hasAura());
    }

    /** The clumsy machine should always stay inside the field. */
    @Test
    void clumsyShouldStayInsideTheField() {
        play(MachineProfile.CLUMSY, new Ball(500, RESTING_Y), 20);
        assertTrue(machine.getLeft() >= 0);
        assertTrue(machine.getRight() <= Field.WIDTH);
    }
}
