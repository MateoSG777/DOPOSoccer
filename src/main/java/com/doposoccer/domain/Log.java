package com.doposoccer.domain;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Error log for developers. Players see clear messages on screen,
 * while the technical details (stack traces) are written to {@value #FILE_NAME}.
 */
public final class Log {

    /** Name of the file where errors are recorded. */
    public static final String FILE_NAME = "doposoccer.log";

    /** Java logger used to write the records. */
    private static final Logger LOGGER = Logger.getLogger("DOPOSoccer");

    /** True once the file handler has been attached to the logger. */
    private static boolean configured = false;

    /** Utility class: it must not be instantiated. */
    private Log() {
    }

    /**
     * Records an exception with its complete stack trace.
     * If the log file cannot be opened, the problem is reported on the console.
     *
     * @param e exception to record
     */
    public static synchronized void record(Exception e) {
        configure();
        LOGGER.log(Level.SEVERE, e.toString(), e);
    }

    /** Attaches the file handler to the logger the first time it is needed. */
    private static void configure() {
        if (configured) {
            return;
        }
        try {
            FileHandler handler = new FileHandler(FILE_NAME, true);
            handler.setFormatter(new SimpleFormatter());
            LOGGER.addHandler(handler);
            LOGGER.setUseParentHandlers(false);
        } catch (IOException e) {
            System.err.println("The error log could not be opened: " + e.getMessage());
        }
        configured = true;
    }
}
