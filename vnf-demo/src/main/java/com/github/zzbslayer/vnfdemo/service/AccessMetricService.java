package com.github.zzbslayer.vnfdemo.service;

import com.github.zzbslayer.vnfdemo.config.ApplicationConfig;
import com.github.zzbslayer.vnfdemo.entity.Dblock;
import com.github.zzbslayer.vnfdemo.entity.History;
import com.github.zzbslayer.vnfdemo.repo.HistoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
public class AccessMetricService {
    @Autowired
    ApplicationConfig applicationConfig;

    @Autowired
    HistoryRepository historyRepository;

    @Autowired
    LockService lockService;

    private AtomicInteger access = new AtomicInteger(0);

    public int increaseAccess(int i) {
        return access.addAndGet(i);
    }

    public int increaseAccess() {
        return access.addAndGet(1);
    }

    @Scheduled(cron = "0 0/10 * * * *")
    public void updateAccess() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.SECOND, 0);

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String createTime = format.format(calendar.getTime());

        int hour = calendar.get(Calendar.MINUTE);
        access.set(hour * 10);

        final String RESOURCE_LOCK = applicationConfig.APP_NAME + "_access";
        Dblock dblock = lockService.lock(RESOURCE_LOCK, "lock of "+applicationConfig.APP_NAME+" for updating access metric");

        try {
            Optional<History> _history = historyRepository.findHistoryByNameAndTime(applicationConfig.APP_NAME, createTime);
            History history = _history.orElseGet(() -> {
               return History.builder()
                       .access(0)
                       .name(applicationConfig.APP_NAME)
                       .createTime(new Timestamp(calendar.getTimeInMillis()))
                       .build();
            });

            int from = history.getAccess();
            int to = from + access.get();
            history.setAccess(to);

            log.debug("Updating history access from {} to {}", from, to);
            historyRepository.save(history);
            access.set(0);
        }
        finally {
            lockService.release(dblock);
        }

    }
}
