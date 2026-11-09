package com.doposoccer.presentation.render;
import com.doposoccer.domain.country.CountryInfo;
import com.doposoccer.domain.enums.Country;
import com.doposoccer.domain.enums.TeamSide;
import com.doposoccer.domain.match.MatchConfig;
import com.doposoccer.domain.match.MatchSnapshot;
import com.doposoccer.domain.match.status.MatchStatusBehavior;
import com.doposoccer.domain.model.Field;
import com.doposoccer.domain.model.TeamSideInfo;
import com.doposoccer.presentation.input.PlayerKeys;
import com.doposoccer.presentation.resource.SpriteCache;
import com.doposoccer.presentation.resource.SpritePath;
import com.doposoccer.presentation.style.Palette;
import com.doposoccer.presentation.style.TextStyle;
import com.doposoccer.presentation.style.Theme;
import java.awt.Graphics2D;

/**
 * Heads-up display: score, clock, players' flags and names, sudden death
 * warning, pause overlay and the keys of each player
 */
public class Hud {

    private static final double BOARD_WIDTH = 420;
    private static final double BOARD_HEIGHT = 92;
    private static final double BOARD_TOP = 8;
    private static final double FLAG_SIZE = 46;
    private static final double KEYS_BAR_HEIGHT = 26;

    private static final String KEYS = "J1: " + PlayerKeys.of(TeamSide.LEFT).getHint() + "      |      J2: "
            + PlayerKeys.of(TeamSide.RIGHT).getHint() + "      |      P: pausa";

    private final SpriteCache sprites;

    /**
     * Creates the heads-up display
     * @param sprites shared sprite cache
     */
    public Hud(SpriteCache sprites) {
        this.sprites = sprites;
    }

    /**
     * Draws all the information of the match over the field
     * @param g graphics in field coordinates
     * @param snapshot current state of the match
     * @param config   configuration of the match
     */
    public void paint(Graphics2D g, MatchSnapshot snapshot, MatchConfig config) {
        double left = Field.centerX() - BOARD_WIDTH / 2;
        g.setColor(Theme.color(Palette.SHADE));
        g.fillRoundRect((int) left, (int) BOARD_TOP, (int) BOARD_WIDTH, (int) BOARD_HEIGHT, 26, 26);
        TeamSide[] sides = TeamSide.values();
        for (int i = 0; i < sides.length; i++) {
            double tagX = Field.centerX() - TeamSideInfo.of(sides[i]).getDirection() * (BOARD_WIDTH / 2 - FLAG_SIZE);
            Country country = config.getCountry(sides[i]);
            CountryInfo info = CountryInfo.get(country);
            Theme.drawCountryImage(g, sprites.get(SpritePath.flag(info.getSpriteKey())), country,
                    tagX, BOARD_TOP + 8 + FLAG_SIZE / 2, FLAG_SIZE);
            TextStyle.TAG.draw(g, TeamSideInfo.of(sides[i]).getLabel(), tagX, BOARD_TOP + 70);
            TextStyle.SMALL.draw(g, info.getDisplayName(), tagX, BOARD_TOP + 86);
        }
        String score = snapshot.getGoals(TeamSide.LEFT) + "  -  " + snapshot.getGoals(TeamSide.RIGHT);
        TextStyle.SCORE.draw(g, score, Field.centerX(), BOARD_TOP + 50);
        TextStyle.TEXT.draw(g, snapshot.getClockText(), Field.centerX(), BOARD_TOP + 82);

        if (snapshot.isSuddenDeath()) {
            TextStyle.WARNING.draw(g, "¡MUERTE SÚBITA! El próximo gol gana", Field.centerX(), 135);
        }
        g.setColor(Theme.color(Palette.SHADE));
        g.fillRect(0, (int) (Field.HEIGHT - KEYS_BAR_HEIGHT), (int) Field.WIDTH, (int) KEYS_BAR_HEIGHT);
        TextStyle.SMALL.draw(g, KEYS, Field.centerX(), Field.HEIGHT - 8);

        if (MatchStatusBehavior.get(snapshot.getStatus()).isPaused()) {
            g.setColor(Theme.color(Palette.SHADE));
            g.fillRect(0, 0, (int) Field.WIDTH, (int) Field.HEIGHT);
            TextStyle.PAUSE.draw(g, "PAUSA", Field.centerX(), Field.HEIGHT / 2);
            TextStyle.TEXT.draw(g, "Pulsa P o el botón SEGUIR para continuar", Field.centerX(), Field.HEIGHT / 2 + 50);
        }
    }
}
