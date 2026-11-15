package com.doposoccer.domain.enums;
import com.doposoccer.domain.model.Ball;
import com.doposoccer.domain.model.Player;

/**
 * Character a player can choose: its name, the key of its sprites and its power shot (Anexo 1)
 * Each country overrides only the part of the power that makes it different
 */
public enum Country {
    // Aura Azul / Empuje: the rival is thrown towards its own goal
    KOREA("Corea del Sur", "korea", "Aura Azul", 40, 1.5) {
        @Override
        public void hit(Player rival, int direction) {
            rival.push(direction, getEffectSeconds());
        }
    },
    // Rayo / Electrocutado: the rival cannot jump and moves at 50 % of its speed
    CAMEROON("Camerún", "cameroon", "Rayo", 60, 3) {
        @Override
        public void hit(Player rival, int direction) {
            rival.slow(getEffectSeconds());
        }
    },
    // Tornado / Inconsciente: the rival is thrown into the air and cannot move, jump or kick
    NIGERIA("Nigeria", "nigeria", "Tornado", 40, 3) {
        @Override
        public void hit(Player rival, int direction) {
            rival.launch();
            rival.daze(getEffectSeconds());
        }
    },
    // Ilusión / Confusión: seven fake balls fly next to the real one
    USA("Estados Unidos", "usa", "Ilusión", 30, 2.5) {
        @Override
        public void shoot(Ball ball) {
            ball.startIllusion(getEffectSeconds());
        }
    };

    private final String displayName;
    private final String spriteKey;
    private final String powerName;
    private final double baseDamage;
    private final double effectSeconds;

    Country(String displayName, String spriteKey, String powerName, double baseDamage, double effectSeconds) {
        this.displayName = displayName;
        this.spriteKey = spriteKey;
        this.powerName = powerName;
        this.baseDamage = baseDamage;
        this.effectSeconds = effectSeconds;
    }

    /**
     * What the power does to the ball when the shot goes out. Nothing by default
     * @param ball the ball of the match
     */
    public void shoot(Ball ball) {
    }

    /**
     * What the power does to the rival it touches. Nothing by default
     * @param rival the player that received the power shot
     * @param direction direction of the shot: 1 to the right, -1 to the left
     */
    public void hit(Player rival, int direction) {
    }

    /**
     * Returns the name shown to the users
     * @return the display name
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Returns the key used to find the sprites of this country
     * @return the sprite key
     */
    public String getSpriteKey() {
        return spriteKey;
    }

    /**
     * Returns the name of the special power
     * @return the power name
     */
    public String getPowerName() {
        return powerName;
    }

    /**
     * Returns DañoTiro: the life that the power shot takes before the kick multiplier
     * @return the base damage
     */
    public double getBaseDamage() {
        return baseDamage;
    }

    /**
     * Returns how long the effect of the power lasts
     * @return seconds of the effect
     */
    public double getEffectSeconds() {
        return effectSeconds;
    }
}
