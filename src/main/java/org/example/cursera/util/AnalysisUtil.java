package org.example.cursera.util;

import java.util.List;

public class AnalysisUtil {

    public static double calculatePercentage(int part, int total) {
        if (total == 0) {
            return 0.0;
        }
        return (part * 100.0) / total;
    }

    public static double calculateAverage(List<Integer> scores) {
        if (scores == null || scores.isEmpty()) {
            return 0.0;
        }
        return scores.stream().mapToInt(Integer::intValue).average().orElse(0.0);
    }
}
