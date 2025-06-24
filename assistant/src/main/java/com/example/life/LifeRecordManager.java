package com.example.life;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class LifeRecordManager {
    private List<LifeRecord> records;
    private static final String DATA_DIR = "data";
    private static final String FILE_NAME = "life_records.txt";
    private static final String FILE_PATH = DATA_DIR + "/" + FILE_NAME;

    // 预定义的分类列表
    private static final List<String> CATEGORIES = Arrays.asList(
            "Daily Log", "Memories", "Events", "Work", "Study", "Health", "Travel", "Family", "Friends", "Hobbies"
    );

    // 预定义的心情列表
    private static final List<String> MOODS = Arrays.asList(
            "😊 Happy", "😢 Sad", "😤 Angry", "😴 Tired", "😎 Excited", "😰 Anxious", "😌 Calm", "🤔 Thoughtful", "😍 Loved", "😕 Confused"
    );

    public LifeRecordManager() {
        this.records = new ArrayList<>();
        createDataDirectory();
        loadRecordsFromFile();
    }

    /**
     * 创建data目录
     */
    private void createDataDirectory() {
        try {
            Path dataPath = Paths.get(DATA_DIR);
            if (!Files.exists(dataPath)) {
                Files.createDirectories(dataPath);
                System.out.println("Created data directory: " + DATA_DIR);
            }
        } catch (IOException e) {
            System.err.println("Error creating data directory: " + e.getMessage());
        }
    }

    /**
     * 从文件加载记录
     */
    private void loadRecordsFromFile() {
        try {
            Path filePath = Paths.get(FILE_PATH);
            if (Files.exists(filePath)) {
                List<String> lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
                for (String line : lines) {
                    if (!line.trim().isEmpty()) {
                        // 解析文件格式: timestamp | category | mood | title | content
                        // Parse file format: timestamp | category | mood | title | content
                        String[] parts = line.split(" \\| ", 5);
                        if (parts.length == 5) {
                            // When loading, reconstruct LocalDateTime from string if needed,
                            // but for now, the LifeRecord constructor sets a new timestamp.
                            // If you want to preserve original timestamp, you'd parse parts[0]
                            // and add a constructor that takes LocalDateTime.
                            records.add(new LifeRecord(parts[3], parts[4], parts[1], parts[2]));
                        }
                    }
                }
                System.out.println("Loaded " + records.size() + " records from file.");
            }
        } catch (IOException e) {
            System.err.println("Error loading records from file: " + e.getMessage());
        }
    }

    /**
     * 保存记录到文件
     * 此方法仅用于添加新记录，因为它使用APPEND模式。
     * 对于编辑和删除，将使用rewriteFile()方法来完全更新文件。
     * Saves record to file.
     * This method is only used for adding new records as it uses APPEND mode.
     * For editing and deleting, the rewriteFile() method will be used to fully update the file.
     */
    private void saveRecordToFile(LifeRecord record) {
        try {
            Path filePath = Paths.get(FILE_PATH);
            String recordLine = record.toFileFormat() + System.lineSeparator();
            Files.write(filePath, recordLine.getBytes(StandardCharsets.UTF_8),
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.err.println("Error saving record to file: " + e.getMessage());
        }
    }

    /**
     * 获取分类列表
     * Gets the list of categories.
     */
    public List<String> getCategories() {
        return new ArrayList<>(CATEGORIES);
    }

    /**
     * 获取心情列表
     * Gets the list of moods.
     */
    public List<String> getMoods() {
        return new ArrayList<>(MOODS);
    }

    /**
     * Adds a new life record.
     * @param title The title of the record.
     * @param content The content of the record.
     * @param category The category of the record.
     * @param mood The mood of the record.
     */
    public void addRecord(String title, String content, String category, String mood) {
        LifeRecord record = new LifeRecord(title, content, category, mood);
        records.add(record);
        saveRecordToFile(record); // Use append for new records
        System.out.println("Life record added and saved successfully!");
    }

    /**
     * Displays all life records.
     * Note: This method now explicitly prints full details.
     * For summary view, use getAllRecords() in conjunction with LifeRecordMenu's new browseRecordsWithDetails().
     */
    public void viewAllRecords() {
        if (records.isEmpty()) {
            System.out.println("No life records found.");
            return;
        }
        System.out.println("\n--- All Life Records ---");
        for (int i = 0; i < records.size(); i++) {
            System.out.println("Record #" + (i + 1));
            System.out.println(records.get(i));
        }
        System.out.println("------------------------");
    }

    /**
     * Searches for records by multiple criteria (title, content, category, mood).
     * This performs a fuzzy search (contains) for each non-empty keyword and combines
     * them with an AND logic.
     * @param titleKeyword Keyword to search in titles (can be null or empty).
     * @param contentKeyword Keyword to search in content (can be null or empty).
     * @param categoryKeyword Keyword to search in categories (can be null or empty).
     * @param moodKeyword Keyword to search in moods (can be null or empty).
     * @return A list of matching records.
     */
    public List<LifeRecord> searchRecords(String titleKeyword, String contentKeyword, String categoryKeyword, String moodKeyword) {
        // Normalize keywords for case-insensitive and empty string handling
        String lowerCaseTitleKeyword = (titleKeyword != null && !titleKeyword.trim().isEmpty()) ? titleKeyword.trim().toLowerCase() : null;
        String lowerCaseContentKeyword = (contentKeyword != null && !contentKeyword.trim().isEmpty()) ? contentKeyword.trim().toLowerCase() : null;
        String lowerCaseCategoryKeyword = (categoryKeyword != null && !categoryKeyword.trim().isEmpty()) ? categoryKeyword.trim().toLowerCase() : null;
        String lowerCaseMoodKeyword = (moodKeyword != null && !moodKeyword.trim().isEmpty()) ? moodKeyword.trim().toLowerCase() : null;

        // Check if all keywords are empty, indicating no search criteria
        if (lowerCaseTitleKeyword == null && lowerCaseContentKeyword == null &&
                lowerCaseCategoryKeyword == null && lowerCaseMoodKeyword == null) {
            System.out.println("没有输入搜索关键词，请至少输入一个关键词。"); // No search keywords entered, please enter at least one.
            return new ArrayList<>();
        }

        List<LifeRecord> matchingRecords = records.stream()
                .filter(record -> {
                    boolean matchesTitle = (lowerCaseTitleKeyword == null) || record.getTitle().toLowerCase().contains(lowerCaseTitleKeyword);
                    boolean matchesContent = (lowerCaseContentKeyword == null) || record.getContent().toLowerCase().contains(lowerCaseContentKeyword);
                    boolean matchesCategory = (lowerCaseCategoryKeyword == null) || record.getCategory().toLowerCase().contains(lowerCaseCategoryKeyword);
                    boolean matchesMood = (lowerCaseMoodKeyword == null) || record.getMood().toLowerCase().contains(lowerCaseMoodKeyword);
                    return matchesTitle && matchesContent && matchesCategory && matchesMood; // AND logic for all conditions
                })
                .collect(Collectors.toList());

        if (matchingRecords.isEmpty()) {
            System.out.println("没有找到匹配的记录。"); // No matching records found.
        } else {
            System.out.println("\n--- 搜索结果 ---"); // --- Search Results ---
            for (int i = 0; i < matchingRecords.size(); i++) {
                System.out.println("结果 #" + (i + 1)); // Result #
                System.out.println(matchingRecords.get(i));
            }
            System.out.println("--------------------");
        }
        return matchingRecords;
    }


    /**
     * Edits an existing life record.
     * @param index The 1-based index of the record to edit.
     * @param newTitle The new title for the record.
     * @param newContent The new content for the record.
     * @param newCategory The new category for the record.
     * @param newMood The new mood for the record.
     * @return true if the record was successfully edited, false otherwise.
     */
    public boolean editRecord(int index, String newTitle, String newContent, String newCategory, String newMood) {
        if (index > 0 && index <= records.size()) {
            LifeRecord recordToEdit = records.get(index - 1);
            recordToEdit.setTitle(newTitle);
            recordToEdit.setContent(newContent);
            recordToEdit.setCategory(newCategory);
            recordToEdit.setMood(newMood);
            recordToEdit.setTimestamp(LocalDateTime.now()); // Update timestamp to reflect edit time
            rewriteFile(); // Save changes to file by rewriting the whole file
            return true;
        } else {
            System.out.println("Invalid record number. Cannot edit record.");
            return false;
        }
    }


    /**
     * Deletes a record by its index (1-based).
     * @param index The 1-based index of the record to delete.
     * @return true if the record was deleted, false otherwise.
     */
    public boolean deleteRecord(int index) {
        if (index > 0 && index <= records.size()) {
            records.remove(index - 1);
            System.out.println("Record #" + index + " deleted successfully.");
            // 重新写入文件
            // Rewrite file
            rewriteFile();
            return true;
        } else {
            System.out.println("Invalid record number. Please enter a valid number.");
            return false;
        }
    }

    /**
     * 重新写入整个文件 - 用于更新(编辑/删除)操作
     * 此方法会清空文件并写入当前内存中的所有记录。
     * Rewrites the entire file - used for update (edit/delete) operations.
     * This method clears the file and writes all current records from memory.
     */
    public void rewriteFile() {
        try {
            Path filePath = Paths.get(FILE_PATH);
            StringBuilder content = new StringBuilder();
            for (LifeRecord record : records) {
                content.append(record.toFileFormat()).append(System.lineSeparator());
            }
            // 使用TRUNCATE_EXISTING选项清空文件，然后写入新内容
            // Use TRUNCATE_EXISTING option to clear the file, then write new content
            Files.write(filePath, content.toString().getBytes(StandardCharsets.UTF_8),
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            System.out.println("所有记录已重新写入文件。"); // All records have been rewritten to the file.
        } catch (IOException e) {
            System.err.println("Error rewriting file: " + e.getMessage());
        }
    }

    /**
     * Retrieves a record by its index (1-based).
     * @param index The 1-based index of the record.
     * @return The LifeRecord object if found, null otherwise.
     */
    public LifeRecord getRecord(int index) {
        if (index > 0 && index <= records.size()) {
            return records.get(index - 1);
        }
        return null;
    }

    /**
     * Returns a copy of the list of all life records.
     * @return A List of LifeRecord objects.
     */
    public List<LifeRecord> getAllRecords() {
        return new ArrayList<>(records);
    }
}
