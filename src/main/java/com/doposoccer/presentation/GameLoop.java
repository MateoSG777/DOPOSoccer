package com.doposoccer.presentation;

import com.doposoccer.domain.match.Match;

import javax.swing.SwingUtilities;

/**
 * Game loop running in its own thread at 60 frames per second.
 * Each frame measures the real elapsed time (delta time) and advances the
 * match physics in fixed steps of 1/60 s with an accumulator: the game runs
 * at the same speed on any computer, and fast balls never skip through
 * crossbars or heads. Then it asks the screen to repaint and sleeps only the
 * time left until the next frame.
 */
public class GameLoop implements Runnable {

    /** Frames per second of the game. */
    public static final int TARGET_FPS = 60;

    /** Duration of one physics step in seconds. */
    public static final double STEP = 1.0 / TARGET_FPS;

    /** Longest frame time taken into account (avoids a jump after the computer freezes). */
    public static final double MAX_FRAME_TIME = 0.25;

    /** Nanoseconds in one second. */
    private static final long NANOS_PER_SECOND = 1_000_000_000L;

    /** Nanoseconds in one millisecond. */
    private static final long NANOS_PER_MILLI = 1_000_000L;

    /** Duration of one frame in nanoseconds. */
    private static final long FRAME_NANOS = NANOS_PER_SECOND / TARGET_FPS;

    /** Match updated by the loop. */
    private final Match match;

    /** Screen that shows the match. */
    private final MatchView view;

    /** True while the loop must keep running (written and read by different threads). */
    private volatile boolean running;

    /** Thread that runs the loop. */
    private Thread thread;

    /**
     * Creates the loop of a match.
     *
     * @param match match to update
     * @param view  screen that shows the match
     */
    public GameLoop(Match match, MatchView view) {
        this.match = match;
        this.view = view;
    }

    /** Starts the loop in a new thread. */
    public void start() {
        running = true;
        thread = new Thread(this, "DOPOSoccer-GameLoop");
        thread.setDaemon(true);
        thread.start();
    }

    /** Asks the loop to stop; it ends at the end of the current frame. */
    public void stop() {
        running = false;
    }

    /**
     * Tells whether the loop is running.
     *
     * @return true while the loop runs
     */
    public boolean isRunning() {
        return running;
    }

    /** Body of the loop: update, repaint, check the end and wait for the next frame. */
    @Override
    public void run() {
        long previous = System.nanoTime();
        double accumulator = 0;
        while (running) {
            long frameStart = System.nanoTime();
            double dt = (frameStart - previous) / (double) NANOS_PER_SECOND;
            previous = frameStart;
            accumulator += Math.min(dt, MAX_FRAME_TIME);
            try {
                while (accumulator >= STEP) {
                    match.update(STEP);
                    accumulator -= STEP;
                }
            } catch (RuntimeException e) {
                running = false;
                notifyError(e);
                return;
            }
            view.refresh();
            if (match.getStatus().isOver()) {
                running = false;
                notifyMatchOver();
                return;
            }
            sleepUntil(frameStart + FRAME_NANOS);
        }
    }

    /**
     * Sleeps until the given moment, if it has not passed yet.
     *
     * @param deadline moment in nanoseconds (System.nanoTime)
     */
    private void sleepUntil(long deadline) {
        long remaining = deadline - System.nanoTime();
        if (remaining <= 0) {
            return;
        }
        try {
            Thread.sleep(remaining / NANOS_PER_MILLI, (int) (remaining % NANOS_PER_MILLI));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            running = false;
        }
    }

    /** Tells the screen, on the Swing thread, that the match is over. */
    private void notifyMatchOver() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                view.matchOver();
            }
        });
    }

    /**
     * Tells the screen, on the Swing thread, that an unexpected error happened.
     *
     * @param error the error
     */
    private void notifyError(final Exception error) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                view.showError(error);
            }
        });
    }
}
