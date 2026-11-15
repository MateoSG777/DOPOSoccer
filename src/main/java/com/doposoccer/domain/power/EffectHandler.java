package com.doposoccer.domain.power;

/**
 * Last link: the country of the attacker applies its own effect to the rival
 */
public class EffectHandler extends ImpactHandler {

    @Override
    protected boolean resolve(Hit hit) {
        hit.getAttacker().getCountry().hit(hit.getRival(), hit.getDirection());
        return true;
    }
}
