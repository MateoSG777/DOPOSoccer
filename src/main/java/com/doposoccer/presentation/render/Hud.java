package com.doposoccer.presentation.render;

import com.doposoccer.domain.enums.Country;
import com.doposoccer.domain.enums.MatchStatus;
import com.doposoccer.domain.enums.TeamSide;
import com.doposoccer.domain.match.Match;
import com.doposoccer.domain.model.Field;
import com.doposoccer.presentation.SpriteCache;
import com.doposoccer.presentation.Theme;
import com.doposoccer.presentation.input.PlayerKeys;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 * Heads-up display: score, clock, players' flags and names, sudden death
 * warning, pause overlay and the keys of each player.
 */
public class Hud {

    /** Width of the scoreboard. */
    private static final double BOARD_WIDTH = 420;

    /** Height of the scoreboard. */
    private static final double BOARD_HEIGHT = 92;

    /** Top margin of the scoreboard. */
    private static final double BOARD_TOP = 8;

    /** Size of the flags on the scoreboard. */
    private static final double FLAG_SIZE = 46;

    /** Shared sprite cache. */
    private final SpriteCache sprites;

    /**
     * Creates the heads-up display.
     *
     * @param sprites shared sprite cache
     */
    public Hud(SpriteCache sprites) {
        this.sprites = sprites;
    }

    /**
     * Draws all the information of the match over the field.
     *
     * @param g     graphics in field coordinates
     * @param match match being played
     */
    public void paint(Graphics2D g, Match match) {
        paintScoreboard(g, match);
        if (match.isSuddenDeath()) {
            Theme.drawCenteredText(g, "¡MUERTE SÚBITA! El próximo gol gana", Field.centerX(), 135,
                    Theme.font(26f), Theme.ALERT, Color.WHITE, 4f);
        }
        paintKeys(g);
        if (match.getStatus() == MatchStatus.PAUSED) {
            paintPause(g);
        }
    }

    /**
     * Draws the scoreboard: flags, names, goals and clock.
     *
     * @param g     graphics in field coordinates
     * @param match match being played
     */
    private void paintScoreboard(Graphics2D g, Match match) {
        double left = Field.centerX() - BOARD_WIDTH / 2;
        g.setColor(Theme.SHADE);
        g.fillRoundRect((int) left, (int) BOARD_TOP, (int) BOARD_WIDTH, (int) BOARD_HEIGHT, 26, 26);

        paintPlayerTag(g, match.getConfig().getCountry(TeamSide.LEFT), TeamSide.LEFT, left + 55);
        paintPlayerTag(g, match.getConfig().getCountry(TeamSide.RIGHT), TeamSide.RIGHT, left + BOARD_WIDTH - 55);

        String score = match.getGoals(TeamSide.LEFT) + "  -  " + match.getGoals(TeamSide.RIGHT);
        Theme.drawCenteredText(g, score, Field.centerX(), BOARD_TOP + 50, Theme.font(44f),
                Theme.GOLD, Theme.DARK, 5f);
        Theme.drawCenteredText(g, match.getClockText(), Field.centerX(), BOARD_TOP + 82, Theme.font(22f),
                Color.WHITE, Theme.DARK, 3f);
    }

    /**
     * Draws the flag, the player number and the country of one player.
     *
     * @param g       graphics in field coordinates
     * @param country character of the player
     * @param side    side of the player
     * @param centerX x of the center of the tag
     */
    private void paintPlayerTag(Graphics2D g, Country country, TeamSide side, double centerX) {
        BufferedImage flag = sprites.get("flag_" + country.getSpriteKey());
        double flagTop = BOARD_TOP + 8;
        if (flag != null) {
            g.drawImage(flag, (int) (centerX - FLAG_SIZE / 2), (int) flagTop, (int) FLAG_SIZE, (int) FLAG_SIZE, null);
        } else {
            g.setColor(Theme.toColor(country.getColor()));
            g.fillOval((int) (centerX - FLAG_SIZE / 2), (int) flagTop, (int) FLAG_SIZE, (int) FLAG_SIZE);
        }
        Theme.drawCenteredText(g, side.getLabel(), centerX, BOARD_TOP + 70, Theme.font(13f),
                Theme.GOLD, Theme.DARK, 2f);
        Theme.drawCenteredText(g, country.getDisplayName(), centerX, BOARD_TOP + 86, Theme.font(12f),
                Color.WHITE, Theme.DARK, 2f);
    }

    /**
     * Draws the keys of both players at the bottom of the field.
     *
     * @param g graphics in field coordinates
     */
    private void paintKeys(Graphics2D g) {
        g.setColor(Theme.SHADE);
        g.fillRect(0, (int) Field.HEIGHT - 26, (int) Field.WIDTH, 26);
        String text = "J1: " + PlayerKeys.PLAYER_ONE.getHint() + "      |      J2: "
                + PlayerKeys.PLAYER_TWO.getHint() + "      |      P: pausa";
        Theme.drawCenteredText(g, text, Field.centerX(), Field.HEIGHT - 8, Theme.font(13f),
                Color.WHITE, Theme.DARK, 0f);
    }

    /**
     * Darkens the field and shows the pause message.
     *
     * @param g graphics in field coordinates
     */
    private void paintPause(Graphics2D g) {
        g.setColor(Theme.SHADE);
        g.fillRect(0, 0, (int) Field.WIDTH, (int) Field.HEIGHT);
        Theme.drawCenteredText(g, "PAUSA", Field.centerX(), Field.HEIGHT / 2, Theme.font(80f),
                Theme.GOLD, Theme.DARK, 8f);
        Theme.drawCenteredText(g, "Pulsa P o el botón SEGUIR para continuar", Field.centerX(),
                Field.HEIGHT / 2 + 50, Theme.font(24f), Color.WHITE, Theme.DARK, 3f);
    }
}
