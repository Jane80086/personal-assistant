package com.example.payment;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class PaymentRecordManager {
    private final List<PaymentRecord> records = new ArrayList<>();
    private int nextId = 1;
    private static final String DATA_FILE = "data/payment_records.txt";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public PaymentRecordManager() {
        loadRecords();
    }

    public void addRecord(double amount, String type, LocalDateTime dateTime, String category, String note, List<String> tags) {
        if (category == null || category.trim().isEmpty()) {
            category = autoCategory(type, note);
        }
        PaymentRecord record = new PaymentRecord(nextId++, amount, type, dateTime, category, note, tags);
        records.add(record);
        saveRecords();
    }

    /**
     * 根据备注和类型自动分配分类
     */
    private String autoCategory(String type, String note) {
        if (note == null) return type;
        String n = note.trim();
        // 简单关键词映射，可根据需要扩展
        String[][] keywords = {
            {"工资", "薪水", "奖金", "收入", "发薪"},
            {"餐饮", "吃饭", "外卖", "饭店", "早餐", "午餐", "晚餐", "饮料"},
            {"交通", "公交", "地铁", "打车", "出租", "高铁", "火车", "加油"},
            {"购物", "买", "购物", "超市", "网购", "淘宝", "京东"},
            {"房租", "租金", "房贷"},
            {"娱乐", "电影", "游戏", "KTV", "娱乐"},
            {"医疗", "医院", "药", "体检", "医疗"},
            {"学习", "学费", "培训", "书", "学习"},
            {"转账", "转账", "还款", "借款"},
            {"投资", "理财", "基金", "股票", "投资"}
        };
        String[] cats = {"工资", "餐饮", "交通", "购物", "房租", "娱乐", "医疗", "学习", "转账", "投资"};
        for (int i = 0; i < keywords.length; i++) {
            for (String k : keywords[i]) {
                if (n.contains(k)) return cats[i];
            }
        }
        return type; // 默认返回类型
    }

    public List<PaymentRecord> getAllRecords() {
        return new ArrayList<>(records);
    }

    public List<PaymentRecord> queryRecords(LocalDateTime start, LocalDateTime end, String type, String category, Double minAmount, Double maxAmount) {
        return records.stream().filter(r ->
            (start == null || !r.getDateTime().isBefore(start)) &&
            (end == null || !r.getDateTime().isAfter(end)) &&
            (type == null || r.getType().equals(type)) &&
            (category == null || r.getCategory().equals(category)) &&
            (minAmount == null || r.getAmount() >= minAmount) &&
            (maxAmount == null || r.getAmount() <= maxAmount)
        ).collect(Collectors.toList());
    }

    public boolean deleteRecord(int id) {
        boolean removed = records.removeIf(r -> r.getId() == id);
        if (removed) saveRecords();
        return removed;
    }

    public boolean updateRecord(int id, Double amount, String type, LocalDateTime dateTime, String category, String note, List<String> tags) {
        for (PaymentRecord r : records) {
            if (r.getId() == id) {
                if (amount != null) r.setAmount(amount);
                if (type != null) r.setType(type);
                if (dateTime != null) r.setDateTime(dateTime);
                if (category != null) r.setCategory(category);
                if (note != null) r.setNote(note);
                if (tags != null) r.setTags(tags);
                saveRecords();
                return true;
            }
        }
        return false;
    }

    private void loadRecords() {
        records.clear();
        File file = new File(DATA_FILE);
        if (!file.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\t");
                if (parts.length < 6) continue;
                int id = Integer.parseInt(parts[0]);
                double amount = Double.parseDouble(parts[1]);
                String type = parts[2];
                LocalDateTime dateTime = LocalDateTime.parse(parts[3], FORMATTER);
                String category = parts[4];
                String note = parts[5];
                List<String> tags = parts.length > 6 ? Arrays.asList(parts[6].split(",")) : new java.util.ArrayList<>();
                records.add(new PaymentRecord(id, amount, type, dateTime, category, note, tags));
                if (id >= nextId) nextId = id + 1;
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    private void saveRecords() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(DATA_FILE))) {
            for (PaymentRecord r : records) {
                bw.write(String.format("%d\t%.2f\t%s\t%s\t%s\t%s\t%s\n",
                        r.getId(), r.getAmount(), r.getType(), r.getDateTime().format(FORMATTER), r.getCategory(), r.getNote(),
                        r.getTags() == null ? "" : String.join(",", r.getTags())));
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
} 