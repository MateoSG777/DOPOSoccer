package com.doposoccer.domain.match;
import com.doposoccer.domain.DOPOSoccerException;
import com.doposoccer.domain.control.ActionSource;
import com.doposoccer.domain.enums.Country;
import com.doposoccer.domain.enums.GameMode;
import com.doposoccer.domain.enums.MachineProfile;
import com.doposoccer.domain.enums.MatchType;
import com.doposoccer.domain.enums.Resolution;
import com.doposoccer.domain.enums.TeamSide;
import com.doposoccer.domain.shop.PlayerProfile;
import java.util.EnumMap;
import java.util.Map;

/**
 * Builder of a match: collects the choices of the setup screens step by step and
 * delivers the match ready to play, with the stats and the control of each player
 */
public class MatchBuilder {

    private final Map<TeamSide, Country> countries = new EnumMap<>(TeamSide.class);
    private final Map<TeamSide, ActionSource> sources = new EnumMap<>(TeamSide.class);
    private final Map<TeamSide, PlayerProfile> profiles = new EnumMap<>(TeamSide.class);
    private GameMode mode;
    private Resolution resolution;
    private MatchType matchType;
    private int target;
    private MachineProfile machine = MachineProfile.CLUMSY;

    /**
     * Sets the game mode
     * @param mode game mode
     * @return this builder
     */
    public MatchBuilder mode(GameMode mode) {
        this.mode = mode;
        return this;
    }

    /**
     * Sets the resolution of the field
     * @param resolution field resolution
     * @return this builder
     */
    public MatchBuilder resolution(Resolution resolution) {
        this.resolution = resolution;
        return this;
    }

    /**
     * Sets the type of match and its target
     * @param matchType type of match
     * @param target minutes or goals to win
     * @return this builder
     */
    public MatchBuilder rules(MatchType matchType, int target) {
        this.matchType = matchType;
        this.target = target;
        return this;
    }

    /**
     * Sets the profile of the machine
     * @param machine profile of the machine
     * @return this builder
     */
    public MatchBuilder machine(MachineProfile machine) {
        this.machine = machine;
        return this;
    }

    /**
     * Copies every choice of a match already played, to play a rematch
     * @param config configuration of the match to repeat
     * @return this builder
     */
    public MatchBuilder copy(MatchConfig config) {
        mode = config.getMode();
        resolution = config.getResolution();
        rules(config.getMatchType(), config.getTarget());
        machine = config.getMachine();
        TeamSide[] sides = TeamSide.values();
        for (int i = 0; i < sides.length; i++) {
            countries.put(sides[i], config.getCountry(sides[i]));
        }
        return this;
    }

    /**
     * Sets the character of one player
     * @param side side of the player
     * @param country character chosen by that player
     * @return this builder
     */
    public MatchBuilder country(TeamSide side, Country country) {
        countries.put(side, country);
        return this;
    }

    /**
     * Sets where the actions of a human player come from
     * @param side side of the player
     * @param source source of its actions
     * @return this builder
     */
    public MatchBuilder source(TeamSide side, ActionSource source) {
        sources.put(side, source);
        return this;
    }

    /**
     * Sets the progress of one player, which gives its upgrades to the character
     * @param side side of the player
     * @param profile progress of that player
     * @return this builder
     */
    public MatchBuilder profile(TeamSide side, PlayerProfile profile) {
        profiles.put(side, profile);
        return this;
    }

    /**
     * Validates the choices and creates the match
     * Humans get their upgrades and their keyboard; the machine gets the stats and the strategy of its profile
     * @return the match ready to play
     * @throws DOPOSoccerException if a value is missing or the target is out of range
     */
    public Match build() throws DOPOSoccerException {
        MatchConfig config = new MatchConfig(mode, resolution, countries.get(TeamSide.LEFT),
                countries.get(TeamSide.RIGHT), matchType, target, machine);
        Match match = new Match(config);
        TeamSide[] sides = TeamSide.values();
        for (int i = 0; i < sides.length; i++) {
            if (!config.isHuman(sides[i])) {
                match.getPlayer(sides[i]).applyStats(machine.createProfile());
            } else if (profiles.containsKey(sides[i])) {
                match.getPlayer(sides[i]).applyStats(profiles.get(sides[i]));
            }
        }
        match.attachControls(sources);
        return match;
    }
}
