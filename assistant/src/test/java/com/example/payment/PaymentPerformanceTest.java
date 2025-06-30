package com.example.payment;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import com.example.payment.PaymentRecord;

public class PaymentPerformanceTest {
    private PaymentRecordManager manager;
    private LocalDateTime testDateTime;

    @BeforeEach
    void setUp() {
        File dataDir = new File("data");
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }
        manager = new PaymentRecordManager();
        testDateTime = LocalDateTime.of(2024, 1, 15, 10, 30, 0);
    }

    @AfterEach
    void tearDown() {
        File dataFile = new File("data/payment_records.txt");
        if (dataFile.exists()) {
            dataFile.delete();
        }
        File dataDir = new File("data");
        if (dataDir.exists()) {
            dataDir.delete();
        }
    }

    @Test
    void testBulkInsertPerformance() {
        int recordCount = 1000;
        long startTime = System.currentTimeMillis();
        
        // 批量插入记录
        for (int i = 0; i < recordCount; i++) {
            manager.addRecord(
                100.0 + i, 
                i % 2 == 0 ? "收入" : "支出", 
                testDateTime.plusDays(i), 
                "测试分类", 
                "测试记录" + i, 
                Arrays.asList("测试")
            );
        }
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        // 验证所有记录都被添加
        List<PaymentRecord> records = manager.getAllRecords();
        assertEquals(recordCount, records.size());
        
        // 性能断言：1000条记录应该在5秒内完成
        assertTrue(duration < 5000, "批量插入1000条记录耗时过长: " + duration + "ms");
        
        System.out.println("批量插入" + recordCount + "条记录耗时: " + duration + "ms");
    }

    @Test
    void testQueryPerformance() {
        // 准备测试数据
        int recordCount = 500;
        for (int i = 0; i < recordCount; i++) {
            manager.addRecord(
                100.0 + i, 
                i % 2 == 0 ? "收入" : "支出", 
                testDateTime.plusDays(i), 
                i % 5 == 0 ? "餐饮" : "购物", 
                "测试记录" + i, 
                Arrays.asList("测试")
            );
        }
        
        // 测试查询性能
        long startTime = System.currentTimeMillis();
        
        // 执行多次查询
        for (int i = 0; i < 100; i++) {
            List<PaymentRecord> incomeRecords = manager.queryRecords(null, null, "收入", null, null, null);
            List<PaymentRecord> expenseRecords = manager.queryRecords(null, null, "支出", null, null, null);
            List<PaymentRecord> categoryRecords = manager.queryRecords(null, null, null, "餐饮", null, null);
        }
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        // 性能断言：100次查询应该在2秒内完成
        assertTrue(duration < 2000, "查询性能测试耗时过长: " + duration + "ms");
        
        System.out.println("100次查询耗时: " + duration + "ms");
    }

    @Test
    void testMemoryUsage() {
        Runtime runtime = Runtime.getRuntime();
        
        // 记录初始内存使用
        long initialMemory = runtime.totalMemory() - runtime.freeMemory();
        
        // 添加大量记录
        int recordCount = 2000;
        for (int i = 0; i < recordCount; i++) {
            manager.addRecord(
                100.0 + i, 
                i % 2 == 0 ? "收入" : "支出", 
                testDateTime.plusDays(i), 
                "测试分类", 
                "测试记录" + i, 
                Arrays.asList("测试标签1", "测试标签2", "测试标签3")
            );
        }
        
        // 强制垃圾回收
        System.gc();
        
        // 记录最终内存使用
        long finalMemory = runtime.totalMemory() - runtime.freeMemory();
        long memoryUsed = finalMemory - initialMemory;
        
        // 验证记录数量
        List<PaymentRecord> records = manager.getAllRecords();
        assertEquals(recordCount, records.size());
        
        // 只输出内存变化
        System.out.println("2000条记录内存使用: " + (memoryUsed / 1024 / 1024) + "MB");
    }

    @Test
    void testUpdatePerformance() {
        // 准备测试数据
        int recordCount = 1000;
        for (int i = 0; i < recordCount; i++) {
            manager.addRecord(
                100.0 + i, 
                "支出", 
                testDateTime.plusDays(i), 
                "测试分类", 
                "测试记录" + i, 
                Arrays.asList("测试")
            );
        }
        
        // 测试批量更新性能
        long startTime = System.currentTimeMillis();
        
        for (int i = 1; i <= recordCount; i++) {
            manager.updateRecord(i, 200.0 + i, null, null, null, "更新后的记录" + i, null);
        }
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        // 验证更新结果
        List<PaymentRecord> records = manager.getAllRecords();
        assertEquals(200.0 + 1, records.get(0).getAmount(), 0.01);
        assertEquals("更新后的记录1", records.get(0).getNote());
        
        // 性能断言：1000次更新应该在3秒内完成
        assertTrue(duration < 3000, "批量更新性能测试耗时过长: " + duration + "ms");
        
        System.out.println("批量更新" + recordCount + "条记录耗时: " + duration + "ms");
    }

    @Test
    void testDeletePerformance() {
        // 准备测试数据
        int recordCount = 1000;
        for (int i = 0; i < recordCount; i++) {
            manager.addRecord(
                100.0 + i, 
                "支出", 
                testDateTime.plusDays(i), 
                "测试分类", 
                "测试记录" + i, 
                Arrays.asList("测试")
            );
        }
        
        // 测试批量删除性能
        long startTime = System.currentTimeMillis();
        
        // 删除所有记录
        for (int i = recordCount; i >= 1; i--) {
            manager.deleteRecord(i);
        }
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        // 验证删除结果
        List<PaymentRecord> records = manager.getAllRecords();
        assertEquals(0, records.size());
        
        // 性能断言：1000次删除应该在3秒内完成
        assertTrue(duration < 3000, "批量删除性能测试耗时过长: " + duration + "ms");
        
        System.out.println("批量删除" + recordCount + "条记录耗时: " + duration + "ms");
    }

    @Test
    void testConcurrentAccessPerformance() {
        // 准备测试数据
        int recordCount = 500;
        for (int i = 0; i < recordCount; i++) {
            manager.addRecord(
                100.0 + i, 
                i % 2 == 0 ? "收入" : "支出", 
                testDateTime.plusDays(i), 
                "测试分类", 
                "测试记录" + i, 
                Arrays.asList("测试")
            );
        }
        
        // 模拟高频率访问（非并发）
        long startTime = System.currentTimeMillis();
        
        // 执行大量操作
        for (int i = 0; i < 100; i++) {
            // 查询操作
            List<PaymentRecord> records = manager.getAllRecords();
            
            // 添加新记录
            manager.addRecord(
                1000.0 + i, 
                "支出", 
                testDateTime.plusDays(recordCount + i), 
                "高频率测试", 
                "记录" + i, 
                Arrays.asList("高频率测试")
            );
            
            // 查询特定类型
            manager.queryRecords(null, null, "支出", null, null, null);
        }
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        // 验证结果
        List<PaymentRecord> finalRecords = manager.getAllRecords();
        assertEquals(recordCount + 100, finalRecords.size()); // 原始500条 + 100条新记录
        
        // 性能断言：高频率操作应该在5秒内完成
        assertTrue(duration < 5000, "高频率访问性能测试耗时过长: " + duration + "ms");
        
        System.out.println("高频率操作耗时: " + duration + "ms");
    }

    @Test
    void testLargeDataSetPerformance() {
        // 测试大数据集性能
        int recordCount = 5000;
        long startTime = System.currentTimeMillis();
        
        // 添加大量记录
        for (int i = 0; i < recordCount; i++) {
            manager.addRecord(
                100.0 + i, 
                i % 2 == 0 ? "收入" : "支出", 
                testDateTime.plusDays(i), 
                i % 10 == 0 ? "餐饮" : i % 10 == 1 ? "购物" : i % 10 == 2 ? "交通" : "其他", 
                "大数据集测试记录" + i, 
                Arrays.asList("大数据集", "测试" + (i % 5))
            );
        }
        
        long insertTime = System.currentTimeMillis() - startTime;
        
        // 测试复杂查询性能
        long queryStartTime = System.currentTimeMillis();
        
        // 执行复杂查询
        List<PaymentRecord> complexQuery = manager.queryRecords(
            testDateTime.plusDays(1000), 
            testDateTime.plusDays(2000), 
            "支出", 
            "餐饮", 
            150.0, 
            300.0
        );
        
        long queryTime = System.currentTimeMillis() - queryStartTime;
        
        // 验证结果
        List<PaymentRecord> allRecords = manager.getAllRecords();
        assertEquals(recordCount, allRecords.size());
        
        // 性能断言
        assertTrue(insertTime < 15000, "大数据集插入耗时过长: " + insertTime + "ms");
        assertTrue(queryTime < 1000, "大数据集查询耗时过长: " + queryTime + "ms");
        
        System.out.println("大数据集(" + recordCount + "条)插入耗时: " + insertTime + "ms");
        System.out.println("大数据集复杂查询耗时: " + queryTime + "ms");
    }
} 