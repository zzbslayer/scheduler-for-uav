package com.github.zzbslayer.simulator.service;

import com.github.zzbslayer.simulator.config.NodeConfig;
import com.github.zzbslayer.simulator.core.availability.graph.Graph;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class GraphInit implements InitializingBean {
    @Autowired
    RedissonClient redissonClient;

    @Override
    public void afterPropertiesSet() throws Exception {
        int[][] graph = Graph.randomUndirectedGraph(NodeConfig.NODE_NUM);
        RBucket<int[][]> bucket = redissonClient.getBucket("graph-topology");
        bucket.set(graph);
    }
}
