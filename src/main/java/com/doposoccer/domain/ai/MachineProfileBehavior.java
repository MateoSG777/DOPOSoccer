package com.doposoccer.domain.ai;
import com.doposoccer.domain.enums.MachineProfile;
import java.util.EnumMap;
import java.util.Map;

/**
 * Behavior of a machine profile. Each one will decide the actions of the machine player
 * The instance of each profile is found with {@link #get}.
 */
public abstract class MachineProfileBehavior {

    private static final Map<MachineProfile, MachineProfileBehavior> BY_PROFILE = new EnumMap<>(MachineProfile.class);
    static {
        BY_PROFILE.put(MachineProfile.CLUMSY, new ClumsyProfile());
        BY_PROFILE.put(MachineProfile.OFFENSIVE, new OffensiveProfile());
        BY_PROFILE.put(MachineProfile.DEFENSIVE, new DefensiveProfile());
        BY_PROFILE.put(MachineProfile.EXPERT, new ExpertProfile());
    }

    /**
     * Returns the behavior of a profile
     * @param profile the profile
     * @return its behavior
     */
    public static MachineProfileBehavior get(MachineProfile profile) {
        return BY_PROFILE.get(profile);
    }

    /**
     * Returns the name shown to the users
     * @return the display name
     */
    public abstract String getDisplayName();

    /**
     * Returns a short explanation of how this profile plays
     * @return the description
     */
    public abstract String getDescription();
}
