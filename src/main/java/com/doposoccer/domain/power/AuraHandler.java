package com.doposoccer.domain.power;

/**
 * First link of a power shot: a rival that also has the aura takes no damage and no effect,
 * so the chain stops. The ball then makes that rival launch its own power shot at once
 */
public class AuraHandler extends ImpactHandler {

    @Override
    protected boolean resolve(Hit hit) {
        return hit.getRival().hasAura();
    }
}
