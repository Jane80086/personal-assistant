package com.example.life;

import java.util.Scanner;

public class LifeRecordMenu {
    private LifeRecordManager manager;
    private Scanner scanner;

    public LifeRecordMenu(Scanner scanner) {
        this.manager = new LifeRecordManager();
        this.scanner = scanner;
    }

    public void displayMenu() {
        int choice;
        do {
            System.out.println("\n--- Life Record Menu ---");
            System.out.println("1. Add New Record");
            System.out.println("2. View All Records");
            System.out.println("3. Search Records");
            System.out.println("4. Delete Record");
            System.out.println("0. Back to Main Menu");
            System.out.print("Enter your choice: ");

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
        System.out.print("Enter record title: ");
        String title = scanner.nextLine();
        System.out.print("Enter record content: ");
        String content = scanner.nextLine();
        System.out.print("Enter record category (e.g., Daily Log, Memories, Events): ");
        String category = scanner.nextLine();
        manager.addRecord(title, content, category);
    }

    private void searchRecords() {
        System.out.print("Enter keyword to search (title, content, or category): ");
        String keyword = scanner.nextLine();
        manager.searchRecords(keyword);
    }

    private void deleteRecord() {
        manager.viewAllRecords(); // Show records to help user choose
        if (manager.getRecord(1) == null) { // Check if there are any records to delete
            return;
        }
        System.out.print("Enter the number of the record to delete: ");
        try {
            int recordNumber = Integer.parseInt(scanner.nextLine());
            manager.deleteRecord(recordNumber);
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
        }
    }
}