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
import java.util.Collections; // <-- 新增导入
import java.util.List;
import java.util.stream.Collectors;

public class LifeRecordManager {
    private final List<LifeRecord> records;

    private final String dataDir;
    @SuppressWarnings("FieldCanBeLocal")
    private final String fileName;
    private final String filePath;

    //预设的生活记录分类列表
    private static final List<String> CATEGORIES = Arrays.asList(
            "日常", "回忆", "事件", "工作", "学习", "健康", "旅行", "家庭", "朋友", "爱好"
    );

    // 预设的心情列表，包含表情符号
    private static final List<String> MOODS = Arrays.asList(
            "😊 开心", "😢 难过", "😤 生气", "😴 疲惫", "😎 激动", "😰 焦虑", "😌 冷静", "🤔 思考", "😍 恋爱", "😕 困惑"
    );

    public LifeRecordManager() {
        this("data", "life_records.txt");
    }

    public LifeRecordManager(String dataDir, String fileName) {
        this.records = new ArrayList<>();
        this.dataDir = dataDir;
        this.fileName = fileName;
        this.filePath = this.dataDir + "/" + this.fileName;
        createDataDirectory();
        loadRecordsFromFile();
    }

    //创建数据目录（如果不存在）
    private void createDataDirectory() {
        try {
            Path dataPath = Paths.get(this.dataDir);
            if (!Files.exists(dataPath)) {
                Files.createDirectories(dataPath);
                System.out.println("已创建数据文件: " + this.dataDir);
            }
        } catch (IOException e) {
            System.err.println("错误创建数据文件: " + e.getMessage());
        }
    }

    //转义方法
    private String unescape(String input) {
        return input == null || input.equals("null") ? null : input.replace("[PIPE]", " | ");
    }

    //从文件中加载记录
    private void loadRecordsFromFile() {
        try {
            Path file = Paths.get(this.filePath);
            if (Files.exists(file)) {
                List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);
                for (String line : lines) {
                    if (!line.trim().isEmpty()) {
                        String[] parts = line.split(" \\| ", 5);
                        if (parts.length == 5) {
                            LifeRecord record = new LifeRecord(
                                    unescape(parts[3]),
                                    unescape(parts[4]),
                                    unescape(parts[1]),
                                    unescape(parts[2])
                            );
                            records.add(record);
                        }
                    }
                }
                System.out.println("已从文件中加载 " + records.size() + " 条记录.");
            }
        } catch (IOException e) {
            System.err.println("从文件加载记录错误: " + e.getMessage());
        }
    }

    //保存记录到文件
    private void saveRecordToFile(LifeRecord record) {
        try {
            Path file = Paths.get(this.filePath);
            String recordLine = record.toFileFormat() + System.lineSeparator();
            Files.writeString(file, recordLine, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.err.println("保存记录到文件错误: " + e.getMessage());
        }
    }

    //获取所有生活记录
    public List<String> getCategories() {
        // 返回一个不可修改的视图
        return Collections.unmodifiableList(CATEGORIES);
    }

    //获取所有心情
    public List<String> getMoods() {
        // 返回一个不可修改的视图
        return Collections.unmodifiableList(MOODS);
    }

    //添加生活记录
    public void addRecord(String title, String content, String category, String mood) {
        LifeRecord record = new LifeRecord(title, content, category, mood);
        records.add(record);
        saveRecordToFile(record);
        System.out.println("生活记录添加并保存成功！");
    }

    //搜索生活记录
    public List<LifeRecord> searchRecords(String titleKeyword, String contentKeyword, String categoryKeyword, String moodKeyword) {
        // 将关键词转换为小写，并处理null或空字符串的情况
        String lowerCaseTitleKeyword = (titleKeyword != null && !titleKeyword.trim().isEmpty()) ? titleKeyword.trim().toLowerCase() : null;
        String lowerCaseContentKeyword = (contentKeyword != null && !contentKeyword.trim().isEmpty()) ? contentKeyword.trim().toLowerCase() : null;
        String lowerCaseCategoryKeyword = (categoryKeyword != null && !categoryKeyword.trim().isEmpty()) ? categoryKeyword.trim().toLowerCase() : null;
        String lowerCaseMoodKeyword = (moodKeyword != null && !moodKeyword.trim().isEmpty()) ? moodKeyword.trim().toLowerCase() : null;

        // 如果所有关键词都为空，则直接返回空列表，避免不必要的遍历
        if (lowerCaseTitleKeyword == null && lowerCaseContentKeyword == null &&
                lowerCaseCategoryKeyword == null && lowerCaseMoodKeyword == null) {
            return new ArrayList<>();
        }

        return records.stream()
                .filter(record -> {
                    // 每个条件都检查：如果关键词为空则认为匹配，否则进行不区分大小写的包含匹配
                    boolean matchesTitle = (lowerCaseTitleKeyword == null) || record.getTitle().toLowerCase().contains(lowerCaseTitleKeyword);
                    boolean matchesContent = (lowerCaseContentKeyword == null) || record.getContent().toLowerCase().contains(lowerCaseContentKeyword);
                    boolean matchesCategory = (lowerCaseCategoryKeyword == null) || record.getCategory().toLowerCase().contains(lowerCaseCategoryKeyword);
                    boolean matchesMood = (lowerCaseMoodKeyword == null) || record.getMood().toLowerCase().contains(lowerCaseMoodKeyword);
                    // 所有非空关键词都必须匹配
                    return matchesTitle && matchesContent && matchesCategory && matchesMood;
                })
                .collect(Collectors.toList());// 将匹配的记录收集为新列表
    }

    //编辑生活记录
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

    //删除生活记录
    public void deleteRecord(int index) {
        if (index > 0 && index <= records.size()) {
            records.remove(index - 1);
            System.out.println("记录 #" + index + " 删除成功.");
            rewriteFile();
        } else {
            System.out.println("非法记录数字，请输入正确的数字.");
        }
    }

    //重写文件
    public void rewriteFile() {
        try {
            Path file = Paths.get(this.filePath);
            StringBuilder content = new StringBuilder();
            for (LifeRecord record : records) {
                content.append(record.toFileFormat()).append(System.lineSeparator());
            }
            Files.writeString(file, content.toString(), StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            System.out.println("所有记录已重新写入文件。");
        } catch (IOException e) {
            System.err.println("重写文件错误: " + e.getMessage());
        }
    }

    //获取生活记录
    public LifeRecord getRecord(int index) {
        if (index > 0 && index <= records.size()) {
            return records.get(index - 1);
        }
        return null;
    }

    //获取所有生活记录
    public List<LifeRecord> getAllRecords() {
        // 返回一个不可修改的视图
        return Collections.unmodifiableList(records);
    }
}