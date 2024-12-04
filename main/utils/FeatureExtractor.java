package main.utils;

import java.util.*;
import java.util.regex.*;

public class FeatureExtractor {
    // find actual spam keywords later
    private static final String[] SPAM_KEYWORDS = {
        "free", "winner", "win", "won", "prize", "urgent", "offer"
    };
    
    private static final Pattern CURRENCY_PATTERN = 
    Pattern.compile("\\$\\d+(?:,\\d{3})*(?:\\.\\d{2})?|\\d+(?:,\\d{3})*(?:\\.\\d{2})?\\s?(?:USD|EUR|GBP)");

    public Map<String, Double> extractFeatures(String text) {
        String normalizedText = text.toLowerCase();
        Map<String, Double> features = new HashMap<>();

        features.put("length", (double) text.length());
        features.put("wordCount", (double) text.split("\\s+").length);
        features.put("avgWordLength", calculateAvgWordLength(text));
        features.put("urlCount", countUrls(text));
        features.put("numberCount", countNumbers(text));
        features.put("uniqueWordRatio", calculateUniqueWordRatio(text));
        features.put("hasHtml", containsHtml(text) ? 1.0 : 0.0);
        features.put("exclamationRatio", countExclamations(text) / (double) text.length());
        features.put("capitalRatio", countCapitals(text) / (double) text.length());
        
        features.put("spamKeywordCount", countSpamKeywords(normalizedText));
        features.put("spamKeywordRatio", features.get("spamKeywordCount") / features.get("wordCount"));
        features.put("hasMoneySymbols", containsMoneySymbols(text) ? 1.0 : 0.0);
        features.put("consecutiveCapitalWords", countConsecutiveCapitalWords(text));
        features.put("emailAddressCount", countEmailAddresses(text));
        features.put("phoneNumberCount", countPhoneNumbers(text));
        features.put("hasWeirdFormatting", hasWeirdFormatting(text) ? 1.0 : 0.0);
        features.put("repeatedCharCount", countRepeatedChars(text));
        features.put("spacingIrregularity", calculateSpacingIrregularity(text));
        
        features.put("suspiciousPatternScore", calculateSuspiciousPatternScore(normalizedText));
        features.put("currencyMentionCount", countCurrencyMentions(text));
        features.put("urgencyScore", calculateUrgencyScore(normalizedText));
        features.put("grammarErrorScore", calculateGrammarErrorScore(text));
        features.put("linkDensity", calculateLinkDensity(text));
        features.put("specialCharRatio", calculateSpecialCharRatio(text));
        features.put("sentenceLengthVariance", calculateSentenceLengthVariance(text));
        return features;
    }
    
    private double countSpamKeywords(String text) {
        int count = 0;
        String[] words = text.split("\\s+");
        for (String keyword : SPAM_KEYWORDS) {
            for (String word : words) {
                if (word.contains(keyword)) {
                    count++;
                }
            }
        }
        return count;
    }
    
    private boolean containsMoneySymbols(String text) {
        return text.matches(".*[$€£¥].*") || 
               text.toLowerCase().matches(".*\\b(usd|eur|gbp)\\b.*");
    }
    
    private double countConsecutiveCapitalWords(String text) {
        String[] words = text.split("\\s+");
        int consecutive = 0;
        int maxConsecutive = 0;
        
        for (String word : words) {
            if (word.matches("[A-Z]{2,}")) {
                consecutive++;
                maxConsecutive = Math.max(maxConsecutive, consecutive);
            } else {
                consecutive = 0;
            }
        }
        return maxConsecutive;
    }
    
    private double countEmailAddresses(String text) {
        Pattern pattern = Pattern.compile("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}");
        Matcher matcher = pattern.matcher(text);
        int count = 0;
        while (matcher.find()) count++;
        return count;
    }
    
    private double countPhoneNumbers(String text) {
        // Match common phone number formats
        Pattern pattern = Pattern.compile("\\b\\d{3}[-.]?\\d{3}[-.]?\\d{4}\\b|\\(\\d{3}\\)\\s?\\d{3}[-.]?\\d{4}");
        Matcher matcher = pattern.matcher(text);
        int count = 0;
        while (matcher.find()) count++;
        return count;
    }
    
    private boolean hasWeirdFormatting(String text) {
        // Check for unusual patterns like mixing cases, repeated punctuation
        return text.matches(".*[A-Z][a-z][A-Z][a-z].*") || // Mixed case
               text.matches(".*[!?]{2,}.*") ||              // Multiple punctuation
               text.matches(".*\\s{2,}.*");                 // Multiple spaces
    }
    
    private double countRepeatedChars(String text) {
        int count = 0;
        for (int i = 0; i < text.length() - 2; i++) {
            if (text.charAt(i) == text.charAt(i + 1) && 
                text.charAt(i) == text.charAt(i + 2)) {
                count++;
                i += 2;
            }
        }
        return count;
    }
    
    private double calculateSpacingIrregularity(String text) {
        String[] words = text.split("\\s+");
        if (words.length < 2) return 0.0;
        
        int irregularSpaces = 0;
        for (int i = 0; i < words.length - 1; i++) {
            if (words[i].isEmpty() || words[i + 1].isEmpty()) {
                irregularSpaces++;
            }
        }
        return (double) irregularSpaces / (words.length - 1);
    }
    
    // Original helper methods remain the same
    private double calculateAvgWordLength(String text) {
        String[] words = text.split("\\s+");
        if (words.length == 0) return 0.0;
        
        int totalLength = 0;
        for (String word : words) {
            totalLength += word.length();
        }
        return (double) totalLength / words.length;
    }
    
    private double countUrls(String text) {
        Pattern pattern = Pattern.compile("https?://[\\w./]+");
        Matcher matcher = pattern.matcher(text);
        int count = 0;
        while (matcher.find()) count++;
        return count;
    }
    
    private double countNumbers(String text) {
        return text.replaceAll("[^0-9]", "").length();
    }
    
    private double calculateUniqueWordRatio(String text) {
        String[] words = text.toLowerCase().split("\\s+");
        if (words.length == 0) return 0.0;
        
        Set<String> uniqueWords = new HashSet<>(Arrays.asList(words));
        return (double) uniqueWords.size() / words.length;
    }
    
    private boolean containsHtml(String text) {
        return text.toLowerCase().matches(".*<[^>]+>.*");
    }
    
    private int countExclamations(String text) {
        return text.length() - text.replace("!", "").length();
    }
    
    private int countCapitals(String text) {
        return text.length() - text.replaceAll("[A-Z]", "").length();
    }

    private double calculateSuspiciousPatternScore(String text) {
        double score = 0;
        
        // Check for repeated punctuation
        if (text.matches(".*[!?]{2,}.*")) score += 0.3;
        
        // Check for ALL CAPS words
        if (text.matches(".*[A-Z]{4,}.*")) score += 0.3;
        
        // Check for mixing numbers and letters in words
        if (text.matches(".*\\w*\\d+\\w*\\d+\\w*.*")) score += 0.2;
        
        // Check for excessive spaces
        if (text.matches(".*\\s{3,}.*")) score += 0.2;
        
        return score;
    }
    
    private double countCurrencyMentions(String text) {
        Matcher matcher = CURRENCY_PATTERN.matcher(text);
        int count = 0;
        while (matcher.find()) count++;
        return count;
    }
    
    private double calculateUrgencyScore(String text) {
        String[] urgencyWords = {"urgent", "immediately", "now", "hurry", "limited time", 
                               "act now", "don't wait", "expires", "deadline"};
        double score = 0;
        for (String word : urgencyWords) {
            if (text.contains(word)) score += 0.2;
        }
        return Math.min(score, 1.0);
    }
    
    private double calculateGrammarErrorScore(String text) {
        double score = 0;
        
        // Check for multiple consecutive punctuation
        if (text.matches(".*[.!?]{2,}.*")) score += 0.2;
        
        // Check for spaces before punctuation
        if (text.matches(".*\\s+[.,!?].*")) score += 0.2;
        
        // Check for repeated words
        if (text.matches(".*(\\b\\w+\\b)\\s+\\1\\b.*")) score += 0.3;
        
        // Check for missing spaces after punctuation
        if (text.matches(".*[.!?][a-zA-Z].*")) score += 0.3;
        
        return score;
    }
    
    private double calculateLinkDensity(String text) {
        int urlCount = (int) countUrls(text);
        int wordCount = text.split("\\s+").length;
        return wordCount > 0 ? (double) urlCount / wordCount : 0;
    }
    
    private double calculateSpecialCharRatio(String text) {
        long specialCount = text.chars()
            .filter(ch -> !Character.isLetterOrDigit(ch) && !Character.isWhitespace(ch))
            .count();
        return text.length() > 0 ? (double) specialCount / text.length() : 0;
    }
    
    private double calculateSentenceLengthVariance(String text) {
        String[] sentences = text.split("[.!?]+");
        if (sentences.length <= 1) return 0;
        
        // Calculate mean sentence length
        double meanLength = Arrays.stream(sentences)
            .mapToInt(s -> s.trim().split("\\s+").length)
            .average()
            .orElse(0);
            
        // Calculate variance
        double variance = Arrays.stream(sentences)
            .mapToDouble(s -> {
                double diff = s.trim().split("\\s+").length - meanLength;
                return diff * diff;
            })
            .average()
            .orElse(0);
            
        return Math.sqrt(variance);
    }
}