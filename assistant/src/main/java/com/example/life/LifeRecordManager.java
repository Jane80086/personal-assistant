package com.example.life;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class LifeRecordManager {
    private List<LifeRecord> records;

    public LifeRecordManager() {
        this.records = new ArrayList<>();
    }

    /**
     * Adds a new life record.
     * @param title The title of the record.
     * @param content The content of the record.
     * @param category The category of the record.
     */
    public void addRecord(String title, String content, String category) {
        records.add(new LifeRecord(title, content, category));
        System.out.println("Life record added successfully!");
    }

    /**
     * Displays all life records.
     */
    public void viewAllRecords() {
        if (records.isEmpty()) {
            System.out.println("No life records found.");
            return;
        }
        System.out.println("\n--- All Life Records ---");
        for (int i = 0; i < records.size(); i++) {
            System.out.println("Record #" + (i + 1));
            System.out.println(records.get(i));
        }
        System.out.println("------------------------");
    }

    /**
     * Searches for records by title or content.
     * @param keyword The keyword to search for.
     * @return A list of matching records.
     */
    public List<LifeRecord> searchRecords(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            System.out.println("Search keyword cannot be empty.");
            return new ArrayList<>();
        }
        String lowerCaseKeyword = keyword.toLowerCase();
        List<LifeRecord> matchingRecords = records.stream()
                .filter(record -> record.getTitle().toLowerCase().contains(lowerCaseKeyword) ||
                        record.getContent().toLowerCase().contains(lowerCaseKeyword) ||
                        record.getCategory().toLowerCase().contains(lowerCaseKeyword))
                .collect(Collectors.toList());

        if (matchingRecords.isEmpty()) {
            System.out.println("No records found matching '" + keyword + "'.");
        } else {
            System.out.println("\n--- Search Results for '" + keyword + "' ---");
            for (int i = 0; i < matchingRecords.size(); i++) {
                System.out.println("Result #" + (i + 1));
                System.out.println(matchingRecords.get(i));
            }
            System.out.println("------------------------------------");
        }
        return matchingRecords;
    }

    /**
     * Deletes a record by its index (1-based).
     * @param index The 1-based index of the record to delete.
     * @return true if the record was deleted, false otherwise.
     */
    public boolean deleteRecord(int index) {
        if (index > 0 && index <= records.size()) {
            records.remove(index - 1);
            System.out.println("Record #" + index + " deleted successfully.");
            return true;
        } else {
            System.out.println("Invalid record number. Please enter a valid number.");
            return false;
        }
    }

    /**
     * Retrieves a record by its index (1-based).
     * @param index The 1-based index of the record.
     * @return The LifeRecord object if found, null otherwise.
     */
    public LifeRecord getRecord(int index) {
        if (index > 0 && index <= records.size()) {
            return records.get(index - 1);
        }
        return null;
    }
}