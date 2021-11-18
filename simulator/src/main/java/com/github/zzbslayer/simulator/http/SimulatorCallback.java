package com.github.zzbslayer.simulator.http;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class SimulatorCallback implements Callback {

    public SimulatorCallback() {

    }

    @Override
    public void onFailure(Request request, IOException e) {
        e.printStackTrace();
    }

    @Override
    public void onResponse(Response response) throws IOException {
        try (ResponseBody responseBody = response.body()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

//            Headers responseHeaders = response.headers();
//            for (int i = 0, size = responseHeaders.size(); i < size; i++) {
//                System.out.println(responseHeaders.name(i) + ": " + responseHeaders.value(i));
//            }

            log.debug("Response body: {}", responseBody.string());
        }
    }
}
