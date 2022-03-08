package com.github.zzbslayer.simulator.core.latency;

import com.github.zzbslayer.simulator.utils.MetricRecorder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

@Slf4j
@Data
public abstract class GraphLatencySimulation {

    private String datasetPath;

    GraphLatencySimulation() {}

    public static GraphLatencySimulation newInstance(String datasetPath) {
        GraphLatencySimulation simulation = new InMemorySimulation();
        simulation.setDatasetPath(datasetPath);
        return simulation;
    }

    protected void start() {

    }

    protected void end() {

    }

    protected void processHead(String head) {

    }

    protected abstract void processLine(String line) throws IOException;

    protected abstract void processFirstLine(String line) throws IOException;

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
            if (cnt >= 1000)
                break;
        }

        try {
            Thread.sleep(5000);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        //log.info("Success ratio: {}%, Average rtt: {}ms", String.format("%.2f", MetricRecorder.getSuccessRatio()*100) , MetricRecorder.getAverageRtt());
        csvReader.close();
    }

    public void execute() {
        start();
        try {
            processCsv();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        end();
    }

    public static void main(String[] args) {
        GraphLatencySimulation simulation = GraphLatencySimulation.newInstance("F:/reins/scheduler-for-ad-hoc-network/dataset/web-server-log/kaggle-eliasdabbas-web-server-access-logs/access_log.csv");
        simulation.execute();
    }
}
