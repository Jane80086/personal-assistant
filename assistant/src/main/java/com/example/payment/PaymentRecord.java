package com.example.payment;

import java.time.LocalDateTime;
import java.util.List;

public class PaymentRecord {
    private final int id;
    private double amount;
    private String type; // "收入" 或 "支出"
    private LocalDateTime dateTime;
    private String category; // 分类，如工资、购物等
    private String note;
    private List<String> tags;

    public PaymentRecord(int id, double amount, String type, LocalDateTime dateTime, String category, String note, List<String> tags) {
        this.id = id;
        this.amount = amount;
        this.type = type;
        this.dateTime = dateTime;
        this.category = category;
        this.note = note;
        this.tags = tags == null ? new java.util.ArrayList<>() : tags;
    }

    public int getId() { return id; }
    public double getAmount() { return amount; }
    public String getType() { return type; }
    public LocalDateTime getDateTime() { return dateTime; }
    public String getCategory() { return category; }
    public String getNote() { return note; }
    public List<String> getTags() { return tags; }

    public void setAmount(double amount) { this.amount = amount; }
    public void setType(String type) { this.type = type; }
    public void setDateTime(LocalDateTime dateTime) { this.dateTime = dateTime; }
    public void setCategory(String category) { this.category = category; }
    public void setNote(String note) { this.note = note; }
    public void setTags(List<String> tags) { this.tags = tags; }

    @Override
    public String toString() {
        return String.format("[ID:%d] %s %.2f 元 | %s | %s | %s | 标签:%s", id, type, amount, dateTime.toString(), category, note, tags == null ? "" : String.join(",", tags));
    }
} 