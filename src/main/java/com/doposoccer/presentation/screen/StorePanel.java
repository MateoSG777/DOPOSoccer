package com.doposoccer.presentation.screen;
import com.doposoccer.domain.DOPOSoccer;
import com.doposoccer.domain.DOPOSoccerException;
import com.doposoccer.domain.enums.Stat;
import com.doposoccer.domain.enums.TeamSide;
import com.doposoccer.domain.shop.PlayerProfile;
import com.doposoccer.presentation.DOPOSoccerGUI;
import com.doposoccer.presentation.component.ChoiceGroup;
import com.doposoccer.presentation.component.ImageButton;
import com.doposoccer.presentation.resource.SpriteCache;
import com.doposoccer.presentation.resource.SpritePath;
import com.doposoccer.presentation.style.NumberStyle;
import com.doposoccer.presentation.style.TextStyle;
import java.awt.Graphics2D;
import java.awt.Graphics;

/**
 * Store screen: player 1 or player 2 enters and spends its points to improve its stats
 * Each stat is a row of the board tienda_mejoras: a gold ball per level and the price on its bar
 * The upgrades belong to the player, so they apply to every character it chooses
 */
public class StorePanel extends BackgroundPanel {

    private static final int DEFAULT_PLAYER = 0;  // player 1

    // Place of the board on the screen; it keeps the 1100 x 643 ratio of its image
    private static final double BOARD_X = 30;
    private static final double BOARD_Y = 150;
    private static final double BOARD_WIDTH = 640;
    private static final double BOARD_HEIGHT = BOARD_WIDTH * 643 / 1100;

    // Positions measured on the image, as fractions of its size; the rows follow the order of Stat
    private static final double[] BAR_Y = {0.1246, 0.3493, 0.5751, 0.7993};
    private static final double[] DOTS_Y = {0.2143, 0.4395, 0.6689, 0.8942};
    private static final double FIRST_DOT_X = 0.0676;
    private static final double DOT_STEP = 0.0409;
    private static final double DOT_SIZE = 0.0330;
    private static final double PRICE_RIGHT = 0.865;  // the price ends just before the word POINT
    private static final double PRICE_HEIGHT = 0.034;

    private final DOPOSoccer game;
    private final TeamSide[] sides = TeamSide.values();
    private final Stat[] stats = Stat.values();
    private final ChoiceGroup players;
    private final ImageButton[] upgradeButtons = new ImageButton[stats.length];
    private final ImageButton backButton;

    /**
     * Creates the store screen
     * @param gui main window, used to navigate
     * @param sprites shared sprite cache
     * @param game facade of the domain
     */
    public StorePanel(final DOPOSoccerGUI gui, SpriteCache sprites, DOPOSoccer game) {
        super(sprites, SpritePath.background("bg_store"), 0);
        this.game = game;
        String[] labels = new String[sides.length];
        for (int i = 0; i < sides.length; i++) {
            labels[i] = sides[i].getLabel();
        }
        players = new ChoiceGroup(labels, DEFAULT_PLAYER, sprites, this);
        players.setOnChange(() -> refresh());
        for (int i = 0; i < stats.length; i++) {
            final Stat stat = stats[i];
            upgradeButtons[i] = new ImageButton("MEJORAR", sprites);
            upgradeButtons[i].addActionListener(e -> upgrade(stat));
            add(upgradeButtons[i]);
        }
        backButton = new ImageButton("ATRÁS", sprites);
        backButton.addActionListener(e -> gui.showMenu());
        add(backButton);
        refresh();
    }

    private void upgrade(Stat stat) {
        try {
            game.upgrade(getSide(), stat);
        } catch (DOPOSoccerException e) {
            DOPOSoccerGUI.showError(this, e);
        }
        refresh();
    }

    /** Shows the points and levels of the chosen player; a stat at its last level cannot be bought */
    public void refresh() {
        PlayerProfile profile = game.getProfile(getSide());
        for (int i = 0; i < stats.length; i++) {
            upgradeButtons[i].setEnabled(!profile.isMaxed(stats[i]));
        }
        repaint();
    }

    private TeamSide getSide() {
        return sides[players.getSelectedIndex()];
    }

    /** Places every control using design coordinates */
    @Override
    public void doLayout() {
        for (int i = 0; i < players.size(); i++) {
            place(players.getButton(i), 30 + i * 215, 72, 205, 66);
        }
        for (int i = 0; i < stats.length; i++) {
            double rowCenter = BOARD_Y + BOARD_HEIGHT * (BAR_Y[i] + DOTS_Y[i]) / 2;
            place(upgradeButtons[i], 700, rowCenter - 33, 230, 66);
        }
        place(backButton, 387, 540, 250, 90);
    }

    /**
     * Paints the background, the points of the player and the board with its levels and prices
     * @param g graphics given by Swing
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = designGraphics(g);
        paintBottomBand(g2);
        TextStyle.TITLE.draw(g2, "TIENDA", DESIGN_WIDTH / 2, 50);
        PlayerProfile profile = game.getProfile(getSide());
        NumberStyle.BIG.drawToLeft(g2, sprites, String.valueOf(profile.getPoints()), 850, 105, 46);
        TextStyle.SUBTITLE.drawFromLeft(g2, "PUNTOS", 860, 116);
        g2.drawImage(sprites.get(SpritePath.ui("tienda_mejoras")), (int) BOARD_X, (int) BOARD_Y,
                (int) BOARD_WIDTH, (int) BOARD_HEIGHT, null);
        for (int i = 0; i < stats.length; i++) {
            paintLevel(g2, i, profile.getLevel(stats[i]));
            paintPrice(g2, i, profile);
        }
        g2.dispose();
    }

    private void paintLevel(Graphics2D g, int row, int level) {
        double size = BOARD_WIDTH * DOT_SIZE;
        double y = BOARD_Y + BOARD_HEIGHT * DOTS_Y[row];
        for (int j = 0; j < level; j++) {
            double x = BOARD_X + BOARD_WIDTH * (FIRST_DOT_X + j * DOT_STEP);
            drawCentered(g, SpritePath.ui("bolita_oro"), x, y, size, size);
        }
    }

    private void paintPrice(Graphics2D g, int row, PlayerProfile profile) {
        double right = BOARD_X + BOARD_WIDTH * PRICE_RIGHT;
        double y = BOARD_Y + BOARD_HEIGHT * BAR_Y[row];
        if (profile.isMaxed(stats[row])) {
            TextStyle.SECTION.draw(g, "MÁX", right - 25, y + 8);
        } else {
            NumberStyle.SMALL.drawToLeft(g, sprites, String.valueOf(profile.getCost(stats[row])), right, y,
                    BOARD_HEIGHT * PRICE_HEIGHT);
        }
    }
}
