package main.utils;

import main.model.Dataset;
import main.model.Email;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class DatasetSplitter {
    public Dataset loadAndSplitData(String dataPath) throws IOException {
        List<Email> emails = loadData(dataPath);

        Collections.shuffle(emails, new Random(42));

        int splitIndex = (int) (emails.size() * 0.8); // 80% training, 20% testing

        List<Email> trainingSet = emails.subList(0, splitIndex);
        List<Email> testSet = emails.subList(splitIndex, emails.size());

        return new Dataset(trainingSet, testSet);
    }

    private List<Email> loadData(String dataPath) throws IOException {
        List<Email> emails = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(dataPath))) {
            String line;
            boolean isHeader = true;

            while ((line = br.readLine()) != null) {
                if (isHeader) {
                    isHeader = false;
                    continue;
                }

                String[] parts = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
                if (parts.length >= 2) {
                    String labelStr = parts[0].trim();
                    String text = parts[1].trim().replaceAll("^\"|\"$", "");

                    try {
                        int label = Integer.parseInt(labelStr);
                        emails.add(new Email(text, label));
                    } catch (NumberFormatException e) {
                        System.err.println("Invalid label found, skipping line: " + line);
                    }
                }
            }
        }

        return emails;
    }
}
