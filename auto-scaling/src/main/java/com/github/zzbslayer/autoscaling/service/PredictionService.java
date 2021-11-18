package com.github.zzbslayer.autoscaling.service;

import com.github.zzbslayer.autoscaling.algorithm.access2replica.AccessToReplicaAlgorithm;
import com.github.zzbslayer.autoscaling.algorithm.prediction.PredictionAlgorithm;
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
import java.util.Comparator;
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
    KubernetesService kubernetesService;

    @Autowired
    KubernetesConfig kubernetesConfig;

    @Autowired
    PredictionAlgorithm predictionAlgorithm;

    @Autowired
    AccessToReplicaAlgorithm accessToReplicaAlgorithm;


    @Override
    public void afterPropertiesSet() throws Exception {
//        log.info("Listing prediction result:");
//        predictionRepository.findAll().forEach(e -> log.info("       '{}': {}", e.getName(), e.getPrediction()));

        periodicalPrediction();
    }

    @Scheduled(cron = "0 6/10 * * * *")
    public void periodicalPrediction() {
        DeploymentList deploymentList = kubernetesService.getDeployments(kubernetesConfig.NAMESPACE);
        deploymentList.getItems().stream().forEach(deployment -> {
            String name =  deployment.getMetadata().getName();
            log.debug("============================================");
            log.debug("Scaling deployment {} starts", name);
            int replica = getExpectedReplica(deployment);
            try {
                kubernetesService.scaleDeployment(kubernetesConfig.NAMESPACE, name, replica);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            log.debug("Scaling deployment {} ends", name);
        });
        log.debug("============================================");
    }

    // TODO
    private List<String> getAllDeploymentName() {
        List<String> names = new ArrayList<>();
        names.add("vnf-a");
        return names;
    }

    private int getExpectedReplica(Deployment deployment) {
        String name = deployment.getMetadata().getName();
        List<History> histories = historyRepository.findLastNHistoryByName(name, 5);

        int predictionAccess = predictionAlgorithm.predictAccess(deployment, histories);
        int replica = accessToReplicaAlgorithm.accessToReplica(deployment, histories, predictionAccess);
        return  replica;
    }
}
