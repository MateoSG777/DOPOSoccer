package com.doposoccer.domain;

/**
 * Checked exception for every error that is specific to DOPOSoccer.
 * Messages are public constants so the presentation layer can show clear
 * texts to the user and the unit tests can verify exactly which error happened.
 * Messages are written in Spanish because they are shown to the players.
 */
public class DOPOSoccerException extends Exception {

    /** Thrown when some piece of the match configuration is missing. */
    public static final String NULL_CONFIGURATION = "Debe completar toda la configuración del partido (modalidad, resolución, personajes y tipo de partido).";

    /** Thrown when the chosen game mode is not playable in this version. */
    public static final String MODE_NOT_AVAILABLE = "La modalidad seleccionada aún no está disponible en esta versión.";

    /** Thrown when the minutes or goals of the match are out of range. */
    public static final String INVALID_TARGET = "El valor del partido está fuera del rango permitido.";

    /** Thrown when the winner is requested before the match is over. */
    public static final String MATCH_NOT_FINISHED = "El partido todavía no ha terminado.";

    /** Thrown when the winner is requested from a match ended by the players. */
    public static final String MATCH_ABANDONED = "El partido fue terminado por los jugadores: no hay ganador ni puntos.";

    /** Thrown when an action is requested on a match that is already over. */
    public static final String MATCH_FINISHED = "El partido ya terminó.";

    /**
     * Creates a new DOPOSoccer exception.
     *
     * @param message user-friendly description of the error
     */
    public DOPOSoccerException(String message) {
        super(message);
    }
}
