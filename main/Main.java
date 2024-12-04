package main;

import java.io.IOException;
import java.util.Map;

import main.classifier.SpamClassifier;
import main.model.Dataset;
import main.utils.DatasetSplitter;

public class Main {
    public static void main(String[] args) throws IOException {
        String dataPath = "data/spam_or_not_spam.csv";
        DatasetSplitter splitter = new DatasetSplitter();
        Dataset dataset = splitter.loadAndSplitData(dataPath);

        SpamClassifier classifier = new SpamClassifier();
        try {
            classifier.train(dataset.trainingSet);
            classifier.evaluatePerformance(dataset.testSet);
            Map<String, Double> featureImportance = classifier.getFeatureImportance();
            System.out.println("\nFeature Importance:");
            featureImportance.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .limit(10)
                .forEach(e -> System.out.printf("%s: %.3f\n", e.getKey(), e.getValue()));
        } finally {
        }
    }
}