package com.doposoccer.domain.match;

import com.doposoccer.domain.DOPOSoccerException;
import com.doposoccer.domain.enums.Country;
import com.doposoccer.domain.enums.GameMode;
import com.doposoccer.domain.enums.MatchType;
import com.doposoccer.domain.enums.Resolution;
import com.doposoccer.domain.enums.TeamSide;

/**
 * Configuration chosen in the setup screens. It is immutable and always
 * valid: if any value is wrong, the constructor throws DOPOSoccerException.
 */
public final class MatchConfig {

    /** Chosen game mode. */
    private final GameMode mode;

    /** Chosen field resolution. */
    private final Resolution resolution;

    /** Character of player 1 (left side). */
    private final Country leftCountry;

    /** Character of player 2 (right side). */
    private final Country rightCountry;

    /** Chosen type of match. */
    private final MatchType matchType;

    /** Minutes (by time) or goals to win (by goals). */
    private final int target;

    /**
     * Creates a validated configuration.
     *
     * @param mode         game mode
     * @param resolution   field resolution
     * @param leftCountry  character of player 1
     * @param rightCountry character of player 2
     * @param matchType    type of match
     * @param target       minutes (by time) or goals to win (by goals)
     * @throws DOPOSoccerException if a value is missing, the mode is not available
     *                             or the target is out of range
     */
    public MatchConfig(GameMode mode, Resolution resolution, Country leftCountry,
                       Country rightCountry, MatchType matchType, int target) throws DOPOSoccerException {
        if (mode == null || resolution == null || leftCountry == null
                || rightCountry == null || matchType == null) {
            throw new DOPOSoccerException(DOPOSoccerException.NULL_CONFIGURATION);
        }
        if (!mode.isAvailable()) {
            throw new DOPOSoccerException(DOPOSoccerException.MODE_NOT_AVAILABLE);
        }
        if (!matchType.isValidTarget(target)) {
            throw new DOPOSoccerException(DOPOSoccerException.INVALID_TARGET + " " + matchType.getTargetLabel()
                    + ": entre " + matchType.getMinTarget() + " y " + matchType.getMaxTarget() + ".");
        }
        this.mode = mode;
        this.resolution = resolution;
        this.leftCountry = leftCountry;
        this.rightCountry = rightCountry;
        this.matchType = matchType;
        this.target = target;
    }

    /**
     * Returns the chosen game mode.
     *
     * @return chosen game mode
     */
    public GameMode getMode() {
        return mode;
    }

    /**
     * Returns the chosen field resolution.
     *
     * @return chosen field resolution
     */
    public Resolution getResolution() {
        return resolution;
    }

    /**
     * Returns the character chosen by one player.
     *
     * @param side side of the player
     * @return character chosen by that player
     */
    public Country getCountry(TeamSide side) {
        if (side == TeamSide.LEFT) {
            return leftCountry;
        }
        return rightCountry;
    }

    /**
     * Returns the chosen type of match.
     *
     * @return chosen type of match
     */
    public MatchType getMatchType() {
        return matchType;
    }

    /**
     * Returns the minutes (by time) or goals to win (by goals).
     *
     * @return minutes (by time) or goals to win (by goals)
     */
    public int getTarget() {
        return target;
    }
}
