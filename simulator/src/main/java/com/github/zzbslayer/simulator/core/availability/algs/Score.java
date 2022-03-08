package com.github.zzbslayer.simulator.core.availability.algs;

public class Score {
    /**
     * TODO combine graph ava and battery ava
     * @param graphAva
     * @param batteryAva
     * @return
     */
    public static double score(double graphAva, double batteryAva) {
        return 0.8 * graphAva + 0.2 * batteryAva;
    }
}
