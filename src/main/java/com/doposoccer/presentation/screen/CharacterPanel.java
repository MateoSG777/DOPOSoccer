package com.doposoccer.presentation.screen;

import com.doposoccer.domain.country.CountryInfo;
import com.doposoccer.domain.enums.Country;
import com.doposoccer.domain.enums.TeamSide;
import com.doposoccer.domain.model.TeamSideInfo;
import com.doposoccer.presentation.DOPOSoccerGUI;
import com.doposoccer.presentation.component.ImageButton;
import com.doposoccer.presentation.input.PlayerKeys;
import com.doposoccer.presentation.resource.SpriteCache;
import com.doposoccer.presentation.resource.SpritePath;
import com.doposoccer.presentation.style.TextStyle;

import java.awt.Graphics2D;
import java.awt.Graphics;
import java.util.HashMap;
import java.util.Map;

/**
 * Character selection screen: each player cycles through the countries with
 * two arrow buttons and sees the head, the flag, the power shot and its keys
 * It keeps the character chosen by each player
 */
public class CharacterPanel extends BackgroundPanel {

    private static final double HEAD_Y = 315;
    private static final double HEAD_SIZE = 190;
    private static final double FLAG_SIZE = 54;

    private final Country[] catalog = Country.values();
    private final Map<TeamSide, Integer> choices = new HashMap<>();
    private final Map<TeamSide, ImageButton[]> arrows = new HashMap<>();
    private final ImageButton backButton;
    private final ImageButton playButton;

    /**
     * Creates the character selection screen
     * @param gui main window, used to navigate
     * @param sprites shared sprite cache
     */
    public CharacterPanel(final DOPOSoccerGUI gui, SpriteCache sprites) {
        super(sprites, SpritePath.background("menu_vs"), 0);
        choices.put(TeamSide.LEFT, 0);
        choices.put(TeamSide.RIGHT, catalog.length - 1);
        TeamSide[] sides = TeamSide.values();
        for (int i = 0; i < sides.length; i++) {
            arrows.put(sides[i], new ImageButton[] {arrow("<", sides[i], -1), arrow(">", sides[i], 1)});
        }
        backButton = new ImageButton("ATRÁS", sprites);
        playButton = new ImageButton("¡A JUGAR!", sprites);
        backButton.addActionListener(e -> gui.showConfig());
        playButton.addActionListener(e -> gui.play());
        add(backButton);
        add(playButton);
    }

    private ImageButton arrow(String text, final TeamSide side, final int steps) {
        ImageButton button = new ImageButton(text, sprites);
        button.addActionListener(e -> {
            int next = (choices.get(side) + steps + catalog.length) % catalog.length;
            choices.put(side, next);
            repaint();
        });
        add(button);
        return button;
    }

    /** Places the arrows next to each head and the buttons at the bottom*/
    @Override
    public void doLayout() {
        TeamSide[] sides = TeamSide.values();
        for (int i = 0; i < sides.length; i++) {
            double x = TeamSideInfo.of(sides[i]).spawnX();
            place(arrows.get(sides[i])[0], x - 200, HEAD_Y - 35, 90, 70);
            place(arrows.get(sides[i])[1], x + 110, HEAD_Y - 35, 90, 70);
        }
        placeBottomButtons(backButton, playButton);
    }

    /**
     * Paints the background and the column of each player
     * @param g graphics given by Swing
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = designGraphics(g);
        paintBottomBand(g2);
        TextStyle.TITLE.draw(g2, "ELIGE TU PERSONAJE", DESIGN_WIDTH / 2, 55);
        TeamSide[] sides = TeamSide.values();
        for (int i = 0; i < sides.length; i++) {
            double x = TeamSideInfo.of(sides[i]).spawnX();
            Country country = getCountry(sides[i]);
            CountryInfo info = CountryInfo.get(country);
            TextStyle.SUBTITLE.draw(g2, TeamSideInfo.of(sides[i]).getLabel().toUpperCase(), x, 120);
            TextStyle.SMALL.draw(g2, PlayerKeys.of(sides[i]).getHint(), x, 150);
            drawCountry(g2, SpritePath.character(info.getSpriteKey()), country, x, HEAD_Y, HEAD_SIZE);
            drawCountry(g2, SpritePath.flag(info.getSpriteKey()), country, x, 448, FLAG_SIZE);
            TextStyle.HEADING.draw(g2, info.getDisplayName().toUpperCase(), x, 500);
            TextStyle.DETAIL.draw(g2, "Tiro de poder: " + info.getPowerName(), x, 525);
        }
        g2.dispose();
    }

    /**
     * Returns the character chosen by a player
     * @param side side of the player
     * @return the chosen character
     */
    public Country getCountry(TeamSide side) {
        return catalog[choices.get(side)];
    }
}
