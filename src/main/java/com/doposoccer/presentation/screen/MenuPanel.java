package com.doposoccer.presentation.screen;
import com.doposoccer.presentation.DOPOSoccerGUI;
import com.doposoccer.presentation.component.ImageButton;
import com.doposoccer.presentation.resource.SpriteCache;
import com.doposoccer.presentation.resource.SpritePath;
import com.doposoccer.presentation.style.Palette;
import com.doposoccer.presentation.style.Theme;
import java.awt.Graphics2D;
import java.awt.Graphics;

/**
 * Main menu: game logo, a character and the main buttons
 * The store is shown disabled because it arrives in version two
 */
public class MenuPanel extends BackgroundPanel {

    private static final double WATERMARK_CROP = 0.12;  // the copyright text uses the last 12 % of menu.png

    private final ImageButton playButton;
    private final ImageButton storeButton;
    private final ImageButton exitButton;

    /**
     * Creates the main menu
     * @param gui main window, used to navigate
     * @param sprites shared sprite cache
     */
    public MenuPanel(final DOPOSoccerGUI gui, SpriteCache sprites) {
        super(sprites, SpritePath.background("menu"), WATERMARK_CROP);
        playButton = new ImageButton("JUGAR", sprites);
        storeButton = new ImageButton("TIENDA", sprites);
        exitButton = new ImageButton("SALIR", sprites);
        storeButton.setEnabled(false);
        storeButton.setToolTipText("Disponible en la versión 2");

        playButton.addActionListener(e -> gui.showConfig());
        exitButton.addActionListener(e -> gui.exit());
        add(playButton);
        add(storeButton);
        add(exitButton);
    }

    /** Places the buttons under the logo, on the left half of the screen*/
    @Override
    public void doLayout() {
        place(playButton, 120, 330, 300, 100);
        place(storeButton, 120, 425, 300, 100);
        place(exitButton, 120, 520, 300, 100);
    }

    /**
     * Paints the background, the logo and the character
     * @param g graphics given by Swing
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = designGraphics(g);
        g2.setColor(Theme.color(Palette.SHADE));
        g2.fillRect(0, 0, (int) DESIGN_WIDTH, (int) DESIGN_HEIGHT);
        drawCentered(g2, SpritePath.ui("menu_ch"), 760, 360, 600, 400);
        drawCentered(g2, SpritePath.ui("logo"), 270, 170, 440, 257);
        g2.dispose();
    }
}
