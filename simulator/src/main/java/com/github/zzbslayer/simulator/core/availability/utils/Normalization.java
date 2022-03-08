package com.github.zzbslayer.simulator.core.availability.utils;

import java.util.Arrays;

public class Normalization {
    public static double[] maxMinNormalization(double[] values, double max, double min) {
        double[] normalizedValues = Arrays.stream(values).map(x -> (x - min)/(max - min)).toArray();
        return normalizedValues;
    }

    public static double[] maxMinNormalization(double[] values) {
        double min = Arrays.stream(values).min().getAsDouble();
        double max = Arrays.stream(values).max().getAsDouble();
        //System.out.println("\n??? min: " + min + ", max: "+max);

        if (min == max) {
            double[] res = new double[values.length];
            Arrays.fill(res, 0.5);
            return res;
        }


        double[] normalizedValues = Arrays.stream(values).map(x -> (x - min)/(max - min)).toArray();
        return normalizedValues;
    }

    public static double[] zscoreNormalization(double[] values) {
        double average = Arrays.stream(values).average().getAsDouble();
        /**
         * sqrt( sum((x - ave)^2) / n )
         */
        double standardDeviation = Math.sqrt(
                Arrays.stream(values)
                    .map(x -> Math.pow(x - average, 2))
                    .average().getAsDouble()
        );

        double[] normalizedValues = Arrays.stream(values).map(x -> (x - average) / standardDeviation).toArray();
        return normalizedValues;
    }

    public static void main(String args[]) {
        double[] test = { 0.3, 0.5, 0.6, 0.66};
        double[] nt = maxMinNormalization(test);
        Arrays.stream(nt).forEach(System.out::println);
    }
}
