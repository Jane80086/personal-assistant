package com.example.life;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LifeRecord {
    private String title;
    private String content;
    private LocalDateTime timestamp;
    private String category;
    private String mood;

    public LifeRecord(String title, String content, String category,String mood) {
        this.title = title;
        this.content = content;
        this.timestamp = LocalDateTime.now();
        this.category = category;
        this.mood = mood;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getMood() {
        return mood;
    }

    public void setMood(String mood) {
        this.mood = mood;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return "----------------------------------------\n" +
                "Title: " + title + "\n" +
                "Category: " + category + "\n" +
                "Mood: " + mood + "\n" +
                "Timestamp: " + timestamp.format(formatter) + "\n" +
                "Content:\n" + content + "\n" +
                "----------------------------------------";
    }

    public String toFileFormat() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return timestamp.format(formatter) + " | " + category + " | " + mood + " | " + title + " | " + content;
    }
}