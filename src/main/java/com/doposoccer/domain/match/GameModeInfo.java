package com.doposoccer.domain.match;
import com.doposoccer.domain.enums.GameMode;
import java.util.EnumMap;
import java.util.Map;

/**
 * Data of a game mode. A mode that is not available yet is shown disabled in the menu
 * The instance of each mode is found with {@link #get}.
 */
public abstract class GameModeInfo {
    private static final Map<GameMode, GameModeInfo> BY_MODE = new EnumMap<>(GameMode.class);
    static {
        BY_MODE.put(GameMode.PLAYER_VS_PLAYER, new PlayerVsPlayer());
        BY_MODE.put(GameMode.PLAYER_VS_MACHINE, new PlayerVsMachine());
    }

    /**
     * Returns the data of a mode
     * @param mode the mode
     * @return its data
     */
    public static GameModeInfo get(GameMode mode) {
        return BY_MODE.get(mode);
    }

    /**
     * Returns the name shown to the users.
     *
     * @return the display name
     */
    public abstract String getDisplayName();

    /**
     * Tells whether the mode can be played in this version
     * @return true if it can be chosen
     */
    public abstract boolean isAvailable();
}
