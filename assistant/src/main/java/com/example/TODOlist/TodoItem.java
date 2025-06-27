package com.example.TODOlist;

public class TodoItem {
    private String content;
    private int priority;
    private boolean isCompleted;

    public TodoItem(String content, int priority) {
        this.content = content;
        this.priority = priority;
        this.isCompleted = false;
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
}