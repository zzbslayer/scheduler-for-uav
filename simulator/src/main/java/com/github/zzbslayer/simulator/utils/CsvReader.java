package com.github.zzbslayer.simulator.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public abstract class CsvReader {
    public abstract void processHead(String head);
    public abstract void processLine(String line);

    public void processCsv(String pathToCsv) throws IOException {
        BufferedReader csvReader = new BufferedReader(new FileReader(pathToCsv));
        String row = csvReader.readLine();
        processHead(row);

        int cnt = 0;
        while ((row = csvReader.readLine()) != null) {
            processLine(row);
            if (cnt++ >= 10)
                break;
        }
        csvReader.close();
    }
}
