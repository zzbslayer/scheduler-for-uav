package com.github.zzbslayer.autoscaling.config;

import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KubernetesConfig {
    @Value("${kubernetes.master}")
    public String KUBERNETES_MASTER;

    @Value("${kubernetes.deployment.namespace}")
    public String NAMESPACE;

    @Value("${kubernetes.deployment.default-replica-ratio")
    public Integer DEFAULT_REPLICA_RATIO;

    @Bean
    public KubernetesClient kubernetesClient() {
        Config config = new ConfigBuilder().withMasterUrl(KUBERNETES_MASTER).build();
        KubernetesClient client = new DefaultKubernetesClient(config);
        return client;
    }
}
