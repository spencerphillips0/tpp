package main.utils;

import java.util.Map;

public class DistanceCalculator {
    public static double calculateEuclideanDistance(Map<String, Double> features1, Map<String, Double> features2) {
        double sum = 0.0;
        for (String key : features1.keySet()) {
            double v1 = features1.getOrDefault(key, 0.0);
            double v2 = features2.getOrDefault(key, 0.0);

            sum += Math.pow(v1 - v2, 2);
        }
        return Math.sqrt(sum);
    }
}