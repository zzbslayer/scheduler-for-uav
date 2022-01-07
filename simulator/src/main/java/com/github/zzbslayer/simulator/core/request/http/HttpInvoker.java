package com.github.zzbslayer.simulator.core.request.http;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;

import java.io.IOException;

public class HttpInvoker {
    public static final OkHttpClient okHttpClient = new OkHttpClient();

    public static void asyncGet(String url, Callback callback) throws IOException {
        Request request = new Request.Builder()
                .get()
                .url(url)
                .build();
        okHttpClient.newCall(request).enqueue(callback);
    }
}
