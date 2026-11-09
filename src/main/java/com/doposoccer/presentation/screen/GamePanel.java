package com.doposoccer.presentation.screen;

import com.doposoccer.domain.DOPOSoccerException;
import com.doposoccer.domain.control.KeyboardControl;
import com.doposoccer.domain.enums.TeamSide;
import com.doposoccer.domain.match.Match;
import com.doposoccer.domain.match.MatchConfig;
import com.doposoccer.domain.match.MatchSnapshot;
import com.doposoccer.domain.match.status.MatchStatusBehavior;
import com.doposoccer.domain.model.Field;
import com.doposoccer.presentation.DOPOSoccerGUI;
import com.doposoccer.presentation.component.ImageButton;
import com.doposoccer.presentation.input.KeyInputHandler;
import com.doposoccer.presentation.input.KeyboardActionSource;
import com.doposoccer.presentation.input.PlayerKeys;
import com.doposoccer.presentation.loop.GameLoop;
import com.doposoccer.presentation.loop.MatchView;
import com.doposoccer.presentation.render.Hud;
import com.doposoccer.presentation.render.MatchRenderer;
import com.doposoccer.presentation.resource.SpriteCache;
import com.doposoccer.presentation.style.Theme;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import java.awt.Graphics2D;
import java.awt.Graphics;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Match screen. It owns the match, the keyboard input and the game loop,
 * and draws every frame scaled to the chosen resolution.
 */
public class GamePanel extends JPanel implements MatchView {

    private static final String PAUSE_TEXT = "PAUSA";
    private static final String RESUME_TEXT = "SEGUIR";

    private final DOPOSoccerGUI gui;
    private final SpriteCache sprites;
    private final Match match;
    private final KeyInputHandler input = new KeyInputHandler();
    private final MatchRenderer renderer;
    private final Hud hud;
    private final GameLoop loop;
    private final ImageButton pauseButton;
    private final ImageButton endButton;

    /**
     * Creates the match screen: both players are driven by the keyboard.
     *
     * @param gui     main window
     * @param sprites shared sprite cache
     * @param config  validated configuration of the match
     */
    public GamePanel(DOPOSoccerGUI gui, SpriteCache sprites, MatchConfig config) {
        super(null);
        this.gui = gui;
        this.sprites = sprites;
        this.match = new Match(config);
        this.renderer = new MatchRenderer(sprites);
        this.hud = new Hud(sprites);
        this.loop = new GameLoop(match, this);

        TeamSide[] sides = TeamSide.values();
        for (int i = 0; i < sides.length; i++) {
            match.setControl(sides[i], new KeyboardControl(new KeyboardActionSource(PlayerKeys.of(sides[i]), input)));
        }

        setFocusable(true);
        addKeyListener(input);
        addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                input.releaseAll();
            }
        });
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_P) {
                    togglePause();
                }
            }
        });
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                requestFocusInWindow();
            }
        });

        pauseButton = new ImageButton(PAUSE_TEXT, sprites);
        endButton = new ImageButton("TERMINAR", sprites);
        pauseButton.addActionListener(e -> togglePause());
        endButton.addActionListener(e -> endMatch());
        add(pauseButton);
        add(endButton);
    }

    /** Starts the game loop and gives the keyboard focus to the match. */
    public void start() {
        loop.start();
        SwingUtilities.invokeLater(() -> requestFocusInWindow());
    }

    /** Stops the game loop. */
    public void stop() {
        loop.stop();
    }

    private void togglePause() {
        try {
            match.togglePause();
            if (MatchStatusBehavior.get(match.getStatus()).isPaused()) {
                pauseButton.setText(RESUME_TEXT);
            } else {
                pauseButton.setText(PAUSE_TEXT);
            }
            repaint();
        } catch (DOPOSoccerException e) {
            DOPOSoccerGUI.showError(this, e);
        }
        requestFocusInWindow();
    }

    private void endMatch() {
        if (MatchStatusBehavior.get(match.getStatus()).isOver()) {
            return;
        }
        boolean wasPlaying = MatchStatusBehavior.get(match.getStatus()).advancesTime();
        if (wasPlaying) {
            togglePause();
        }
        int answer = JOptionPane.showConfirmDialog(this,
                "¿Terminar el partido ahora? No habrá ganador ni puntos para nadie.",
                DOPOSoccerGUI.TITLE, JOptionPane.YES_NO_OPTION);
        try {
            if (answer == JOptionPane.YES_OPTION) {
                match.end();
            } else if (wasPlaying) {
                togglePause();
            }
        } catch (DOPOSoccerException e) {
            DOPOSoccerGUI.showError(this, e);
        }
        requestFocusInWindow();
    }

    /** Places the buttons scaled to the size of the screen. */
    @Override
    public void doLayout() {
        double s = getWidth() / Field.WIDTH;
        pauseButton.setBounds((int) (12 * s), (int) (12 * s), (int) (170 * s), (int) (64 * s));
        endButton.setBounds((int) ((Field.WIDTH - 182) * s), (int) (12 * s), (int) (170 * s), (int) (64 * s));
    }

    /**
     * Draws the current frame scaled to the size of the screen.
     *
     * @param g graphics given by Swing
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        Theme.applyQuality(g2);
        g2.scale(getWidth() / Field.WIDTH, getHeight() / Field.HEIGHT);
        MatchSnapshot snapshot = match.snapshot();
        renderer.paint(g2, snapshot.getRenderStates());
        hud.paint(g2, snapshot, match.getConfig());
        g2.dispose();
    }

    /** Asks Swing to repaint; safe to call from the game loop thread. */
    @Override
    public void refresh() {
        repaint();
    }

    /** Shows the result of the match and goes to a rematch or to the menu. */
    @Override
    public void matchOver() {
        repaint();
        ResultDialog dialog = new ResultDialog(gui, sprites, match);
        dialog.setVisible(true);
        if (dialog.wantsRematch()) {
            gui.startMatch(match.getConfig());
        } else {
            gui.showMenu();
        }
    }

    /**
     * Shows an unexpected error and goes back to the menu.
     *
     * @param error the error that stopped the match
     */
    @Override
    public void showError(Exception error) {
        DOPOSoccerGUI.showError(this, error);
        gui.showMenu();
    }

    /**
     * Returns the match shown by this screen.
     *
     * @return the match
     */
    public Match getMatch() {
        return match;
    }
}
