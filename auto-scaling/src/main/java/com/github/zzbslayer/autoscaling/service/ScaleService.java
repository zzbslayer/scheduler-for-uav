package com.github.zzbslayer.autoscaling.service;

import com.github.zzbslayer.autoscaling.config.KubernetesConfig;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.DeploymentList;
import io.fabric8.kubernetes.client.KubernetesClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
            log.info("       '{}': {} replica(s)" ,e.getMetadata().getName(), e.getSpec().getReplicas());
        });

        //kubernetesClient.apps().deployments().inNamespace("default").withName("nginx-deployment").scale(1);
    }

    public DeploymentList getDeployments(String namespace) {
        return kubernetesClient.apps().deployments()
                .inNamespace(namespace)
                .list();
    }

    public void scaleDeployment(String namespace, String name, int replica) {
        log.info("       Scale '{}' in '{}' to {}", name, namespace, replica);
        kubernetesClient.apps().deployments()
                .inNamespace(namespace)
                .withName(name)
                .scale(replica);
    }

//    @Scheduled(cron = "0 5/30 * * * *")
//    public void autoScaling(){
//
//        DeploymentList deploymentList = kubernetesClient.apps().deployments().inNamespace(kubernetesConfig.NAMESPACE).list();
//        deploymentList.getItems().stream().forEach(e -> {
//            int replica = predictionService.getExpectedRelica(e.getMetadata().getName());
//            e.getSpec().setReplicas(replica);
//        });
//
//    }
}
