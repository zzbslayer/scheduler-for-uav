package com.github.zzbslayer.autoscaling.algorithm.access2replica;

import com.github.zzbslayer.autoscaling.entity.History;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class RatioAccessToReplica implements AccessToReplicaAlgorithm {
    @Value("${kubernetes.deployment.default-replica-ratio}")
    public Integer DEFAULT_REPLICA_RATIO;

    @Value("${kubernetes.deployment.ratio-key}")
    public String RATIO_KEY;

    @Value("${kubernetes.deployment.replica.max}")
    public Integer REPLICA_MAX;

    @Value("${kubernetes.deployment.replica.min}")
    public Integer REPLICA_MIN;

    @Override
    public int accessToReplica(Deployment deployment, List<History> histories, int predictionAccess) {
        int targetAccess = predictionAccess;
        int currentAccess = histories.stream()
                .max(Comparator.comparing(History::getCreateTime))
                .orElseGet(() -> History.builder().access(0).build())
                .getAccess();
        /* If futureAccess is 200 => 2 replica and currentAccess is 300 => 3 replica,
         * then we should scale by currentAccess (3 replica)
         * that means proactive scaling only happens when access increasing
         */
        if (targetAccess > currentAccess)
            targetAccess = currentAccess;

        int ratio = getRatio(deployment);
        int replica = mapAccessToReplica(targetAccess, ratio);
        return replica;
    }

    private int mapAccessToReplica(int access, int ratio) {
        return replicaCheck(access / ratio);
    }

    private int getRatio(Deployment deployment) {
        Map<String, String> labels = deployment.getMetadata().getLabels();

        if (labels != null) {
            String ratioStr = labels.get(RATIO_KEY);
            if (ratioStr != null) {

                try {
                    int ratio = Integer.parseInt(ratioStr);
                    log.debug("       Get ratio from label: {}", ratio);
                    return ratio;
                }
                catch (NumberFormatException e) {
                }
            }
        }

        log.debug("       Get ratio by default: {}", DEFAULT_REPLICA_RATIO);
        return DEFAULT_REPLICA_RATIO;
    }


    private int replicaCheck(int replica) {
        if (replica > REPLICA_MAX) {
            log.debug("       Expected replica number {} is too big", replica);
            replica = REPLICA_MAX;
        }
        else if (replica < REPLICA_MIN) {
            log.debug("       Expected replica number {} is too small", replica);
            replica = REPLICA_MIN;
        }
        return replica;
    }
}
