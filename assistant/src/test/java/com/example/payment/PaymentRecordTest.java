package com.example.payment;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import com.example.payment.PaymentRecord;

public class PaymentRecordTest {
    private PaymentRecord record;
    private LocalDateTime testDateTime;

    @BeforeEach
    void setUp() {
        testDateTime = LocalDateTime.of(2024, 1, 15, 10, 30, 0);
        List<String> tags = Arrays.asList("工资", "月薪");
        record = new PaymentRecord(1, 5000.0, "收入", testDateTime, "工资", "1月工资", tags);
    }

    @Test
    void testConstructor() {
        assertNotNull(record);
        assertEquals(1, record.getId());
        assertEquals(5000.0, record.getAmount(), 0.01);
        assertEquals("收入", record.getType());
        assertEquals(testDateTime, record.getDateTime());
        assertEquals("工资", record.getCategory());
        assertEquals("1月工资", record.getNote());
        assertEquals(Arrays.asList("工资", "月薪"), record.getTags());
    }

    @Test
    void testConstructorWithNullTags() {
        PaymentRecord recordWithNullTags = new PaymentRecord(2, 100.0, "支出", testDateTime, "餐饮", "午餐", null);
        assertNotNull(recordWithNullTags.getTags());
        assertTrue(recordWithNullTags.getTags().isEmpty());
    }

    @Test
    void testGetters() {
        assertEquals(1, record.getId());
        assertEquals(5000.0, record.getAmount(), 0.01);
        assertEquals("收入", record.getType());
        assertEquals(testDateTime, record.getDateTime());
        assertEquals("工资", record.getCategory());
        assertEquals("1月工资", record.getNote());
        assertEquals(Arrays.asList("工资", "月薪"), record.getTags());
    }

    @Test
    void testSetters() {
        LocalDateTime newDateTime = LocalDateTime.of(2024, 2, 1, 12, 0, 0);
        List<String> newTags = Arrays.asList("奖金", "年终奖");

        record.setAmount(6000.0);
        record.setType("收入");
        record.setDateTime(newDateTime);
        record.setCategory("奖金");
        record.setNote("年终奖金");
        record.setTags(newTags);

        assertEquals(6000.0, record.getAmount(), 0.01);
        assertEquals("收入", record.getType());
        assertEquals(newDateTime, record.getDateTime());
        assertEquals("奖金", record.getCategory());
        assertEquals("年终奖金", record.getNote());
        assertEquals(newTags, record.getTags());
    }

    @Test
    void testToString() {
        String result = record.toString();
        assertTrue(result.contains("[ID:1]"));
        assertTrue(result.contains("收入"));
        assertTrue(result.contains("5000.00"));
        assertTrue(result.contains("工资"));
        assertTrue(result.contains("1月工资"));
        assertTrue(result.contains("工资,月薪"));
    }

    @Test
    void testToStringWithNullTags() {
        PaymentRecord recordWithNullTags = new PaymentRecord(3, 200.0, "支出", testDateTime, "购物", "买衣服", null);
        String result = recordWithNullTags.toString();
        assertTrue(result.contains("标签:"));
        assertFalse(result.contains("null"));
    }

    @Test
    void testBoundaryValues() {
        PaymentRecord minRecord = new PaymentRecord(1, 0.01, "支出", testDateTime, "测试", "最小金额", Arrays.asList("测试"));
        assertEquals(0.01, minRecord.getAmount(), 0.001);
        
        PaymentRecord maxRecord = new PaymentRecord(2, 999999.99, "收入", testDateTime, "测试", "最大金额", Arrays.asList("测试"));
        assertEquals(999999.99, maxRecord.getAmount(), 0.01);
        
        PaymentRecord zeroRecord = new PaymentRecord(3, 0.0, "支出", testDateTime, "测试", "零金额", Arrays.asList("测试"));
        assertEquals(0.0, zeroRecord.getAmount(), 0.0);
    }

    @Test
    void testNegativeAmount() {
        PaymentRecord negativeRecord = new PaymentRecord(1, -100.0, "支出", testDateTime, "测试", "负数金额", Arrays.asList("测试"));
        assertEquals(-100.0, negativeRecord.getAmount(), 0.01);
    }

    @Test
    void testLargeAmount() {
        PaymentRecord largeRecord = new PaymentRecord(1, 1000000.0, "收入", testDateTime, "测试", "大金额", Arrays.asList("测试"));
        assertEquals(1000000.0, largeRecord.getAmount(), 0.01);
    }

    @Test
    void testEmptyStrings() {
        PaymentRecord emptyRecord = new PaymentRecord(1, 100.0, "支出", testDateTime, "", "空分类", Arrays.asList("测试"));
        assertEquals("", emptyRecord.getCategory());
        assertEquals("空分类", emptyRecord.getNote());
    }

    @Test
    void testSpecialCharacters() {
        String specialNote = "特殊字符!@#$%^&*()_+-=[]{}|;':\",./<>?";
        PaymentRecord specialRecord = new PaymentRecord(1, 100.0, "支出", testDateTime, "测试", specialNote, Arrays.asList("测试"));
        assertEquals(specialNote, specialRecord.getNote());
    }

    @Test
    void testLongStrings() {
        String longNote = "这是一个非常长的备注，用来测试系统对长字符串的处理能力，看看是否会出现截断或其他问题。这个备注包含了中文字符、英文字符、数字和标点符号，总长度超过100个字符。";
        PaymentRecord longRecord = new PaymentRecord(1, 100.0, "支出", testDateTime, "测试", longNote, Arrays.asList("测试"));
        assertEquals(longNote, longRecord.getNote());
    }

    @Test
    void testNullHandling() {
        PaymentRecord nullRecord = new PaymentRecord(1, 100.0, "支出", testDateTime, null, null, null);
        assertNotNull(nullRecord.getTags());
        assertTrue(nullRecord.getTags().isEmpty());
        assertNull(nullRecord.getCategory());
        assertNull(nullRecord.getNote());
    }

    @Test
    void testDateTimePrecision() {
        LocalDateTime preciseTime = LocalDateTime.of(2024, 1, 15, 10, 30, 45, 123456789);
        PaymentRecord preciseRecord = new PaymentRecord(1, 100.0, "支出", preciseTime, "测试", "精确时间", Arrays.asList("测试"));
        assertEquals(preciseTime, preciseRecord.getDateTime());
    }
}
