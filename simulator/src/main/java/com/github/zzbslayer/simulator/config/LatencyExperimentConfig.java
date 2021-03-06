package com.github.zzbslayer.simulator.config;

import com.github.zzbslayer.simulator.core.availability.utils.ScenarioParamter;

public class LatencyExperimentConfig {
    // TODO 调度周期需要调整，因为数据集中的访问频率很低
    public final static int SCHEDULE_CYCLE = 1000 * 60 * 10;
    public final static String DATASET_PATH = "F:/reins/scheduler-for-ad-hoc-network/dataset/web-server-log/kaggle-eliasdabbas-web-server-access-logs/access_log.csv";

    public final static int NODE_NUM = 10;
    public final static int NODE_CAPACITY = 20;
    public final static int SERVICE_NUM = 10;
    public final static double AVAILABILITY = 1;
    public final static ScenarioParamter SCENARIO_PARAMTER = ScenarioParamter.randomNewInstance(NODE_NUM, NODE_CAPACITY, SERVICE_NUM, AVAILABILITY);
    public final static int HISTORY_SIZE = 10;

    // 总共 100w 条数据，后60w用来训练，前30w用来测试。
    public final static int TESTING_SET_END = 300000;
    public final static int TRAINING_SET_START = 300000;

    public final static int MARKOV_MATRIX_SIZE = 40;
    public final static String MARKOV_MATRIX_FILE_PATH = "F:/reins/scheduler-for-ad-hoc-network/dataset/web-server-log/kaggle-eliasdabbas-web-server-access-logs/markov.csv";
}
