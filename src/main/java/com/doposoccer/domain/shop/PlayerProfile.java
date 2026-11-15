package com.doposoccer.domain.shop;
import com.doposoccer.domain.DOPOSoccerException;
import com.doposoccer.domain.enums.Stat;
import java.io.Serializable;
import java.util.EnumMap;
import java.util.Map;

/**
 * Progress of a player: the points won in its matches and the level of each stat
 * The upgrades belong to the player, so they apply to every character it chooses
 */
public class PlayerProfile implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Map<Stat, Integer> levels = new EnumMap<>(Stat.class);
    private int points;

    /** Creates a profile without points and with every stat at the first level */
    public PlayerProfile() {
        this(Stat.MIN_LEVEL);
    }

    /**
     * Creates a profile without points and with every stat at the same level, as the machine uses
     * @param level level of every stat
     */
    public PlayerProfile(int level) {
        Stat[] stats = Stat.values();
        for (int i = 0; i < stats.length; i++) {
            levels.put(stats[i], level);
        }
    }

    /**
     * Adds the points won in a match
     * @param earned points to add
     */
    public void addPoints(int earned) {
        points += earned;
    }

    /**
     * Buys the next level of a stat
     * @param stat stat to improve
     * @throws DOPOSoccerException if the stat is at its last level or the points are not enough
     */
    public void upgrade(Stat stat) throws DOPOSoccerException {
        if (isMaxed(stat)) {
            throw new DOPOSoccerException(DOPOSoccerException.MAX_LEVEL);
        }
        if (points < getCost(stat)) {
            throw new DOPOSoccerException(DOPOSoccerException.NOT_ENOUGH_POINTS);
        }
        points -= getCost(stat);
        levels.put(stat, getLevel(stat) + 1);
    }

    /**
     * Returns how much a stat multiplies its base value at the level of this player
     * @param stat stat to query
     * @return the multiplier
     */
    public double multiplier(Stat stat) {
        return Stat.multiplier(getLevel(stat));
    }

    /**
     * Returns the points needed to buy the next level of a stat
     * @param stat stat to query
     * @return the cost of the upgrade
     */
    public int getCost(Stat stat) {
        return Stat.cost(getLevel(stat));
    }

    /**
     * Tells whether a stat is at its last level
     * @param stat stat to query
     * @return true if it cannot be improved anymore
     */
    public boolean isMaxed(Stat stat) {
        return getLevel(stat) >= Stat.MAX_LEVEL;
    }

    /**
     * Returns the level of a stat
     * @param stat stat to query
     * @return its level
     */
    public int getLevel(Stat stat) {
        return levels.get(stat);
    }

    /**
     * Returns the points the player can spend
     * @return the points
     */
    public int getPoints() {
        return points;
    }
}
