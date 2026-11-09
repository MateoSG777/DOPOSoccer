package com.doposoccer.domain.enums;

/**
 * Way of playing of the machine. Its behavior is found with "MachineProfileBehavior" class
 */
public enum MachineProfile {
    // Picks its moves at random
    CLUMSY,
    // Always attacks
    OFFENSIVE,
    // Stays near its own goal
    DEFENSIVE,
    // Mixes attack and defense
    EXPERT
}
