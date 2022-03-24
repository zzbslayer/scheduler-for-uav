package com.github.zzbslayer.simulator.core.dataset;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

@Slf4j
@Data
public abstract class DatasetProcessor {

    protected String datasetPath;

    public DatasetProcessor() {};

    public DatasetProcessor(String datasetPath) {
        this.datasetPath = datasetPath;
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
            if (cnt >= 20000)
                break;
        }

        try {
            Thread.sleep(5000);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

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
    }
}
