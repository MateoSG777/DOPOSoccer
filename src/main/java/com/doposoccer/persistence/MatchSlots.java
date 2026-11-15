package com.doposoccer.persistence;
import com.doposoccer.domain.DOPOSoccerException;
import com.doposoccer.domain.Log;
import com.doposoccer.domain.match.Match;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * The three slots where matches are saved. They are kept in a file, so a saved match can be
 * opened after closing the game. Each slot keeps a copy of the match as it was when it was saved
 */
public class MatchSlots {

    /** Name of the file where the matches are saved*/
    public static final String FILE_NAME = "doposoccer-partidas.dat";

    /** Number of slots: 1, 2 and 3*/
    public static final int SLOTS = 3;

    private final File file;
    private Map<Integer, String> labels = new HashMap<>();
    private Map<Integer, byte[]> matches = new HashMap<>();
    private Map<Integer, String> ids = new HashMap<>();

    /**
     * Opens the slots saved in a file. A missing or damaged file gives empty slots
     * @param file file to read and write
     */
    public MatchSlots(File file) {
        this.file = file;
        load();
    }

    @SuppressWarnings("unchecked")
    private void load() {
        if (!file.exists()) {
            return;
        }
        try (ObjectInputStream input = new ObjectInputStream(new FileInputStream(file))) {
            labels = (Map<Integer, String>) input.readObject();
            matches = (Map<Integer, byte[]>) input.readObject();
            ids = (Map<Integer, String>) input.readObject();
        } catch (IOException | ClassNotFoundException | ClassCastException e) {
            Log.record(e);
            labels = new HashMap<>();
            matches = new HashMap<>();
            ids = new HashMap<>();
        }
    }

    /**
     * Saves a copy of a match in a slot, replacing what the slot had, and writes the file
     * @param slot slot from 1 to 3
     * @param match match to save
     * @throws DOPOSoccerException if the slot does not exist or the file cannot be written
     */
    public void save(int slot, Match match) throws DOPOSoccerException {
        checkSlot(slot);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (ObjectOutputStream output = new ObjectOutputStream(bytes)) {
            synchronized (match) {
                output.writeObject(match);
            }
        } catch (IOException e) {
            Log.record(e);
            throw new DOPOSoccerException(DOPOSoccerException.MATCH_SAVE_FAILED);
        }
        matches.put(slot, bytes.toByteArray());
        labels.put(slot, match.describe());
        ids.put(slot, match.getId());
        write();
    }

    /**
     * Empties every slot that keeps a copy of a match, so a finished match cannot be played again
     * @param matchId identifier of the match
     * @throws DOPOSoccerException if the file cannot be written
     */
    public void clearMatch(String matchId) throws DOPOSoccerException {
        boolean changed = false;
        for (int i = 1; i <= SLOTS; i++) {
            if (matchId.equals(ids.get(i))) {
                matches.remove(i);
                labels.remove(i);
                ids.remove(i);
                changed = true;
            }
        }
        if (changed) {
            write();
        }
    }

    private void write() throws DOPOSoccerException {
        try (ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(file))) {
            output.writeObject(labels);
            output.writeObject(matches);
            output.writeObject(ids);
        } catch (IOException e) {
            Log.record(e);
            throw new DOPOSoccerException(DOPOSoccerException.MATCH_SAVE_FAILED);
        }
    }

    /**
     * Opens a new copy of the match saved in a slot; the slot keeps its own copy
     * @param slot slot from 1 to 3
     * @return the saved match
     * @throws DOPOSoccerException if the slot does not exist, is empty or its match is damaged
     */
    public Match load(int slot) throws DOPOSoccerException {
        checkSlot(slot);
        if (isEmpty(slot)) {
            throw new DOPOSoccerException(DOPOSoccerException.EMPTY_SLOT);
        }
        try (ObjectInputStream input = new ObjectInputStream(new ByteArrayInputStream(matches.get(slot)))) {
            return (Match) input.readObject();
        } catch (IOException | ClassNotFoundException | ClassCastException e) {
            Log.record(e);
            throw new DOPOSoccerException(DOPOSoccerException.MATCH_LOAD_FAILED);
        }
    }

    /**
     * Tells whether a slot has no match
     * @param slot slot from 1 to 3
     * @return true if nothing was saved there
     */
    public boolean isEmpty(int slot) {
        return !matches.containsKey(slot);
    }

    /**
     * Returns the description of the match saved in a slot
     * @param slot slot from 1 to 3
     * @return the description, or an empty text if the slot is empty
     */
    public String getLabel(int slot) {
        return labels.getOrDefault(slot, "");
    }

    private void checkSlot(int slot) throws DOPOSoccerException {
        if (slot < 1 || slot > SLOTS) {
            throw new DOPOSoccerException(DOPOSoccerException.INVALID_SLOT);
        }
    }
}
