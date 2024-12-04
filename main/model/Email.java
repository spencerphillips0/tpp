package main.model;

public class Email {
    public String text;
    public int label; // 1 for spam, 0 for not spam

    public Email(String text, int label) {
        this.text = text;
        this.label = label;
    }
}