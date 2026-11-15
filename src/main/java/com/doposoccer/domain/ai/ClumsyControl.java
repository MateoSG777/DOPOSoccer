package com.doposoccer.domain.ai;
import com.doposoccer.domain.enums.PlayerAction;
import com.doposoccer.domain.model.Ball;
import com.doposoccer.domain.model.Player;
import java.util.Random;

/**
 * Clumsy machine that picks its moves at random
 */
public class ClumsyControl extends MachineControl {

    private static final double DECISION_SECONDS = 0.35;  // long enough to see each move, short enough to look erratic

    private final PlayerAction[] actions = PlayerAction.values();
    private final Random random = new Random();
    private PlayerAction current = PlayerAction.KICK;
    private double timer;

    /**
     * Repeats a random action for a moment and then picks another one
     * @param player the player being driven
     * @param ball the ball of the match
     * @param dt seconds elapsed since the last frame
     */
    @Override
    public void update(Player player, Ball ball, double dt) {
        timer -= dt;
        if (timer <= 0) {
            current = actions[random.nextInt(actions.length)];
            timer = DECISION_SECONDS;
        }
        current.apply(player);
    }
}
