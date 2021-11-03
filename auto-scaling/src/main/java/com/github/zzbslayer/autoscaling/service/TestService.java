package com.github.zzbslayer.autoscaling.service;

import io.fabric8.kubernetes.api.model.PodList;
import io.fabric8.kubernetes.api.model.apps.DeploymentList;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TestService implements InitializingBean {

    @Autowired
    KubernetesClient kubernetesClient;

    @Override
    public void afterPropertiesSet() throws Exception {
        PodList podList = kubernetesClient.pods().inNamespace("default").list();

        podList.getItems().stream().forEach(e -> {
            System.out.println(e.getMetadata());
        });

        DeploymentList aDeploymentList = kubernetesClient.apps().deployments().inNamespace("default").list();
        aDeploymentList.getItems().stream().forEach(e -> {
            System.out.println(e.getMetadata());
        });

        kubernetesClient.apps().deployments().inNamespace("default").withName("nginx-deployment").scale(1);
    }
}
