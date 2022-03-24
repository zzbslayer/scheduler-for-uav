package com.github.zzbslayer.simulator.core.latency.prediction.mapper;

public class HundredMapper extends AccessInstanceMapper{
    // 9 -> 1; 10 -> 1; 15 -> 2
    @Override
    public int accessToInstance(int svcCnt) {
        if (svcCnt <= 0)
            return 1;

        int num = (svcCnt - 1) / 100 + 1;
        return num;
    }

    @Override
    public int instanceToAccess(int instanceNum) {
        if (instanceNum <= 0)
            throw new RuntimeException("Negative instanceNum");
        return instanceNum * 100;
    }
}
