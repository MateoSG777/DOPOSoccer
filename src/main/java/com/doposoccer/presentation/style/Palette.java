package com.doposoccer.presentation.style;

/**
 * Names of every color of the game. Screens refer to a color by its name and
 * {@link Theme#color(Palette)} looks up its value in the color dictionary
 */
public enum Palette {

    /** Titles and highlighted texts*/
    GOLD,

    /** Outlines and text on gold buttons*/
    DARK,

    /** Normal texts*/
    WHITE,

    /** Outline of the ball when its image is missing*/
    BLACK,

    /** Disabled buttons and secondary texts*/
    MUTED,

    /** Warnings such as sudden death*/
    ALERT,

    /** Translucent black behind texts and panels*/
    SHADE,

    /** Almost opaque band that hides part of a background image*/
    BAND,

    /** Sky when the stadium image is missing*/
    SKY,

    /** Grass when the stadium image is missing*/
    GRASS,

    /** Background of a screen when its image is missing*/
    FIELD,

    /** Goal frame when the goal images are missing*/
    NET,

    /** Head of South Korea when its image is missing*/
    KOREA,

    /** Head of Cameroon when its image is missing*/
    CAMEROON,

    /** Head of Nigeria when its image is missing*/
    NIGERIA,

    /** Head of the United States when its image is missing*/
    USA
}
