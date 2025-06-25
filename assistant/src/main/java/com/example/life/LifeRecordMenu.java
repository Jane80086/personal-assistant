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
                        browseRecordsWithDetails();
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
                        System.out.println("返回主菜单...");
                        break;
                    default:
                        System.out.println("非法选择，请重试.");
                }
            } catch (NumberFormatException e) {
                System.out.println("非法输入，请输入数字.");
                choice = -1;
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
                return null;
            }
            int choice = Integer.parseInt(input);
            if (choice == 0) {
                return null;
            } else if (choice >= 1 && choice <= categories.size()) {
                return categories.get(choice - 1);
            } else {
                System.out.println("无效选择，请重新选择。");
                return selectCategory();
            }
        } catch (NumberFormatException e) {
            System.out.println("无效输入，请输入数字。");
            return selectCategory();
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
                return null;
            }
            int choice = Integer.parseInt(input);
            if (choice == 0) {
                return null;
            } else if (choice >= 1 && choice <= moods.size()) {
                return moods.get(choice - 1);
            } else {
                System.out.println("无效选择，请重新选择。");
                return selectMood();
            }
        } catch (NumberFormatException e) {
            System.out.println("无效输入，请输入数字。");
            return selectMood();
        }
    }

    private void searchRecords() {
        System.out.println("\n--- 搜索记录 (多条件模糊查询) ---");
        System.out.print("请输入标题关键词 (留空跳过): ");
        String titleKeyword = scanner.nextLine();

        System.out.print("请输入内容关键词 (留空跳过): ");
        String contentKeyword = scanner.nextLine();

        System.out.print("请输入分类关键词 (留空跳过): ");
        String categoryKeyword = scanner.nextLine();

        System.out.print("请输入心情关键词 (留空跳过): ");
        String moodKeyword = scanner.nextLine();

        List<LifeRecord> matchingRecords = manager.searchRecords(titleKeyword, contentKeyword, categoryKeyword, moodKeyword);

        displaySearchResults(matchingRecords, titleKeyword, contentKeyword, categoryKeyword, moodKeyword);
    }

    private void displaySearchResults(List<LifeRecord> matchingRecords, String titleKeyword, String contentKeyword, String categoryKeyword, String moodKeyword) {
        if (matchingRecords.isEmpty()) {
            if ((titleKeyword == null || titleKeyword.trim().isEmpty()) &&
                    (contentKeyword == null || contentKeyword.trim().isEmpty()) &&
                    (categoryKeyword == null || categoryKeyword.trim().isEmpty()) &&
                    (moodKeyword == null || moodKeyword.trim().isEmpty())) {
                System.out.println("未输入任何搜索关键词，请至少输入一个以进行搜索。");
            } else {
                System.out.println("没有找到匹配的记录。");
            }
        } else {
            System.out.println("\n--- 搜索结果 ---");
            for (int i = 0; i < matchingRecords.size(); i++) {
                System.out.println("结果 #" + (i + 1));
                System.out.println(matchingRecords.get(i)); // Print full details of matched records
            }
            System.out.println("--------------------");
            System.out.println("搜索完成，共找到 " + matchingRecords.size() + " 条匹配记录。");
        }
    }

    private void editRecord() {
        List<LifeRecord> allRecords = manager.getAllRecords();

        if (allRecords.isEmpty()) {
            System.out.println("没有记录可以编辑。");
            return;
        }

        System.out.println("\n--- 所有生活记录摘要 (用于编辑) ---");
        for (int i = 0; i < allRecords.size(); i++) {
            System.out.println("记录 #" + (i + 1) + ": " + allRecords.get(i).getTitle());
        }
        System.out.println("0. 取消编辑");
        System.out.print("输入想要编辑记录的序号 (输入 0 取消): ");
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
                    newTitle = recordToEdit.getTitle();
                }

                System.out.println("当前内容:");
                System.out.println(recordToEdit.getContent());
                System.out.print("请输入新内容 (留空则保留原内容): ");
                String newContent = scanner.nextLine().trim();
                if (newContent.isEmpty()) {
                    newContent = recordToEdit.getContent();
                }

                System.out.println("当前分类: " + recordToEdit.getCategory());
                String newCategory = selectCategory();
                if (newCategory == null) {
                    newCategory = recordToEdit.getCategory();
                }

                System.out.println("当前心情: " + recordToEdit.getMood());
                String newMood = selectMood();
                if (newMood == null) {
                    newMood = recordToEdit.getMood();
                }

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

    private void browseRecordsWithDetails() {
        while (true) {
            List<LifeRecord> allRecords = manager.getAllRecords();

            if (allRecords.isEmpty()) {
                System.out.println("没有记录可以浏览。");
                return;
            }

            System.out.println("\n--- 所有生活记录摘要 ---");
            for (int i = 0; i < allRecords.size(); i++) {
                System.out.println("记录 #" + (i + 1) + ": " + allRecords.get(i).getTitle());
            }
            System.out.println("0. 返回上一级");
            System.out.print("输入序号查看详情 (输入 0 返回): ");

            try {
                int choice = Integer.parseInt(scanner.nextLine());
                if (choice == 0) {
                    System.out.println("返回上一级菜单。");
                    break;
                } else {
                    LifeRecord record = manager.getRecord(choice);
                    if (record != null) {
                        System.out.println("\n--- 记录详情 #" + choice + " ---");
                        System.out.println(record);
                        System.out.println("-------------------------");
                    } else {
                        System.out.println("无效的记录序号，请检查。");
                    }
                }
            } catch (NumberFormatException e) {
                System.out.println("非法输入,请输入数字。");
            }
        }
    }

    private void deleteRecord() {
        while (true) { // Loop to allow deleting multiple records or returning to the summary list
            List<LifeRecord> allRecords = manager.getAllRecords();

            if (allRecords.isEmpty()) {
                System.out.println("没有记录可以删除。");
                return; // Exit if no records to delete
            }

            System.out.println("\n--- 所有生活记录摘要 (用于删除) ---");
            for (int i = 0; i < allRecords.size(); i++) {
                System.out.println("记录 #" + (i + 1) + ": " + allRecords.get(i).getTitle());
            }
            System.out.println("0. 返回上一级");
            System.out.print("输入序号查看详情或删除 (输入 0 返回): ");

            try {
                int recordNumberToSelect = Integer.parseInt(scanner.nextLine());

                if (recordNumberToSelect == 0) {
                    System.out.println("删除操作已取消，返回上一级菜单。");
                    break; // Exit the loop and return to the main menu
                }

                LifeRecord recordToDelete = manager.getRecord(recordNumberToSelect);
                if (recordToDelete != null) {
                    System.out.println("\n--- 记录详情 #" + recordNumberToSelect + " ---");
                    System.out.println(recordToDelete); // Display full details
                    System.out.println("-------------------------");

                    System.out.print("确认删除此记录？(1 删除, 0 返回): ");
                    int confirmChoice = Integer.parseInt(scanner.nextLine());

                    if (confirmChoice == 1) {
                        manager.deleteRecord(recordNumberToSelect); // Call manager to delete
                        // No need to break; loop will continue, showing updated list
                    } else if (confirmChoice == 0) {
                        System.out.println("删除操作已取消。");
                        // Continue loop to show the summary list again
                    } else {
                        System.out.println("无效选择，请重新输入。");
                    }
                } else {
                    System.out.println("无效的记录序号，请检查。");
                }
            } catch (NumberFormatException e) {
                System.out.println("非法输入,请输入数字。");
            }
        }
    }
}
