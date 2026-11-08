package com.doposoccer.presentation;

import com.doposoccer.domain.DOPOSoccerException;
import com.doposoccer.domain.control.KeyboardControl;
import com.doposoccer.domain.enums.MatchStatus;
import com.doposoccer.domain.enums.TeamSide;
import com.doposoccer.domain.match.Match;
import com.doposoccer.domain.match.MatchConfig;
import com.doposoccer.domain.model.Field;
import com.doposoccer.presentation.input.KeyInputHandler;
import com.doposoccer.presentation.input.KeyboardActionSource;
import com.doposoccer.presentation.input.PlayerKeys;
import com.doposoccer.presentation.render.Hud;
import com.doposoccer.presentation.render.MatchRenderer;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Match screen. It owns the match, the keyboard input and the game loop,
 * and draws every frame scaled to the chosen resolution.
 */
public class GamePanel extends JPanel implements MatchView {

    /** Text of the pause button while the match is running. */
    private static final String PAUSE_TEXT = "PAUSA";

    /** Text of the pause button while the match is paused. */
    private static final String RESUME_TEXT = "SEGUIR";

    /** Main window, used to navigate when the match is over. */
    private final DOPOSoccerGUI gui;

    /** Shared sprite cache. */
    private final SpriteCache sprites;

    /** The match being played. */
    private final Match match;

    /** Keys pressed right now, shared by both players. */
    private final KeyInputHandler input = new KeyInputHandler();

    /** Draws the stadium and the objects of the match. */
    private final MatchRenderer renderer;

    /** Draws the score, the clock and the messages. */
    private final Hud hud;

    /** Thread that updates the match 60 times per second. */
    private final GameLoop loop;

    /** Button that pauses and resumes the match. */
    private final ImageButton pauseButton;

    /** Button that ends the match without winner. */
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
        addFocusListener(input);
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
        pauseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                togglePause();
            }
        });
        endButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                endMatch();
            }
        });
        add(pauseButton);
        add(endButton);
    }

    /** Starts the game loop and gives the keyboard focus to the match. */
    public void start() {
        loop.start();
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                requestFocusInWindow();
            }
        });
    }

    /** Stops the game loop. */
    public void stop() {
        loop.stop();
    }

    /** Pauses or resumes the match and updates the pause button. */
    private void togglePause() {
        try {
            match.togglePause();
            if (match.getStatus() == MatchStatus.PAUSED) {
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

    /** Asks for confirmation and ends the match without winner and without points. */
    private void endMatch() {
        if (match.getStatus().isOver()) {
            return;
        }
        boolean wasPlaying = match.getStatus() == MatchStatus.PLAYING;
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
        renderer.paint(g2, match.getRenderStates());
        hud.paint(g2, match);
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
