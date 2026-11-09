package com.doposoccer.presentation.loop;

/**
 * What the game loop needs from the screen that shows the match
 */
public interface MatchView {

    /** Asks the screen to draw the current frame*/
    void refresh();
    /** Tells the screen that the match is over. Called on the Swing thread*/
    void matchOver();

    /**
     * Tells the screen that an unexpected error stopped the match. Called on the Swing thread
     * @param error the error that happened
     */
    void showError(Exception error);
}
