package com.doposoccer.domain.ai;
/**
 * Machine that chooses its action from the state of the match
 */
public class ExpertProfile extends MachineProfileBehavior {

    @Override
    public String getDisplayName() {
        return "Experta";
    }

    @Override
    public String getDescription() {
        return "Decide la acción según el estado del partido";
    }
}
