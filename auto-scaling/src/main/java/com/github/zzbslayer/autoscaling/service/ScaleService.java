package com.github.zzbslayer.autoscaling.service;

import com.github.zzbslayer.autoscaling.config.KubernetesConfig;
import io.fabric8.kubernetes.api.model.PodList;
import io.fabric8.kubernetes.api.model.apps.DeploymentList;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.RollableScalableResource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ScaleService implements InitializingBean {

    @Autowired
    KubernetesClient kubernetesClient;

    @Autowired
    KubernetesConfig kubernetesConfig;

    @Autowired
    PredictionService predictionService;

    @Override
    public void afterPropertiesSet() throws Exception {
//        PodList podList = kubernetesClient.pods().inNamespace("default").list();
//
//        podList.getItems().stream().forEach(e -> {
//            System.out.println(e.getMetadata());
//        });

        DeploymentList deploymentList = kubernetesClient.apps().deployments().inNamespace(kubernetesConfig.NAMESPACE).list();
        log.info("Listing deployments in namespace {}:", kubernetesConfig.NAMESPACE);
        deploymentList.getItems().stream().forEach(e -> {
            log.info("       '{}' with {} replica(s)" ,e.getMetadata().getName(), e.getSpec().getReplicas());
        });

        kubernetesClient.apps().deployments().inNamespace("default").withName("nginx-deployment").scale(1);
    }

    @Scheduled(cron = "0 0/30 * * * *")
    public void autoScaling(){

        DeploymentList deploymentList = kubernetesClient.apps().deployments().inNamespace(kubernetesConfig.NAMESPACE).list();
        deploymentList.getItems().stream().forEach(e -> {
            int replica = predictionService.getExpectedRelica(e.getMetadata().getName());
            e.getSpec().setReplicas(replica);
        });

    }
}
