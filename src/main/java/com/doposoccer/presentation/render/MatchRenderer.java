package com.doposoccer.presentation.render;
import com.doposoccer.domain.enums.Figure;
import com.doposoccer.domain.model.RenderState;
import com.doposoccer.presentation.resource.SpriteCache;
import java.awt.Graphics2D;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Draws a whole frame of the match using one painter per kind of figure
 */
public class MatchRenderer {

    private final Map<Figure, FigurePainter> painters = new EnumMap<>(Figure.class);
    private final StadiumPainter stadium;

    /**
     * Creates the renderer with one painter per figure
     * @param sprites shared sprite cache
     */
    public MatchRenderer(SpriteCache sprites) {
        stadium = new StadiumPainter(sprites);
        painters.put(Figure.PLAYER, new PlayerPainter(sprites));
        painters.put(Figure.BALL, new BallPainter(sprites));
        painters.put(Figure.GOAL, new GoalPainter(sprites));
    }

    /**
     * Draws the stadium and then every object in three layers
     * @param g graphics in field coordinates
     * @param states what to draw, in the order given by the match
     */
    public void paint(Graphics2D g, List<RenderState> states) {
        stadium.paint(g);
        for (int i = 0; i < states.size(); i++) {
            painters.get(states.get(i).getFigure()).paintBack(g, states.get(i));
        }
        for (int i = 0; i < states.size(); i++) {
            painters.get(states.get(i).getFigure()).paint(g, states.get(i));
        }
        for (int i = 0; i < states.size(); i++) {
            painters.get(states.get(i).getFigure()).paintFront(g, states.get(i));
        }
    }
}
