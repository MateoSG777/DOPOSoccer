package com.doposoccer.presentation;

import com.doposoccer.domain.DOPOSoccer;
import com.doposoccer.domain.DOPOSoccerException;
import com.doposoccer.domain.Log;
import com.doposoccer.domain.control.ActionSource;
import com.doposoccer.domain.enums.Resolution;
import com.doposoccer.domain.enums.TeamSide;
import com.doposoccer.domain.match.MatchBuilder;
import com.doposoccer.presentation.input.KeyInputHandler;
import com.doposoccer.presentation.input.KeyboardActionSource;
import com.doposoccer.presentation.input.PlayerKeys;
import com.doposoccer.presentation.resource.SpriteCache;
import com.doposoccer.presentation.screen.CharacterPanel;
import com.doposoccer.presentation.screen.ConfigPanel;
import com.doposoccer.presentation.screen.GamePanel;
import com.doposoccer.presentation.screen.LoadPanel;
import com.doposoccer.presentation.screen.MenuPanel;
import com.doposoccer.presentation.screen.SettingsPanel;
import com.doposoccer.presentation.screen.StorePanel;
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
 * Main window of DOPOSoccer. It navigates between the screens: menu, settings, saved matches,
 * store, configuration, character selection and match
 */
public class DOPOSoccerGUI extends JFrame {

    /** Title of the window*/
    public static final String TITLE = "DOPOSoccer";

    private static final String MENU = "menu";
    private static final String CONFIG = "config";
    private static final String CHARACTERS = "characters";
    private static final String GAME = "game";
    private static final String STORE = "store";
    private static final String LOAD = "load";
    private static final String SETTINGS = "settings";
    private static final Resolution MENU_RESOLUTION = Resolution.MEDIUM;

    private final DOPOSoccer game = new DOPOSoccer();
    private final SpriteCache sprites = new SpriteCache();
    private final CardLayout cards = new CardLayout();
    private final JPanel screens = new JPanel(cards);
    private final ConfigPanel configPanel;
    private final CharacterPanel characterPanel;
    private final StorePanel storePanel;
    private final LoadPanel loadPanel;
    private final SettingsPanel settingsPanel;
    private GamePanel gamePanel;

    /** Creates the window with all the screens and shows the main menu*/
    public DOPOSoccerGUI() {
        super(TITLE);
        configPanel = new ConfigPanel(this, sprites);
        characterPanel = new CharacterPanel(this, sprites);
        storePanel = new StorePanel(this, sprites, game);
        loadPanel = new LoadPanel(this, sprites, game);
        settingsPanel = new SettingsPanel(this, sprites);
        screens.add(new MenuPanel(this, sprites), MENU);
        screens.add(configPanel, CONFIG);
        screens.add(characterPanel, CHARACTERS);
        screens.add(storePanel, STORE);
        screens.add(loadPanel, LOAD);
        screens.add(settingsPanel, SETTINGS);
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

    /** Shows the main menu*/
    public void showMenu() {
        stopMatch();
        resizeTo(MENU_RESOLUTION);
        cards.show(screens, MENU);
    }

    /** Shows the store with the current points of the players*/
    public void showStore() {
        storePanel.refresh();
        cards.show(screens, STORE);
    }

    /** Shows the three slots of saved matches*/
    public void showLoad() {
        loadPanel.refresh();
        cards.show(screens, LOAD);
    }

    /** Shows the settings screen, where the resolution of the field is chosen*/
    public void showSettings() {
        cards.show(screens, SETTINGS);
    }

    /** Shows the match configuration screen*/
    public void showConfig() {
        cards.show(screens, CONFIG);
    }

    /** Shows the character selection screen*/
    public void showCharacters() {
        cards.show(screens, CHARACTERS);
    }

    /**
     * Validates the chosen options and starts a match
     * If something is wrong, a clear message is shown instead
     */
    public void play() {
        MatchBuilder builder = new MatchBuilder()
                .mode(configPanel.getMode())
                .resolution(settingsPanel.getResolution())
                .rules(configPanel.getMatchType(), configPanel.getTarget())
                .machine(configPanel.getMachine());
        TeamSide[] sides = TeamSide.values();
        for (int i = 0; i < sides.length; i++) {
            builder.country(sides[i], characterPanel.getCountry(sides[i]));
        }
        start(builder);
    }

    /** Plays again the match that just ended, with the same options */
    public void rematch() {
        start(new MatchBuilder().copy(game.getConfig()));
    }

    private void start(MatchBuilder builder) {
        KeyInputHandler input = new KeyInputHandler();
        Map<TeamSide, ActionSource> sources = keyboard(input);
        TeamSide[] sides = TeamSide.values();
        for (int i = 0; i < sides.length; i++) {
            builder.source(sides[i], sources.get(sides[i]));
        }
        try {
            game.startMatch(builder);
            showMatch(input);
        } catch (DOPOSoccerException e) {
            showError(this, e);
        }
    }

    /**
     * Opens the match saved in a slot. It starts paused, so the players can get ready
     * @param slot slot from 1 to 3
     */
    public void loadMatch(int slot) {
        KeyInputHandler input = new KeyInputHandler();
        try {
            game.loadMatch(slot, keyboard(input));
            showMatch(input);
        } catch (DOPOSoccerException e) {
            showError(this, e);
        }
    }

    private Map<TeamSide, ActionSource> keyboard(KeyInputHandler input) {
        Map<TeamSide, ActionSource> sources = new EnumMap<>(TeamSide.class);
        TeamSide[] sides = TeamSide.values();
        for (int i = 0; i < sides.length; i++) {
            sources.put(sides[i], new KeyboardActionSource(PlayerKeys.of(sides[i]), input));
        }
        return sources;
    }

    private void showMatch(KeyInputHandler input) {
        stopMatch();
        gamePanel = new GamePanel(this, sprites, game, input);
        screens.add(gamePanel, GAME);
        resizeTo(game.getConfig().getResolution());
        cards.show(screens, GAME);
        gamePanel.start();
    }

    private void stopMatch() {
        if (gamePanel != null) {
            gamePanel.stop();
            screens.remove(gamePanel);
            gamePanel = null;
        }
    }

    private void resizeTo(Resolution size) {
        screens.setPreferredSize(new Dimension(size.getWidth(), size.getHeight()));
        pack();
        setLocationRelativeTo(null);
    }

    /** Asks for confirmation and closes the game*/
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
     * Shows a clear message to the user and records the details in the log
     * @param parent component the message belongs to
     * @param e error to report
     */
    public static void showError(Component parent, Exception e) {
        Log.record(e);
        JOptionPane.showMessageDialog(parent, e.getMessage(), TITLE, JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Starts DOPOSoccer
     * @param args not used
     */
    public static void main(String[] args) {
        Thread.setDefaultUncaughtExceptionHandler((thread, error) ->
                Log.record(new RuntimeException("Unexpected error in " + thread.getName(), error)));
        SwingUtilities.invokeLater(() -> new DOPOSoccerGUI().setVisible(true));
    }
}
