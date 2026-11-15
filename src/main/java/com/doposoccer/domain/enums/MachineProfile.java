package com.doposoccer.domain.enums;
import com.doposoccer.domain.ai.ClumsyControl;
import com.doposoccer.domain.ai.DefensiveControl;
import com.doposoccer.domain.ai.ExpertControl;
import com.doposoccer.domain.ai.OffensiveControl;
import com.doposoccer.domain.control.ControlStrategy;
import com.doposoccer.domain.shop.PlayerProfile;
import java.util.function.Supplier;

/**
 * Way of playing of the machine. Each profile creates the control strategy that drives the machine player
 * and has its own level of stats: the harder the profile, the stronger its character
 */
public enum MachineProfile {
    // Picks its moves at random
    CLUMSY("Torpe", "Tor", "Escoge al azar sus movimientos", 1, ClumsyControl::new),
    // Always attacks
    OFFENSIVE("Ofensiva", "Ofe", "Su prioridad es anotar", 4, OffensiveControl::new),
    // Stays near its own goal
    DEFENSIVE("Defensiva", "Def", "Su prioridad es defender el arco", 4, DefensiveControl::new),
    // Mixes attack and defense
    EXPERT("Experta", "Exp", "Decide la acción según el estado del partido", 10, ExpertControl::new);

    private final String displayName;
    private final String shortName;
    private final String description;
    private final int statLevel;
    private final Supplier<ControlStrategy> factory;

    MachineProfile(String displayName, String shortName, String description, int statLevel,
                   Supplier<ControlStrategy> factory) {
        this.displayName = displayName;
        this.shortName = shortName;
        this.description = description;
        this.statLevel = statLevel;
        this.factory = factory;
    }

    /**
     * Creates the stats of the machine: every stat at the level of this profile
     * @return a profile with the stats of the machine
     */
    public PlayerProfile createProfile() {
        return new PlayerProfile(statLevel);
    }

    /**
     * Returns the level of every stat of the machine with this profile
     * @return the stat level
     */
    public int getStatLevel() {
        return statLevel;
    }

    /**
     * Creates the strategy that plays with this profile
     * @return a new control strategy
     */
    public ControlStrategy createControl() {
        return factory.get();
    }

    /**
     * Returns the name shown to the users
     * @return the display name
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Returns the abbreviation shown on the scoreboard, such as "Exp"
     * @return the short name
     */
    public String getShortName() {
        return shortName;
    }

    /**
     * Returns a short explanation of how this profile plays
     * @return the description
     */
    public String getDescription() {
        return description;
    }
}
