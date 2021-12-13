package com.github.zzbslayer.gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration("sys-config")
public class SysConfig {
    @Value("system.node.name")
    public String SYS_NODE_NAME;

    @Value("system.node.ip")
    public String SYS_NODE_IP;

    @Value("system.pod.namespace")
    public String SYS_POD_NAMESPACE;
}
