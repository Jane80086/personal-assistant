package com.example.payment;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import com.example.payment.PaymentRecord;

@SuppressWarnings("unused") // 这个类可能在其他测试中被使用
public class PaymentTestDataGenerator {
    private static final Random random = new Random(42);
    
    public static com.example.payment.PaymentRecord generateIncomeRecord(int id, LocalDateTime dateTime) {
        String[] categories = {"工资", "奖金", "投资", "兼职", "其他收入"};
        String[] notes = {"月薪", "年终奖", "股票收益", "兼职收入", "其他"};
        String[] tags = {"收入", "月薪", "奖金", "投资", "兼职"};
        
        int index = random.nextInt(categories.length);
        double amount = 1000 + random.nextDouble() * 9000;
        
        return new com.example.payment.PaymentRecord(
            id,
            amount,
            "收入",
            dateTime,
            categories[index],
            notes[index],
            Arrays.asList(tags[index], "收入")
        );
    }
    
    public static com.example.payment.PaymentRecord generateExpenseRecord(int id, LocalDateTime dateTime) {
        String[] categories = {"餐饮", "交通", "购物", "娱乐", "医疗", "学习", "房租"};
        String[] notes = {"午餐", "公交费", "买衣服", "看电影", "买药", "买书", "房租"};
        String[] tags = {"餐饮", "交通", "购物", "娱乐", "医疗", "学习", "房租"};
        
        int index = random.nextInt(categories.length);
        double amount = 10 + random.nextDouble() * 500;
        
        return new com.example.payment.PaymentRecord(
            id,
            amount,
            "支出",
            dateTime,
            categories[index],
            notes[index],
            Arrays.asList(tags[index], "支出")
        );
    }
    
    @SuppressWarnings("unused") // 这个方法可能在其他测试中被使用
    public static List<com.example.payment.PaymentRecord> generateTestRecords(int count, LocalDateTime startDate) {
        List<com.example.payment.PaymentRecord> records = new java.util.ArrayList<>();
        
        for (int i = 0; i < count; i++) {
            LocalDateTime dateTime = startDate.plusDays(i);
            
            if (random.nextBoolean()) {
                records.add(generateIncomeRecord(i + 1, dateTime));
            } else {
                records.add(generateExpenseRecord(i + 1, dateTime));
            }
        }
        
        return records;
    }
}
