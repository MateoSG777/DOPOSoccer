package com.doposoccer.presentation;

import com.doposoccer.domain.DOPOSoccerException;
import com.doposoccer.domain.Log;
import com.doposoccer.domain.enums.Resolution;
import com.doposoccer.domain.enums.TeamSide;
import com.doposoccer.domain.match.MatchConfig;
import com.doposoccer.domain.match.ResolutionInfo;
import com.doposoccer.presentation.resource.SpriteCache;
import com.doposoccer.presentation.screen.CharacterPanel;
import com.doposoccer.presentation.screen.ConfigPanel;
import com.doposoccer.presentation.screen.GamePanel;
import com.doposoccer.presentation.screen.MenuPanel;
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

/**
 * Main window of DOPOSoccer. It navigates between the screens: menu,
 * configuration, character selection and match
 */
public class DOPOSoccerGUI extends JFrame {

    /** Title of the window*/
    public static final String TITLE = "DOPOSoccer";

    private static final String MENU = "menu";
    private static final String CONFIG = "config";
    private static final String CHARACTERS = "characters";
    private static final String GAME = "game";
    private static final Resolution MENU_RESOLUTION = Resolution.MEDIUM;

    private final SpriteCache sprites = new SpriteCache();
    private final CardLayout cards = new CardLayout();
    private final JPanel screens = new JPanel(cards);
    private final ConfigPanel configPanel;
    private final CharacterPanel characterPanel;
    private GamePanel gamePanel;

    /** Creates the window with all the screens and shows the main menu*/
    public DOPOSoccerGUI() {
        super(TITLE);
        configPanel = new ConfigPanel(this, sprites);
        characterPanel = new CharacterPanel(this, sprites);
        screens.add(new MenuPanel(this, sprites), MENU);
        screens.add(configPanel, CONFIG);
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

    /** Shows the main menu*/
    public void showMenu() {
        stopMatch();
        resizeTo(MENU_RESOLUTION);
        cards.show(screens, MENU);
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
        try {
            MatchConfig config = new MatchConfig(configPanel.getMode(), configPanel.getResolution(),
                    characterPanel.getCountry(TeamSide.LEFT), characterPanel.getCountry(TeamSide.RIGHT),
                    configPanel.getMatchType(), configPanel.getTarget());
            startMatch(config);
        } catch (DOPOSoccerException e) {
            showError(this, e);
        }
    }

    /**
     * Starts a match with a configuration
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

    private void stopMatch() {
        if (gamePanel != null) {
            gamePanel.stop();
            screens.remove(gamePanel);
            gamePanel = null;
        }
    }

    private void resizeTo(Resolution size) {
        ResolutionInfo pixels = ResolutionInfo.get(size);
        screens.setPreferredSize(new Dimension(pixels.getWidth(), pixels.getHeight()));
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
