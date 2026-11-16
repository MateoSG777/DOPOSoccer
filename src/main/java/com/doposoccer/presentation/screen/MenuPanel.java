package com.doposoccer.presentation.screen;
import com.doposoccer.presentation.DOPOSoccerGUI;
import com.doposoccer.presentation.component.GearButton;
import com.doposoccer.presentation.component.ImageButton;
import com.doposoccer.presentation.resource.SpriteCache;
import com.doposoccer.presentation.resource.SpritePath;
import com.doposoccer.presentation.style.Palette;
import com.doposoccer.presentation.style.Theme;
import java.awt.Graphics2D;
import java.awt.Graphics;
import javax.swing.Timer;

/**
 * Main menu: game logo, a ball that keeps spinning in its place, the main buttons and the settings gear
 */
public class MenuPanel extends BackgroundPanel {

    private static final double WATERMARK_CROP = 0.12;  // the copyright text uses the last 12 % of menu.png
    private static final double BALL_X = 740;  // center of the free space on the right of the buttons
    private static final double BALL_Y = 330;
    private static final double BALL_SIZE = 330;
    private static final double DEGREES_PER_SECOND = 90;  // one turn every four seconds
    private static final int FRAME_MS = 16;

    private final ImageButton playButton;
    private final ImageButton loadButton;
    private final ImageButton storeButton;
    private final ImageButton exitButton;
    private final GearButton settingsButton = new GearButton();
    private final long start = System.nanoTime();

    /**
     * Creates the main menu
     * @param gui main window, used to navigate
     * @param sprites shared sprite cache
     */
    public MenuPanel(final DOPOSoccerGUI gui, SpriteCache sprites) {
        super(sprites, SpritePath.background("menu"), WATERMARK_CROP);
        playButton = new ImageButton("JUGAR", sprites);
        loadButton = new ImageButton("CARGAR", sprites);
        storeButton = new ImageButton("TIENDA", sprites);
        exitButton = new ImageButton("SALIR", sprites);

        playButton.addActionListener(e -> gui.showConfig());
        loadButton.addActionListener(e -> gui.showLoad());
        storeButton.addActionListener(e -> gui.showStore());
        exitButton.addActionListener(e -> gui.exit());
        settingsButton.setToolTipText("Ajustes");
        settingsButton.addActionListener(e -> gui.showSettings());
        add(playButton);
        add(loadButton);
        add(storeButton);
        add(exitButton);
        add(settingsButton);
        new Timer(FRAME_MS, e -> spin()).start();
    }

    private void spin() {
        if (isShowing()) {
            repaint();
        }
    }

    /** Places the buttons under the logo, on the left half of the screen*/
    @Override
    public void doLayout() {
        place(playButton, 135, 300, 270, 86);
        place(loadButton, 135, 382, 270, 86);
        place(storeButton, 135, 464, 270, 86);
        place(exitButton, 135, 546, 270, 86);
        place(settingsButton, 940, 24, 60, 60);
    }

    /**
     * Paints the background, the logo and the spinning ball
     * @param g graphics given by Swing
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = designGraphics(g);
        g2.setColor(Theme.color(Palette.SHADE));
        g2.fillRect(0, 0, (int) DESIGN_WIDTH, (int) DESIGN_HEIGHT);
        double seconds = (System.nanoTime() - start) / 1e9;
        Graphics2D ball = (Graphics2D) g2.create();
        ball.rotate(Math.toRadians(seconds * DEGREES_PER_SECOND), BALL_X, BALL_Y);
        drawCentered(ball, SpritePath.ui("ball-2"), BALL_X, BALL_Y, BALL_SIZE, BALL_SIZE);
        ball.dispose();
        drawCentered(g2, SpritePath.ui("logo"), 270, 170, 440, 257);
        g2.dispose();
    }
}
