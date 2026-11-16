package com.doposoccer.presentation.component;
import com.doposoccer.presentation.resource.SpriteCache;
import javax.swing.JPanel;

/**
 * Number picker with a minus and a plus button. The screen places the buttons and draws the number
 * between them, so it looks like the rest of the screen
 */
public class Stepper {

    private static final double SYMBOL_RATIO = 0.65;  // + and - are single characters, so they can be bigger

    private final ImageButton minus;
    private final ImageButton plus;
    private int min;
    private int max;
    private int value;

    /**
     * Creates the two buttons and adds them to a panel
     * @param sprites shared sprite cache
     * @param panel   panel where the buttons are added
     */
    public Stepper(SpriteCache sprites, JPanel panel) {
        minus = new ImageButton("-", sprites);
        plus = new ImageButton("+", sprites);
        minus.setFontRatio(SYMBOL_RATIO);
        plus.setFontRatio(SYMBOL_RATIO);
        minus.addActionListener(e -> change(-1));
        plus.addActionListener(e -> change(1));
        panel.add(minus);
        panel.add(plus);
    }

    /**
     * Sets the limits and the current number
     * @param min   lowest number
     * @param max   highest number
     * @param value number shown now
     */
    public void setRange(int min, int max, int value) {
        this.min = min;
        this.max = max;
        this.value = value;
        refresh();
    }

    private void change(int step) {
        value = Math.max(min, Math.min(max, value + step));
        refresh();
        minus.getParent().repaint();
    }

    // a button that cannot move the number any further is shown disabled
    private void refresh() {
        minus.setEnabled(value > min);
        plus.setEnabled(value < max);
    }

    /**
     * Returns the number shown now
     * @return the current number
     */
    public int getValue() {
        return value;
    }

    /**
     * Returns the button that lowers the number
     * @return the minus button
     */
    public ImageButton getMinusButton() {
        return minus;
    }

    /**
     * Returns the button that raises the number
     * @return the plus button
     */
    public ImageButton getPlusButton() {
        return plus;
    }
}
