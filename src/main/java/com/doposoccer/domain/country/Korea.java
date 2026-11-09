package com.doposoccer.domain.country;

/**
 * South Korea character
 */
public class Korea extends CountryInfo {

    @Override
    public String getDisplayName() {
        return "Corea del Sur";
    }

    @Override
    public String getPowerName() {
        return "Aura Azul";
    }

    @Override
    public String getSpriteKey() {
        return "korea";
    }
}
