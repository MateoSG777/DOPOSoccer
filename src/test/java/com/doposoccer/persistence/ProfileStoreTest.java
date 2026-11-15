package com.doposoccer.persistence;

import com.doposoccer.domain.DOPOSoccerException;
import com.doposoccer.domain.enums.Stat;
import com.doposoccer.domain.enums.TeamSide;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Unit tests of ProfileStore. They use a temporary file, so the real progress is never touched.
 */
class ProfileStoreTest {

    private File file;
    private ProfileStore store;

    /** Points the store to a new temporary file. */
    @BeforeEach
    void setUp() throws IOException {
        file = File.createTempFile("doposoccer", ".dat");
        file.delete();
        file.deleteOnExit();
        store = ProfileStore.getInstance();
        store.useFile(file);
    }

    /** The store should be the same object for the whole game. */
    @Test
    void shouldBeASingleton() {
        assertSame(store, ProfileStore.getInstance());
    }

    /** A missing file should give new profiles for both players. */
    @Test
    void missingFileShouldGiveNewProfiles() {
        assertEquals(0, store.get(TeamSide.LEFT).getPoints());
        assertEquals(Stat.MIN_LEVEL, store.get(TeamSide.RIGHT).getLevel(Stat.KICK));
    }

    /** Points and upgrades should survive after saving and loading again. */
    @Test
    void progressShouldSurviveSavingAndLoading() throws DOPOSoccerException {
        store.get(TeamSide.LEFT).addPoints(700);
        store.get(TeamSide.LEFT).upgrade(Stat.SURVIVAL);
        store.get(TeamSide.RIGHT).addPoints(25);
        store.save();
        store.useFile(file);
        assertEquals(200, store.get(TeamSide.LEFT).getPoints());
        assertEquals(2, store.get(TeamSide.LEFT).getLevel(Stat.SURVIVAL));
        assertEquals(25, store.get(TeamSide.RIGHT).getPoints());
    }

    /** A damaged file should give new profiles instead of stopping the game. */
    @Test
    void damagedFileShouldGiveNewProfiles() throws IOException {
        try (FileWriter writer = new FileWriter(file)) {
            writer.write("this is not a saved game");
        }
        store.useFile(file);
        assertEquals(0, store.get(TeamSide.LEFT).getPoints());
    }

    /** Should report when the progress cannot be written. */
    @Test
    void shouldReportWhenTheFileCannotBeWritten() {
        store.useFile(new File(file.getParentFile(), "missing-folder/doposoccer.dat"));
        try {
            store.save();
            fail("DOPOSoccerException was expected");
        } catch (DOPOSoccerException e) {
            assertEquals(DOPOSoccerException.SAVE_FAILED, e.getMessage());
        }
    }
}
