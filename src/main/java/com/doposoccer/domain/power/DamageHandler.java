package com.doposoccer.domain.power;

/**
 * First link: takes the life of the rival. The hit always goes on
 */
public class DamageHandler extends ImpactHandler {

    @Override
    protected boolean resolve(Hit hit) {
        hit.getRival().damage(hit.getDamage());
        return false;
    }
}
