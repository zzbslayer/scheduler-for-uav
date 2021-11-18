package com.github.zzbslayer.simulator.http;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

public class HttpHeler {
    public final OkHttpClient okHttpClient = new OkHttpClient();

    public void asyncGet(String url, Callback callback) throws IOException {
        Request request = new Request.Builder()
                .get()
                .url(url)
                .build();
        okHttpClient.newCall(request).enqueue(callback);
    }
}
