package com.doposoccer.domain.power;

import com.doposoccer.domain.DOPOSoccerException;
import com.doposoccer.domain.enums.Country;
import com.doposoccer.domain.enums.PlayerState;
import com.doposoccer.domain.enums.Stat;
import com.doposoccer.domain.enums.TeamSide;
import com.doposoccer.domain.model.Field;
import com.doposoccer.domain.model.Player;
import com.doposoccer.domain.shop.PlayerProfile;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests of the chain of responsibility that resolves kicks and power shots.
 */
class ImpactChainTest {

    private static final double DELTA = 1e-6;
    private static final double STANDING_Y = Field.GROUND_Y - Player.HEIGHT / 2;

    private Player rival;

    /** Creates a rival standing on the right. */
    @BeforeEach
    void setUp() {
        rival = new Player(Country.KOREA, TeamSide.RIGHT, 700, STANDING_Y);
        rival.update(0.01);
    }

    private Player attacker(Country country) {
        return new Player(country, TeamSide.LEFT, 300, STANDING_Y);
    }

    private void power(Player attacker) {
        ImpactHandler.POWER.handle(new Hit(attacker, rival,
                attacker.getCountry().getBaseDamage() * attacker.getKickFactor()));
    }

    private PlayerProfile levels(Stat stat, int level) throws DOPOSoccerException {
        PlayerProfile profile = new PlayerProfile();
        profile.addPoints(1000000);
        for (int i = Stat.MIN_LEVEL; i < level; i++) {
            profile.upgrade(stat);
        }
        return profile;
    }

    /** The example of the statement: Cameroon with Kick 5 against a rival with Survival 3. */
    @Test
    void shouldFollowTheExampleOfTheStatement() throws DOPOSoccerException {
        Player cameroon = attacker(Country.CAMEROON);
        cameroon.applyStats(levels(Stat.KICK, 5));
        rival.applyStats(levels(Stat.SURVIVAL, 3));
        assertEquals(120, rival.getMaxLife(), DELTA);
        ImpactHandler.KICK.handle(new Hit(cameroon, rival, Player.KICK_DAMAGE * cameroon.getKickFactor()));
        assertEquals(106, rival.getLife(), DELTA);
        rival.applyStats(levels(Stat.SURVIVAL, 3));
        power(cameroon);
        assertEquals(36, rival.getLife(), DELTA);
        assertTrue(rival.isAffected());
    }

    /** A kick should only take life, without any effect. */
    @Test
    void kickShouldOnlyTakeLife() {
        ImpactHandler.KICK.handle(new Hit(attacker(Country.NIGERIA), rival, Player.KICK_DAMAGE));
        assertEquals(Player.BASE_LIFE - Player.KICK_DAMAGE, rival.getLife(), DELTA);
        assertFalse(rival.isAffected());
    }

    /** Cameroon should slow the rival, which then cannot jump. */
    @Test
    void cameroonShouldSlowTheRival() {
        power(attacker(Country.CAMEROON));
        rival.jump();
        rival.move(-1);
        rival.update(0.1);
        assertTrue(rival.isOnGround());
        assertEquals(700 - Player.BASE_SPEED * Player.SLOW_FACTOR * 0.1, rival.getX(), DELTA);
    }

    /** Korea should throw the rival towards its own goal for 1.5 seconds. */
    @Test
    void koreaShouldPushTheRivalTowardsItsGoal() {
        power(attacker(Country.KOREA));
        rival.update(0.1);
        assertTrue(rival.getX() > 700);
        assertEquals(PlayerState.STUNNED, rival.getState());
        rival.update(Country.KOREA.getEffectSeconds());
        assertFalse(rival.isAffected());
    }

    /** Nigeria should throw the rival into the air and leave it unable to act. */
    @Test
    void nigeriaShouldLaunchAndDazeTheRival() {
        power(attacker(Country.NIGERIA));
        rival.update(0.05);
        assertFalse(rival.isOnGround());
        assertEquals(PlayerState.STUNNED, rival.getState());
    }

    /** The illusion should hurt the rival without any other effect. */
    @Test
    void usaShouldOnlyHurtTheRival() {
        power(attacker(Country.USA));
        assertEquals(Player.BASE_LIFE - Country.USA.getBaseDamage(), rival.getLife(), DELTA);
        assertFalse(rival.isAffected());
    }

    /** Effects should not stack: the new effect replaces the old one. */
    @Test
    void newEffectShouldReplaceTheOldOne() throws DOPOSoccerException {
        rival.applyStats(levels(Stat.SURVIVAL, 10));
        power(attacker(Country.CAMEROON));
        power(attacker(Country.NIGERIA));
        rival.update(0.05);
        assertFalse(rival.isOnGround());
        assertEquals(PlayerState.STUNNED, rival.getState());
        rival.update(Country.NIGERIA.getEffectSeconds());
        rival.update(1);
        rival.jump();
        rival.update(0.05);
        assertFalse(rival.isOnGround());
    }

    /** A rival without life should be stunned 3 seconds and then recover all its life. */
    @Test
    void rivalWithoutLifeShouldBeStunnedAndRecover() {
        power(attacker(Country.CAMEROON));
        power(attacker(Country.CAMEROON));
        assertEquals(0, rival.getLife(), DELTA);
        assertTrue(rival.isKnockedOut());
        rival.update(0.1);
        assertEquals(PlayerState.STUNNED, rival.getState());
        rival.update(Player.KNOCKOUT_SECONDS);
        assertEquals(Player.BASE_LIFE, rival.getLife(), DELTA);
        assertFalse(rival.getState() == PlayerState.STUNNED);
    }

    /** The knockout should stop the chain: no effect of the power. */
    @Test
    void knockoutShouldStopTheChain() {
        rival.damage(Player.BASE_LIFE - 1);
        power(attacker(Country.CAMEROON));
        assertTrue(rival.isKnockedOut());
        assertFalse(rival.isAffected());
    }

    /** A rival with the aura should take no damage and no effect from a power shot. */
    @Test
    void rivalWithTheAuraShouldNotBeHurt() {
        rival.chargePower(1);
        rival.activatePower();
        power(attacker(Country.CAMEROON));
        assertEquals(Player.BASE_LIFE, rival.getLife(), DELTA);
        assertFalse(rival.isAffected());
    }

    /** The aura should not protect from a normal kick. */
    @Test
    void auraShouldNotStopAKick() {
        rival.chargePower(1);
        rival.activatePower();
        ImpactHandler.KICK.handle(new Hit(attacker(Country.KOREA), rival, Player.KICK_DAMAGE));
        assertEquals(Player.BASE_LIFE - Player.KICK_DAMAGE, rival.getLife(), DELTA);
    }
}
