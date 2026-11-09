package com.doposoccer.domain.country;

/**
 * United States character
 */
public class Usa extends CountryInfo {

    @Override
    public String getDisplayName() {
        return "Estados Unidos";
    }

    @Override
    public String getPowerName() {
        return "Ilusión";
    }

    @Override
    public String getSpriteKey() {
        return "usa";
    }
}
