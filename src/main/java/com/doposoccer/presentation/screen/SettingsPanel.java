package com.doposoccer.presentation.screen;
import com.doposoccer.domain.enums.Resolution;
import com.doposoccer.presentation.DOPOSoccerGUI;
import com.doposoccer.presentation.component.ChoiceGroup;
import com.doposoccer.presentation.component.ImageButton;
import com.doposoccer.presentation.resource.SpriteCache;
import com.doposoccer.presentation.resource.SpritePath;
import com.doposoccer.presentation.style.Palette;
import com.doposoccer.presentation.style.TextStyle;
import com.doposoccer.presentation.style.Theme;
import java.awt.Graphics2D;
import java.awt.Graphics;

/**
 * Settings screen, opened with the gear of the menu: the size of the field for the next matches
 */
public class SettingsPanel extends BackgroundPanel {

    private static final int DEFAULT_RESOLUTION = 1;  // 1024 x 640: same size as the field, nothing is scaled

    private final Resolution[] resolutionOptions = Resolution.values();
    private final ChoiceGroup resolutions;
    private final ImageButton backButton;

    /**
     * Creates the settings screen
     * @param gui main window, used to navigate
     * @param sprites shared sprite cache
     */
    public SettingsPanel(final DOPOSoccerGUI gui, SpriteCache sprites) {
        super(sprites, SpritePath.background("bg_store"), 0);
        String[] labels = new String[resolutionOptions.length];
        for (int i = 0; i < resolutionOptions.length; i++) {
            labels[i] = resolutionOptions[i].getDisplayName();
        }
        resolutions = new ChoiceGroup(labels, DEFAULT_RESOLUTION, sprites, this);
        backButton = new ImageButton("ATRÁS", sprites);
        backButton.addActionListener(e -> gui.showMenu());
        add(backButton);
    }

    /** Places the options in a row and the back button at the bottom */
    @Override
    public void doLayout() {
        for (int i = 0; i < resolutions.size(); i++) {
            place(resolutions.getButton(i), 150 + i * 245, 270, 230, 84);
        }
        place(backButton, 387, 540, 250, 90);
    }

    /**
     * Paints the background, the title and the name of the section
     * @param g graphics given by Swing
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = designGraphics(g);
        g2.setColor(Theme.color(Palette.SHADE));
        g2.fillRoundRect(120, 190, 784, 220, 30, 30);
        TextStyle.TITLE.draw(g2, "AJUSTES", DESIGN_WIDTH / 2, 70);
        TextStyle.SECTION.drawFromLeft(g2, "RESOLUCIÓN DE LA CANCHA", 150, 258);
        g2.dispose();
    }

    /**
     * Returns the chosen field resolution
     * @return the chosen resolution
     */
    public Resolution getResolution() {
        return resolutionOptions[resolutions.getSelectedIndex()];
    }
}
