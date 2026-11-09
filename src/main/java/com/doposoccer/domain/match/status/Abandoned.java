package com.doposoccer.domain.match.status;
/**
 * The players ended the match, therefore there is no winner and no points
 */
public class Abandoned extends OverStatus {

    @Override
    public String getLabel() {
        return "Partido terminado por los jugadores";
    }
}
