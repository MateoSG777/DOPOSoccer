package com.doposoccer.domain.country;
import com.doposoccer.domain.enums.Country;
import java.util.EnumMap;
import java.util.Map;

/**
 * General data of a country, including its name, its power and the key of its sprites
 * The instance of each country is found with {@link #get}
 */
public abstract class CountryInfo {
    private static final Map<Country, CountryInfo> BY_COUNTRY = new EnumMap<>(Country.class);
    static {
        BY_COUNTRY.put(Country.KOREA, new Korea());
        BY_COUNTRY.put(Country.CAMEROON, new Cameroon());
        BY_COUNTRY.put(Country.NIGERIA, new Nigeria());
        BY_COUNTRY.put(Country.USA, new Usa());
    }

    /**
     * Returns the data of a country.
     * @param country the country
     * @return its data
     */
    public static CountryInfo get(Country country) {
        return BY_COUNTRY.get(country);
    }

    /**
     * Returns the name shown to the users
     * @return the display name
     */
    public abstract String getDisplayName();

    /**
     * Returns the name of the special power
     * @return the power name
     */
    public abstract String getPowerName();

    /**
     * Returns the key used to find the sprites of this country
     * @return the sprite key
     */
    public abstract String getSpriteKey();
}