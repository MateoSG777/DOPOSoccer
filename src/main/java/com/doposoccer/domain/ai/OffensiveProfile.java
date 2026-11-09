package com.doposoccer.domain.ai;
/**
 * Machine whose priority is to score
 */
public class OffensiveProfile extends MachineProfileBehavior {

    @Override
    public String getDisplayName() {
        return "Ofensiva";
    }

    @Override
    public String getDescription() {
        return "Su prioridad es anotar";
    }
}
