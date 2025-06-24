package com.example.health;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class HealthRecordMenu {
    private final HealthRecordManager manager;
    private final Scanner scanner;

    public HealthRecordMenu(Scanner scanner) {
        this.manager  = new HealthRecordManager();
        this.scanner  = scanner;
    }

    public void displayMenu() {
        int choice;
        do {
            System.out.println("\n---  健康记录菜单 ---");
            System.out.println("1.  浏览所有记录");
            System.out.println("2.  添加记录");
            System.out.println("3.  按日期范围查询");
            System.out.println("4.  编辑记录");
            System.out.println("5.  删除记录");
            System.out.println("6.  查看统计信息");
            System.out.println("0.  返回主菜单");
            System.out.print(" 输入你的选择： ");

            try {
                choice = Integer.parseInt(scanner.nextLine());
                switch (choice) {
                    case 1:
                        browseRecordsWithDetails();
                        break;
                    case 2:
                        addRecord();
                        break;
                    case 3:
                        searchByDateRange();
                        break;
                    case 4:
                        editRecord();
                        break;
                    case 5:
                        deleteRecord();
                        break;
                    case 6:
                        manager.showStatistics();
                        break;
                    case 0:
                        System.out.println(" 返回主菜单...");
                        break;
                    default:
                        System.out.println(" 无效选择，请重试。");
                }
            } catch (NumberFormatException e) {
                System.out.println(" 无效输入，请输入数字。");
                choice = -1;
            }
        } while (choice != 0);
    }

    private void addRecord() {
        System.out.println("\n=====  添加新记录 =====");

        LocalDate date = getDateInput("输入记录日期(YYYY-MM-DD，留空使用今天): ", true);
        Double weight = getDoubleInput("输入体重(kg，留空跳过): ");
        Double height = getDoubleInput("输入身高(cm，留空跳过): ");
        Integer heartRate = getIntegerInput("输入心率(留空跳过): ");
        Integer bpHigh = getIntegerInput("输入高压(留空跳过): ");
        Integer bpLow = getIntegerInput("输入低压(留空跳过): ");
        Integer sleepHours = getIntegerInput("输入睡眠小时数(留空跳过): ");
        Integer steps = getIntegerInput("输入步数(留空跳过): ");

        System.out.print(" 输入备注(留空跳过): ");
        String notes = scanner.nextLine().trim();
        if (notes.isEmpty())  {
            notes = null;
        }

        manager.addRecord(date,  weight, height, heartRate, bpHigh, bpLow, sleepHours, steps, notes);
        System.out.println(" 记录添加成功!");
    }

    private void browseRecordsWithDetails() {
        while (true) {
            List<HealthRecord> allRecords = manager.getAllRecords();

            if (allRecords.isEmpty())  {
                System.out.println(" 没有记录可以浏览。");
                return;
            }

            System.out.println("\n---  所有健康记录摘要 ---");
            for (int i = 0; i < allRecords.size();  i++) {
                System.out.println(" 记录 #" + (i + 1) + ": " +
                        allRecords.get(i).getRecordDate()  + " - " +
                        (allRecords.get(i).getWeight()  != null ? allRecords.get(i).getWeight()  + "kg" : "无体重数据"));
            }
            System.out.println("0.  返回上一级");
            System.out.print(" 输入序号查看详情 (输入 0 返回): ");

            try {
                int choice = Integer.parseInt(scanner.nextLine());
                if (choice == 0) {
                    System.out.println(" 返回上一级菜单。");
                    break;
                } else {
                    HealthRecord record = manager.getRecord(choice);
                    if (record != null) {
                        System.out.println("\n---  记录详情 #" + choice + " ---");
                        System.out.println(record);
                        System.out.println("-------------------------");
                    } else {
                        System.out.println(" 无效的记录序号，请检查。");
                    }
                }
            } catch (NumberFormatException e) {
                System.out.println(" 非法输入,请输入数字。");
            }
        }
    }

    private void searchByDateRange() {
        System.out.println("\n=====  按日期范围查询 =====");
        LocalDate start = getDateInput("输入开始日期(YYYY-MM-DD): ", false);
        LocalDate end = getDateInput("输入结束日期(YYYY-MM-DD): ", false);

        List<HealthRecord> results = manager.searchByDateRange(start,  end);
        if (results.isEmpty())  {
            System.out.println(" 在" + start + "到" + end + "范围内没有找到记录。");
        } else {
            System.out.println("\n=====  查询结果 =====");
            for (int i = 0; i < results.size();  i++) {
                System.out.println(" 结果 #" + (i + 1));
                System.out.println(results.get(i));
            }
            System.out.println("-------------------------");
        }
    }

    private void editRecord() {
        List<HealthRecord> allRecords = manager.getAllRecords();

        if (allRecords.isEmpty())  {
            System.out.println(" 没有记录可以编辑。");
            return;
        }

        System.out.println("\n---  所有健康记录摘要 (用于编辑) ---");
        for (int i = 0; i < allRecords.size();  i++) {
            System.out.println(" 记录 #" + (i + 1) + ": " +
                    allRecords.get(i).getRecordDate()  + " - " +
                    (allRecords.get(i).getWeight()  != null ? allRecords.get(i).getWeight()  + "kg" : "无体重数据"));
        }
        System.out.println("0.  取消编辑");
        System.out.print(" 输入想要编辑记录的序号 (输入 0 取消): ");

        try {
            int recordNumber = Integer.parseInt(scanner.nextLine());
            if (recordNumber == 0) {
                System.out.println(" 编辑操作已取消。");
                return;
            }

            HealthRecord recordToEdit = manager.getRecord(recordNumber);
            if (recordToEdit != null) {
                System.out.println("\n---  编辑记录 #" + recordNumber + " ---");
                System.out.println(" 当前记录:");
                System.out.println(recordToEdit);
                System.out.println("\n 输入新信息(留空保持不变):");

                LocalDate newDate = getDateInput("新日期(YYYY-MM-DD，留空保持不变): ", true);
                Double newWeight = getDoubleInput("新体重(kg，留空保持不变): ");
                Double newHeight = getDoubleInput("新身高(cm，留空保持不变): ");
                Integer newHeartRate = getIntegerInput("新心率(留空保持不变): ");
                Integer newBpHigh = getIntegerInput("新高压(留空保持不变): ");
                Integer newBpLow = getIntegerInput("新低压(留空保持不变): ");
                Integer newSleepHours = getIntegerInput("新睡眠小时数(留空保持不变): ");
                Integer newSteps = getIntegerInput("新步数(留空保持不变): ");

                System.out.print(" 新备注(留空保持不变): ");
                String newNotes = scanner.nextLine().trim();
                if (newNotes.isEmpty())  {
                    newNotes = null;
                }

                if (manager.editRecord(recordNumber,  newDate, newWeight, newHeight, newHeartRate,
                        newBpHigh, newBpLow, newSleepHours, newSteps, newNotes)) {
                    System.out.println(" 记录 #" + recordNumber + " 编辑成功！");
                } else {
                    System.out.println(" 编辑失败，请检查序号。");
                }
            } else {
                System.out.println(" 无效的记录序号，请检查。");
            }
        } catch (NumberFormatException e) {
            System.out.println(" 非法输入,请输入数字。");
        }
    }

    private void deleteRecord() {
        List<HealthRecord> allRecords = manager.getAllRecords();

        if (allRecords.isEmpty())  {
            System.out.println(" 没有记录可以删除。");
            return;
        }

        System.out.println("\n---  所有健康记录摘要 (用于删除) ---");
        for (int i = 0; i < allRecords.size();  i++) {
            System.out.println(" 记录 #" + (i + 1) + ": " +
                    allRecords.get(i).getRecordDate()  + " - " +
                    (allRecords.get(i).getWeight()  != null ? allRecords.get(i).getWeight()  + "kg" : "无体重数据"));
        }
        System.out.println("0.  取消删除");
        System.out.print(" 输入想要删除记录的序号 (输入 0 取消): ");

        try {
            int recordNumber = Integer.parseInt(scanner.nextLine());
            if (recordNumber == 0) {
                System.out.println(" 删除操作已取消。");
                return;
            }

            if (manager.deleteRecord(recordNumber))  {
                System.out.println(" 记录 #" + recordNumber + " 删除成功！");
            } else {
                System.out.println(" 删除失败，请检查序号。");
            }
        } catch (NumberFormatException e) {
            System.out.println(" 非法输入,请输入数字。");
        }
    }

    private LocalDate getDateInput(String prompt, boolean allowEmpty) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (allowEmpty && input.isEmpty())  {
                return null;
            }
            try {
                return LocalDate.parse(input);
            } catch (DateTimeParseException e) {
                System.out.println(" 无效日期格式，请使用YYYY-MM-DD格式。");
            }
        }
    }

    private Double getDoubleInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (input.isEmpty())  {
                return null;
            }
            try {
                return Double.parseDouble(input);
            } catch (NumberFormatException e) {
                System.out.println(" 无效数字，请重新输入。");
            }
        }
    }

    private Integer getIntegerInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (input.isEmpty())  {
                return null;
            }
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println(" 无效整数，请重新输入。");
            }
        }
    }
}