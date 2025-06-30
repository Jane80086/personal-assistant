package com.example.TODOlist;

import java.time.LocalDateTime;

public class TodoItem implements Comparable<TodoItem> {
    private String content;
    private int priority;
    private boolean isCompleted;
    private LocalDateTime creationTime;

    public TodoItem(String content, int priority) {
        this.content = content;
        this.priority = priority;
        this.isCompleted = false;
        this.creationTime = LocalDateTime.now();
    }

    // 新增构造函数，用于从文件加载时设置创建时间
    public TodoItem(String content, int priority, boolean isCompleted, LocalDateTime creationTime) {
        this.content = content;
        this.priority = priority;
        this.isCompleted = isCompleted;
        this.creationTime = creationTime;
    }

    public String getContent() {
        return content;
    }

    public int getPriority() {
        return priority;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public LocalDateTime getCreationTime() {
        return creationTime;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    @Override
    public int compareTo(TodoItem other) {
        if (this.priority != other.priority) {
            return Integer.compare(other.priority, this.priority); // 优先级高的在前
        }
        return this.creationTime.compareTo(other.creationTime); // 时间早的在前
    }
}