package com.github.zzbslayer.simulator.core.latency.http;

import com.github.zzbslayer.simulator.utils.MetricRecorder;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
@Data
public class HttpCallback implements Callback {
    private final long httpStartTimeStamp;
    private final String source;
    private final long timestamp;
    private final String url;

    public HttpCallback(long httpStartTimeStamp, String source, long timestamp, String url) {
        this.httpStartTimeStamp = httpStartTimeStamp;
        this.source = source;
        this.timestamp = timestamp;
        this.url = url;
    }

    @Override
    public void onFailure(Request request, IOException e) {
        log.debug("Http request fail for (source: {}  timestamp: {}  url: {})", source, timestamp, url);
        MetricRecorder.fail();
        //e.printStackTrace();
    }

    @Override
    public void onResponse(Response response) throws IOException {
        final long httpEndTimeStamp = System.currentTimeMillis();
        final long roundTripTime = httpEndTimeStamp - httpStartTimeStamp;
        if (response.isSuccessful())
            MetricRecorder.success(roundTripTime);
        else
            MetricRecorder.fail();

        log.debug("Get response {} from (source: {}  timestamp: {}  url: {}), RTT: {} ms", response.body().string(), source, timestamp, url, roundTripTime);
    }
}
