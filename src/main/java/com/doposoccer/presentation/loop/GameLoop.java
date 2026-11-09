package com.doposoccer.presentation.loop;
import com.doposoccer.domain.match.Match;
import com.doposoccer.domain.match.status.MatchStatusBehavior;
import javax.swing.SwingUtilities;

/**
 * Game loop running in its own thread at 60 frames per second. It measures the
 * elapsed time and advances the physics in fixed steps of 1/60 s.
 */
public class GameLoop implements Runnable {

    /** Frames per second of the game*/
    public static final int TARGET_FPS = 60;

    /** Duration of one physics step in seconds*/
    public static final double STEP = 1.0 / TARGET_FPS;

    /** Longest frame time taken into account*/
    public static final double MAX_FRAME_TIME = 0.25;

    private static final long NANOS_PER_SECOND = 1_000_000_000L;
    private static final long NANOS_PER_MILLI = 1_000_000L;
    private static final long FRAME_NANOS = NANOS_PER_SECOND / TARGET_FPS;

    private final Match match;
    private final MatchView view;
    private volatile boolean running;
    private Thread thread;

    /**
     * Creates the loop of a match
     * @param match match to update
     * @param view  screen that shows the match
     */
    public GameLoop(Match match, MatchView view) {
        this.match = match;
        this.view = view;
    }

    /** Starts the loop in a new thread*/
    public void start() {
        running = true;
        thread = new Thread(this, "DOPOSoccer-GameLoop");
        thread.setDaemon(true);
        thread.start();
    }

    /** Asks the loop to stop; it ends at the end of the current frame*/
    public void stop() {
        running = false;
    }

    /**
     * Tells whether the loop is running
     * @return true while the loop runs
     */
    public boolean isRunning() {
        return running;
    }

    /** Body of the loop: update, repaint, check the end and wait for the next frame*/
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
                notifyView(e);
                return;
            }
            view.refresh();
            if (MatchStatusBehavior.get(match.getStatus()).isOver()) {
                running = false;
                notifyView(null);
                return;
            }
            sleepUntil(frameStart + FRAME_NANOS);
        }
    }

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

    private void notifyView(final Exception error) {
        SwingUtilities.invokeLater(() -> {
            if (error == null) {
                view.matchOver();
            } else {
                view.showError(error);
            }
        });
    }
}
