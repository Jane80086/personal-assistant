package com.example.payment;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;
import java.util.Arrays;

public class PaymentMenu {
    private final PaymentRecordManager manager = new PaymentRecordManager();
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final Scanner scanner = new Scanner(System.in);

    public void showMenu() {
        while (true) {
            System.out.println("\n==== 收支管理 ====");
            System.out.println("1. 添加收支记录");
            System.out.println("2. 查看所有记录");
            System.out.println("3. 条件查询记录");
            System.out.println("4. 删除记录");
            System.out.println("5. 修改记录");
            System.out.println("6. 收支对比分析");
            System.out.println("7. 数据可视化");
            System.out.println("8. 搜索记录");
            System.out.println("9. 智能分析与建议");
            System.out.println("0. 返回主菜单");
            System.out.print("请选择: ");
            String choice = scanner.nextLine();
            switch (choice) {
                case "1": addRecord(); break;
                case "2": showAllRecords(); break;
                case "3": queryRecords(); break;
                case "4": deleteRecord(); break;
                case "5": updateRecord(); break;
                case "6": compareAnalysis(); break;
                case "7": showVisualization(); break;
                case "8": searchRecords(); break;
                case "9": smartAnalysis(); break;
                case "0": return;
                default: System.out.println("无效选择，请重试。");
            }
        }
    }

    private void addRecord() {
        try {
            System.out.print("金额: ");
            double amount = Double.parseDouble(scanner.nextLine());
            System.out.print("类型(收入/支出): ");
            String type = scanner.nextLine();
            System.out.print("时间(yyyy-MM-dd HH:mm:ss, 回车为当前): ");
            String dateStr = scanner.nextLine();
            LocalDateTime dateTime = dateStr.isEmpty() ? LocalDateTime.now() : LocalDateTime.parse(dateStr, FORMATTER);
            System.out.print("分类(可留空自动分类): ");
            String category = scanner.nextLine();
            System.out.print("备注: ");
            String note = scanner.nextLine();
            System.out.print("标签(逗号分隔, 可留空): ");
            String tagsStr = scanner.nextLine();
            List<String> tags = tagsStr.isEmpty() ? null : Arrays.asList(tagsStr.split(","));
            manager.addRecord(amount, type, dateTime, category, note, tags);
            System.out.println("添加成功！");
        } catch (Exception e) {
            System.out.println("输入有误，添加失败。");
        }
    }

    private void showAllRecords() {
        List<PaymentRecord> list = manager.getAllRecords();
        if (list.isEmpty()) {
            System.out.println("暂无记录。");
        } else {
            list.forEach(System.out::println);
        }
    }

    private void queryRecords() {
        try {
            System.out.print("起始时间(yyyy-MM-dd HH:mm:ss, 回车跳过): ");
            String startStr = scanner.nextLine();
            LocalDateTime start = startStr.isEmpty() ? null : LocalDateTime.parse(startStr, FORMATTER);
            System.out.print("结束时间(yyyy-MM-dd HH:mm:ss, 回车跳过): ");
            String endStr = scanner.nextLine();
            LocalDateTime end = endStr.isEmpty() ? null : LocalDateTime.parse(endStr, FORMATTER);
            System.out.print("类型(收入/支出, 回车跳过): ");
            String type = scanner.nextLine();
            if (type.isEmpty()) type = null;
            System.out.print("分类(回车跳过): ");
            String category = scanner.nextLine();
            if (category.isEmpty()) category = null;
            System.out.print("最小金额(回车跳过): ");
            String minStr = scanner.nextLine();
            Double minAmount = minStr.isEmpty() ? null : Double.parseDouble(minStr);
            System.out.print("最大金额(回车跳过): ");
            String maxStr = scanner.nextLine();
            Double maxAmount = maxStr.isEmpty() ? null : Double.parseDouble(maxStr);
            List<PaymentRecord> list = manager.queryRecords(start, end, type, category, minAmount, maxAmount);
            if (list.isEmpty()) {
                System.out.println("无匹配记录。");
            } else {
                list.forEach(System.out::println);
            }
        } catch (Exception e) {
            System.out.println("输入有误，查询失败。");
        }
    }

    private void deleteRecord() {
        try {
            System.out.print("请输入要删除的记录ID: ");
            int id = Integer.parseInt(scanner.nextLine());
            if (manager.deleteRecord(id)) {
                System.out.println("删除成功。");
            } else {
                System.out.println("未找到该ID的记录。");
            }
        } catch (Exception e) {
            System.out.println("输入有误，删除失败。");
        }
    }

    private void updateRecord() {
        try {
            System.out.print("请输入要修改的记录ID: ");
            int id = Integer.parseInt(scanner.nextLine());
            System.out.print("新金额(回车跳过): ");
            String amountStr = scanner.nextLine();
            Double amount = amountStr.isEmpty() ? null : Double.parseDouble(amountStr);
            System.out.print("新类型(收入/支出, 回车跳过): ");
            String type = scanner.nextLine();
            if (type.isEmpty()) type = null;
            System.out.print("新时间(yyyy-MM-dd HH:mm:ss, 回车跳过): ");
            String dateStr = scanner.nextLine();
            LocalDateTime dateTime = dateStr.isEmpty() ? null : LocalDateTime.parse(dateStr, FORMATTER);
            System.out.print("新分类(回车跳过): ");
            String category = scanner.nextLine();
            if (category.isEmpty()) category = null;
            System.out.print("新备注(回车跳过): ");
            String note = scanner.nextLine();
            if (note.isEmpty()) note = null;
            System.out.print("新标签(逗号分隔, 回车跳过): ");
            String tagsStr = scanner.nextLine();
            List<String> tags = tagsStr.isEmpty() ? null : Arrays.asList(tagsStr.split(","));
            if (manager.updateRecord(id, amount, type, dateTime, category, note, tags)) {
                System.out.println("修改成功。");
            } else {
                System.out.println("未找到该ID的记录。");
            }
        } catch (Exception e) {
            System.out.println("输入有误，修改失败。");
        }
    }

    /**
     * 收支对比分析：本月与上月收支对比、分类收支对比、自定义时间段对比
     */
    private void compareAnalysis() {
        System.out.println("\n==== 收支对比分析 ====");
        System.out.println("1. 本月与上月对比");
        System.out.println("2. 分类收支对比");
        System.out.println("3. 自定义时间段对比");
        System.out.print("请选择: ");
        String choice = scanner.nextLine();
        switch (choice) {
            case "1":
                compareMonth();
                break;
            case "2":
                compareCategory();
                break;
            case "3":
                compareCustomPeriod();
                break;
            default:
                System.out.println("无效选择。");
        }
    }

    // 本月与上月收支对比
    private void compareMonth() {
        List<PaymentRecord> all = manager.getAllRecords();
        java.time.LocalDate now = java.time.LocalDate.now();
        java.time.YearMonth thisMonth = java.time.YearMonth.from(now);
        java.time.YearMonth lastMonth = thisMonth.minusMonths(1);
        double thisIncome = 0, thisExpense = 0, lastIncome = 0, lastExpense = 0;
        for (PaymentRecord r : all) {
            java.time.YearMonth ym = java.time.YearMonth.from(r.getDateTime().toLocalDate());
            if (ym.equals(thisMonth)) {
                if ("收入".equals(r.getType())) thisIncome += r.getAmount();
                else thisExpense += r.getAmount();
            } else if (ym.equals(lastMonth)) {
                if ("收入".equals(r.getType())) lastIncome += r.getAmount();
                else lastExpense += r.getAmount();
            }
        }
        System.out.println("\n【本月 vs 上月】");
        System.out.printf("%-8s | %-10s | %-10s | %-10s\n", "类型", "本月", "上月", "变化");
        System.out.println("----------------------------------------");
        System.out.printf("%-8s | %-10.2f | %-10.2f | %+9.2f\n", "收入", thisIncome, lastIncome, thisIncome - lastIncome);
        System.out.printf("%-8s | %-10.2f | %-10.2f | %+9.2f\n", "支出", thisExpense, lastExpense, thisExpense - lastExpense);
        System.out.printf("%-8s | %-10.2f | %-10.2f | %+9.2f\n", "结余", thisIncome - thisExpense, lastIncome - lastExpense, (thisIncome - thisExpense) - (lastIncome - lastExpense));
    }

    // 分类收支对比
    private void compareCategory() {
        List<PaymentRecord> all = manager.getAllRecords();
        java.util.Map<String, Double> incomeMap = new java.util.HashMap<>();
        java.util.Map<String, Double> expenseMap = new java.util.HashMap<>();
        for (PaymentRecord r : all) {
            String cat = r.getCategory();
            if ("收入".equals(r.getType())) {
                incomeMap.put(cat, incomeMap.getOrDefault(cat, 0.0) + r.getAmount());
            } else {
                expenseMap.put(cat, expenseMap.getOrDefault(cat, 0.0) + r.getAmount());
            }
        }
        System.out.println("\n【各分类收入】");
        System.out.printf("%-10s | %-10s\n", "分类", "收入");
        System.out.println("----------------------");
        incomeMap.forEach((k, v) -> System.out.printf("%-10s | %-10.2f\n", k, v));
        System.out.println("\n【各分类支出】");
        System.out.printf("%-10s | %-10s\n", "分类", "支出");
        System.out.println("----------------------");
        expenseMap.forEach((k, v) -> System.out.printf("%-10s | %-10.2f\n", k, v));
    }

    // 自定义时间段对比
    private void compareCustomPeriod() {
        try {
            System.out.print("请输入第一个时间段起始(yyyy-MM-dd): ");
            String start1Str = scanner.nextLine();
            System.out.print("请输入第一个时间段结束(yyyy-MM-dd): ");
            String end1Str = scanner.nextLine();
            System.out.print("请输入第二个时间段起始(yyyy-MM-dd): ");
            String start2Str = scanner.nextLine();
            System.out.print("请输入第二个时间段结束(yyyy-MM-dd): ");
            String end2Str = scanner.nextLine();
            java.time.LocalDateTime start1 = java.time.LocalDate.parse(start1Str).atStartOfDay();
            java.time.LocalDateTime end1 = java.time.LocalDate.parse(end1Str).atTime(23,59,59);
            java.time.LocalDateTime start2 = java.time.LocalDate.parse(start2Str).atStartOfDay();
            java.time.LocalDateTime end2 = java.time.LocalDate.parse(end2Str).atTime(23,59,59);
            double income1 = 0, expense1 = 0, income2 = 0, expense2 = 0;
            for (PaymentRecord r : manager.getAllRecords()) {
                if (!r.getDateTime().isBefore(start1) && !r.getDateTime().isAfter(end1)) {
                    if ("收入".equals(r.getType())) income1 += r.getAmount();
                    else expense1 += r.getAmount();
                } else if (!r.getDateTime().isBefore(start2) && !r.getDateTime().isAfter(end2)) {
                    if ("收入".equals(r.getType())) income2 += r.getAmount();
                    else expense2 += r.getAmount();
                }
            }
            System.out.println("\n【自定义时间段对比】");
            System.out.printf("%-8s | %-12s | %-12s | %-10s\n", "类型", "时间段1", "时间段2", "变化");
            System.out.println("---------------------------------------------------");
            System.out.printf("%-8s | %-12.2f | %-12.2f | %+9.2f\n", "收入", income1, income2, income2 - income1);
            System.out.printf("%-8s | %-12.2f | %-12.2f | %+9.2f\n", "支出", expense1, expense2, expense2 - expense1);
            System.out.printf("%-8s | %-12.2f | %-12.2f | %+9.2f\n", "结余", income1 - expense1, income2 - expense2, (income2 - expense2) - (income1 - expense1));
        } catch (Exception e) {
            System.out.println("输入有误，对比失败。");
        }
    }

    /**
     * 数据可视化：月度收支趋势柱状图、分类支出占比图
     */
    private void showVisualization() {
        System.out.println("\n==== 数据可视化 ====");
        System.out.println("1. 月度收支趋势柱状图");
        System.out.println("2. 分类支出占比图");
        System.out.print("请选择: ");
        String choice = scanner.nextLine();
        switch (choice) {
            case "1":
                showMonthlyBarChart();
                break;
            case "2":
                showCategoryPieChart();
                break;
            default:
                System.out.println("无效选择。");
        }
    }

    // 月度收支趋势柱状图
    private void showMonthlyBarChart() {
        List<PaymentRecord> all = manager.getAllRecords();
        java.util.Map<String, Double> incomeMap = new java.util.TreeMap<>();
        java.util.Map<String, Double> expenseMap = new java.util.TreeMap<>();
        for (PaymentRecord r : all) {
            String ym = r.getDateTime().getYear() + "-" + String.format("%02d", r.getDateTime().getMonthValue());
            if ("收入".equals(r.getType())) {
                incomeMap.put(ym, incomeMap.getOrDefault(ym, 0.0) + r.getAmount());
            } else {
                expenseMap.put(ym, expenseMap.getOrDefault(ym, 0.0) + r.getAmount());
            }
        }
        System.out.println("\n【月度收支趋势】");
        System.out.printf("%-8s | %-8s | %-8s\n", "月份", "收入", "支出");
        System.out.println("-----------------------------");
        for (String ym : incomeMap.keySet()) {
            double income = incomeMap.getOrDefault(ym, 0.0);
            double expense = expenseMap.getOrDefault(ym, 0.0);
            System.out.printf("%-8s | %-8.2f | %-8.2f\n", ym, income, expense);
            // 柱状图
            System.out.print(" 收入: ");
            printBar(income);
            System.out.print(" 支出: ");
            printBar(expense);
            System.out.println();
        }
    }

    // 分类支出占比字符饼图
    private void showCategoryPieChart() {
        List<PaymentRecord> all = manager.getAllRecords();
        java.util.Map<String, Double> expenseMap = new java.util.HashMap<>();
        double total = 0;
        for (PaymentRecord r : all) {
            if (!"收入".equals(r.getType())) {
                String cat = r.getCategory();
                expenseMap.put(cat, expenseMap.getOrDefault(cat, 0.0) + r.getAmount());
                total += r.getAmount();
            }
        }
        System.out.println("\n【分类支出占比】");
        for (String cat : expenseMap.keySet()) {
            double v = expenseMap.get(cat);
            int len = (int) Math.round(v / total * 40);
            System.out.printf("%-10s | %6.2f | %5.1f%% | ", cat, v, v / total * 100);
            for (int i = 0; i < len; i++) System.out.print("█");
            System.out.println();
        }
    }

    // 打印柱状图
    private void printBar(double value) {
        int len = (int) Math.round(value / getMaxBarValue() * 50);
        for (int i = 0; i < len; i++) System.out.print("█");
        System.out.println();
    }

    // 获取柱状图最大值（收入和支出中的最大值）
    private double getMaxBarValue() {
        List<PaymentRecord> all = manager.getAllRecords();
        double max = 1;
        for (PaymentRecord r : all) {
            if (r.getAmount() > max) max = r.getAmount();
        }
        return max;
    }

    /**
     * 搜索记录：支持按备注、分类、标签、金额、时间等条件模糊搜索
     */
    private void searchRecords() {
        try {
            System.out.println("\n==== 搜索记录 ====");
            System.out.print("起始时间(yyyy-MM-dd HH:mm:ss, 回车跳过): ");
            String startStr = scanner.nextLine();
            LocalDateTime start = startStr.isEmpty() ? null : LocalDateTime.parse(startStr, FORMATTER);
            System.out.print("结束时间(yyyy-MM-dd HH:mm:ss, 回车跳过): ");
            String endStr = scanner.nextLine();
            LocalDateTime end = endStr.isEmpty() ? null : LocalDateTime.parse(endStr, FORMATTER);
            System.out.print("类型(收入/支出, 回车跳过): ");
            String type = scanner.nextLine();
            if (type.isEmpty()) type = null;
            System.out.print("分类(回车跳过): ");
            String category = scanner.nextLine();
            if (category.isEmpty()) category = null;
            System.out.print("备注关键词(回车跳过): ");
            String noteKey = scanner.nextLine();
            if (noteKey.isEmpty()) noteKey = null;
            System.out.print("标签(逗号分隔, 回车跳过): ");
            String tagsStr = scanner.nextLine();
            List<String> tags = tagsStr.isEmpty() ? null : Arrays.asList(tagsStr.split(","));
            System.out.print("最小金额(回车跳过): ");
            String minStr = scanner.nextLine();
            Double minAmount = minStr.isEmpty() ? null : Double.parseDouble(minStr);
            System.out.print("最大金额(回车跳过): ");
            String maxStr = scanner.nextLine();
            Double maxAmount = maxStr.isEmpty() ? null : Double.parseDouble(maxStr);
            List<PaymentRecord> list = manager.getAllRecords();
            List<PaymentRecord> result = new java.util.ArrayList<>();
            for (PaymentRecord r : list) {
                if (start != null && r.getDateTime().isBefore(start)) continue;
                if (end != null && r.getDateTime().isAfter(end)) continue;
                if (type != null && !type.equals(r.getType())) continue;
                if (category != null && !category.equals(r.getCategory())) continue;
                if (noteKey != null && (r.getNote() == null || !r.getNote().contains(noteKey))) continue;
                if (tags != null && !tags.isEmpty()) {
                    boolean found = false;
                    for (String t : tags) {
                        if (r.getTags() != null && r.getTags().contains(t)) { found = true; break; }
                    }
                    if (!found) continue;
                }
                if (minAmount != null && r.getAmount() < minAmount) continue;
                if (maxAmount != null && r.getAmount() > maxAmount) continue;
                result.add(r);
            }
            if (result.isEmpty()) {
                System.out.println("无匹配记录。");
            } else {
                System.out.println("搜索结果:");
                result.forEach(System.out::println);
            }
        } catch (Exception e) {
            System.out.println("输入有误，搜索失败。");
        }
    }

    /**
     * 智能分析与建议：高频支出分类、异常波动、节省建议
     */
    private void smartAnalysis() {
        System.out.println("\n==== 智能分析与建议 ====");
        List<PaymentRecord> all = manager.getAllRecords();
        if (all.isEmpty()) {
            System.out.println("暂无数据，无法分析。");
            return;
        }
        java.time.LocalDate now = java.time.LocalDate.now();
        java.time.YearMonth thisMonth = java.time.YearMonth.from(now);
        java.time.YearMonth lastMonth = thisMonth.minusMonths(1);
        double thisTotal = 0, lastTotal = 0;
        java.util.Map<String, Double> catMap = new java.util.HashMap<>();
        for (PaymentRecord r : all) {
            if (!"收入".equals(r.getType())) {
                java.time.YearMonth ym = java.time.YearMonth.from(r.getDateTime().toLocalDate());
                if (ym.equals(thisMonth)) {
                    thisTotal += r.getAmount();
                    String cat = r.getCategory();
                    catMap.put(cat, catMap.getOrDefault(cat, 0.0) + r.getAmount());
                } else if (ym.equals(lastMonth)) {
                    lastTotal += r.getAmount();
                }
            }
        }
        // 高频支出分类
        String topCat = null;
        double topVal = 0;
        for (String cat : catMap.keySet()) {
            if (catMap.get(cat) > topVal) {
                topVal = catMap.get(cat);
                topCat = cat;
            }
        }
        if (topCat != null) {
            System.out.printf("本月支出最多的分类：%s（%.2f元）\n", topCat, topVal);
        }
        // 异常波动
        if (lastTotal > 0) {
            double change = (thisTotal - lastTotal) / lastTotal * 100;
            if (change > 30) {
                System.out.printf("警告：本月总支出较上月增长%.1f%%，请注意控制消费！\n", change);
            } else if (change < -30) {
                System.out.printf("提示：本月总支出较上月下降%.1f%%，继续保持！\n", -change);
            } else {
                System.out.printf("本月总支出与上月变化幅度为%.1f%%。\n", change);
            }
        }
        // 节省建议
        if (topCat != null && topVal > thisTotal * 0.5) {
            System.out.printf("建议：本月%s支出占总支出%.1f%%，可考虑适当减少该类消费。\n", topCat, topVal / thisTotal * 100);
        }
        System.out.println("分析完毕。");
    }
} 