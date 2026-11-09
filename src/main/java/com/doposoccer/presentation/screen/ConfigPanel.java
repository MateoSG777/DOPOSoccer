package com.doposoccer.presentation.screen;

import com.doposoccer.domain.ai.MachineProfileBehavior;
import com.doposoccer.domain.enums.GameMode;
import com.doposoccer.domain.enums.MachineProfile;
import com.doposoccer.domain.enums.MatchType;
import com.doposoccer.domain.enums.Resolution;
import com.doposoccer.domain.match.GameModeInfo;
import com.doposoccer.domain.match.MatchTypeInfo;
import com.doposoccer.domain.match.ResolutionInfo;
import com.doposoccer.presentation.DOPOSoccerGUI;
import com.doposoccer.presentation.component.ChoiceGroup;
import com.doposoccer.presentation.component.ImageButton;
import com.doposoccer.presentation.resource.SpriteCache;
import com.doposoccer.presentation.resource.SpritePath;
import com.doposoccer.presentation.style.Palette;
import com.doposoccer.presentation.style.TextStyle;
import com.doposoccer.presentation.style.Theme;

import javax.swing.JComboBox;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Graphics;

/**
 * Match configuration screen: game mode, field resolution, type of match and its target.
 * It keeps the options chosen by the users. Player vs Machine and its profiles
 * are shown disabled until version two.
 */
public class ConfigPanel extends BackgroundPanel {

    private static final String NEXT_VERSION = "Disponible en la versión 2";
    private static final float INPUT_FONT = 26f;  // same visual weight as the button texts
    private static final double MIN_FONT_SCALE = 0.5;  // keeps the text readable before the panel gets its size
    private static final float COMBO_FONT_RATIO = 0.6f;  // the profile names are longer than a number
    private static final int DEFAULT_MODE = 0;  // player vs player, the only mode of version one
    private static final int DEFAULT_RESOLUTION = 1;  // 1024 x 640: same size as the field, nothing is scaled
    private static final int DEFAULT_TYPE = 1;  // by goals

    private final GameMode[] modeOptions = GameMode.values();
    private final Resolution[] resolutionOptions = Resolution.values();
    private final MatchType[] typeOptions = MatchType.values();
    private final MachineProfile[] profileOptions = MachineProfile.values();
    private final ChoiceGroup modes;
    private final ChoiceGroup resolutions;
    private final ChoiceGroup types;
    private final JComboBox<String> profileBox = new JComboBox<>();
    private final JSpinner targetSpinner = new JSpinner();
    private final ImageButton backButton;
    private final ImageButton nextButton;

    /**
     * Creates the configuration screen.
     *
     * @param gui     main window, used to navigate
     * @param sprites shared sprite cache
     */
    public ConfigPanel(final DOPOSoccerGUI gui, SpriteCache sprites) {
        super(sprites, SpritePath.background("bg_store"), 0);
        String[] modeLabels = new String[modeOptions.length];
        for (int i = 0; i < modeOptions.length; i++) {
            modeLabels[i] = GameModeInfo.get(modeOptions[i]).getDisplayName();
        }
        modes = new ChoiceGroup(modeLabels, DEFAULT_MODE, sprites, this);
        for (int i = 0; i < modeOptions.length; i++) {
            if (!GameModeInfo.get(modeOptions[i]).isAvailable()) {
                modes.disable(i, NEXT_VERSION);
            }
        }
        String[] resolutionLabels = new String[resolutionOptions.length];
        for (int i = 0; i < resolutionOptions.length; i++) {
            resolutionLabels[i] = ResolutionInfo.get(resolutionOptions[i]).getDisplayName();
        }
        resolutions = new ChoiceGroup(resolutionLabels, DEFAULT_RESOLUTION, sprites, this);
        String[] typeLabels = new String[typeOptions.length];
        for (int i = 0; i < typeOptions.length; i++) {
            typeLabels[i] = MatchTypeInfo.of(typeOptions[i]).getDisplayName();
        }
        types = new ChoiceGroup(typeLabels, DEFAULT_TYPE, sprites, this);
        types.setOnChange(() -> resetTarget(getMatchType()));
        resetTarget(getMatchType());

        for (int i = 0; i < profileOptions.length; i++) {
            profileBox.addItem(MachineProfileBehavior.get(profileOptions[i]).getDisplayName());
        }
        profileBox.setEnabled(false);
        profileBox.setToolTipText(NEXT_VERSION);
        add(profileBox);
        add(targetSpinner);

        backButton = new ImageButton("ATRÁS", sprites);
        nextButton = new ImageButton("SIGUIENTE", sprites);
        backButton.addActionListener(e -> gui.showMenu());
        nextButton.addActionListener(e -> gui.showCharacters());
        add(backButton);
        add(nextButton);
    }

    private void resetTarget(MatchType type) {
        MatchTypeInfo rules = MatchTypeInfo.of(type);
        targetSpinner.setModel(new SpinnerNumberModel(rules.getDefaultTarget(), rules.getMinTarget(),
                rules.getMaxTarget(), 1));
        applyInputFont();
        repaint();
    }

    /** Places every control using design coordinates. */
    @Override
    public void doLayout() {
        placeRow(modes, 150, 160, 280, 90, 290);
        place(profileBox, 735, 185, 150, 44);
        placeRow(resolutions, 150, 290, 230, 84, 245);
        placeRow(types, 150, 420, 240, 86, 255);
        place(targetSpinner, 730, 438, 140, 52);
        placeBottomButtons(backButton, nextButton);
        applyInputFont();
    }

    private void placeRow(ChoiceGroup group, double x, double y, double width, double height, double step) {
        for (int i = 0; i < group.size(); i++) {
            place(group.getButton(i), x + i * step, y, width, height);
        }
    }

    private void applyInputFont() {
        Font font = Theme.font((float) (INPUT_FONT * Math.max(scale(), MIN_FONT_SCALE)));
        ((JSpinner.DefaultEditor) targetSpinner.getEditor()).getTextField().setFont(font);
        profileBox.setFont(font.deriveFont(font.getSize2D() * COMBO_FONT_RATIO));
    }

    /**
     * Paints the background, the title and the section names.
     *
     * @param g graphics given by Swing
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = designGraphics(g);
        g2.setColor(Theme.color(Palette.SHADE));
        g2.fillRoundRect(120, 100, 784, 420, 30, 30);
        TextStyle.TITLE.draw(g2, "CONFIGURACIÓN DEL PARTIDO", DESIGN_WIDTH / 2, 70);
        TextStyle.SECTION.drawFromLeft(g2, "MODALIDAD", 150, 148);
        TextStyle.SECTION.drawFromLeft(g2, "PERFIL (v2)", 735, 172);
        TextStyle.SECTION.drawFromLeft(g2, "RESOLUCIÓN DE LA CANCHA", 150, 280);
        TextStyle.SECTION.drawFromLeft(g2, "TIPO DE PARTIDO", 150, 410);
        TextStyle.SECTION.drawFromLeft(g2, MatchTypeInfo.of(getMatchType()).getTargetLabel().toUpperCase(), 730, 425);
        g2.dispose();
    }

    /**
     * Returns the chosen game mode.
     *
     * @return the chosen game mode
     */
    public GameMode getMode() {
        return modeOptions[modes.getSelectedIndex()];
    }

    /**
     * Returns the chosen field resolution.
     *
     * @return the chosen resolution
     */
    public Resolution getResolution() {
        return resolutionOptions[resolutions.getSelectedIndex()];
    }

    /**
     * Returns the chosen type of match.
     *
     * @return the chosen type of match
     */
    public MatchType getMatchType() {
        return typeOptions[types.getSelectedIndex()];
    }

    /**
     * Returns the chosen minutes or goals to win.
     *
     * @return the chosen target
     */
    public int getTarget() {
        return (Integer) targetSpinner.getValue();
    }
}
