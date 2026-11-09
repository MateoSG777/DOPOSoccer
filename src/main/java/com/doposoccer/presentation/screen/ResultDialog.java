package com.doposoccer.presentation.screen;
import com.doposoccer.domain.DOPOSoccerException;
import com.doposoccer.domain.country.CountryInfo;
import com.doposoccer.domain.enums.Country;
import com.doposoccer.domain.enums.TeamSide;
import com.doposoccer.domain.match.Match;
import com.doposoccer.domain.match.MatchSnapshot;
import com.doposoccer.domain.model.TeamSideInfo;
import com.doposoccer.presentation.DOPOSoccerGUI;
import com.doposoccer.presentation.component.ImageButton;
import com.doposoccer.presentation.resource.SpriteCache;
import com.doposoccer.presentation.resource.SpritePath;
import com.doposoccer.presentation.style.TextStyle;
import javax.swing.JDialog;
import javax.swing.JFrame;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Graphics;

/**
 * Window shown when a match is over: winner, final score and points earned
 * by each player. A match ended by the players shows that nobody wins
 */
public class ResultDialog extends JDialog {

    private static final int WIDTH = 760;  // fits inside the smallest window (800 x 500)
    private static final int HEIGHT = 475;  // keeps the 1.6 ratio of the field
    private static final double HEAD_SIZE = 190;  // same size as in the character selection

    private boolean rematch;

    /**
     * Creates the result window of a match
     * @param owner   main window
     * @param sprites shared sprite cache
     * @param match   finished match
     */
    public ResultDialog(JFrame owner, SpriteCache sprites, Match match) {
        super(owner, DOPOSoccerGUI.TITLE, true);
        ResultPanel panel = new ResultPanel(sprites, match);
        panel.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setContentPane(panel);
        setResizable(false);
        pack();
        setLocationRelativeTo(owner);
    }

    /**
     * Tells whether the users want a rematch
     * @return true for a rematch, false to go back to the menu
     */
    public boolean wantsRematch() {
        return rematch;
    }

    private class ResultPanel extends BackgroundPanel {

        private final Match match;
        private final MatchSnapshot result;
        private final String title;
        private final ImageButton rematchButton;
        private final ImageButton menuButton;

        ResultPanel(SpriteCache sprites, Match match) {
            super(sprites, SpritePath.background("menu_vs"), 0);
            this.match = match;
            this.result = match.snapshot();
            this.title = titleOf(match);
            rematchButton = new ImageButton("REVANCHA", sprites);
            menuButton = new ImageButton("MENÚ", sprites);
            rematchButton.addActionListener(e -> {
                rematch = true;
                dispose();
            });
            menuButton.addActionListener(e -> dispose());
            add(rematchButton);
            add(menuButton);
        }

        private String titleOf(Match finished) {
            try {
                return "¡GANA EL " + TeamSideInfo.of(finished.getWinner()).getLabel().toUpperCase() + "!";
            } catch (DOPOSoccerException e) {
                return "PARTIDO TERMINADO";
            }
        }

        /** Places the buttons at the bottom*/
        @Override
        public void doLayout() {
            placeBottomButtons(rematchButton, menuButton);
        }

        /**
         * Paints the title, both players, the score and the points
         * @param g graphics given by Swing
         */
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = designGraphics(g);
            paintBottomBand(g2);
            TextStyle.TITLE.draw(g2, title, DESIGN_WIDTH / 2, 70);
            TeamSide[] sides = TeamSide.values();
            for (int i = 0; i < sides.length; i++) {
                double x = TeamSideInfo.of(sides[i]).spawnX();
                Country country = match.getConfig().getCountry(sides[i]);
                CountryInfo info = CountryInfo.get(country);
                String heading = TeamSideInfo.of(sides[i]).getLabel().toUpperCase() + " · " + info.getDisplayName();
                TextStyle.HEADING.draw(g2, heading, x, 150);
                drawCountry(g2, SpritePath.character(info.getSpriteKey()), country, x, 300, HEAD_SIZE);
                if (match.hasWinner()) {
                    TextStyle.SUBTITLE.draw(g2, "+" + match.getPointsEarned(sides[i]) + " puntos", x, 455);
                }
            }
            String score = result.getGoals(TeamSide.LEFT) + "  -  " + result.getGoals(TeamSide.RIGHT);
            TextStyle.SCORE.draw(g2, score, DESIGN_WIDTH / 2, 470);
            if (!match.hasWinner()) {
                TextStyle.TEXT.draw(g2, "Partido terminado por los jugadores: sin ganador ni puntos", DESIGN_WIDTH / 2, 510);
            }
            g2.dispose();
        }
    }
}