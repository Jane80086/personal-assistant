package com.example.life;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LifeRecord {
    private String title;//标题
    private String content;//内容
    private LocalDateTime timestamp;//记录创建或最后修改的时间戳
    private String category;//分类
    private String mood;//心情

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
                "标题: " + title + "\n" +
                "分类: " + category + "\n" +
                "心情: " + mood + "\n" +
                "时间戳: " + timestamp.format(formatter) + "\n" +
                "内容:\n" + content + "\n" +
                "----------------------------------------";
    }

    //返回此记录的文件存储格式字符串
    public String toFileFormat() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return timestamp.format(formatter) + " | " +
                escape(category) + " | " +
                escape(mood) + " | " +
                escape(title) + " | " +
                escape(content);
    }


    //转义方法
    private String escape(String input) {
        return input == null ? "null" : input.replace(" | ", "[PIPE]");
    }

}