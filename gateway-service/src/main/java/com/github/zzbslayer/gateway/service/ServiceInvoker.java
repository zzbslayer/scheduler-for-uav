package com.github.zzbslayer.gateway.service;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Service
public class ServiceInvoker {
    public static final OkHttpClient okHttpClient = new OkHttpClient();

    @Autowired
    RedissonClient redissonClient;


    public String invokeService(String serviceName) {
        Optional<String> optionalS = findService(serviceName);
        if (!optionalS.isPresent())
            return "[ERROR] Service Not Found";
        String url = optionalS.get();
        Request request = new Request.Builder().url(url).build();
        try {
            Response response = okHttpClient.newCall(request).execute();
            return response.body().string();
        }
        catch (IOException e) {
            return "[ERROR] Service Invoke Error";
        }
    }

    private Optional<String> findService(String serviceName) {
        RBucket <byte[]> bucket = redissonClient.getBucket(serviceName);
        if (!bucket.isExists())
            return Optional.empty();
        byte[] bytes = bucket.get();
        String s = new String(bytes, StandardCharsets.UTF_8);

        return Optional.of("http://" + s + "/" + serviceName + "/test");
    }
}
