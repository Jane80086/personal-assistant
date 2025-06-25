package com.example.health;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class HealthRecord {
    private LocalDate recordDate;
    private Double weight; // 体重(kg)
    private Double height; // 身高(cm)
    private Integer heartRate; // 心率
    private Integer bloodPressureHigh; // 高压
    private Integer bloodPressureLow; // 低压
    private Integer sleepHours; // 睡眠小时数
    private Integer steps; // 步数
    private String notes; // 备注

    public HealthRecord(LocalDate recordDate, Double weight, Double height, Integer heartRate,
                        Integer bloodPressureHigh, Integer bloodPressureLow,
                        Integer sleepHours, Integer steps, String notes) {
        this.recordDate  = recordDate != null ? recordDate : LocalDate.now();
        this.weight  = weight;
        this.height  = height;
        this.heartRate  = heartRate;
        this.bloodPressureHigh  = bloodPressureHigh;
        this.bloodPressureLow  = bloodPressureLow;
        this.sleepHours  = sleepHours;
        this.steps  = steps;
        this.notes  = notes;
    }

    // Getters and Setters
    public LocalDate getRecordDate() {
        return recordDate;
    }

    public void setRecordDate(LocalDate recordDate) {
        this.recordDate  = recordDate;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight  = weight;
    }

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height  = height;
    }

    public Integer getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(Integer heartRate) {
        this.heartRate  = heartRate;
    }

    public Integer getBloodPressureHigh() {
        return bloodPressureHigh;
    }

    public void setBloodPressureHigh(Integer bloodPressureHigh) {
        this.bloodPressureHigh  = bloodPressureHigh;
    }

    public Integer getBloodPressureLow() {
        return bloodPressureLow;
    }

    public void setBloodPressureLow(Integer bloodPressureLow) {
        this.bloodPressureLow  = bloodPressureLow;
    }

    public Integer getSleepHours() {
        return sleepHours;
    }

    public void setSleepHours(Integer sleepHours) {
        this.sleepHours  = sleepHours;
    }

    public Integer getSteps() {
        return steps;
    }

    public void setSteps(Integer steps) {
        this.steps  = steps;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes  = notes;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return "----------------------------------------\n" +
                "日期: " + (recordDate != null ? recordDate.format(formatter)  : "无") + "\n" +
                "身高: " + (height != null ? height + " cm" : "无") + "\n" +
                "体重: " + (weight != null ? weight + " kg" : "无") + "\n" +
                "心率: " + (heartRate != null ? heartRate + " 次/分钟" : "无") + "\n" +
                "血压: " + (bloodPressureHigh != null || bloodPressureLow != null ?
                (bloodPressureHigh != null ? bloodPressureHigh : "?") + "/" +
                        (bloodPressureLow != null ? bloodPressureLow : "?") + " mmHg" : "无") + "\n" +
                "睡眠: " + (sleepHours != null ? sleepHours + " 小时" : "无") + "\n" +
                "步数: " + (steps != null ? steps + " 步" : "无") + "\n" +
                "备注: " + (notes != null ? notes : "无") + "\n" +
                "----------------------------------------";
    }

    public String toFileFormat() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return (recordDate != null ? recordDate.format(formatter)  : "") + " | " +
                (weight != null ? weight : "") + " | " +
                (height != null ? height : "") + " | " +
                (heartRate != null ? heartRate : "") + " | " +
                (bloodPressureHigh != null ? bloodPressureHigh : "") + " | " +
                (bloodPressureLow != null ? bloodPressureLow : "") + " | " +
                (sleepHours != null ? sleepHours : "") + " | " +
                (steps != null ? steps : "") + " | " +
                (notes != null ? notes : "");
    }
}