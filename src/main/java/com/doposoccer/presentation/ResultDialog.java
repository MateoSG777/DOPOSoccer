package com.doposoccer.presentation;

import com.doposoccer.domain.DOPOSoccerException;
import com.doposoccer.domain.enums.Country;
import com.doposoccer.domain.enums.TeamSide;
import com.doposoccer.domain.match.Match;

import javax.swing.JDialog;
import javax.swing.JFrame;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Window shown when a match is over: winner, final score and points earned
 * by each player. A match ended by the players shows that nobody wins.
 */
public class ResultDialog extends JDialog {

    /** Width of the window. */
    private static final int WIDTH = 760;

    /** Height of the window. */
    private static final int HEIGHT = 475;

    /** True if the users chose to play again with the same configuration. */
    private boolean rematch;

    /**
     * Creates the result window of a match.
     *
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
     * Tells whether the users want a rematch.
     *
     * @return true for a rematch, false to go back to the menu
     */
    public boolean wantsRematch() {
        return rematch;
    }

    /**
     * Content of the result window.
     */
    private class ResultPanel extends BackgroundPanel {

        /** Horizontal center of the player on the left, in design coordinates. */
        private static final double LEFT_X = 235;

        /** Horizontal center of the player on the right, in design coordinates. */
        private static final double RIGHT_X = 789;

        /** Finished match. */
        private final Match match;

        /** Button that starts a rematch. */
        private final ImageButton rematchButton;

        /** Button that goes back to the menu. */
        private final ImageButton menuButton;

        /**
         * Creates the content of the result window.
         *
         * @param sprites shared sprite cache
         * @param match   finished match
         */
        ResultPanel(SpriteCache sprites, Match match) {
            super(sprites, "menu_vs", 0);
            this.match = match;
            rematchButton = new ImageButton("REVANCHA", sprites);
            menuButton = new ImageButton("MENÚ", sprites);
            rematchButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    rematch = true;
                    dispose();
                }
            });
            menuButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    rematch = false;
                    dispose();
                }
            });
            add(rematchButton);
            add(menuButton);
        }

        /** Places the buttons at the bottom. */
        @Override
        public void doLayout() {
            place(rematchButton, 150, 540, 250, 90);
            place(menuButton, 624, 540, 250, 90);
        }

        /**
         * Paints the title, both players, the score and the points.
         *
         * @param g graphics given by Swing
         */
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = designGraphics(g);
            g2.setColor(Theme.BAND);
            g2.fillRect(0, 528, (int) DESIGN_WIDTH, 112);
            Theme.drawCenteredText(g2, title(), DESIGN_WIDTH / 2, 70, Theme.font(44f),
                    Theme.GOLD, Theme.DARK, 6f);
            paintPlayer(g2, TeamSide.LEFT, LEFT_X);
            paintPlayer(g2, TeamSide.RIGHT, RIGHT_X);
            String score = match.getGoals(TeamSide.LEFT) + "  -  " + match.getGoals(TeamSide.RIGHT);
            Theme.drawCenteredText(g2, score, DESIGN_WIDTH / 2, 470, Theme.font(56f),
                    Color.WHITE, Theme.DARK, 6f);
            if (!match.hasWinner()) {
                Theme.drawCenteredText(g2, "Partido terminado por los jugadores: sin ganador ni puntos",
                        DESIGN_WIDTH / 2, 510, Theme.font(20f), Color.WHITE, Theme.DARK, 3f);
            }
            g2.dispose();
        }

        /**
         * Builds the title of the window.
         *
         * @return the winner announcement, or the abandoned message
         */
        private String title() {
            if (!match.hasWinner()) {
                return "PARTIDO TERMINADO";
            }
            try {
                return "¡GANA EL " + match.getWinner().getLabel().toUpperCase() + "!";
            } catch (DOPOSoccerException e) {
                return "PARTIDO TERMINADO";
            }
        }

        /**
         * Paints the head, the country and the points of one player.
         *
         * @param g       graphics in design coordinates
         * @param side    side of the player
         * @param centerX x of the center of the player's column
         */
        private void paintPlayer(Graphics2D g, TeamSide side, double centerX) {
            Country country = match.getConfig().getCountry(side);
            if (!drawCentered(g, country.getSpriteKey(), centerX, 300, 190, 190)) {
                g.setColor(Theme.toColor(country.getColor()));
                g.fillOval((int) (centerX - 95), 205, 190, 190);
            }
            Theme.drawCenteredText(g, side.getLabel().toUpperCase() + " · " + country.getDisplayName(),
                    centerX, 150, Theme.font(24f), Color.WHITE, Theme.DARK, 4f);
            int points = match.getPointsEarned(side);
            if (match.hasWinner()) {
                Theme.drawCenteredText(g, "+" + points + " puntos", centerX, 455, Theme.font(30f),
                        Theme.GOLD, Theme.DARK, 4f);
            }
        }
    }
}
