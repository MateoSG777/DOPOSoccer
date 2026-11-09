package com.doposoccer.domain.control;

import com.doposoccer.domain.enums.Country;
import com.doposoccer.domain.enums.PlayerAction;
import com.doposoccer.domain.enums.PlayerState;
import com.doposoccer.domain.enums.TeamSide;
import com.doposoccer.domain.model.Ball;
import com.doposoccer.domain.model.Field;
import com.doposoccer.domain.model.Player;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

/**
 * Unit tests of KeyboardControl.
 */
class KeyboardControlTest {

    private FakeActions actions;
    private Player player;
    private Ball ball;

    /** Creates fresh objects before each test. */
    @BeforeEach
    void setUp() {
        actions = new FakeActions();
        player = new Player(Country.CAMEROON, TeamSide.LEFT, 300, Field.GROUND_Y - Player.HEIGHT / 2);
        player.setControl(new KeyboardControl(actions));
        player.update(0.01);
        ball = new Ball(800, 100);
    }

    /** Should move when move action is active. */
    @Test
    void shouldMoveWhenMoveActionIsActive() {
        actions.active.add(PlayerAction.MOVE_RIGHT);
        player.updateControl(ball, 0.1);
        player.update(0.1);
        assertTrue(player.getX() > 300);
    }

    /** Should stop when action is released. */
    @Test
    void shouldStopWhenActionIsReleased() {
        actions.active.add(PlayerAction.MOVE_RIGHT);
        player.updateControl(ball, 0.1);
        player.update(0.1);
        actions.active.clear();
        double x = player.getX();
        player.updateControl(ball, 0.1);
        player.update(0.1);
        assertEquals(x, player.getX(), 1e-6);
    }

    /** Should jump and kick at the same time. */
    @Test
    void shouldJumpAndKickAtTheSameTime() {
        actions.active.add(PlayerAction.JUMP);
        actions.active.add(PlayerAction.KICK);
        player.updateControl(ball, 0.01);
        player.update(0.01);
        assertTrue(player.isKicking());
        assertTrue(player.getVy() < 0);
        assertEquals(PlayerState.KICKING, player.getState());
    }

    /** No control should do nothing */
    @Test
    void noControlShouldDoNothing() {
        player.setControl(new NoControl());
        player.updateControl(ball, 0.1);
        player.update(0.1);
        assertEquals(300, player.getX(), 1e-6);
    }

    private static class FakeActions implements ActionSource {
        private final List<PlayerAction> active = new ArrayList<>();

        @Override
        public List<PlayerAction> getActiveActions() {
            return active;
        }
    }
}
