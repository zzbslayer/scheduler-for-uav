package com.github.zzbslayer.simulator.core.availability.algs;

public class Score {
    /**
     * TODO combine graph ava and battery ava
     * @param graphAva
     * @param batteryAva
     * @return
     */
    private final static double ALPHA = 0.8;
    public static double score(double graphAva, double batteryAva) {
        return ALPHA * graphAva + (1 - ALPHA) * batteryAva;
    }
}
