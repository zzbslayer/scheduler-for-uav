package com.github.zzbslayer.simulator.core.latency.prediction;

import com.github.zzbslayer.simulator.core.latency.record.AccessRecord;

public interface Prediction {
    int[] predict(AccessRecord currentCycleAccess);
}
