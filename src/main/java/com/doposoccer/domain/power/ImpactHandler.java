package com.doposoccer.domain.power;

/**
 * Link of the chain of responsibility that resolves a hit to the rival
 * Each link does its part and decides whether the hit goes on to the next link
 * A kick goes through damage and knockout; a power shot can be stopped by the aura of the rival
 * and also replaces the effect of the rival
 */
public abstract class ImpactHandler {

    /** Chain of a normal kick: damage and knockout*/
    public static final ImpactHandler KICK = kickChain();

    /** Chain of a power shot: aura of the rival, damage, knockout, the old effect goes away and the new one arrives*/
    public static final ImpactHandler POWER = powerChain();

    private ImpactHandler next;

    private static ImpactHandler kickChain() {
        ImpactHandler first = new DamageHandler();
        first.setNext(new KnockoutHandler());
        return first;
    }

    private static ImpactHandler powerChain() {
        ImpactHandler first = new AuraHandler();
        first.setNext(new DamageHandler()).setNext(new KnockoutHandler()).setNext(new ReplaceEffectHandler())
                .setNext(new EffectHandler());
        return first;
    }

    /**
     * Sets the link that goes after this one
     * @param next the next link
     * @return the next link, so more links can be added in the same line
     */
    public ImpactHandler setNext(ImpactHandler next) {
        this.next = next;
        return next;
    }

    /**
     * Resolves the hit in this link and passes it to the next one if it is not finished
     * @param hit the hit to the rival
     */
    public void handle(Hit hit) {
        if (!resolve(hit) && next != null) {
            next.handle(hit);
        }
    }

    /**
     * Does the part of this link
     * @param hit the hit to the rival
     * @return true if the hit is finished and the chain must stop
     */
    protected abstract boolean resolve(Hit hit);
}
