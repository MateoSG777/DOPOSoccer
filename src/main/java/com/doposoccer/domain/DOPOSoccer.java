package com.doposoccer.domain;
import com.doposoccer.domain.control.ActionSource;
import com.doposoccer.domain.enums.Stat;
import com.doposoccer.domain.enums.TeamSide;
import com.doposoccer.domain.match.Match;
import com.doposoccer.domain.match.MatchBuilder;
import com.doposoccer.domain.match.MatchConfig;
import com.doposoccer.domain.match.MatchSnapshot;
import com.doposoccer.domain.shop.PlayerProfile;
import com.doposoccer.persistence.MatchSlots;
import com.doposoccer.persistence.ProfileStore;
import java.io.File;
import java.util.Map;

/**
 * Facade of the domain: the only door of the presentation layer to the game
 * It owns the current match, the progress of the players and the saved matches, so the screens
 * never deal with the match, the store and the saved files one by one
 */
public class DOPOSoccer {

    private final ProfileStore store = ProfileStore.getInstance();
    private final MatchSlots slots;
    private volatile Match match;
    private boolean rewarded;

    /** Creates the game with the saved matches of the file of the game */
    public DOPOSoccer() {
        this(new MatchSlots(new File(MatchSlots.FILE_NAME)));
    }

    /**
     * Creates the game with the saved matches of another place, as the unit tests use
     * @param slots slots of saved matches
     */
    public DOPOSoccer(MatchSlots slots) {
        this.slots = slots;
    }

    /**
     * Starts a new match with the upgrades of each player
     * @param builder choices of the setup screens
     * @throws DOPOSoccerException if a value is missing or the target is out of range
     */
    public void startMatch(MatchBuilder builder) throws DOPOSoccerException {
        TeamSide[] sides = TeamSide.values();
        for (int i = 0; i < sides.length; i++) {
            builder.profile(sides[i], store.get(sides[i]));
        }
        match = builder.build();
        rewarded = false;
    }

    /**
     * Advances the match one step. When it finishes, the points are given and saved only once,
     * and the slots where this match was saved are emptied, so its end cannot be played again for more points
     * @param seconds duration of the step
     */
    public void update(double seconds) {
        match.update(seconds);
        if (match.hasWinner() && !rewarded) {
            rewarded = true;
            reward();
        }
    }

    private void reward() {
        TeamSide[] sides = TeamSide.values();
        for (int i = 0; i < sides.length; i++) {
            if (match.getConfig().isHuman(sides[i])) {
                store.get(sides[i]).addPoints(match.getPointsEarned(sides[i]));
            }
        }
        try {
            store.save();
            slots.clearMatch(match.getId());
        } catch (DOPOSoccerException e) {
            Log.record(e);
        }
    }

    // Only for the unit tests of this package, which need to score goals directly
    Match getMatch() {
        return match;
    }

    /**
     * Saves a copy of the current match in a slot. Only a paused match can be saved
     * @param slot slot from 1 to 3
     * @throws DOPOSoccerException if the match is not paused, the slot does not exist or the file cannot be written
     */
    public void saveMatch(int slot) throws DOPOSoccerException {
        if (!isPaused()) {
            throw new DOPOSoccerException(DOPOSoccerException.SAVE_ONLY_PAUSED);
        }
        slots.save(slot, match);
    }

    /**
     * Opens the match saved in a slot. It comes back paused, with the keyboard of the humans
     * and the strategy of the machine attached again
     * @param slot slot from 1 to 3
     * @param sources where the actions of each human come from
     * @throws DOPOSoccerException if the slot does not exist, is empty or its match is damaged
     */
    public void loadMatch(int slot, Map<TeamSide, ActionSource> sources) throws DOPOSoccerException {
        Match loaded = slots.load(slot);
        loaded.attachControls(sources);
        match = loaded;
        rewarded = false;
    }

    /**
     * Tells whether a slot has a saved match
     * @param slot slot from 1 to 3
     * @return true if a match was saved there
     */
    public boolean hasSavedMatch(int slot) {
        return !slots.isEmpty(slot);
    }

    /**
     * Returns the description of the match saved in a slot, such as "JvJ · Por tiempo · 1 - 0"
     * @param slot slot from 1 to 3
     * @return the description, or an empty text if the slot is empty
     */
    public String getSavedMatchLabel(int slot) {
        return slots.getLabel(slot);
    }

    /**
     * Pauses a running match or resumes a paused one
     * @throws DOPOSoccerException if the match is already over
     */
    public void togglePause() throws DOPOSoccerException {
        match.togglePause();
    }

    /**
     * The players end the match: nobody wins and nobody earns points
     * @throws DOPOSoccerException if the match is already over
     */
    public void endMatch() throws DOPOSoccerException {
        match.end();
    }

    /**
     * Buys the next level of a stat for one player and saves the progress
     * @param side side of the player
     * @param stat stat to improve
     * @throws DOPOSoccerException if the upgrade cannot be bought or saved
     */
    public void upgrade(TeamSide side, Stat stat) throws DOPOSoccerException {
        store.get(side).upgrade(stat);
        store.save();
    }

    /**
     * Returns the progress of one player
     * @param side side of the player
     * @return its profile
     */
    public PlayerProfile getProfile(TeamSide side) {
        return store.get(side);
    }

    /**
     * Takes a picture of everything the screen needs to draw one frame
     * @return the current state of the match
     */
    public MatchSnapshot snapshot() {
        return match.snapshot();
    }

    /**
     * Returns the configuration of the current match
     * @return configuration chosen by the users
     */
    public MatchConfig getConfig() {
        return match.getConfig();
    }

    /**
     * Tells whether the match is paused
     * @return true while paused
     */
    public boolean isPaused() {
        return match.getStatus().isPaused();
    }

    /**
     * Tells whether the match is over
     * @return true if it finished or was ended by the players
     */
    public boolean isOver() {
        return match.getStatus().isOver();
    }

    /**
     * Tells whether the match finished by its rules and there is a winner
     * @return true if there is a winner
     */
    public boolean hasWinner() {
        return match.hasWinner();
    }

    /**
     * Returns the winner of a match that finished by its rules
     * @return side of the winner
     * @throws DOPOSoccerException if the match is not over or was ended by the players
     */
    public TeamSide getWinner() throws DOPOSoccerException {
        return match.getWinner();
    }

    /**
     * Returns the points earned in the current match
     * @param side side of the player
     * @return points earned by that player
     */
    public int getPointsEarned(TeamSide side) {
        return match.getPointsEarned(side);
    }
}
