package com.doposoccer.domain;

/**
 * Checked exception for every error that is specific to DOPOSoccer
 * Messages are public constants so the presentation layer can show clear
 * texts to the user and the unit tests can verify exactly which error happened
 * Messages are written in Spanish because they are shown to the players
 */
public class DOPOSoccerException extends Exception {

    /** Thrown when some piece of the match configuration is missing*/
    public static final String NULL_CONFIGURATION = "Debe completar toda la configuración del partido (modalidad, perfil, resolución, personajes y tipo de partido)";

    /** Thrown when the minutes or goals of the match are out of range*/
    public static final String INVALID_TARGET = "El valor del partido está fuera del rango permitido";

    /** Thrown when the winner is requested before the match is over*/
    public static final String MATCH_NOT_FINISHED = "El partido todavía no ha terminado";

    /** Thrown when the winner is requested from a match ended by the players*/
    public static final String MATCH_ABANDONED = "El partido fue terminado por los jugadores: no hay ganador ni puntos";

    /** Thrown when an action is requested on a match that is already over*/
    public static final String MATCH_FINISHED = "El partido ya terminó";

    /** Thrown when a stat that is already at its last level is upgraded*/
    public static final String MAX_LEVEL = "Esta habilidad ya está en el nivel máximo";

    /** Thrown when a player cannot pay an upgrade*/
    public static final String NOT_ENOUGH_POINTS = "No tienes puntos suficientes para esta mejora";

    /** Thrown when the progress of the players cannot be written to disk*/
    public static final String SAVE_FAILED = "No se pudo guardar el progreso de los jugadores";

    /** Thrown when a saved match is requested from a slot that does not exist*/
    public static final String INVALID_SLOT = "Ese espacio de guardado no existe";

    /** Thrown when an empty slot is opened*/
    public static final String EMPTY_SLOT = "No hay ningún partido guardado en ese espacio";

    /** Thrown when a match cannot be written to disk*/
    public static final String MATCH_SAVE_FAILED = "No se pudo guardar el partido";

    /** Thrown when a saved match is damaged or comes from another version of the game*/
    public static final String MATCH_LOAD_FAILED = "No se pudo abrir el partido guardado: el archivo está dañado o es de otra versión";

    /** Thrown when a match is saved while it is not paused*/
    public static final String SAVE_ONLY_PAUSED = "Pausa el partido para poder guardarlo";

    /**
     * Creates a new DOPOSoccer exception
     * @param message user-friendly description of the error
     */
    public DOPOSoccerException(String message) {
        super(message);
    }
}
