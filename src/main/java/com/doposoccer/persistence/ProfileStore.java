package com.doposoccer.persistence;
import com.doposoccer.domain.DOPOSoccerException;
import com.doposoccer.domain.Log;
import com.doposoccer.domain.enums.TeamSide;
import com.doposoccer.domain.shop.PlayerProfile;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.EnumMap;
import java.util.Map;

/**
 * Keeps the progress of player 1 and player 2 and saves it to disk, so points and upgrades
 * survive when the game is closed. It is a singleton: the whole game shares the same progress
 */
public final class ProfileStore {

    /** Name of the file where the progress is saved*/
    public static final String FILE_NAME = "doposoccer.dat";

    private static ProfileStore instance;

    private File file;
    private Map<TeamSide, PlayerProfile> profiles;

    private ProfileStore() {
        useFile(new File(FILE_NAME));
    }

    /**
     * Returns the only store of the game, creating it the first time
     * @return the store
     */
    public static synchronized ProfileStore getInstance() {
        if (instance == null) {
            instance = new ProfileStore();
        }
        return instance;
    }

    /**
     * Changes the file of the store and loads the progress saved in it
     * A missing or damaged file gives new profiles without points
     * @param file file to read and write
     */
    public void useFile(File file) {
        this.file = file;
        this.profiles = load();
        TeamSide[] sides = TeamSide.values();
        for (int i = 0; i < sides.length; i++) {
            profiles.putIfAbsent(sides[i], new PlayerProfile());
        }
    }

    @SuppressWarnings("unchecked")
    private Map<TeamSide, PlayerProfile> load() {
        if (!file.exists()) {
            return new EnumMap<>(TeamSide.class);
        }
        try (ObjectInputStream input = new ObjectInputStream(new FileInputStream(file))) {
            return (Map<TeamSide, PlayerProfile>) input.readObject();
        } catch (IOException | ClassNotFoundException | ClassCastException e) {
            Log.record(e);
            return new EnumMap<>(TeamSide.class);
        }
    }

    /**
     * Writes the progress of both players to disk
     * @throws DOPOSoccerException if the file cannot be written
     */
    public void save() throws DOPOSoccerException {
        try (ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(file))) {
            output.writeObject(profiles);
        } catch (IOException e) {
            Log.record(e);
            throw new DOPOSoccerException(DOPOSoccerException.SAVE_FAILED);
        }
    }

    /**
     * Returns the progress of one player
     * @param side side of the player: player 1 on the left, player 2 on the right
     * @return its profile
     */
    public PlayerProfile get(TeamSide side) {
        return profiles.get(side);
    }
}
