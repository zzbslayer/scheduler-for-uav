package com.github.zzbslayer.simulator.core.utils;

import java.util.HashMap;
import java.util.Map;

public class UrlMapper {
    private static Map<String, String> map = new HashMap<>();

    static  {
        map.put("/rank1", "http://10.0.0.94/vnf-2/test");
        map.put("/rank2", "http://localhost/vnf-3/test");
        map.put("/rank3", "http://localhost/vnf-4/test");
    }

    public static String mapToRealUrl(String rawUrl) {
        return map.get(rawUrl);
    }
}
