package com.doposoccer.presentation;

import com.doposoccer.domain.enums.Country;
import com.doposoccer.domain.enums.TeamSide;
import com.doposoccer.presentation.input.PlayerKeys;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EnumMap;
import java.util.Map;

/**
 * Character selection screen: each player cycles through the countries with
 * two arrow buttons and sees the head, the flag, the power shot and its keys.
 */
public class CharacterPanel extends BackgroundPanel {

    /** Horizontal center of each player's column, in design coordinates. */
    private static final Map<TeamSide, Double> COLUMN_X = new EnumMap<>(TeamSide.class);

    static {
        COLUMN_X.put(TeamSide.LEFT, 235.0);
        COLUMN_X.put(TeamSide.RIGHT, 789.0);
    }

    /** Vertical center of the heads, in design coordinates. */
    private static final double HEAD_Y = 315;

    /** Size of the heads, in design coordinates. */
    private static final double HEAD_SIZE = 190;

    /** Main window, used to navigate and to store the chosen characters. */
    private final DOPOSoccerGUI gui;

    /** Button that shows the previous country of each player. */
    private final Map<TeamSide, ImageButton> previousButtons = new EnumMap<>(TeamSide.class);

    /** Button that shows the next country of each player. */
    private final Map<TeamSide, ImageButton> nextButtons = new EnumMap<>(TeamSide.class);

    /** Button that goes back to the configuration. */
    private final ImageButton backButton;

    /** Button that starts the match. */
    private final ImageButton playButton;

    /**
     * Creates the character selection screen.
     *
     * @param gui     main window
     * @param sprites shared sprite cache
     */
    public CharacterPanel(final DOPOSoccerGUI gui, SpriteCache sprites) {
        super(sprites, "menu_vs", 0);
        this.gui = gui;
        TeamSide[] sides = TeamSide.values();
        for (int i = 0; i < sides.length; i++) {
            final TeamSide side = sides[i];
            ImageButton previous = new ImageButton("<", sprites);
            ImageButton next = new ImageButton(">", sprites);
            previous.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    cycle(side, -1);
                }
            });
            next.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    cycle(side, 1);
                }
            });
            previousButtons.put(side, previous);
            nextButtons.put(side, next);
            add(previous);
            add(next);
        }
        backButton = new ImageButton("ATRÁS", sprites);
        playButton = new ImageButton("¡A JUGAR!", sprites);
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gui.showConfig();
            }
        });
        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gui.play();
            }
        });
        add(backButton);
        add(playButton);
    }

    /**
     * Moves a player's choice to the previous or next country (it wraps around).
     *
     * @param side  side of the player
     * @param steps -1 for the previous country, +1 for the next one
     */
    private void cycle(TeamSide side, int steps) {
        Country[] countries = Country.values();
        int index = gui.getCountry(side).ordinal();
        int nextIndex = (index + steps + countries.length) % countries.length;
        gui.setCountry(side, countries[nextIndex]);
        repaint();
    }

    /** Places the arrows next to each head and the buttons at the bottom. */
    @Override
    public void doLayout() {
        TeamSide[] sides = TeamSide.values();
        for (int i = 0; i < sides.length; i++) {
            double x = COLUMN_X.get(sides[i]);
            place(previousButtons.get(sides[i]), x - 200, HEAD_Y - 35, 90, 70);
            place(nextButtons.get(sides[i]), x + 110, HEAD_Y - 35, 90, 70);
        }
        place(backButton, 150, 540, 250, 90);
        place(playButton, 624, 540, 250, 90);
    }

    /**
     * Paints the background and the column of each player.
     *
     * @param g graphics given by Swing
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = designGraphics(g);
        g2.setColor(Theme.BAND);
        g2.fillRect(0, 528, (int) DESIGN_WIDTH, 112);
        Theme.drawCenteredText(g2, "ELIGE TU PERSONAJE", DESIGN_WIDTH / 2, 55,
                Theme.font(36f), Theme.GOLD, Theme.DARK, 5f);
        TeamSide[] sides = TeamSide.values();
        for (int i = 0; i < sides.length; i++) {
            paintColumn(g2, sides[i]);
        }
        g2.dispose();
    }

    /**
     * Paints the title, keys, head, flag, country and power shot of one player.
     *
     * @param g    graphics in design coordinates
     * @param side side of the player
     */
    private void paintColumn(Graphics2D g, TeamSide side) {
        double x = COLUMN_X.get(side);
        Country country = gui.getCountry(side);
        Theme.drawCenteredText(g, side.getLabel().toUpperCase(), x, 120,
                Theme.font(30f), Theme.GOLD, Theme.DARK, 4f);
        Theme.drawCenteredText(g, PlayerKeys.of(side).getHint(), x, 150,
                Theme.font(14f), Color.WHITE, Theme.DARK, 3f);
        if (!drawCentered(g, country.getSpriteKey(), x, HEAD_Y, HEAD_SIZE, HEAD_SIZE)) {
            g.setColor(Theme.toColor(country.getColor()));
            g.fillOval((int) (x - HEAD_SIZE / 2), (int) (HEAD_Y - HEAD_SIZE / 2), (int) HEAD_SIZE, (int) HEAD_SIZE);
        }
        drawCentered(g, "flag_" + country.getSpriteKey(), x, 448, 54, 54);
        Theme.drawCenteredText(g, country.getDisplayName().toUpperCase(), x, 500,
                Theme.font(28f), Color.WHITE, Theme.DARK, 4f);
        Theme.drawCenteredText(g, "Tiro de poder: " + country.getPowerName(), x, 525,
                Theme.font(16f), Theme.GOLD, Theme.DARK, 3f);
    }
}
