package com.github.zzbslayer.vnfdemo.service;

import com.github.zzbslayer.vnfdemo.config.ApplicationConfig;
import com.github.zzbslayer.vnfdemo.entity.History;
import com.github.zzbslayer.vnfdemo.repo.HistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class AccessMetricService {
    @Autowired
    ApplicationConfig applicationConfig;

    @Autowired
    HistoryRepository historyRepository;

    private AtomicInteger access = new AtomicInteger(0);

    public int increaseAccess(int i) {
        return access.addAndGet(i);
    }

    public int increaseAccess() {
        return access.addAndGet(1);
    }

    @Scheduled(cron = "0 58 * * * *")
    public void updateAccess() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR);
        access.set(hour * 100);

        History history = History.builder()
                        .access(access.get())
                        .name(applicationConfig.APP_NAME)
                        .build();
        historyRepository.save(history);
        access.set(0);
    }
}
