package com.doposoccer.domain.enums;

/**
 * Status of a match. What each one allows is found with MatchStatusBehavior
 */
public enum MatchStatus {
    PLAYING,
    PAUSED,
    FINISHED,
    // The players ended the match
    ABANDONED
}
