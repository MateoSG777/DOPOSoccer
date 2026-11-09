package com.doposoccer.domain.match.status;

/**
 * The match reached its end condition. The score counts and there is a definitive winner
 */
public class Finished extends OverStatus {

    @Override
    public String getLabel() {
        return "Partido terminado";
    }

    @Override
    public boolean hasResult() {
        return true;
    }
}
