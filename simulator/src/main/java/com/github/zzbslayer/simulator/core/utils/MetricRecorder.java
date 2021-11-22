package com.github.zzbslayer.simulator.core.utils;

public class MetricRecorder {
    private static long sumRtt = 0;
    private static int cnt = 0;
    private static int successCnt = 0;

    public synchronized static void success(long currentRtt) {
        sumRtt += currentRtt;
        successCnt++;
        cnt++;
    }

    public synchronized static void fail() {
        cnt++;
    }

    public static long getAverageRtt() {
        return sumRtt / successCnt;
    }

    public static double getSuccessRatio() {
        return (double)successCnt / cnt;
    }
}
