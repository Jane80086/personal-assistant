package com.example.life;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LifeRecord {
    private String title;
    private String content;
    private LocalDateTime timestamp;
    private String category; // e.g., "Daily Log", "Memories", "Events"

    public LifeRecord(String title, String content, String category) {
        this.title = title;
        this.content = content;
        this.timestamp = LocalDateTime.now();
        this.category = category;
    }

    // Getters
    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getCategory() {
        return category;
    }

    // Setters (if modification is allowed after creation)
    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return "----------------------------------------\n" +
                "Title: " + title + "\n" +
                "Category: " + category + "\n" +
                "Timestamp: " + timestamp.format(formatter) + "\n" +
                "Content:\n" + content + "\n" +
                "----------------------------------------";
    }
}