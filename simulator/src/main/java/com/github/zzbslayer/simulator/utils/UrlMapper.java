package com.github.zzbslayer.simulator.utils;

import com.github.zzbslayer.simulator.config.NodeConfig;

import java.util.HashMap;
import java.util.Map;

public class UrlMapper {
    private static Map<String, String> serviceMap = new HashMap<>();
    private static String[] nodeList = NodeConfig.NODE_LIST;

    static  {
        serviceMap.put("/rank1", "vnf-2");
        serviceMap.put("/rank2", "vnf-3");
        serviceMap.put("/rank3", "vnf-4/test");
    }

    private static String mapToNode(String from) {
        return nodeList[from.hashCode() % nodeList.length];
    }

    private static String mapToService(String rawUrl) {
        return serviceMap.get(rawUrl);
    }

    public static String mapToRealUrl(String fromIp ,String rawUrl) {
        return "http://" + mapToNode(fromIp) + ":8000/gateway?service=" + mapToService(rawUrl);
    }


}
