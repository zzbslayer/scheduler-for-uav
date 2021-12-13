package com.github.zzbslayer.gateway.service;

import com.github.zzbslayer.gateway.config.SysConfig;
import com.github.zzbslayer.gateway.entity.History;
import com.github.zzbslayer.gateway.repo.HistoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
public class AccessMetricService {
    @Autowired
    SysConfig sysConfig;

    @Autowired
    HistoryRepository historyRepository;

    private ConcurrentHashMap<String, AtomicInteger> accessMap = new ConcurrentHashMap<>();


    public int increaseAccess(String serviceName) {
        return accessMap.computeIfAbsent(serviceName, k -> new AtomicInteger(0)).incrementAndGet();
    }

    /**
     * 假设 5 min 为一个预测周期
     * 提前 10s 把访问量更新一下
     * 00:04:50
     * 00:09:50
     * 00:14:50
     * 00:19:50
     *
     */
    @Scheduled(cron = "50 4/5 * * * *")
    public void updateAccess() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.SECOND, 0);

        for (Map.Entry<String, AtomicInteger> e: accessMap.entrySet()) {
            String node = sysConfig.SYS_NODE_NAME;
            String name = e.getKey();
            int access = e.getValue().get();
            History history = History.builder()
                            .node(node)
                            .name(name)
                            .access(access)
                            .createTime(new Timestamp(calendar.getTimeInMillis()))
                            .build();

            log.debug("Saving history node: {}, name: {}, access: {}", node, name, access);
            historyRepository.save(history);
        }

        accessMap.clear();
    }
}
