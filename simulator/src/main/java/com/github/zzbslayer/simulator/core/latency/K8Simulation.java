package com.github.zzbslayer.simulator.core.latency;

import com.github.zzbslayer.simulator.core.dataset.DatasetProcessor;
import com.github.zzbslayer.simulator.core.latency.http.HttpCallback;
import com.github.zzbslayer.simulator.core.latency.http.HttpInvoker;
import com.github.zzbslayer.simulator.utils.DataMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class K8Simulation extends DatasetProcessor {
    private long startMillis;
    private long datasetStartMillis;


    @Override
    protected void processLine(String line) throws IOException {
        String[] items = line.split(",");
        String source = items[0];
        long timestamp = (long) Double.parseDouble(items[1]);
        String uri = items[2];
        uri = DataMapper.mapToRealUrl(source, uri);

        while(true) {
            long currentMillis = System.currentTimeMillis();
            if (canBeTriggeredByTime(currentMillis, timestamp))
                break;
        }

        invokeHttpRequest(source, timestamp, uri);
    }

    private boolean canBeTriggeredByTime(long currentMillis, long datasetCurrentMillis) {
        if (currentMillis - startMillis >= datasetCurrentMillis - datasetStartMillis)
            return true;
        return false;
    }

    @Override
    protected void processFirstLine(String line) throws IOException{
        String[] items = line.split(",");
        String source = items[0];
        long timestamp = (long) Double.parseDouble(items[1]);
        String rawUrl = items[2];

        startMillis = System.currentTimeMillis();
        datasetStartMillis = timestamp;

        String url = DataMapper.mapToRealUrl(source, rawUrl);

        invokeHttpRequest(source, timestamp, url);
    }

    private void invokeHttpRequest(final String source, final long timestamp, final String url) throws IOException {
        if (url == null) {
            return;
        }

        log.debug("Preparing http request for (source: {}  timestamp: {}  url: {})", source, timestamp, url);

        final long httpStartTimeStamp = System.currentTimeMillis();
        HttpInvoker.asyncGet(url, new HttpCallback(httpStartTimeStamp, source, timestamp, url));
    }


}
