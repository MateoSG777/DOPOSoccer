package com.doposoccer.presentation;

import com.doposoccer.domain.enums.GameMode;
import com.doposoccer.domain.enums.MachineProfile;
import com.doposoccer.domain.enums.MatchType;
import com.doposoccer.domain.enums.Resolution;

import javax.swing.JComboBox;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Match configuration screen: game mode, field resolution and type of match
 * (by time with its minutes, or by goals with the goals to win).
 * Player vs Machine and its profiles are shown disabled until version two.
 */
public class ConfigPanel extends BackgroundPanel {

    /** Font size of the section titles, in design coordinates. */
    private static final float SECTION_FONT = 22f;

    /** Font size of the screen title, in design coordinates. */
    private static final float TITLE_FONT = 38f;

    /** Font size of the spinner and combo box, in design coordinates. */
    private static final float INPUT_FONT = 26f;

    /** Main window, used to navigate and to store the chosen options. */
    private final DOPOSoccerGUI gui;

    /** One button per game mode, in the order of GameMode.values(). */
    private final ImageButton[] modeButtons;

    /** One button per resolution, in the order of Resolution.values(). */
    private final ImageButton[] resolutionButtons;

    /** One button per type of match, in the order of MatchType.values(). */
    private final ImageButton[] typeButtons;

    /** Machine profile selector (disabled until version two). */
    private final JComboBox<MachineProfile> profileBox;

    /** Minutes or goals to win. */
    private final JSpinner targetSpinner;

    /** Button that goes back to the menu. */
    private final ImageButton backButton;

    /** Button that goes to the character selection. */
    private final ImageButton nextButton;

    /**
     * Creates the configuration screen with the options currently stored in the window.
     *
     * @param gui     main window
     * @param sprites shared sprite cache
     */
    public ConfigPanel(final DOPOSoccerGUI gui, SpriteCache sprites) {
        super(sprites, "bg_store", 0);
        this.gui = gui;

        GameMode[] modes = GameMode.values();
        modeButtons = new ImageButton[modes.length];
        for (int i = 0; i < modes.length; i++) {
            final GameMode mode = modes[i];
            modeButtons[i] = new ImageButton(mode.getDisplayName(), sprites);
            modeButtons[i].setEnabled(mode.isAvailable());
            if (!mode.isAvailable()) {
                modeButtons[i].setToolTipText("Disponible en la versión 2");
            }
            modeButtons[i].addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    gui.setMode(mode);
                    refresh();
                }
            });
            add(modeButtons[i]);
        }

        profileBox = new JComboBox<>(MachineProfile.values());
        profileBox.setEnabled(false);
        profileBox.setToolTipText("Perfiles de la máquina: disponibles en la versión 2");
        add(profileBox);

        Resolution[] resolutions = Resolution.values();
        resolutionButtons = new ImageButton[resolutions.length];
        for (int i = 0; i < resolutions.length; i++) {
            final Resolution resolution = resolutions[i];
            resolutionButtons[i] = new ImageButton(resolution.toString(), sprites);
            resolutionButtons[i].addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    gui.setResolution(resolution);
                    refresh();
                }
            });
            add(resolutionButtons[i]);
        }

        MatchType[] types = MatchType.values();
        typeButtons = new ImageButton[types.length];
        for (int i = 0; i < types.length; i++) {
            final MatchType type = types[i];
            typeButtons[i] = new ImageButton(type.getDisplayName(), sprites);
            typeButtons[i].addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    gui.setMatchType(type);
                    refresh();
                }
            });
            add(typeButtons[i]);
        }

        targetSpinner = new JSpinner();
        targetSpinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                gui.setTarget((Integer) targetSpinner.getValue());
            }
        });
        add(targetSpinner);

        backButton = new ImageButton("ATRÁS", sprites);
        nextButton = new ImageButton("SIGUIENTE", sprites);
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gui.showMenu();
            }
        });
        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gui.showCharacters();
            }
        });
        add(backButton);
        add(nextButton);
        refresh();
    }

    /** Highlights the chosen options and updates the range of the spinner. */
    private void refresh() {
        GameMode[] modes = GameMode.values();
        for (int i = 0; i < modes.length; i++) {
            modeButtons[i].setHighlighted(modes[i] == gui.getMode());
        }
        Resolution[] resolutions = Resolution.values();
        for (int i = 0; i < resolutions.length; i++) {
            resolutionButtons[i].setHighlighted(resolutions[i] == gui.getResolution());
        }
        MatchType[] types = MatchType.values();
        for (int i = 0; i < types.length; i++) {
            typeButtons[i].setHighlighted(types[i] == gui.getMatchType());
        }
        MatchType type = gui.getMatchType();
        targetSpinner.setModel(new SpinnerNumberModel(gui.getTarget(), type.getMinTarget(), type.getMaxTarget(), 1));
        applyInputFont();
        repaint();
    }

    /** Places every control using design coordinates. */
    @Override
    public void doLayout() {
        place(modeButtons[0], 150, 160, 280, 90);
        place(modeButtons[1], 440, 160, 280, 90);
        place(profileBox, 735, 185, 150, 44);
        for (int i = 0; i < resolutionButtons.length; i++) {
            place(resolutionButtons[i], 150 + i * 245, 290, 230, 84);
        }
        for (int i = 0; i < typeButtons.length; i++) {
            place(typeButtons[i], 150 + i * 255, 420, 240, 86);
        }
        place(targetSpinner, 730, 438, 140, 52);
        place(backButton, 150, 540, 250, 90);
        place(nextButton, 624, 540, 250, 90);
        applyInputFont();
    }

    /** Scales the font of the spinner and the combo box to the panel size. */
    private void applyInputFont() {
        Font font = Theme.font((float) (INPUT_FONT * Math.max(scale(), 0.5)));
        ((JSpinner.DefaultEditor) targetSpinner.getEditor()).getTextField().setFont(font);
        profileBox.setFont(font.deriveFont(font.getSize2D() * 0.6f));
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
        g2.setColor(Theme.SHADE);
        g2.fillRoundRect(120, 100, 784, 420, 30, 30);
        Theme.drawCenteredText(g2, "CONFIGURACIÓN DEL PARTIDO", DESIGN_WIDTH / 2, 70,
                Theme.font(TITLE_FONT), Theme.GOLD, Theme.DARK, 5f);
        drawSection(g2, "MODALIDAD", 150, 148);
        drawSection(g2, "PERFIL (v2)", 735, 172);
        drawSection(g2, "RESOLUCIÓN DE LA CANCHA", 150, 280);
        drawSection(g2, "TIPO DE PARTIDO", 150, 410);
        drawSection(g2, gui.getMatchType().getTargetLabel().toUpperCase(), 730, 425);
        g2.dispose();
    }

    /**
     * Draws the name of a section aligned to the left.
     *
     * @param g        graphics in design coordinates
     * @param text     name of the section
     * @param x        left x of the text
     * @param baseline y of the text baseline
     */
    private void drawSection(Graphics2D g, String text, double x, double baseline) {
        Font font = Theme.font(SECTION_FONT);
        double width = g.getFontMetrics(font).stringWidth(text);
        Theme.drawCenteredText(g, text, x + width / 2, baseline, font, Theme.GOLD, Theme.DARK, 3f);
    }
}
