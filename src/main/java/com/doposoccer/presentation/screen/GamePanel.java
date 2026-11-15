package com.doposoccer.presentation.screen;

import com.doposoccer.domain.DOPOSoccer;
import com.doposoccer.domain.DOPOSoccerException;
import com.doposoccer.domain.match.MatchSnapshot;
import com.doposoccer.domain.model.Field;
import com.doposoccer.persistence.MatchSlots;
import com.doposoccer.presentation.DOPOSoccerGUI;
import com.doposoccer.presentation.component.ImageButton;
import com.doposoccer.presentation.input.KeyInputHandler;
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
 * Match screen. It owns the game loop and listens to the keyboard,
 * and draws every frame scaled to the chosen resolution.
 * While the match is paused it can be saved in one of the three slots.
 */
public class GamePanel extends JPanel implements MatchView {

    private static final String PAUSE_TEXT = "PAUSA";
    private static final String RESUME_TEXT = "SEGUIR";

    private final DOPOSoccerGUI gui;
    private final SpriteCache sprites;
    private final DOPOSoccer game;
    private final KeyInputHandler input;
    private final MatchRenderer renderer;
    private final Hud hud;
    private final GameLoop loop;
    private final ImageButton pauseButton;
    private final ImageButton endButton;
    private final ImageButton[] saveButtons = new ImageButton[MatchSlots.SLOTS];

    /**
     * Creates the screen of the match that the game already started.
     *
     * @param gui     main window
     * @param sprites shared sprite cache
     * @param game    facade of the domain, with the match ready to play
     * @param input   pressed keys read by the human players
     */
    public GamePanel(DOPOSoccerGUI gui, SpriteCache sprites, DOPOSoccer game, KeyInputHandler input) {
        super(null);
        this.gui = gui;
        this.sprites = sprites;
        this.game = game;
        this.input = input;
        this.renderer = new MatchRenderer(sprites);
        this.hud = new Hud(sprites);
        this.loop = new GameLoop(game, this);

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
        for (int i = 0; i < saveButtons.length; i++) {
            final int slot = i + 1;
            saveButtons[i] = new ImageButton("GUARDAR " + slot, sprites);
            saveButtons[i].addActionListener(e -> saveMatch(slot));
            add(saveButtons[i]);
        }
        showPauseControls();
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
            game.togglePause();
            showPauseControls();
            repaint();
        } catch (DOPOSoccerException e) {
            DOPOSoccerGUI.showError(this, e);
        }
        requestFocusInWindow();
    }

    private void showPauseControls() {
        boolean paused = game.isPaused();
        pauseButton.setText(paused ? RESUME_TEXT : PAUSE_TEXT);
        for (int i = 0; i < saveButtons.length; i++) {
            saveButtons[i].setVisible(paused);
        }
    }

    private void saveMatch(int slot) {
        try {
            game.saveMatch(slot);
            JOptionPane.showMessageDialog(this, "Partido guardado en el espacio " + slot + ".",
                    DOPOSoccerGUI.TITLE, JOptionPane.INFORMATION_MESSAGE);
        } catch (DOPOSoccerException e) {
            DOPOSoccerGUI.showError(this, e);
        }
        requestFocusInWindow();
    }

    private void endMatch() {
        if (game.isOver()) {
            return;
        }
        boolean wasPlaying = !game.isPaused();
        if (wasPlaying) {
            togglePause();
        }
        int answer = JOptionPane.showConfirmDialog(this,
                "¿Terminar el partido ahora? No habrá ganador ni puntos para nadie.",
                DOPOSoccerGUI.TITLE, JOptionPane.YES_NO_OPTION);
        try {
            if (answer == JOptionPane.YES_OPTION) {
                game.endMatch();
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
        for (int i = 0; i < saveButtons.length; i++) {
            double left = Field.centerX() - 300 + i * 205;
            saveButtons[i].setBounds((int) (left * s), (int) ((Field.HEIGHT / 2 + 85) * s), (int) (190 * s), (int) (64 * s));
        }
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
        MatchSnapshot snapshot = game.snapshot();
        renderer.paint(g2, snapshot.getRenderStates());
        hud.paint(g2, snapshot, game.getConfig());
        g2.dispose();
    }

    /** Asks Swing to repaint. */
    @Override
    public void refresh() {
        repaint();
    }

    /** Shows the result of the match and goes to a rematch or to the menu. */
    @Override
    public void matchOver() {
        repaint();
        ResultDialog dialog = new ResultDialog(gui, sprites, game);
        dialog.setVisible(true);
        if (dialog.wantsRematch()) {
            gui.rematch();
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
}
