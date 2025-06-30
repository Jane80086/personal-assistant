package com.example.TODOlist;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class TodoListMenu {
    private Scanner scanner;
    public List<TodoItem> todoList;
    private static final String FILE_PATH = "todo_list.txt";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public TodoListMenu(Scanner scanner) {
        this.scanner = scanner;
        this.todoList = loadTodoList();
        Collections.sort(todoList); // 排序待办列表
    }

    public void displayMenu() {
        int choice;
        do {
            System.out.println("\n--- 待办事项子菜单 ---");
            System.out.println("1. 查看待办列表");
            System.out.println("2. 新建待办事项");
            System.out.println("3. 编辑待办事项");
            System.out.println("4. 删除待办事项");
            System.out.println("0. 返回上一级菜单");
            System.out.print(" 输入你的选择: ");

            try {
                choice = Integer.parseInt(scanner.nextLine());
                switch (choice) {
                    case 1:
                        viewTodoList();
                        break;
                    case 2:
                        createTodoItem();
                        break;
                    case 3:
                        editTodoItem();
                        break;
                    case 4:
                        deleteTodoItem();
                        break;
                    case 0:
                        System.out.println(" 返回上一级菜单...");
                        break;
                    default:
                        System.out.println(" 输入有误，请重新输入。");
                }
            } catch (NumberFormatException e) {
                System.out.println(" 输入有误，请输入数字。");
                choice = -1; // Set to an invalid choice to re-loop
            }
        } while (choice != 0);
    }

    public void viewTodoList() {
        List<TodoItem> activeTodoList = view();
        if (activeTodoList.isEmpty()) {
            return;
        }

        int subChoice;
        do {
            System.out.println(" 输入 0 返回上一级，输入大于零的数字表示该待办已完成: ");
            try {
                subChoice = Integer.parseInt(scanner.nextLine());
                if (subChoice == 0) {
                    break;
                } else if (subChoice > 0 && subChoice <= activeTodoList.size()) {
                    TodoItem completedItem = activeTodoList.get(subChoice - 1);
                    completedItem.setCompleted(true);
                    saveTodoList();
                    System.out.println(" 待办事项已完成。");
                    break;
                } else {
                    System.out.println(" 输入有误，请重新输入。");
                }
            } catch (NumberFormatException e) {
                System.out.println(" 输入有误，请输入数字。");
            }
        } while (true);
    }

    public List<TodoItem> view() {
        List<TodoItem> activeTodoList = new ArrayList<>();
        for (TodoItem item : todoList) {
            if (!item.isCompleted()) {
                activeTodoList.add(item);
            }
        }

        if (activeTodoList.isEmpty()) {
            System.out.println(" 待办列表为空。");
            return activeTodoList;
        }

        System.out.println("\n--- 待办列表 ---");
        for (int i = 0; i < activeTodoList.size(); i++) {
            TodoItem item = activeTodoList.get(i);
            System.out.println((i + 1) + ". 内容: " + item.getContent() + ", 优先级: " + getStarString(item.getPriority()) +
                    ", 创建时间: " + item.getCreationTime().format(DATE_TIME_FORMATTER));
        }

        return activeTodoList;
    }

    public String getStarString(int priority) {
        StringBuilder stars = new StringBuilder();
        for (int i = 0; i < priority; i++) {
            stars.append("*");
        }
        return stars.toString();
    }

    public void createTodoItem() {
        System.out.print(" 请输入待办内容: ");
        String content = scanner.nextLine();

        int priority;
        do {
            System.out.print(" 请输入优先级 (1-3): ");
            try {
                priority = Integer.parseInt(scanner.nextLine());
                if (priority >= 1 && priority <= 3) {
                    break;
                } else {
                    System.out.println(" 输入有误，请输入 1-3 之间的数字。");
                }
            } catch (NumberFormatException e) {
                System.out.println(" 输入有误，请输入数字。");
            }
        } while (true);

        TodoItem newItem = new TodoItem(content, priority);
        todoList.add(newItem);
        Collections.sort(todoList); // 重新排序
        saveTodoList();
        System.out.println(" 待办事项已添加。");

        int subChoice;
        do {
            System.out.println(" 输入 1 继续新建，输入 0 返回上一级菜单: ");
            try {
                subChoice = Integer.parseInt(scanner.nextLine());
                if (subChoice == 0 || subChoice == 1) {
                    break;
                } else {
                    System.out.println(" 输入有误，请重新输入。");
                }
            } catch (NumberFormatException e) {
                System.out.println(" 输入有误，请输入数字。");
            }
        } while (true);

        if (subChoice == 1) {
            createTodoItem();
        }
    }

    public List<TodoItem> loadTodoList() {
        List<TodoItem> list = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                String content = parts[0];
                int priority = Integer.parseInt(parts[1]);
                boolean isCompleted = Boolean.parseBoolean(parts[2]);
                LocalDateTime creationTime = LocalDateTime.parse(parts[3], DATE_TIME_FORMATTER);
                TodoItem item = new TodoItem(content, priority, isCompleted, creationTime);
                list.add(item);
            }
        } catch (IOException e) {
            // File does not exist or error reading, start with an empty list
        }
        return list;
    }

    public void saveTodoList() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (TodoItem item : todoList) {
                writer.write(item.getContent() + "," + item.getPriority() + "," + item.isCompleted() + "," +
                        item.getCreationTime().format(DATE_TIME_FORMATTER));
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println(" 保存待办列表时出错: " + e.getMessage());
        }
    }

    public void editTodoItem() {
        view();
        if (todoList.isEmpty()) {
            return;
        }
        System.out.print(" 请输入要编辑的待办事项编号: ");
        int itemNumber;
        try {
            itemNumber = Integer.parseInt(scanner.nextLine());
            if (itemNumber > 0 && itemNumber <= todoList.size()) {
                TodoItem itemToEdit = todoList.get(itemNumber - 1);
                System.out.print(" 请输入新的待办内容: ");
                String newContent = scanner.nextLine();
                System.out.print(" 请输入新的优先级 (1-3): ");
                int newPriority;
                do {
                    try {
                        newPriority = Integer.parseInt(scanner.nextLine());
                        if (newPriority >= 1 && newPriority <= 3) {
                            break;
                        } else {
                            System.out.println(" 输入有误，请输入 1-3 之间的数字。");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println(" 输入有误，请输入数字。");
                    }
                } while (true);
                itemToEdit.setContent(newContent);
                itemToEdit.setPriority(newPriority);
                Collections.sort(todoList); // 重新排序
                saveTodoList();
                System.out.println(" 待办事项已编辑。");
            } else {
                System.out.println(" 输入的编号无效，请重新输入。");
            }
        } catch (NumberFormatException e) {
            System.out.println(" 输入有误，请输入数字。");
        }
    }

    public void deleteTodoItem() {
        view();
        if (todoList.isEmpty()) {
            return;
        }
        System.out.print(" 请输入要删除的待办事项编号: ");
        int itemNumber;
        try {
            itemNumber = Integer.parseInt(scanner.nextLine());
            if (itemNumber > 0 && itemNumber <= todoList.size()) {
                todoList.remove(itemNumber - 1);
                Collections.sort(todoList); // 重新排序
                saveTodoList();
                System.out.println(" 待办事项已删除。");
            } else {
                System.out.println(" 输入的编号无效，请重新输入。");
            }
        } catch (NumberFormatException e) {
            System.out.println(" 输入有误，请输入数字。");
        }
    }
}