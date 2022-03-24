package com.github.zzbslayer.simulator.core.markov;

import com.github.zzbslayer.simulator.config.LatencyExperimentConfig;
import lombok.extern.slf4j.Slf4j;

import java.io.*;

@Slf4j
public class FileHelper {
    public static double[][] readStateFromFile() {
        double[][] state = new double[LatencyExperimentConfig.MARKOV_MATRIX_SIZE][LatencyExperimentConfig.MARKOV_MATRIX_SIZE];
        try {
            BufferedReader csvReader = new BufferedReader(new FileReader(LatencyExperimentConfig.MARKOV_MATRIX_FILE_PATH));
            String row;
            int i = 0;
            while ((row = csvReader.readLine()) != null) {
                String[] arr = row.split(",");
                for (int j = 0; j < LatencyExperimentConfig.MARKOV_MATRIX_SIZE; ++j) {
                    state[i][j] = Double.valueOf(arr[j]);
                }
                ++i;
            }
            csvReader.close();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        return state;

    }

    public static void writeStateToFile(double[][] state) {
        try {
            File stateFile = new File(LatencyExperimentConfig.MARKOV_MATRIX_FILE_PATH);
            if (!stateFile.createNewFile()) {
                stateFile.delete();
                stateFile.createNewFile();
            }
            FileWriter stateFileWriter = new FileWriter(LatencyExperimentConfig.MARKOV_MATRIX_FILE_PATH);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < LatencyExperimentConfig.MARKOV_MATRIX_SIZE; ++i) {
                for (int j = 0; j < LatencyExperimentConfig.MARKOV_MATRIX_SIZE; ++j) {
                    sb.append(state[i][j]);
                    sb.append(',');
                }
                sb.append('\n');
            }
            stateFileWriter.write(sb.toString());
            stateFileWriter.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
