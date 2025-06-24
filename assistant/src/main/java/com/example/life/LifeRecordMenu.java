package com.example.life;

import java.sql.SQLOutput;
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
            System.out.println("4. 编辑记录");
            System.out.println("5. 删除记录");
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
                        editRecord();
                        break;
                    case 5:
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

    /**
     * Handles the user interaction for editing a record.
     */
    private void editRecord() {
        manager.viewAllRecords(); // Show all records first
        if (manager.getRecord(1) == null) { // Check if any records exist
            System.out.println("没有记录可以编辑。");
            return;
        }

        System.out.println("输入想要编辑记录的序号 (输入 0 取消): ");
        try {
            int recordNumber = Integer.parseInt(scanner.nextLine());

            if (recordNumber == 0) {
                System.out.println("编辑操作已取消。");
                return;
            }

            LifeRecord recordToEdit = manager.getRecord(recordNumber);
            if (recordToEdit != null) {
                System.out.println("\n--- 编辑记录 #" + recordNumber + " ---");
                System.out.println("当前标题: " + recordToEdit.getTitle());
                System.out.print("请输入新标题 (留空则保留原标题): ");
                String newTitle = scanner.nextLine().trim();
                if (newTitle.isEmpty()) {
                    newTitle = recordToEdit.getTitle(); // Keep old title if input is empty
                }

                System.out.println("当前内容:");
                System.out.println(recordToEdit.getContent());
                System.out.print("请输入新内容 (留空则保留原内容): ");
                String newContent = scanner.nextLine().trim();
                if (newContent.isEmpty()) {
                    newContent = recordToEdit.getContent(); // Keep old content if input is empty
                }

                System.out.println("当前分类: " + recordToEdit.getCategory());
                String newCategory = selectCategory(); // Use existing helper for category selection
                if (newCategory == null) { // If user cancels category selection (enters 0), retain old category
                    newCategory = recordToEdit.getCategory();
                }

                System.out.println("当前心情: " + recordToEdit.getMood());
                String newMood = selectMood(); // Use existing helper for mood selection
                if (newMood == null) { // If user cancels mood selection (enters 0), retain old mood
                    newMood = recordToEdit.getMood();
                }

                // Call the manager to perform the actual edit and file rewrite
                if (manager.editRecord(recordNumber, newTitle, newContent, newCategory, newMood)) {
                    System.out.println("记录 #" + recordNumber + " 编辑成功！");
                } else {
                    System.out.println("编辑失败，请检查序号。");
                }

            } else {
                System.out.println("无效的记录序号，请检查。");
            }

        } catch (NumberFormatException e) {
            System.out.println("非法输入,请输入数字。");
        }
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