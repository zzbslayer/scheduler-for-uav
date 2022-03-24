package com.github.zzbslayer.simulator.core.availability.utils;

import java.util.Arrays;

public class StandardDeviationUtils {
    private static double calculateWorstStandardDevicationOfGivenLimit(int nodeNum, int deployedNodeNum, int serviceNum) {
        int serviceNumOnOneNode = serviceNum / deployedNodeNum;
        int remain = serviceNum % deployedNodeNum;

        double ave = serviceNum * 1.0 / nodeNum;

        double sumOfSquare = 0;

        sumOfSquare = sumOfSquare + Math.pow(serviceNumOnOneNode - ave, 2) * deployedNodeNum;

        sumOfSquare = sumOfSquare + Math.pow(remain - ave, 2);

        sumOfSquare = sumOfSquare + Math.pow(ave, 2) * (nodeNum - deployedNodeNum - 1);

        return Math.sqrt(sumOfSquare / nodeNum);
    }

    private static double calculateStandardDeviation(int[] arr) {
        double ave =  Arrays.stream(arr).average().getAsDouble();
        double temp = 0;
        for (int i: arr) {
            System.out.println(Math.pow(i - ave, 2));
            temp = temp + Math.pow(i - ave, 2);
        }
        return Math.sqrt(temp / arr.length);
    }

    public static void main(String[] args) {
        int[] arr = {9, 9, 9, 9, 9, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        int nodeNum = arr.length;
        int deployedNodeNum = 5;
        int serviceNum = 45;
        System.out.println(calculateStandardDeviation(arr));
        System.out.println(calculateWorstStandardDevicationOfGivenLimit(nodeNum, deployedNodeNum, serviceNum));
    }

}
