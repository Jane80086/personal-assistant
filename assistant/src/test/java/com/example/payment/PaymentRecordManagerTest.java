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

public class PaymentRecordManagerTest {
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
    void testAddRecord() {
        List<String> tags = Arrays.asList("工资", "月薪");
        manager.addRecord(5000.0, "收入", testDateTime, "工资", "1月工资", tags);
        
        List<PaymentRecord> records = manager.getAllRecords();
        assertEquals(1, records.size());
        
        PaymentRecord record = records.get(0);
        assertEquals(1, record.getId());
        assertEquals(5000.0, record.getAmount(), 0.01);
        assertEquals("收入", record.getType());
    }

    @Test
    void testQueryRecordsByType() {
        manager.addRecord(5000.0, "收入", testDateTime, "工资", "1月工资", Arrays.asList("工资"));
        manager.addRecord(100.0, "支出", testDateTime.plusDays(1), "餐饮", "午餐", Arrays.asList("餐饮"));
        
        List<PaymentRecord> incomeRecords = manager.queryRecords(null, null, "收入", null, null, null);
        assertEquals(1, incomeRecords.size());
        assertEquals("收入", incomeRecords.get(0).getType());
    }

    @Test
    void testDeleteRecord() {
        manager.addRecord(5000.0, "收入", testDateTime, "工资", "1月工资", Arrays.asList("工资"));
        
        assertTrue(manager.deleteRecord(1));
        assertEquals(0, manager.getAllRecords().size());
    }

    @Test
    void testUpdateRecord() {
        manager.addRecord(5000.0, "收入", testDateTime, "工资", "1月工资", Arrays.asList("工资"));
        
        assertTrue(manager.updateRecord(1, 6000.0, null, null, null, null, null));
        
        PaymentRecord updatedRecord = manager.getAllRecords().get(0);
        assertEquals(6000.0, updatedRecord.getAmount(), 0.01);
    }

    @Test
    void testAddRecordWithAutoCategory() {
        manager.addRecord(100.0, "支出", testDateTime, null, "午餐外卖", Arrays.asList("餐饮"));
        
        List<PaymentRecord> records = manager.getAllRecords();
        assertEquals(1, records.size());
        
        PaymentRecord record = records.get(0);
        assertEquals("餐饮", record.getCategory());
    }

    @Test
    void testQueryRecordsByDateRange() {
        LocalDateTime start = testDateTime;
        LocalDateTime end = testDateTime.plusDays(2);
        
        manager.addRecord(5000.0, "收入", testDateTime, "工资", "1月工资", Arrays.asList("工资"));
        manager.addRecord(100.0, "支出", testDateTime.plusDays(1), "餐饮", "午餐", Arrays.asList("餐饮"));
        manager.addRecord(200.0, "支出", testDateTime.plusDays(3), "购物", "买衣服", Arrays.asList("购物"));
        
        List<PaymentRecord> records = manager.queryRecords(start, end, null, null, null, null);
        assertEquals(2, records.size());
    }

    @Test
    void testQueryRecordsByAmountRange() {
        manager.addRecord(5000.0, "收入", testDateTime, "工资", "1月工资", Arrays.asList("工资"));
        manager.addRecord(100.0, "支出", testDateTime.plusDays(1), "餐饮", "午餐", Arrays.asList("餐饮"));
        manager.addRecord(200.0, "支出", testDateTime.plusDays(2), "购物", "买衣服", Arrays.asList("购物"));
        
        List<PaymentRecord> records = manager.queryRecords(null, null, null, null, 150.0, 300.0);
        assertEquals(1, records.size());
        assertEquals(200.0, records.get(0).getAmount(), 0.01);
    }

    @Test
    void testQueryRecordsByCategory() {
        manager.addRecord(5000.0, "收入", testDateTime, "工资", "1月工资", Arrays.asList("工资"));
        manager.addRecord(100.0, "支出", testDateTime.plusDays(1), "餐饮", "午餐", Arrays.asList("餐饮"));
        manager.addRecord(200.0, "支出", testDateTime.plusDays(2), "购物", "买衣服", Arrays.asList("购物"));
        
        List<PaymentRecord> records = manager.queryRecords(null, null, null, "餐饮", null, null);
        assertEquals(1, records.size());
        assertEquals("餐饮", records.get(0).getCategory());
    }

    @Test
    void testQueryRecordsWithMultipleFilters() {
        LocalDateTime start = testDateTime;
        LocalDateTime end = testDateTime.plusDays(2);
        
        manager.addRecord(5000.0, "收入", testDateTime, "工资", "1月工资", Arrays.asList("工资"));
        manager.addRecord(100.0, "支出", testDateTime.plusDays(1), "餐饮", "午餐", Arrays.asList("餐饮"));
        manager.addRecord(200.0, "支出", testDateTime.plusDays(2), "购物", "买衣服", Arrays.asList("购物"));
        
        List<PaymentRecord> records = manager.queryRecords(start, end, "支出", null, 50.0, 150.0);
        assertEquals(1, records.size());
        assertEquals(100.0, records.get(0).getAmount(), 0.01);
    }

    @Test
    void testUpdateRecordPartial() {
        manager.addRecord(5000.0, "收入", testDateTime, "工资", "1月工资", Arrays.asList("工资"));
        
        assertTrue(manager.updateRecord(1, 6000.0, null, null, null, null, null));
        
        PaymentRecord updatedRecord = manager.getAllRecords().get(0);
        assertEquals(6000.0, updatedRecord.getAmount(), 0.01);
        assertEquals("收入", updatedRecord.getType());
        assertEquals(testDateTime, updatedRecord.getDateTime());
        assertEquals("工资", updatedRecord.getCategory());
        assertEquals("1月工资", updatedRecord.getNote());
        assertEquals(Arrays.asList("工资"), updatedRecord.getTags());
    }

    @Test
    void testUpdateNonExistentRecord() {
        assertFalse(manager.updateRecord(999, 1000.0, "收入", testDateTime, "工资", "测试", Arrays.asList("测试")));
    }

    @Test
    void testDeleteNonExistentRecord() {
        assertFalse(manager.deleteRecord(999));
    }

    @Test
    void testIdIncrement() {
        manager.addRecord(100.0, "支出", testDateTime, "餐饮", "午餐", Arrays.asList("餐饮"));
        manager.addRecord(200.0, "支出", testDateTime.plusDays(1), "购物", "买衣服", Arrays.asList("购物"));
        manager.addRecord(300.0, "支出", testDateTime.plusDays(2), "交通", "打车", Arrays.asList("交通"));
        
        List<PaymentRecord> records = manager.getAllRecords();
        assertEquals(1, records.get(0).getId());
        assertEquals(2, records.get(1).getId());
        assertEquals(3, records.get(2).getId());
    }

    @Test
    void testGetAllRecordsReturnsCopy() {
        manager.addRecord(100.0, "支出", testDateTime, "餐饮", "午餐", Arrays.asList("餐饮"));
        
        List<PaymentRecord> records = manager.getAllRecords();
        assertEquals(1, records.size());
        
        records.clear();
        assertEquals(1, manager.getAllRecords().size());
    }
}
