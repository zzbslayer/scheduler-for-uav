package com.github.zzbslayer.autoscaling.algorithm.prediction;

import com.github.zzbslayer.autoscaling.entity.History;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class LastValuePrediction implements PredictionAlgorithm {
    @Override
    public int predictAccess(Deployment deployment, List<History> histories) {
        StringBuilder sb = new StringBuilder();
        histories.stream().forEach(h -> {
            sb.append(h.getAccess());
            sb.append(", ");
        });
        log.debug("       Histories: {}", sb);
        int size = histories.size();
        int res = 0;
        if (size > 0)
             res = histories.get(size-1).getAccess();
        log.debug("       Prediction result: {}", res);
        return res;
    }
}
