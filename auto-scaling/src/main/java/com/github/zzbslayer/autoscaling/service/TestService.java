package com.github.zzbslayer.autoscaling.service;


import io.fabric8.kubernetes.api.model.NamespaceList;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;

public class TestService {
    public static void main(String[] args) {
        Config config = new ConfigBuilder().withMasterUrl("http://10.0.0.94:8080").build();
        KubernetesClient client = new DefaultKubernetesClient(config);
        NamespaceList myNs = client.namespaces().list();
        myNs.getItems().stream().forEach(e -> {
            System.out.println(e.toString());
        });

    }
}
