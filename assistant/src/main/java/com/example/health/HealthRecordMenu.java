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
        int choice = -1;  // 显式初始化
        do {
            System.out.println("\n===  健康记录管理系统 ===");
            System.out.println("1.  浏览所有记录");
            System.out.println("2.  添加新记录");
            System.out.println("3.  按日期范围查询");
            System.out.println("4.  编辑现有记录");
            System.out.println("5.  删除记录");
            System.out.println("6.  查看统计信息");
            System.out.println("7.  显示输入帮助");
            System.out.println("0.  返回主菜单");
            System.out.print(" 请输入您的选择(0-7): ");

            try {
                String input = scanner.nextLine().trim();
                if (input.isEmpty())  {
                    System.out.println(" 错误：请输入一个数字");
                    continue;
                }

                choice = Integer.parseInt(input);
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
                    case 7:
                        showInputHelp();
                        break;
                    case 0:
                        System.out.println(" 正在返回主菜单...");
                        break;
                    default:
                        System.out.println(" 错误：无效的选择，请输入0-7之间的数字");
                }
            } catch (NumberFormatException e) {
                System.out.println(" 错误：请输入有效的数字(0-7)");
                choice = -1;
            }
        } while (choice != 0);
    }

    private void showInputHelp() {
        System.out.println("\n===  输入帮助 ===");
        System.out.println("1.  日期格式: YYYY-MM-DD (例如: 2023-05-20)");
        System.out.println("    - 直接按回车键将使用今天日期");
        System.out.println("2.  数值输入: 请输入数字或直接按回车键跳过");
        System.out.println("    - 体重: 以kg为单位 (例如: 65.5)");
        System.out.println("    - 身高: 以cm为单位 (例如: 175)");
        System.out.println("3.  血压: 分别输入高压和低压两个数值");
        System.out.println("4.  备注: 可直接按回车键跳过");
        System.out.println("5.  所有操作: 按Ctrl+C可随时取消当前操作");
        System.out.println("==============================\n");
    }

    private void addRecord() {
        System.out.println("\n=====  添加新记录 =====");
        System.out.println(" 提示：");
        System.out.println("   - 日期留空将自动使用今天日期");
        System.out.println("   - 其他字段留空将不记录该信息");
        System.out.println("   - 输入过程中可随时按Ctrl+C返回菜单\n");

        LocalDate date = getDateInput("输入记录日期(YYYY-MM-DD，直接按回车使用今天): ", true);
        Double weight = getDoubleInput("输入体重(kg，直接按回车跳过): ", "体重");
        Double height = getDoubleInput("输入身高(cm，直接按回车跳过): ", "身高");
        Integer heartRate = getIntegerInput("输入心率(次/分钟，直接按回车跳过): ", "心率");
        Integer bpHigh = getIntegerInput("输入高压(mmHg，直接按回车跳过): ", "高压");
        Integer bpLow = getIntegerInput("输入低压(mmHg，直接按回车跳过): ", "低压");
        Integer sleepHours = getIntegerInput("输入睡眠小时数(0-24，直接按回车跳过): ", "睡眠");
        Integer steps = getIntegerInput("输入步数(直接按回车跳过): ", "步数");

        System.out.print(" 输入备注(直接按回车跳过): ");
        String notes = scanner.nextLine().trim();
        if (notes.isEmpty())  {
            notes = null;
            System.out.println(" 提示：备注留空，将不记录备注信息");
        }

        // 检查是否所有字段都为空
        if (weight == null && height == null && heartRate == null &&
                bpHigh == null && bpLow == null && sleepHours == null &&
                steps == null && notes == null) {
            System.out.println("\n 警告：您没有输入任何数据（日期自动设置为今天）");
            System.out.print(" 确定要保存这个空记录吗？(y/n): ");
            if (!scanner.nextLine().trim().equalsIgnoreCase("y"))  {
                System.out.println(" 记录添加已取消");
                return;
            }
        }

        manager.addRecord(date,  weight, height, heartRate, bpHigh, bpLow, sleepHours, steps, notes);
        System.out.println("\n 记录添加成功!");
        System.out.println(" 提示：您可以通过菜单选项1浏览所有记录");
    }
    private void browseRecordsWithDetails() {
        while (true) {
            List<HealthRecord> allRecords = manager.getAllRecords();

            if (allRecords.isEmpty())  {
                System.out.println("\n 目前没有健康记录。");
                System.out.println(" 提示：您可以通过菜单选项2添加新记录");
                return;
            }

            System.out.println("\n---  所有健康记录摘要 ---");
            System.out.println(" 序号 | 日期       | 体重(kg) | 身高(cm) | 心率");
            System.out.println("----------------------------------------");
            for (int i = 0; i < allRecords.size();  i++) {
                HealthRecord record = allRecords.get(i);
                System.out.printf("%-4d  | %-10s | %-8s | %-8s | %-4s%n",
                        (i + 1),
                        record.getRecordDate(),
                        record.getWeight()  != null ? String.format("%.1f",  record.getWeight())  : "无",
                        record.getHeight()  != null ? String.format("%.1f",  record.getHeight())  : "无",
                        record.getHeartRate()  != null ? record.getHeartRate().toString()  : "无");
            }
            System.out.println("\n0.  返回上一级");
            System.out.print(" 输入序号查看详情 (输入0返回): ");

            try {
                int choice = Integer.parseInt(scanner.nextLine());
                if (choice == 0) {
                    System.out.println(" 返回上一级菜单。");
                    break;
                } else if (choice > 0 && choice <= allRecords.size())  {
                    HealthRecord record = manager.getRecord(choice);
                    System.out.println("\n---  记录详情 #" + choice + " ---");
                    System.out.println(record);
                    System.out.println("-------------------------");

                    // 添加操作选项
                    System.out.println("1.  返回列表");
                    System.out.println("2.  编辑此记录");
                    System.out.println("3.  删除此记录");
                    System.out.println("0.  返回主菜单");
                    System.out.print(" 请选择操作: ");

                    String action = scanner.nextLine().trim();
                    switch (action) {
                        case "2":
                            editSpecificRecord(choice);
                            break;
                        case "3":
                            if (manager.deleteRecord(choice))  {
                                System.out.println(" 记录 #" + choice + " 已删除");
                                if (allRecords.size()  == 1) {
                                    return; // 如果删除了最后一个记录，直接返回
                                }
                            } else {
                                System.out.println(" 删除失败");
                            }
                            break;
                        case "0":
                            return;
                        default:
                            // 继续显示列表
                    }
                } else {
                    System.out.println(" 无效的记录序号，请检查。");
                }
            } catch (NumberFormatException e) {
                System.out.println(" 非法输入,请输入数字。");
            }
        }
    }

    private void editSpecificRecord(int recordNumber) {
        HealthRecord recordToEdit = manager.getRecord(recordNumber);
        if (recordToEdit == null) {
            System.out.println(" 错误：找不到指定的记录");
            return;
        }

        System.out.println("\n---  编辑记录 #" + recordNumber + " ---");
        System.out.println(" 当前记录:");
        System.out.println(recordToEdit);
        System.out.println("\n 输入新信息(留空保持不变):");

        LocalDate newDate = getDateInput("新日期(YYYY-MM-DD，留空保持不变): ", true);
        Double newWeight = getDoubleInput("新体重(kg，留空保持不变): ", "体重");
        Double newHeight = getDoubleInput("新身高(cm，留空保持不变): ", "身高");
        Integer newHeartRate = getIntegerInput("新心率(留空保持不变): ", "心率");
        Integer newBpHigh = getIntegerInput("新高压(留空保持不变): ", "高压");
        Integer newBpLow = getIntegerInput("新低压(留空保持不变): ", "低压");
        Integer newSleepHours = getIntegerInput("新睡眠小时数(留空保持不变): ", "睡眠");
        Integer newSteps = getIntegerInput("新步数(留空保持不变): ", "步数");

        System.out.print(" 新备注(留空保持不变): ");
        String newNotes = scanner.nextLine().trim();
        if (newNotes.isEmpty())  {
            newNotes = null;
        }

        if (manager.editRecord(recordNumber,  newDate, newWeight, newHeight, newHeartRate,
                newBpHigh, newBpLow, newSleepHours, newSteps, newNotes)) {
            System.out.println("\n 记录 #" + recordNumber + " 编辑成功！");
        } else {
            System.out.println(" 编辑失败，请检查输入。");
        }
    }

    private void searchByDateRange() {
        System.out.println("\n=====  按日期范围查询 =====");
        System.out.println(" 提示：请输入开始和结束日期来查询该时间段的记录");

        LocalDate start = getDateInput("输入开始日期(YYYY-MM-DD): ", false);
        LocalDate end = getDateInput("输入结束日期(YYYY-MM-DD): ", false);

        if (start.isAfter(end))  {
            System.out.println(" 错误：开始日期不能晚于结束日期");
            return;
        }

        List<HealthRecord> results = manager.searchByDateRange(start,  end);
        if (results.isEmpty())  {
            System.out.println("\n 在" + start + "到" + end + "范围内没有找到记录。");
            System.out.println(" 提示：您可以尝试扩大日期范围或添加新记录");
        } else {
            System.out.println("\n=====  查询结果 (" + results.size()  + "条记录) =====");
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
            System.out.println("\n 目前没有可编辑的记录。");
            System.out.println(" 提示：您可以通过菜单选项2添加新记录");
            return;
        }

        System.out.println("\n---  所有健康记录摘要 (用于编辑) ---");
        System.out.println(" 序号 | 日期       | 体重(kg) | 身高(cm)");
        System.out.println("----------------------------------------");
        for (int i = 0; i < allRecords.size();  i++) {
            HealthRecord record = allRecords.get(i);
            System.out.printf("%-4d  | %-10s | %-8s | %-8s%n",
                    (i + 1),
                    record.getRecordDate(),
                    record.getWeight()  != null ? String.format("%.1f",  record.getWeight())  : "无",
                    record.getHeight()  != null ? String.format("%.1f",  record.getHeight())  : "无");
        }
        System.out.println("\n0.  取消编辑");
        System.out.print(" 输入想要编辑记录的序号 (输入0取消): ");

        try {
            int recordNumber = Integer.parseInt(scanner.nextLine());
            if (recordNumber == 0) {
                System.out.println(" 编辑操作已取消。");
                return;
            }

            editSpecificRecord(recordNumber);
        } catch (NumberFormatException e) {
            System.out.println(" 非法输入,请输入数字。");
        }
    }

    private void deleteRecord() {
        List<HealthRecord> allRecords = manager.getAllRecords();

        if (allRecords.isEmpty())  {
            System.out.println("\n 目前没有可删除的记录。");
            return;
        }

        System.out.println("\n---  所有健康记录摘要 (用于删除) ---");
        System.out.println(" 序号 | 日期       | 体重(kg) | 备注");
        System.out.println("----------------------------------------");
        for (int i = 0; i < allRecords.size();  i++) {
            HealthRecord record = allRecords.get(i);
            String notePreview = record.getNotes()  != null ?
                    (record.getNotes().length()  > 15 ?
                            record.getNotes().substring(0,  12) + "..." :
                            record.getNotes())  : "无";
            System.out.printf("%-4d  | %-10s | %-8s | %-15s%n",
                    (i + 1),
                    record.getRecordDate(),
                    record.getWeight()  != null ? String.format("%.1f",  record.getWeight())  : "无",
                    notePreview);
        }
        System.out.println("\n0.  取消删除");
        System.out.print(" 输入想要删除记录的序号 (输入0取消): ");

        try {
            int recordNumber = Integer.parseInt(scanner.nextLine());
            if (recordNumber == 0) {
                System.out.println(" 删除操作已取消。");
                return;
            }

            System.out.println("\n 您确定要删除以下记录吗？");
            HealthRecord recordToDelete = manager.getRecord(recordNumber);
            if (recordToDelete != null) {
                System.out.println(recordToDelete);
                System.out.print(" 确认删除？此操作不可恢复！(y/n): ");
                String confirmation = scanner.nextLine().trim();
                if (confirmation.equalsIgnoreCase("y"))  {
                    if (manager.deleteRecord(recordNumber))  {
                        System.out.println(" 记录 #" + recordNumber + " 已成功删除！");
                    } else {
                        System.out.println(" 删除失败，请检查序号。");
                    }
                } else {
                    System.out.println(" 删除操作已取消。");
                }
            } else {
                System.out.println(" 无效的记录序号，请检查。");
            }
        } catch (NumberFormatException e) {
            System.out.println(" 非法输入,请输入数字。");
        }
    }

    private LocalDate getDateInput(String prompt, boolean allowEmpty) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            // 处理留空情况
            if (input.isEmpty())  {
                if (allowEmpty) {
                    System.out.println(" 提示：日期留空将自动使用今天日期");
                    return LocalDate.now();
                } else {
                    System.out.println(" 错误：此项为必填项，请输入日期或按Ctrl+C返回菜单");
                    continue;
                }
            }

            try {
                LocalDate date = LocalDate.parse(input);
                if (date.isAfter(LocalDate.now()))  {
                    System.out.println(" 警告：日期不能是未来日期，请重新输入");
                    continue;
                }
                return date;
            } catch (DateTimeParseException e) {
                System.out.println(" 错误：无效日期格式，请使用YYYY-MM-DD格式(例如:2023-05-15)");
                System.out.println(" 提示：直接按回车键将使用今天日期");
            }
        }
    }
    private Double getDoubleInput(String prompt, String fieldName) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            // 处理留空情况
            if (input.isEmpty())  {
                System.out.println(" 提示：此项留空，将不记录" + fieldName + "信息");
                return null;
            }

            try {
                double value = Double.parseDouble(input);

                // 根据字段名提供特定验证
                switch (fieldName) {
                    case "体重":
                        if (value <= 0 || value > 300) {
                            System.out.println(" 错误：体重应在0-300kg之间");
                            System.out.println(" 提示：正常成人体重范围通常为40-150kg");
                            continue;
                        }
                        break;
                    case "身高":
                        if (value <= 0 || value > 300) {
                            System.out.println(" 错误：身高应在0-300cm之间");
                            System.out.println(" 提示：正常成人身高范围通常为140-220cm");
                            continue;
                        }
                        break;
                }

                return value;
            } catch (NumberFormatException e) {
                System.out.println(" 错误：请输入有效的数字(例如:65.5)或直接按回车键跳过");
            }
        }
    }
    private Integer getIntegerInput(String prompt, String fieldName) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (input.isEmpty())  {
                return null;
            }
            try {
                int value = Integer.parseInt(input);

                // 根据字段名提供特定验证
                switch (fieldName) {
                    case "心率":
                        if (value <= 0 || value > 250) {
                            System.out.println(" 错误：心率应在0-250次/分钟之间");
                            System.out.println(" 提示：正常静息心率范围为60-100次/分钟");
                            continue;
                        }
                        break;
                    case "高压":
                        if (value < 30 || value > 300) {
                            System.out.println(" 错误：高压应在30-300mmHg之间");
                            System.out.println(" 提示：正常血压高压约为90-140mmHg");
                            continue;
                        }
                        break;
                    case "低压":
                        if (value < 20 || value > 200) {
                            System.out.println(" 错误：低压应在20-200mmHg之间");
                            System.out.println(" 提示：正常血压低压约为60-90mmHg");
                            continue;
                        }
                        break;
                    case "睡眠":
                        if (value < 0 || value > 24) {
                            System.out.println(" 错误：睡眠时间应在0-24小时之间");
                            System.out.println(" 提示：成人推荐睡眠时间为7-9小时");
                            continue;
                        }
                        break;
                    case "步数":
                        if (value < 0 || value > 100000) {
                            System.out.println(" 错误：步数应在0-100000步之间");
                            System.out.println(" 提示：日常活动约5000-10000步");
                            continue;
                        }
                        break;
                }

                return value;
            } catch (NumberFormatException e) {
                System.out.println(" 错误：请输入有效的整数");
            }
        }
    }
}