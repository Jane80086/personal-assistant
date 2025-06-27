package com.example;

import com.example.TODOlist.TodoListMenu;
import com.example.health.HealthRecordMenu;
import com.example.life.LifeRecordMenu;

import java.util.Scanner;

public class MainApp {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        LifeRecordMenu lifeRecordMenu = new LifeRecordMenu(scanner);
        HealthRecordMenu healthRecordMenu = new HealthRecordMenu(scanner);
        TodoListMenu todoListMenu = new TodoListMenu(scanner);
        int choice;
        do {
            System.out.println("\n---  个人助手主菜单 ---");
            System.out.println("1.  收支情况（敬请期待）");
            System.out.println("2.  记录生活");
            System.out.println("3.  记录健康信息");
            System.out.println("4.  待办事项");
            System.out.println("0.  退出");
            System.out.print(" 输入你的选择: ");

            try {
                choice = Integer.parseInt(scanner.nextLine());
                switch (choice) {
                    case 1:
                        System.out.println(" 收支情况模块正在开发中...");
                        break;
                    case 2:
                        lifeRecordMenu.displayMenu();
                        break;
                    case 3:
                        healthRecordMenu.displayMenu();
                        break;
                    case 4:
                        todoListMenu.displayMenu();
                        break;
                    case 0:
                        System.out.println(" 退出个人助手，再见！");
                        break;
                    default:
                        System.out.println(" 输入有误，请重新输入。");
                }
            } catch (NumberFormatException e) {
                System.out.println(" 输入有误，请输入数字。");
                choice = -1; // Set to an invalid choice to re-loop
            }
        } while (choice != 0);

        scanner.close();
    }
}