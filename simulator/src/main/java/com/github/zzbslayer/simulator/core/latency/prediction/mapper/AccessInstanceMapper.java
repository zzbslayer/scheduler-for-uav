package com.github.zzbslayer.simulator.core.latency.prediction.mapper;

public abstract class AccessInstanceMapper {
    private static AccessInstanceMapper mapper = new HundredMapper();
    public static AccessInstanceMapper getMapper() {
        return mapper;
    }
    public abstract int accessToInstance(int access);
    public abstract int instanceToAccess(int instance);
}
