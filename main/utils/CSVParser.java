package main.utils;

import java.io.*;
import java.util.*;

import main.model.Email;

public class CSVParser {
    public List<Email> parseEmails(String filepath) {
        List<Email> emails = new ArrayList<>();
        
        try (BufferedReader br = new BufferedReader(new FileReader(filepath))) {
            String line;
            boolean isHeader = true;
            
            while ((line = br.readLine()) != null) {
                if (isHeader) {
                    isHeader = false;
                    continue;
                }
                
                // email,is_spam
                String[] parts = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
                if (parts.length >= 2) {
                    String text = parts[0].trim();
                    int isSpam = Integer.parseInt(parts[1].trim());
                    emails.add(new Email(text, isSpam));
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading CSV file: " + e.getMessage());
        }
        
        return emails;
    }
}