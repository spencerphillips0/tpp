package main.classifier;

import main.model.Email;
import main.utils.DistanceCalculator;
import main.utils.FeatureExtractor;

import java.util.*;

public class SpamClassifier {
    private List<Map<String, Double>> trainingFeatures;
    private List<Integer> trainingLabels;
    private FeatureExtractor featureExtractor;
    private int k = 5;

    public SpamClassifier() {
        trainingFeatures = new ArrayList<>();
        trainingLabels = new ArrayList<>();
        featureExtractor = new FeatureExtractor();
    }

    public void train(List<Email> trainingSet) {
        for (Email email : trainingSet) {
            trainingFeatures.add(featureExtractor.extractFeatures(email.text));
            trainingLabels.add(email.label);
        }
    }

    public void evaluatePerformance(List<Email> testSet) {
        int correct = 0;
        int total = testSet.size();

        for (Email email : testSet) {
            Map<String, Double> features = featureExtractor.extractFeatures(email.text);
            int predictedLabel = predict(features);
            if (predictedLabel == email.label) {
                correct++;
            }
        }

        double accuracy = (double) correct / total;
        System.out.printf("Accuracy: %.2f%% (%d/%d)\n", accuracy * 100, correct, total);
    }

    public int predict(Map<String, Double> features) {
        List<Double> distances = new ArrayList<>();
        for (Map<String, Double> trainFeatures : trainingFeatures) {
            double distance = DistanceCalculator.calculateEuclideanDistance(features, trainFeatures);
            distances.add(distance);
        }

        List<Integer> neighborsIndices = getKSmallestIndices(distances, k);

        int spamVotes = 0;
        for (int index : neighborsIndices) {
            if (trainingLabels.get(index) == 1) {
                spamVotes++;
            }
        }

        return spamVotes > k / 2 ? 1 : 0;
    }

    private List<Integer> getKSmallestIndices(List<Double> distances, int k) {
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < distances.size(); i++) {
            indices.add(i);
        }
        indices.sort(Comparator.comparingDouble(distances::get));
        return indices.subList(0, k);
    }

    public Map<String, Double> getFeatureImportance() {
        Map<String, Double> featureVariances = new HashMap<>();
        Map<String, Double> featureMeans = new HashMap<>();
        int n = trainingFeatures.size();

        for (String key : trainingFeatures.get(0).keySet()) {
            featureMeans.put(key, 0.0);
            featureVariances.put(key, 0.0);
        }

        for (Map<String, Double> features : trainingFeatures) {
            for (Map.Entry<String, Double> entry : features.entrySet()) {
                featureMeans.put(entry.getKey(), featureMeans.get(entry.getKey()) + entry.getValue());
            }
        }
        for (String key : featureMeans.keySet()) {
            featureMeans.put(key, featureMeans.get(key) / n);
        }

        for (Map<String, Double> features : trainingFeatures) {
            for (Map.Entry<String, Double> entry : features.entrySet()) {
                double diff = entry.getValue() - featureMeans.get(entry.getKey());
                featureVariances.put(entry.getKey(), featureVariances.get(entry.getKey()) + diff * diff);
            }
        }
        for (String key : featureVariances.keySet()) {
            featureVariances.put(key, featureVariances.get(key) / (n - 1));
        }

        return featureVariances;
    }
}