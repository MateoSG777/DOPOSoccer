package com.doposoccer.presentation.screen;

import com.doposoccer.domain.enums.GameMode;
import com.doposoccer.domain.enums.MachineProfile;
import com.doposoccer.domain.enums.MatchType;
import com.doposoccer.presentation.DOPOSoccerGUI;
import com.doposoccer.presentation.component.ChoiceGroup;
import com.doposoccer.presentation.component.ImageButton;
import com.doposoccer.presentation.component.Stepper;
import com.doposoccer.presentation.resource.SpriteCache;
import com.doposoccer.presentation.resource.SpritePath;
import com.doposoccer.presentation.style.NumberStyle;
import com.doposoccer.presentation.style.Palette;
import com.doposoccer.presentation.style.TextStyle;
import com.doposoccer.presentation.style.Theme;

import java.awt.Graphics2D;
import java.awt.Graphics;

/**
 * Match configuration screen: game mode, profile of the machine, type of match and its target.
 * It keeps the options chosen by the users. The profile of the machine
 * can only be chosen in Player vs Machine.
 */
public class ConfigPanel extends BackgroundPanel {

    private static final int DEFAULT_MODE = 0;  // player vs player
    private static final int DEFAULT_TYPE = 1;  // by goals
    private static final double STEPPER_CENTER_X = 752;  // the target label and its buttons share this center
    private static final double STEPPER_Y = 400;
    private static final double STEP_BUTTON_WIDTH = 74;
    private static final double STEP_BUTTON_HEIGHT = 66;
    private static final double VALUE_HEIGHT = 56;

    private final GameMode[] modeOptions = GameMode.values();
    private final MatchType[] typeOptions = MatchType.values();
    private final MachineProfile[] profileOptions = MachineProfile.values();
    private final ChoiceGroup modes;
    private final ChoiceGroup profiles;
    private final ChoiceGroup types;
    private final Stepper target;
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
            modeLabels[i] = modeOptions[i].getDisplayName();
        }
        modes = new ChoiceGroup(modeLabels, DEFAULT_MODE, sprites, this);
        String[] profileLabels = new String[profileOptions.length];
        for (int i = 0; i < profileOptions.length; i++) {
            profileLabels[i] = profileOptions[i].getDisplayName();
        }
        profiles = new ChoiceGroup(profileLabels, 0, sprites, this);
        profiles.setEnabled(false);
        modes.setOnChange(() -> profiles.setEnabled(getMode().hasMachine()));
        String[] typeLabels = new String[typeOptions.length];
        for (int i = 0; i < typeOptions.length; i++) {
            typeLabels[i] = typeOptions[i].getDisplayName();
        }
        target = new Stepper(sprites, this);
        types = new ChoiceGroup(typeLabels, DEFAULT_TYPE, sprites, this);
        types.setOnChange(() -> resetTarget(getMatchType()));
        resetTarget(getMatchType());

        backButton = new ImageButton("ATRÁS", sprites);
        nextButton = new ImageButton("SIGUIENTE", sprites);
        backButton.addActionListener(e -> gui.showMenu());
        nextButton.addActionListener(e -> gui.showCharacters());
        add(backButton);
        add(nextButton);
    }

    private void resetTarget(MatchType type) {
        target.setRange(MatchType.MIN_TARGET, MatchType.MAX_TARGET, type.getDefaultTarget());
        repaint();
    }

    /** Places every control using design coordinates. */
    @Override
    public void doLayout() {
        placeRow(modes, 150, 152, 280, 84, 290);
        placeRow(profiles, 150, 280, 170, 66, 184);
        placeRow(types, 150, 400, 210, 84, 220);
        place(target.getMinusButton(), STEPPER_CENTER_X - 115, STEPPER_Y + 9, STEP_BUTTON_WIDTH, STEP_BUTTON_HEIGHT);
        place(target.getPlusButton(), STEPPER_CENTER_X + 115 - STEP_BUTTON_WIDTH, STEPPER_Y + 9,
                STEP_BUTTON_WIDTH, STEP_BUTTON_HEIGHT);
        placeBottomButtons(backButton, nextButton);
    }

    private void placeRow(ChoiceGroup group, double x, double y, double width, double height, double step) {
        for (int i = 0; i < group.size(); i++) {
            place(group.getButton(i), x + i * step, y, width, height);
        }
    }

    /**
     * Paints the background, the title, the section names and the target number.
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
        TextStyle.SECTION.drawFromLeft(g2, "MODALIDAD", 150, 142);
        TextStyle.SECTION.drawFromLeft(g2, "MÁQUINA", 150, 270);
        TextStyle.SECTION.drawFromLeft(g2, "TIPO DE PARTIDO", 150, 390);
        TextStyle.SECTION.draw(g2, getMatchType().getTargetLabel().toUpperCase(), STEPPER_CENTER_X, 390);
        NumberStyle.BIG.draw(g2, sprites, String.valueOf(target.getValue()), STEPPER_CENTER_X,
                STEPPER_Y + 42, VALUE_HEIGHT);
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
        return target.getValue();
    }

    /**
     * Returns the chosen profile of the machine.
     *
     * @return the chosen profile
     */
    public MachineProfile getMachine() {
        return profileOptions[profiles.getSelectedIndex()];
    }
}
