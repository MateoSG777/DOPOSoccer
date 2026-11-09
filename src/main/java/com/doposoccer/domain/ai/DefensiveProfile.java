package com.doposoccer.domain.ai;
/**
 * Machine whose priority is to defend its goal
 */
public class DefensiveProfile extends MachineProfileBehavior {

    @Override
    public String getDisplayName() {
        return "Defensiva";
    }

    @Override
    public String getDescription() {
        return "Su prioridad es defender el arco";
    }
}
