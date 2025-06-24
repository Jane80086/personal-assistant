package com.example.life;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class LifeRecordManager {
    private final List<LifeRecord> records;
    private static final String DATA_DIR = "data";
    private static final String FILE_NAME = "life_records.txt";
    private static final String FILE_PATH = DATA_DIR + "/" + FILE_NAME;

    private static final List<String> CATEGORIES = Arrays.asList(
            "日常", "回忆", "事件", "工作", "学习", "健康", "旅行", "家庭", "朋友", "爱好"
    );

    private static final List<String> MOODS = Arrays.asList(
            "😊 开心", "😢 难过", "😤 生气", "😴 疲惫", "😎 激动", "😰 焦虑", "😌 冷静", "🤔 思考", "😍 恋爱", "😕 困惑"
    );

    public LifeRecordManager() {
        this.records = new ArrayList<>();
        createDataDirectory();
        loadRecordsFromFile();
    }

    private void createDataDirectory() {
        try {
            Path dataPath = Paths.get(DATA_DIR);
            if (!Files.exists(dataPath)) {
                Files.createDirectories(dataPath);
                System.out.println("已创建数据文件: " + DATA_DIR);
            }
        } catch (IOException e) {
            System.err.println("错误创建数据文件: " + e.getMessage());
        }
    }

    private void loadRecordsFromFile() {
        try {
            Path filePath = Paths.get(FILE_PATH);
            if (Files.exists(filePath)) {
                List<String> lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
                for (String line : lines) {
                    if (!line.trim().isEmpty()) {
                        String[] parts = line.split(" \\| ", 5);
                        if (parts.length == 5) {
                            records.add(new LifeRecord(parts[3], parts[4], parts[1], parts[2]));
                        }
                    }
                }
                System.out.println("已从文件中加载 " + records.size() + " 条记录.");
            }
        } catch (IOException e) {
            System.err.println("从文件加载记录错误: " + e.getMessage());
        }
    }

    private void saveRecordToFile(LifeRecord record) {
        try {
            Path filePath = Paths.get(FILE_PATH);
            String recordLine = record.toFileFormat() + System.lineSeparator();
            Files.writeString(filePath, recordLine, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.err.println("保存记录到文件错误: " + e.getMessage());
        }
    }

    public List<String> getCategories() {
        return new ArrayList<>(CATEGORIES);
    }

    public List<String> getMoods() {
        return new ArrayList<>(MOODS);
    }

    public void addRecord(String title, String content, String category, String mood) {
        LifeRecord record = new LifeRecord(title, content, category, mood);
        records.add(record);
        saveRecordToFile(record);
        System.out.println("生活记录添加并保存成功！");
    }

    public List<LifeRecord> searchRecords(String titleKeyword, String contentKeyword, String categoryKeyword, String moodKeyword) {
        String lowerCaseTitleKeyword = (titleKeyword != null && !titleKeyword.trim().isEmpty()) ? titleKeyword.trim().toLowerCase() : null;
        String lowerCaseContentKeyword = (contentKeyword != null && !contentKeyword.trim().isEmpty()) ? contentKeyword.trim().toLowerCase() : null;
        String lowerCaseCategoryKeyword = (categoryKeyword != null && !categoryKeyword.trim().isEmpty()) ? categoryKeyword.trim().toLowerCase() : null;
        String lowerCaseMoodKeyword = (moodKeyword != null && !moodKeyword.trim().isEmpty()) ? moodKeyword.trim().toLowerCase() : null;

        if (lowerCaseTitleKeyword == null && lowerCaseContentKeyword == null &&
                lowerCaseCategoryKeyword == null && lowerCaseMoodKeyword == null) {
            return new ArrayList<>();
        }

        return records.stream()
                .filter(record -> {
                    boolean matchesTitle = (lowerCaseTitleKeyword == null) || record.getTitle().toLowerCase().contains(lowerCaseTitleKeyword);
                    boolean matchesContent = (lowerCaseContentKeyword == null) || record.getContent().toLowerCase().contains(lowerCaseContentKeyword);
                    boolean matchesCategory = (lowerCaseCategoryKeyword == null) || record.getCategory().toLowerCase().contains(lowerCaseCategoryKeyword);
                    boolean matchesMood = (lowerCaseMoodKeyword == null) || record.getMood().toLowerCase().contains(lowerCaseMoodKeyword);
                    return matchesTitle && matchesContent && matchesCategory && matchesMood;
                })
                .collect(Collectors.toList());
    }

    public boolean editRecord(int index, String newTitle, String newContent, String newCategory, String newMood) {
        if (index > 0 && index <= records.size()) {
            LifeRecord recordToEdit = records.get(index - 1);
            recordToEdit.setTitle(newTitle);
            recordToEdit.setContent(newContent);
            recordToEdit.setCategory(newCategory);
            recordToEdit.setMood(newMood);
            recordToEdit.setTimestamp(LocalDateTime.now());
            rewriteFile();
            return true;
        } else {
            System.out.println("非法数字，无法编辑.");
            return false;
        }
    }

    public void deleteRecord(int index) {
        if (index > 0 && index <= records.size()) {
            records.remove(index - 1);
            System.out.println("记录 #" + index + " 删除成功.");
            rewriteFile();
        } else {
            System.out.println("非法记录数字，请输入正确的数字.");
        }
    }

    public void rewriteFile() {
        try {
            Path filePath = Paths.get(FILE_PATH);
            StringBuilder content = new StringBuilder();
            for (LifeRecord record : records) {
                content.append(record.toFileFormat()).append(System.lineSeparator());
            }
            Files.writeString(filePath, content.toString(), StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            System.out.println("所有记录已重新写入文件。");
        } catch (IOException e) {
            System.err.println("重写文件错误: " + e.getMessage());
        }
    }

    public LifeRecord getRecord(int index) {
        if (index > 0 && index <= records.size()) {
            return records.get(index - 1);
        }
        return null;
    }

    public List<LifeRecord> getAllRecords() {
        return new ArrayList<>(records);
    }
}
