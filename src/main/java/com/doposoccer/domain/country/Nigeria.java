package com.doposoccer.domain.country;

/**
 * Nigeria character
 */
public class Nigeria extends CountryInfo {

    @Override
    public String getDisplayName() {
        return "Nigeria";
    }

    @Override
    public String getPowerName() {
        return "Tornado";
    }

    @Override
    public String getSpriteKey() {
        return "nigeria";
    }
}
