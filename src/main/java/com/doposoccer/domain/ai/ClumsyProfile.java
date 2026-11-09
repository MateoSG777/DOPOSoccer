package com.doposoccer.domain.ai;
/**
 * Clumsy achine that picks its moves at random
 */
public class ClumsyProfile extends MachineProfileBehavior {

    @Override
    public String getDisplayName() {
        return "Torpe";
    }

    @Override
    public String getDescription() {
        return "Escoge al azar sus movimientos";
    }
}
