package main.model;

import java.util.List;
import main.model.Email;

public class Dataset {
    public List<Email> trainingSet;
    public List<Email> testSet;
    
    public Dataset(List<Email> training, List<Email> test) {
        this.trainingSet = training;
        this.testSet = test;
    }
}