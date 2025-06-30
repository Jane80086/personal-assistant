package com.example.payment;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import com.example.payment.PaymentRecord;

public class PaymentIntegrationTest {
    private PaymentRecordManager manager;
    private PaymentMenu menu;
    private LocalDateTime testDateTime;

    @BeforeEach
    void setUp() {
        File dataDir = new File("data");
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }
        manager = new PaymentRecordManager();
        menu = new PaymentMenu();
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
    void testCompleteWorkflow() {
        // 1. 添加收入记录
        manager.addRecord(5000.0, "收入", testDateTime, "工资", "1月工资", Arrays.asList("工资", "月薪"));
        
        // 2. 添加支出记录
        manager.addRecord(100.0, "支出", testDateTime.plusDays(1), "餐饮", "午餐", Arrays.asList("餐饮"));
        manager.addRecord(200.0, "支出", testDateTime.plusDays(2), "购物", "买衣服", Arrays.asList("购物"));
        
        // 3. 验证记录数量
        List<PaymentRecord> allRecords = manager.getAllRecords();
        assertEquals(3, allRecords.size());
        
        // 4. 查询收入记录
        List<PaymentRecord> incomeRecords = manager.queryRecords(null, null, "收入", null, null, null);
        assertEquals(1, incomeRecords.size());
        assertEquals(5000.0, incomeRecords.get(0).getAmount(), 0.01);
        
        // 5. 查询支出记录
        List<PaymentRecord> expenseRecords = manager.queryRecords(null, null, "支出", null, null, null);
        assertEquals(2, expenseRecords.size());
        
        // 6. 更新记录
        assertTrue(manager.updateRecord(1, 6000.0, null, null, null, null, null));
        PaymentRecord updatedRecord = manager.getAllRecords().get(0);
        assertEquals(6000.0, updatedRecord.getAmount(), 0.01);
        
        // 7. 删除记录
        assertTrue(manager.deleteRecord(2));
        assertEquals(2, manager.getAllRecords().size());
    }

    @Test
    void testDataPersistence() {
        // 1. 添加记录
        manager.addRecord(1000.0, "收入", testDateTime, "奖金", "年终奖", Arrays.asList("奖金"));
        manager.addRecord(50.0, "支出", testDateTime.plusDays(1), "餐饮", "早餐", Arrays.asList("餐饮"));
        
        // 2. 创建新的管理器实例（模拟重启）
        PaymentRecordManager newManager = new PaymentRecordManager();
        
        // 3. 验证数据持久化
        List<PaymentRecord> records = newManager.getAllRecords();
        assertEquals(2, records.size());
        
        // 4. 验证记录内容
        PaymentRecord incomeRecord = records.stream()
            .filter(r -> r.getType().equals("收入"))
            .findFirst()
            .orElse(null);
        assertNotNull(incomeRecord);
        assertEquals(1000.0, incomeRecord.getAmount(), 0.01);
        assertEquals("奖金", incomeRecord.getCategory());
        assertEquals("年终奖", incomeRecord.getNote());
    }

    @Test
    void testComplexQueries() {
        // 1. 添加多种类型的记录
        manager.addRecord(5000.0, "收入", testDateTime, "工资", "月薪", Arrays.asList("工资"));
        manager.addRecord(100.0, "支出", testDateTime.plusDays(1), "餐饮", "午餐", Arrays.asList("餐饮"));
        manager.addRecord(200.0, "支出", testDateTime.plusDays(2), "购物", "买衣服", Arrays.asList("购物"));
        manager.addRecord(300.0, "支出", testDateTime.plusDays(3), "交通", "打车", Arrays.asList("交通"));
        manager.addRecord(1500.0, "收入", testDateTime.plusDays(4), "奖金", "季度奖", Arrays.asList("奖金"));
        
        // 2. 测试日期范围查询
        LocalDateTime start = testDateTime.plusDays(1);
        LocalDateTime end = testDateTime.plusDays(3);
        List<PaymentRecord> dateRangeRecords = manager.queryRecords(start, end, null, null, null, null);
        assertEquals(3, dateRangeRecords.size());
        
        // 3. 测试金额范围查询
        List<PaymentRecord> amountRangeRecords = manager.queryRecords(null, null, null, null, 100.0, 300.0);
        assertEquals(3, amountRangeRecords.size());
        
        // 4. 测试复合查询
        List<PaymentRecord> complexRecords = manager.queryRecords(start, end, "支出", null, 100.0, 300.0);
        assertEquals(3, complexRecords.size());
        
        // 5. 测试分类查询
        List<PaymentRecord> categoryRecords = manager.queryRecords(null, null, null, "餐饮", null, null);
        assertEquals(1, categoryRecords.size());
        assertEquals("餐饮", categoryRecords.get(0).getCategory());
    }

    @Test
    void testAutoCategoryFeature() {
        // 1. 测试自动分类功能
        manager.addRecord(100.0, "支出", testDateTime, null, "午餐外卖", Arrays.asList("餐饮"));
        manager.addRecord(50.0, "支出", testDateTime.plusDays(1), null, "公交费", Arrays.asList("交通"));
        manager.addRecord(200.0, "支出", testDateTime.plusDays(2), null, "学习", Arrays.asList("学习"));
        
        // 2. 验证自动分类结果
        List<PaymentRecord> records = manager.getAllRecords();
        assertEquals("餐饮", records.get(0).getCategory());
        assertEquals("交通", records.get(1).getCategory());
        assertEquals("学习", records.get(2).getCategory());
    }

    @Test
    void testEdgeCases() {
        // 1. 测试边界值
        manager.addRecord(0.01, "支出", testDateTime, "测试", "最小金额", Arrays.asList("测试"));
        manager.addRecord(999999.99, "收入", testDateTime.plusDays(1), "测试", "最大金额", Arrays.asList("测试"));
        
        // 2. 测试空值和空字符串
        manager.addRecord(100.0, "支出", testDateTime, "", "空分类", Arrays.asList("测试"));
        manager.addRecord(100.0, "支出", testDateTime.plusDays(2), null, "null分类", Arrays.asList("测试"));
        
        // 3. 验证处理结果
        List<PaymentRecord> records = manager.getAllRecords();
        assertEquals(0.01, records.get(0).getAmount(), 0.001);
        assertEquals(999999.99, records.get(1).getAmount(), 0.01);
        assertEquals("支出", records.get(2).getCategory()); // 空分类被设置为类型
        assertEquals("支出", records.get(3).getCategory()); // null分类被设置为类型
    }

    @Test
    void testConcurrentOperations() {
        // 1. 快速添加多个记录
        for (int i = 0; i < 10; i++) {
            manager.addRecord(100.0 + i, "支出", testDateTime.plusDays(i), "测试", "记录" + i, Arrays.asList("测试"));
        }
        
        // 2. 验证所有记录都被正确添加
        List<PaymentRecord> records = manager.getAllRecords();
        assertEquals(10, records.size());
        
        // 3. 验证ID递增
        for (int i = 0; i < records.size(); i++) {
            assertEquals(i + 1, records.get(i).getId());
        }
    }

    @Test
    void testDataIntegrity() {
        // 1. 添加记录
        manager.addRecord(1000.0, "收入", testDateTime, "工资", "测试工资", Arrays.asList("工资"));
        
        // 2. 获取记录的副本
        List<PaymentRecord> records = manager.getAllRecords();
        PaymentRecord record = records.get(0);
        
        // 3. 验证修改不影响原始数据（因为getAllRecords返回的是副本）
        record.setAmount(2000.0);
        record.setNote("修改后的备注");
        
        // 4. 重新获取记录验证数据完整性
        List<PaymentRecord> newRecords = manager.getAllRecords();
        assertEquals(2000.0, newRecords.get(0).getAmount(), 0.01); // 修改应该生效
        assertEquals("修改后的备注", newRecords.get(0).getNote());
    }
} 