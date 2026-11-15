package com.doposoccer.domain.match;
import com.doposoccer.domain.DOPOSoccerException;
import com.doposoccer.domain.enums.Country;
import com.doposoccer.domain.enums.GameMode;
import com.doposoccer.domain.enums.MachineProfile;
import com.doposoccer.domain.enums.MatchType;
import com.doposoccer.domain.enums.Resolution;
import com.doposoccer.domain.enums.TeamSide;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Configuration chosen in the setup screens. It is immutable and always
 * valid: if any value is wrong, the constructor throws DOPOSoccerException
 */
public final class MatchConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    private final GameMode mode;
    private final Resolution resolution;
    private final Map<TeamSide, Country> countries = new HashMap<>();
    private final MatchType matchType;
    private final int target;
    private final MachineProfile machine;

    /**
     * Creates a validated configuration
     *
     * @param mode game mode
     * @param resolution field resolution
     * @param leftCountry character of player 1
     * @param rightCountry character of player 2
     * @param matchType type of match
     * @param target minutes or goals to win
     * @param machine profile of the machine, used only when the mode has a machine
     * @throws DOPOSoccerException if a value is missing or the target is out of range
     */
    public MatchConfig(GameMode mode, Resolution resolution, Country leftCountry, Country rightCountry,
                       MatchType matchType, int target, MachineProfile machine) throws DOPOSoccerException {
        if (mode == null || resolution == null || leftCountry == null
                || rightCountry == null || matchType == null || machine == null) {
            throw new DOPOSoccerException(DOPOSoccerException.NULL_CONFIGURATION);
        }
        if (!matchType.isValidTarget(target)) {
            throw new DOPOSoccerException(DOPOSoccerException.INVALID_TARGET + " " + matchType.getTargetLabel()
                    + ": entre " + MatchType.MIN_TARGET + " y " + MatchType.MAX_TARGET + ".");
        }
        this.mode = mode;
        this.resolution = resolution;
        countries.put(TeamSide.LEFT, leftCountry);
        countries.put(TeamSide.RIGHT, rightCountry);
        this.matchType = matchType;
        this.target = target;
        this.machine = machine;
    }

    /**
     * Tells whether a person drives the player of one side
     * @param side side of the player
     * @return false only for the side driven by the machine
     */
    public boolean isHuman(TeamSide side) {
        return side == TeamSide.LEFT || !mode.hasMachine();
    }

    /**
     * Returns the name of the player of one side for the scoreboard: "Jugador 1", "Jugador 2" or "Máquina (Exp)"
     * @param side side of the player
     * @return the name of the player
     */
    public String getPlayerName(TeamSide side) {
        return isHuman(side) ? side.getLabel() : "Máquina (" + machine.getShortName() + ")";
    }

    /**
     * Returns the profile of the machine
     * @return profile of the machine
     */
    public MachineProfile getMachine() {
        return machine;
    }

    /**
     * Returns the chosen game mode
     * @return chosen game mode
     */
    public GameMode getMode() {
        return mode;
    }

    /**
     * Returns the chosen field resolution
     * @return chosen field resolution
     */
    public Resolution getResolution() {
        return resolution;
    }

    /**
     * Returns the character chosen by one player
     * @param side side of the player
     * @return character chosen by that player
     */
    public Country getCountry(TeamSide side) {
        return countries.get(side);
    }

    /**
     * Returns the chosen type of match
     * @return chosen type of match
     */
    public MatchType getMatchType() {
        return matchType;
    }

    /**
     * Returns the minutes or goals to win
     * @return minutes or goals to win
     */
    public int getTarget() {
        return target;
    }
}
