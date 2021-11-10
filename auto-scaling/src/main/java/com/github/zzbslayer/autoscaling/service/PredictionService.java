package com.github.zzbslayer.autoscaling.service;

import com.github.zzbslayer.autoscaling.config.KubernetesConfig;
import com.github.zzbslayer.autoscaling.entity.History;
import com.github.zzbslayer.autoscaling.repo.HistoryRepository;
import com.github.zzbslayer.autoscaling.repo.PredictionRepository;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.DeploymentList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class PredictionService implements InitializingBean {

    @Autowired
    PredictionRepository predictionRepository;

    @Autowired
    HistoryRepository historyRepository;

    @Autowired
    ScaleService scaleService;

    @Autowired
    KubernetesConfig kubernetesConfig;

    String RATIO_KEY = "ratio";

    @Override
    public void afterPropertiesSet() throws Exception {
        log.info("Listing prediction result:");
        predictionRepository.findAll().forEach(e -> log.info("       '{}': {}", e.getName(), e.getPrediction()));

        periodicalPrediction();
    }

    @Scheduled(cron = "0 1/30 * * * *")
    public void periodicalPrediction() {
        DeploymentList deploymentList = scaleService.getDeployments(kubernetesConfig.NAMESPACE);
        deploymentList.getItems().stream().forEach(deployment -> {
            String name =  deployment.getMetadata().getName();
            log.debug("Scaling deployment {} starts", name);
            int replica = getExpectedReplica(deployment);
            try {
                scaleService.scaleDeployment(kubernetesConfig.NAMESPACE, name, replica);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            log.debug("Scaling deployment {} ends", name);
        });
    }

    // TODO
    private List<String> getAllDeploymentName() {
        List<String> names = new ArrayList<>();
        names.add("vnf-a");
        return names;
    }

    // TODO
    private int predictAccess(List<History> histories){
        int res = 100;
        log.debug("Prediction result: {}", res);
        return res;
    }

    private int replicaCheck(int replica) {
        if (replica > 10) {
            log.debug("Expected replica number {} is too big", replica);
            replica = 10;
        }
        else if (replica < 1) {
            log.debug("Expected replica number {} is too small", replica);
            replica = 1;
        }
        return replica;
    }

    private int mapAccessToReplica(int access, int ratio) {
        return replicaCheck(access / ratio);
    }

    private int getRatio(Deployment deployment) {
        int defaultVal = kubernetesConfig.DEFAULT_REPLICA_RATIO;

        Map<String, String> labels = deployment.getMetadata().getLabels();

        if (labels != null) {
            String ratioStr = labels.get(RATIO_KEY);
            if (ratioStr != null) {

                try {
                    int ratio = Integer.parseInt(ratioStr);
                    log.debug("Get ratio from label: {}", ratio);
                    return ratio;
                }
                catch (NumberFormatException e) {
                }
            }
        }

        log.debug("Get ratio by default: {}", kubernetesConfig.DEFAULT_REPLICA_RATIO);
        return defaultVal;
    }

    private int getExpectedReplica(Deployment deployment) {
        String name = deployment.getMetadata().getName();
        List<History> histories = historyRepository.findLastNHistoryByName(name);
        int futureAccess = predictAccess(histories);
        int ratio = getRatio(deployment);
        int replica = mapAccessToReplica(futureAccess, ratio);
        return  replica;
    }
}
