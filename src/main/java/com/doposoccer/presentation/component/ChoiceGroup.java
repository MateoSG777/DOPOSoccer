package com.doposoccer.presentation.component;
import com.doposoccer.presentation.resource.SpriteCache;
import javax.swing.JPanel;

/**
 * Row of buttons where only one option is selected. The selected button is shown
 * highlighted. Options are identified by their position in the array given to the
 * constructor, so the screen reads the chosen option from its own array.
 */
public class ChoiceGroup {

    private final ImageButton[] buttons;
    private int selected;
    private Runnable onChange = () -> { };

    /**
     * Creates one button per option, adds them to a panel and selects the initial one
     * @param labels  text of each button, one per option
     * @param initial position of the option selected at the beginning
     * @param sprites shared sprite cache
     * @param panel   panel where the buttons are added
     */
    public ChoiceGroup(String[] labels, int initial, SpriteCache sprites, JPanel panel) {
        buttons = new ImageButton[labels.length];
        for (int i = 0; i < labels.length; i++) {
            final int index = i;
            buttons[i] = new ImageButton(labels[i], sprites);
            buttons[i].addActionListener(e -> {
                select(index);
                onChange.run();
            });
            panel.add(buttons[i]);
        }
        select(initial);
    }

    /**
     * Selects an option and highlights its button
     * @param index position of the option to select
     */
    public void select(int index) {
        selected = index;
        for (int i = 0; i < buttons.length; i++) {
            buttons[i].setHighlighted(i == index);
        }
    }

    /**
     * Sets the action executed after the user picks an option
     * @param action action to execute, usually a lambda of the screen
     */
    public void setOnChange(Runnable action) {
        onChange = action;
    }

    /**
     * Disables the button of an option that cannot be chosen yet
     * @param index   position of the option to disable
     * @param tooltip explanation shown over the button
     */
    public void disable(int index, String tooltip) {
        buttons[index].setEnabled(false);
        buttons[index].setToolTipText(tooltip);
    }

    /**
     * Returns the button of one option, so the screen can place it
     * @param index position of the option
     * @return the button of that option
     */
    public ImageButton getButton(int index) {
        return buttons[index];
    }

    /**
     * Returns how many options the group offers
     * @return the number of buttons
     */
    public int size() {
        return buttons.length;
    }

    /**
     * Returns the position of the option currently selected
     * @return the selected position
     */
    public int getSelectedIndex() {
        return selected;
    }
}
