package com.example.health;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class HealthRecordManager {
    private List<HealthRecord> records;
    private static final String DATA_DIR = "data";
    private static final String FILE_NAME = "health_records.txt";
    private static final String FILE_PATH = DATA_DIR + "/" + FILE_NAME;

    public HealthRecordManager() {
        this.records  = new ArrayList<>();
        createDataDirectory();
        loadRecordsFromFile();
    }

    private void createDataDirectory() {
        try {
            Path dataPath = Paths.get(DATA_DIR);
            if (!Files.exists(dataPath))  {
                Files.createDirectories(dataPath);
                System.out.println(" 创建数据目录: " + dataPath.toAbsolutePath());
            }

            Path filePath = Paths.get(FILE_PATH);
            if (!Files.exists(filePath))  {
                Files.createFile(filePath);
                System.out.println(" 创建数据文件: " + filePath.toAbsolutePath());
            }
        } catch (IOException e) {
            System.err.println(" 创建数据目录/文件错误: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadRecordsFromFile() {
        try {
            Path filePath = Paths.get(FILE_PATH);
            if (Files.exists(filePath))  {
                List<String> lines = Files.readAllLines(filePath,  StandardCharsets.UTF_8);
                for (String line : lines) {
                    if (!line.trim().isEmpty())  {
                        // 解析文件格式: date | weight | height | heartRate | bpHigh | bpLow | sleepHours | steps | notes
                        String[] parts = line.split(" \\| ", 9);
                        if (parts.length  >= 8) {
                            LocalDate date = LocalDate.parse(parts[0],  DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                            Double weight = parts[1].isEmpty() ? null : Double.parseDouble(parts[1]);
                            Double height = parts[2].isEmpty() ? null : Double.parseDouble(parts[2]);
                            Integer heartRate = parts[3].isEmpty() ? null : Integer.parseInt(parts[3]);
                            Integer bpHigh = parts[4].isEmpty() ? null : Integer.parseInt(parts[4]);
                            Integer bpLow = parts[5].isEmpty() ? null : Integer.parseInt(parts[5]);
                            Integer sleepHours = parts[6].isEmpty() ? null : Integer.parseInt(parts[6]);
                            Integer steps = parts[7].isEmpty() ? null : Integer.parseInt(parts[7]);
                            String notes = parts.length  > 8 ? parts[8] : "";

                            records.add(new  HealthRecord(date, weight, height, heartRate,
                                    bpHigh, bpLow, sleepHours, steps, notes));
                        }
                    }
                }
                System.out.println("Loaded  " + records.size()  + " health records from file.");
            }
        } catch (IOException e) {
            System.err.println("Error  loading health records from file: " + e.getMessage());
        }
    }

    private void saveRecordToFile(HealthRecord record) {
        try {
            Path filePath = Paths.get(FILE_PATH);
            String recordLine = record.toFileFormat()  + System.lineSeparator();
            Files.write(filePath,  recordLine.getBytes(StandardCharsets.UTF_8),
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.err.println("Error  saving health record to file: " + e.getMessage());
        }
    }

    public void addRecord(LocalDate date, Double weight, Double height, Integer heartRate,
                          Integer bloodPressureHigh, Integer bloodPressureLow,
                          Integer sleepHours, Integer steps, String notes) {
        if (!validateRecordData(date, weight, height, heartRate,
                bloodPressureHigh, bloodPressureLow,
                sleepHours, steps)) {
            return;
        }

        HealthRecord record = new HealthRecord(date, weight, height, heartRate,
                bloodPressureHigh, bloodPressureLow,
                sleepHours, steps, notes);
        records.add(record);
        saveRecordToFile(record);
        System.out.println("Health  record added and saved successfully!");
    }
    private boolean validateRecordData(LocalDate date, Double weight, Double height,
                                       Integer heartRate, Integer bpHigh, Integer bpLow,
                                       Integer sleepHours, Integer steps) {
        // 1. 验证日期
        if (date == null) {
            System.err.println(" 错误: 记录日期不能为空");
            return false;
        }
        if (date.isAfter(LocalDate.now()))  {
            System.err.println(" 错误: 记录日期不能是未来日期");
            return false;
        }

        // 2. 验证体重(kg)
        if (weight != null) {
            if (weight <= 0 || weight > 300) {
                System.err.println(" 错误: 体重值无效 (有效范围: 0-300kg)");
                return false;
            }
        }

        // 3. 验证身高(cm)
        if (height != null) {
            if (height <= 0 || height > 300) {
                System.err.println(" 错误: 身高值无效 (有效范围: 0-300cm)");
                return false;
            }
        }

        // 4. 验证心率(bpm)
        if (heartRate != null) {
            if (heartRate <= 0 || heartRate > 250) {
                System.err.println(" 错误: 心率值无效 (有效范围: 0-250bpm)");
                return false;
            }
        }

        // 5. 验证血压(mmHg)
        if (bpHigh != null || bpLow != null) {
            if (bpHigh != null && (bpHigh < 30 || bpHigh > 300)) {
                System.err.println(" 错误: 高压值无效 (有效范围: 30-300mmHg)");
                return false;
            }
            if (bpLow != null && (bpLow < 20 || bpLow > 200)) {
                System.err.println(" 错误: 低压值无效 (有效范围: 20-200mmHg)");
                return false;
            }
            if (bpHigh != null && bpLow != null && bpHigh <= bpLow) {
                System.err.println(" 错误: 高压值必须大于低压值");
                return false;
            }
        }

        // 6. 验证睡眠时间(小时)
        if (sleepHours != null) {
            if (sleepHours < 0 || sleepHours > 24) {
                System.err.println(" 错误: 睡眠时间无效 (有效范围: 0-24小时)");
                return false;
            }
        }

        // 7. 验证步数
        if (steps != null) {
            if (steps < 0 || steps > 100000) {
                System.err.println(" 错误: 步数无效 (有效范围: 0-100000步)");
                return false;
            }
        }

        // 8. 验证身高体重比例(BMI粗略检查)
        if (weight != null && height != null && height > 0) {
            double bmi = weight / ((height / 100) * (height / 100));
            if (bmi < 10 || bmi > 60) {
                System.err.println(" 警告: 身高体重比例异常 (BMI: " + String.format("%.1f",  bmi) + ")");
                // 这里只是警告，不阻止记录
            }
        }

        return true;
    }

    public void viewAllRecords() {
        if (records.isEmpty())  {
            System.out.println(" 没有找到健康记录。");
            return;
        }
        System.out.println("\n---  所有健康记录 ---");
        for (int i = 0; i < records.size();  i++) {
            System.out.println(" 记录 #" + (i + 1));
            System.out.println(records.get(i));
        }
        System.out.println("------------------------");
    }

    public List<HealthRecord> searchByDateRange(LocalDate start, LocalDate end) {
        List<HealthRecord> results = new ArrayList<>();
        for (HealthRecord record : records) {
            if (!record.getRecordDate().isBefore(start)  && !record.getRecordDate().isAfter(end))  {
                results.add(record);
            }
        }
        return results;
    }

    public boolean editRecord(int index, LocalDate date, Double weight, Double height, Integer heartRate,
                              Integer bloodPressureHigh, Integer bloodPressureLow,
                              Integer sleepHours, Integer steps, String notes) {
        if (index > 0 && index <= records.size())  {
            HealthRecord record = records.get(index  - 1);
            record.setRecordDate(date  != null ? date : record.getRecordDate());
            record.setWeight(weight  != null ? weight : record.getWeight());
            record.setHeight(height  != null ? height : record.getHeight());
            record.setHeartRate(heartRate  != null ? heartRate : record.getHeartRate());
            record.setBloodPressureHigh(bloodPressureHigh  != null ? bloodPressureHigh : record.getBloodPressureHigh());
            record.setBloodPressureLow(bloodPressureLow  != null ? bloodPressureLow : record.getBloodPressureLow());
            record.setSleepHours(sleepHours  != null ? sleepHours : record.getSleepHours());
            record.setSteps(steps  != null ? steps : record.getSteps());
            record.setNotes(notes  != null ? notes : record.getNotes());

            rewriteFile();
            return true;
        }
        return false;
    }

    public boolean deleteRecord(int index) {
        if (index > 0 && index <= records.size())  {
            records.remove(index  - 1);
            rewriteFile();
            return true;
        }
        return false;
    }

    private void rewriteFile() {
        try {
            Path filePath = Paths.get(FILE_PATH);
            StringBuilder content = new StringBuilder();
            for (HealthRecord record : records) {
                content.append(record.toFileFormat()).append(System.lineSeparator());
            }
            Files.write(filePath,  content.toString().getBytes(StandardCharsets.UTF_8),
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            System.err.println(" 重写文件错误: " + e.getMessage());
        }
    }

    public HealthRecord getRecord(int index) {
        if (index > 0 && index <= records.size())  {
            return records.get(index  - 1);
        }
        return null;
    }

    public List<HealthRecord> getAllRecords() {
        return new ArrayList<>(records);
    }

    public void showStatistics() {
        if (records.isEmpty())  {
            System.out.println(" 没有足够的数据生成统计信息。");
            return;
        }

        System.out.println("\n=====  健康数据统计 =====");

        // 计算平均体重
        double avgWeight = records.stream()
                .filter(r -> r.getWeight()  != null)
                .mapToDouble(HealthRecord::getWeight)
                .average()
                .orElse(0);
        System.out.printf("  平均体重: %.2f kg\n", avgWeight);

        // 计算平均心率
        double avgHeartRate = records.stream()
                .filter(r -> r.getHeartRate()  != null)
                .mapToInt(HealthRecord::getHeartRate)
                .average()
                .orElse(0);
        System.out.printf("  平均心率: %.1f 次/分钟\n", avgHeartRate);

        // 计算平均血压
        double avgBpHigh = records.stream()
                .filter(r -> r.getBloodPressureHigh()  != null)
                .mapToInt(HealthRecord::getBloodPressureHigh)
                .average()
                .orElse(0);
        double avgBpLow = records.stream()
                .filter(r -> r.getBloodPressureLow()  != null)
                .mapToInt(HealthRecord::getBloodPressureLow)
                .average()
                .orElse(0);
        System.out.printf("  平均血压: %.1f/%.1f mmHg\n", avgBpHigh, avgBpLow);

        // 计算平均睡眠时间
        double avgSleep = records.stream()
                .filter(r -> r.getSleepHours()  != null)
                .mapToInt(HealthRecord::getSleepHours)
                .average()
                .orElse(0);
        System.out.printf("  平均睡眠时间: %.1f 小时\n", avgSleep);

        // 计算平均步数
        double avgSteps = records.stream()
                .filter(r -> r.getSteps()  != null)
                .mapToInt(HealthRecord::getSteps)
                .average()
                .orElse(0);
        System.out.printf("  平均每日步数: %.1f 步\n", avgSteps);
    }
}