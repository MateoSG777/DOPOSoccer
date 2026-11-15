package com.doposoccer.domain.power;

/**
 * Third link: effects do not stack, so the effect the rival suffers goes away before the new one
 */
public class ReplaceEffectHandler extends ImpactHandler {

    @Override
    protected boolean resolve(Hit hit) {
        hit.getRival().clearEffects();
        return false;
    }
}
