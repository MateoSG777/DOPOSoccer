package com.doposoccer.presentation.render;
import com.doposoccer.domain.enums.Country;
import com.doposoccer.domain.enums.TeamSide;
import com.doposoccer.domain.match.MatchConfig;
import com.doposoccer.domain.match.MatchSnapshot;
import com.doposoccer.domain.model.Field;
import com.doposoccer.presentation.input.PlayerKeys;
import com.doposoccer.presentation.resource.SpriteCache;
import com.doposoccer.presentation.resource.SpritePath;
import com.doposoccer.presentation.style.NumberStyle;
import com.doposoccer.presentation.style.Palette;
import com.doposoccer.presentation.style.TextStyle;
import com.doposoccer.presentation.style.Theme;
import java.awt.Graphics2D;

/**
 * Heads-up display: score, clock, players' flags and names (or the machine and its level), life and power bars,
 * sudden death warning, pause overlay and the keys of each player
 */
public class Hud {

    private static final double BOARD_WIDTH = 420;
    private static final double BOARD_HEIGHT = 92;
    private static final double BOARD_TOP = 8;
    private static final double FLAG_SIZE = 46;
    private static final double KEYS_BAR_HEIGHT = 26;
    private static final double BAR_WIDTH = 190;
    private static final double BAR_HEIGHT = 12;
    private static final double BARS_TOP = BOARD_TOP + BOARD_HEIGHT + 6;
    private static final double SUDDEN_DEATH_Y = 170;
    private static final double SCORE_HEIGHT = 46;
    private static final double CLOCK_HEIGHT = 20;

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
            double tagX = Field.centerX() - sides[i].getDirection() * (BOARD_WIDTH / 2 - FLAG_SIZE);
            Country country = config.getCountry(sides[i]);
            Theme.drawCountryImage(g, sprites.get(SpritePath.flag(country.getSpriteKey())), country,
                    tagX, BOARD_TOP + 8 + FLAG_SIZE / 2, FLAG_SIZE);
            TextStyle.DETAIL.draw(g, config.getPlayerName(sides[i]), tagX, BOARD_TOP + 80);
            paintBar(g, sides[i], BARS_TOP, snapshot.getLife(sides[i]), Palette.LIFE);
            Palette powerColor = snapshot.getPower(sides[i]) >= 1 ? Palette.GOLD : Palette.POWER;
            paintBar(g, sides[i], BARS_TOP + BAR_HEIGHT + 4, snapshot.getPower(sides[i]), powerColor);
        }
        String score = snapshot.getGoals(TeamSide.LEFT) + " - " + snapshot.getGoals(TeamSide.RIGHT);
        NumberStyle.BIG.draw(g, sprites, score, Field.centerX(), BOARD_TOP + 36, SCORE_HEIGHT);
        NumberStyle.SMALL.draw(g, sprites, snapshot.getClockText(), Field.centerX(), BOARD_TOP + 76, CLOCK_HEIGHT);

        if (snapshot.isSuddenDeath()) {
            TextStyle.WARNING.draw(g, "¡MUERTE SÚBITA! El próximo gol gana", Field.centerX(), SUDDEN_DEATH_Y);
        }
        g.setColor(Theme.color(Palette.SHADE));
        g.fillRect(0, (int) (Field.HEIGHT - KEYS_BAR_HEIGHT), (int) Field.WIDTH, (int) KEYS_BAR_HEIGHT);
        TextStyle.SMALL.draw(g, KEYS, Field.centerX(), Field.HEIGHT - 8);

        if (snapshot.getStatus().isPaused()) {
            g.setColor(Theme.color(Palette.SHADE));
            g.fillRect(0, 0, (int) Field.WIDTH, (int) Field.HEIGHT);
            TextStyle.PAUSE.draw(g, "PAUSA", Field.centerX(), Field.HEIGHT / 2);
            TextStyle.TEXT.draw(g, "Pulsa P o el botón SEGUIR para continuar, o guarda el partido", Field.centerX(), Field.HEIGHT / 2 + 50);
        }
    }

    private void paintBar(Graphics2D g, TeamSide side, double top, double fraction, Palette color) {
        double outer = Field.centerX() - side.getDirection() * BOARD_WIDTH / 2;
        double left = Math.min(outer, outer + side.getDirection() * BAR_WIDTH);
        double filled = BAR_WIDTH * fraction;
        double fillLeft = Math.min(outer, outer + side.getDirection() * filled);
        g.setColor(Theme.color(Palette.SHADE));
        g.fillRoundRect((int) left, (int) top, (int) BAR_WIDTH, (int) BAR_HEIGHT, 8, 8);
        g.setColor(Theme.color(color));
        g.fillRoundRect((int) fillLeft, (int) top, (int) filled, (int) BAR_HEIGHT, 8, 8);
    }
}
