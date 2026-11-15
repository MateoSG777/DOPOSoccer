package com.doposoccer.presentation.screen;
import com.doposoccer.domain.DOPOSoccer;
import com.doposoccer.persistence.MatchSlots;
import com.doposoccer.presentation.DOPOSoccerGUI;
import com.doposoccer.presentation.component.ImageButton;
import com.doposoccer.presentation.resource.SpriteCache;
import com.doposoccer.presentation.resource.SpritePath;
import com.doposoccer.presentation.style.Palette;
import com.doposoccer.presentation.style.TextStyle;
import com.doposoccer.presentation.style.Theme;
import java.awt.Graphics2D;
import java.awt.Graphics;

/**
 * Saved matches screen: the three slots, each with the match it keeps, such as "JvM (Experta) · Por goles"
 * An empty slot cannot be opened
 */
public class LoadPanel extends BackgroundPanel {

    private static final double FIRST_ROW_Y = 190;  // below the title
    private static final double ROW_STEP = 105;  // three rows fit above the bottom band

    private final DOPOSoccer game;
    private final ImageButton[] openButtons = new ImageButton[MatchSlots.SLOTS];
    private final ImageButton backButton;

    /**
     * Creates the screen of saved matches
     * @param gui main window, used to navigate
     * @param sprites shared sprite cache
     * @param game facade of the domain
     */
    public LoadPanel(final DOPOSoccerGUI gui, SpriteCache sprites, DOPOSoccer game) {
        super(sprites, SpritePath.background("bg_store"), 0);
        this.game = game;
        for (int i = 0; i < openButtons.length; i++) {
            final int slot = i + 1;
            openButtons[i] = new ImageButton("ABRIR", sprites);
            openButtons[i].addActionListener(e -> gui.loadMatch(slot));
            add(openButtons[i]);
        }
        backButton = new ImageButton("ATRÁS", sprites);
        backButton.addActionListener(e -> gui.showMenu());
        add(backButton);
        refresh();
    }

    /** Reads the slots again; an empty slot cannot be opened */
    public void refresh() {
        for (int i = 0; i < openButtons.length; i++) {
            openButtons[i].setEnabled(game.hasSavedMatch(i + 1));
        }
        repaint();
    }

    /** Places every control using design coordinates */
    @Override
    public void doLayout() {
        for (int i = 0; i < openButtons.length; i++) {
            place(openButtons[i], 700, FIRST_ROW_Y + i * ROW_STEP - 42, 180, 62);
        }
        place(backButton, 387, 540, 250, 90);
    }

    /**
     * Paints the background and one row per slot
     * @param g graphics given by Swing
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = designGraphics(g);
        g2.setColor(Theme.color(Palette.SHADE));
        g2.fillRoundRect(120, 100, 784, 360, 30, 30);
        paintBottomBand(g2);
        TextStyle.TITLE.draw(g2, "CARGAR PARTIDO", DESIGN_WIDTH / 2, 70);
        for (int i = 0; i < openButtons.length; i++) {
            int slot = i + 1;
            double y = FIRST_ROW_Y + i * ROW_STEP;
            TextStyle.HEADING.drawFromLeft(g2, "ESPACIO " + slot, 160, y);
            String label = game.hasSavedMatch(slot) ? game.getSavedMatchLabel(slot) : "Vacío";
            TextStyle.TEXT.drawFromLeft(g2, label, 330, y);
        }
        g2.dispose();
    }
}
