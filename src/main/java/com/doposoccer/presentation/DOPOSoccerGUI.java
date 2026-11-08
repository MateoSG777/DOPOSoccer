package com.doposoccer.presentation;

import com.doposoccer.domain.DOPOSoccerException;
import com.doposoccer.domain.Log;
import com.doposoccer.domain.enums.Country;
import com.doposoccer.domain.enums.GameMode;
import com.doposoccer.domain.enums.MatchType;
import com.doposoccer.domain.enums.Resolution;
import com.doposoccer.domain.enums.TeamSide;
import com.doposoccer.domain.match.MatchConfig;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.EnumMap;
import java.util.Map;

/**
 * Main window of DOPOSoccer. It navigates between the screens (menu,
 * configuration, character selection and match) and keeps the options
 * chosen by the users until the match starts.
 */
public class DOPOSoccerGUI extends JFrame {

    /** Title of the window. */
    public static final String TITLE = "DOPOSoccer";

    /** Name of the menu screen in the card layout. */
    private static final String MENU = "menu";

    /** Name of the configuration screen in the card layout. */
    private static final String CONFIG = "config";

    /** Name of the character selection screen in the card layout. */
    private static final String CHARACTERS = "characters";

    /** Name of the match screen in the card layout. */
    private static final String GAME = "game";

    /** Size of the menus (the match uses the chosen resolution). */
    private static final Resolution MENU_RESOLUTION = Resolution.MEDIUM;

    /** Shared sprite cache (Flyweight). */
    private final SpriteCache sprites = new SpriteCache();

    /** Layout that shows one screen at a time. */
    private final CardLayout cards = new CardLayout();

    /** Container of all the screens. */
    private final JPanel screens = new JPanel(cards);

    /** Character selection screen (refreshed every time it is shown). */
    private final CharacterPanel characterPanel;

    /** Current match screen, or null when no match is being played. */
    private GamePanel gamePanel;

    /** Chosen game mode. */
    private GameMode mode = GameMode.PLAYER_VS_PLAYER;

    /** Chosen field resolution. */
    private Resolution resolution = Resolution.MEDIUM;

    /** Chosen type of match. */
    private MatchType matchType = MatchType.BY_GOALS;

    /** Minutes or goals to win. */
    private int target = MatchType.BY_GOALS.getDefaultTarget();

    /** Character chosen by each player. */
    private final Map<TeamSide, Country> countries = new EnumMap<>(TeamSide.class);

    /** Creates the window with all the screens and shows the main menu. */
    public DOPOSoccerGUI() {
        super(TITLE);
        countries.put(TeamSide.LEFT, Country.KOREA);
        countries.put(TeamSide.RIGHT, Country.USA);

        characterPanel = new CharacterPanel(this, sprites);
        screens.add(new MenuPanel(this, sprites), MENU);
        screens.add(new ConfigPanel(this, sprites), CONFIG);
        screens.add(characterPanel, CHARACTERS);
        setContentPane(screens);

        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                exit();
            }
        });
        setResizable(false);
        resizeTo(MENU_RESOLUTION);
        setLocationRelativeTo(null);
    }

    /** Shows the main menu. */
    public void showMenu() {
        stopMatch();
        resizeTo(MENU_RESOLUTION);
        cards.show(screens, MENU);
    }

    /** Shows the match configuration screen. */
    public void showConfig() {
        cards.show(screens, CONFIG);
    }

    /** Shows the character selection screen. */
    public void showCharacters() {
        characterPanel.repaint();
        cards.show(screens, CHARACTERS);
    }

    /**
     * Validates the chosen options and starts a match.
     * If something is wrong, a clear message is shown instead.
     */
    public void play() {
        try {
            MatchConfig config = new MatchConfig(mode, resolution, countries.get(TeamSide.LEFT),
                    countries.get(TeamSide.RIGHT), matchType, target);
            startMatch(config);
        } catch (DOPOSoccerException e) {
            showError(this, e);
        }
    }

    /**
     * Starts a match with a configuration (also used for a rematch).
     *
     * @param config validated configuration
     */
    public void startMatch(MatchConfig config) {
        stopMatch();
        gamePanel = new GamePanel(this, sprites, config);
        screens.add(gamePanel, GAME);
        resizeTo(config.getResolution());
        cards.show(screens, GAME);
        gamePanel.start();
    }

    /** Stops and removes the current match, if any. */
    private void stopMatch() {
        if (gamePanel != null) {
            gamePanel.stop();
            screens.remove(gamePanel);
            gamePanel = null;
        }
    }

    /**
     * Sets the size of the drawing area and centers the window.
     *
     * @param size resolution of the drawing area
     */
    private void resizeTo(Resolution size) {
        screens.setPreferredSize(new Dimension(size.getWidth(), size.getHeight()));
        pack();
        setLocationRelativeTo(null);
    }

    /** Asks for confirmation and closes the game. */
    public void exit() {
        int answer = JOptionPane.showConfirmDialog(this, "¿Seguro que quieres salir de DOPOSoccer?",
                TITLE, JOptionPane.YES_NO_OPTION);
        if (answer == JOptionPane.YES_OPTION) {
            stopMatch();
            dispose();
            System.exit(0);
        }
    }

    /**
     * Shows a clear message to the user and records the details in the log.
     *
     * @param parent component the message belongs to
     * @param e      error to report
     */
    public static void showError(Component parent, Exception e) {
        Log.record(e);
        JOptionPane.showMessageDialog(parent, e.getMessage(), TITLE, JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Returns the chosen game mode.
     *
     * @return the chosen game mode
     */
    public GameMode getMode() {
        return mode;
    }

    /**
     * Chooses the game mode.
     *
     * @param mode new game mode
     */
    public void setMode(GameMode mode) {
        this.mode = mode;
    }

    /**
     * Returns the chosen resolution.
     *
     * @return the chosen resolution
     */
    public Resolution getResolution() {
        return resolution;
    }

    /**
     * Chooses the field resolution.
     *
     * @param resolution new resolution
     */
    public void setResolution(Resolution resolution) {
        this.resolution = resolution;
    }

    /**
     * Returns the chosen type of match.
     *
     * @return the chosen type of match
     */
    public MatchType getMatchType() {
        return matchType;
    }

    /**
     * Chooses the type of match and resets the target to its default value.
     *
     * @param matchType new type of match
     */
    public void setMatchType(MatchType matchType) {
        this.matchType = matchType;
        this.target = matchType.getDefaultTarget();
    }

    /**
     * Returns the minutes or goals to win.
     *
     * @return the chosen target
     */
    public int getTarget() {
        return target;
    }

    /**
     * Chooses the minutes or goals to win.
     *
     * @param target new target
     */
    public void setTarget(int target) {
        this.target = target;
    }

    /**
     * Returns the character chosen by a player.
     *
     * @param side side of the player
     * @return the chosen character
     */
    public Country getCountry(TeamSide side) {
        return countries.get(side);
    }

    /**
     * Chooses the character of a player.
     *
     * @param side    side of the player
     * @param country new character
     */
    public void setCountry(TeamSide side, Country country) {
        countries.put(side, country);
    }

    /**
     * Starts DOPOSoccer.
     *
     * @param args not used
     */
    public static void main(String[] args) {
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable error) {
                Log.record(new RuntimeException("Unexpected error in " + thread.getName(), error));
            }
        });
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new DOPOSoccerGUI().setVisible(true);
            }
        });
    }
}
