package com.doposoccer.domain.power;

/**
 * Second link: a rival without life is stunned and the chain stops, so it receives no other effect
 */
public class KnockoutHandler extends ImpactHandler {

    @Override
    protected boolean resolve(Hit hit) {
        if (hit.getRival().getLife() > 0) {
            return false;
        }
        hit.getRival().knockOut();
        return true;
    }
}
