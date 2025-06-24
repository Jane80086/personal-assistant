package com.example.life;

import java.util.List;
import java.util.Scanner;

public class LifeRecordMenu {
    private final LifeRecordManager manager;
    private final Scanner scanner;

    public LifeRecordMenu(Scanner scanner) {
        this.manager = new LifeRecordManager();
        this.scanner = scanner;
    }

    public void displayMenu() {
        int choice;
        do {
            System.out.println("\n--- 记录生活菜单 ---");
            System.out.println("1. 添加记录");
            System.out.println("2. 浏览所有记录");
            System.out.println("3. 搜索记录");
            System.out.println("4. 删除记录");
            System.out.println("0. 返回主菜单");
            System.out.print("输入你的选择： ");

            try {
                choice = Integer.parseInt(scanner.nextLine());
                switch (choice) {
                    case 1:
                        addRecord();
                        break;
                    case 2:
                        manager.viewAllRecords();
                        break;
                    case 3:
                        searchRecords();
                        break;
                    case 4:
                        deleteRecord();
                        break;
                    case 0:
                        System.out.println("Returning to Main Menu...");
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
                choice = -1; // Set to an invalid choice to re-loop
            }
        } while (choice != 0);
    }

    private void addRecord() {
        System.out.print("请输入记录标题: ");
        String title = scanner.nextLine().trim();
        if (title.isEmpty()) {
            System.out.println("标题不能为空，添加记录已取消。");
            return;
        }

        String category = selectCategory();
        if (category == null) {
            category = "未分类";
        }

        String mood = selectMood();
        if (mood == null) {
            mood = "未记录";
        }

        System.out.print("请输入记录内容: ");
        String content = scanner.nextLine().trim();
        if (content.isEmpty()) {
            content = "";
        }

        manager.addRecord(title, content, category, mood);
    }

    private String selectCategory() {
        List<String> categories = manager.getCategories();

        System.out.println("\n请选择分类:");
        for (int i = 0; i < categories.size(); i++) {
            System.out.println((i + 1) + ". " + categories.get(i));
        }
        System.out.println("0. 取消");
        System.out.print("输入选择 (1-" + categories.size() + "): ");

        try {
            String input = scanner.nextLine();
            if (input.trim().isEmpty()) {
                return null; // 空输入时返回null
            }
            int choice = Integer.parseInt(input);
            if (choice == 0) {
                return null; // 取消
            } else if (choice >= 1 && choice <= categories.size()) {
                return categories.get(choice - 1);
            } else {
                System.out.println("无效选择，请重新选择。");
                return selectCategory(); // 递归重新选择
            }
        } catch (NumberFormatException e) {
            System.out.println("无效输入，请输入数字。");
            return selectCategory(); // 递归重新选择
        }
    }

    private String selectMood() {
        List<String> moods = manager.getMoods();

        System.out.println("\n请选择心情:");
        for (int i = 0; i < moods.size(); i++) {
            System.out.println((i + 1) + ". " + moods.get(i));
        }
        System.out.println("0. 取消");
        System.out.print("输入选择 (1-" + moods.size() + "): ");

        try {
            String input = scanner.nextLine();
            if (input.trim().isEmpty()) {
                return null; // 空输入时返回null
            }
            int choice = Integer.parseInt(input);
            if (choice == 0) {
                return null; // 取消
            } else if (choice >= 1 && choice <= moods.size()) {
                return moods.get(choice - 1);
            } else {
                System.out.println("无效选择，请重新选择。");
                return selectMood(); // 递归重新选择
            }
        } catch (NumberFormatException e) {
            System.out.println("无效输入，请输入数字。");
            return selectMood(); // 递归重新选择
        }
    }

    private void searchRecords() {
        System.out.print("Enter keyword to search (title, content, category, or mood): ");
        String keyword = scanner.nextLine();
        manager.searchRecords(keyword);
    }

    private void deleteRecord() {
        manager.viewAllRecords();
        if (manager.getRecord(1) == null) {
            System.out.println("没有记录可以删除。"); // Added message for clarity
            return;
        }

        System.out.println("输入想要删除记录的序号 (输入 0 取消): "); // Added prompt for cancellation
        try {
            int recordNumber = Integer.parseInt(scanner.nextLine());

            if (recordNumber == 0) {
                System.out.println("删除操作已取消。"); // Confirmation message for cancellation
                return;
            }

            manager.deleteRecord(recordNumber);
        } catch (NumberFormatException e) {
            System.out.println("非法输入,请输入数字。");
        }
    }
}