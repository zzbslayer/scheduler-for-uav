package com.github.zzbslayer.simulator.core.request;

import com.github.zzbslayer.simulator.core.request.http.HttpCallback;
import com.github.zzbslayer.simulator.core.request.http.HttpInvoker;
import com.github.zzbslayer.simulator.utils.MetricRecorder;
import com.github.zzbslayer.simulator.utils.UrlMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

@Slf4j
public class SimulatorCsvReader {
    private long startMillis;;
    private long datasetStartMillis;
    private String datasetPath;

    public SimulatorCsvReader(String datasetPath) {
        this.datasetPath = datasetPath;
    }

    private void processHead(String head) {

    }

    private void processLine(String line) throws IOException{
        String[] items = line.split(",");
        String source = items[0];
        long timestamp = (long) Double.parseDouble(items[1]);
        String uri = items[2];
        uri = UrlMapper.mapToRealUrl(source, uri);

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

    private void processFirstLine(String line) throws IOException{
        String[] items = line.split(",");
        String source = items[0];
        long timestamp = (long) Double.parseDouble(items[1]);
        String rawUrl = items[2];

        startMillis = System.currentTimeMillis();
        datasetStartMillis = timestamp;

        String url = UrlMapper.mapToRealUrl(source, rawUrl);

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

    public void processCsv() throws IOException {

        BufferedReader csvReader = new BufferedReader(new FileReader(this.datasetPath));
        String row = csvReader.readLine();
        processHead(row);

        row = csvReader.readLine();
        processFirstLine(row);

        int cnt = 0;
        while ((row = csvReader.readLine()) != null) {
            processLine(row);
            cnt++;
            if (cnt >= 10)
                break;
        }

        try {
            Thread.sleep(5000);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        log.info("Success ratio: {}%, Average rtt: {}ms", String.format("%.2f", MetricRecorder.getSuccessRatio()*100) , MetricRecorder.getAverageRtt());
        csvReader.close();
    }
}
