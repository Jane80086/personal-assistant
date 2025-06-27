package com.example.health;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;


import static org.junit.jupiter.api.Assertions.*;

public class HealthRecordTest {
    
    @Test
    void testConstructorAndGetters() {
        LocalDate recordDate = LocalDate.of(2024, 6, 25);
        Double weight = 70.5;
        Double height = 175.0;
        Integer heartRate = 75;
        Integer bloodPressureHigh = 120;
        Integer bloodPressureLow = 80;
        Integer sleepHours = 8;
        Integer steps = 10000;
        String notes = "今天感觉很好";

        HealthRecord record = new HealthRecord(recordDate, weight, height, heartRate,
                bloodPressureHigh, bloodPressureLow, sleepHours, steps, notes);

        assertEquals(recordDate, record.getRecordDate(), "记录日期应该匹配构造函数参数");
        assertEquals(weight, record.getWeight(), "体重应该匹配构造函数参数");
        assertEquals(height, record.getHeight(), "身高应该匹配构造函数参数");
        assertEquals(heartRate, record.getHeartRate(), "心率应该匹配构造函数参数");
        assertEquals(bloodPressureHigh, record.getBloodPressureHigh(), "高压应该匹配构造函数参数");
        assertEquals(bloodPressureLow, record.getBloodPressureLow(), "低压应该匹配构造函数参数");
        assertEquals(sleepHours, record.getSleepHours(), "睡眠时间应该匹配构造函数参数");
        assertEquals(steps, record.getSteps(), "步数应该匹配构造函数参数");
        assertEquals(notes, record.getNotes(), "备注应该匹配构造函数参数");
    }

    @Test
    void testConstructorWithNullDate() {
        HealthRecord record = new HealthRecord(null, 70.0, 175.0, 75, 120, 80, 8, 10000, "测试");
        
        assertNotNull(record.getRecordDate(), "当传入null日期时，应该自动设置为当前日期");
        assertEquals(LocalDate.now(), record.getRecordDate(), "null日期应该被替换为当前日期");
    }

    @Test
    void testSetters() {
        HealthRecord record = new HealthRecord(LocalDate.now(), 70.0, 175.0, 75, 120, 80, 8, 10000, "旧备注");

        LocalDate newDate = LocalDate.of(2024, 7, 1);
        Double newWeight = 71.2;
        Double newHeight = 176.0;
        Integer newHeartRate = 78;
        Integer newBpHigh = 125;
        Integer newBpLow = 82;
        Integer newSleepHours = 7;
        Integer newSteps = 12000;
        String newNotes = "新备注";

        record.setRecordDate(newDate);
        record.setWeight(newWeight);
        record.setHeight(newHeight);
        record.setHeartRate(newHeartRate);
        record.setBloodPressureHigh(newBpHigh);
        record.setBloodPressureLow(newBpLow);
        record.setSleepHours(newSleepHours);
        record.setSteps(newSteps);
        record.setNotes(newNotes);

        assertEquals(newDate, record.getRecordDate(), "记录日期应该被更新");
        assertEquals(newWeight, record.getWeight(), "体重应该被更新");
        assertEquals(newHeight, record.getHeight(), "身高应该被更新");
        assertEquals(newHeartRate, record.getHeartRate(), "心率应该被更新");
        assertEquals(newBpHigh, record.getBloodPressureHigh(), "高压应该被更新");
        assertEquals(newBpLow, record.getBloodPressureLow(), "低压应该被更新");
        assertEquals(newSleepHours, record.getSleepHours(), "睡眠时间应该被更新");
        assertEquals(newSteps, record.getSteps(), "步数应该被更新");
        assertEquals(newNotes, record.getNotes(), "备注应该被更新");
    }

    @Test
    void testToStringFormat() {
        LocalDate recordDate = LocalDate.of(2024, 6, 25);
        HealthRecord record = new HealthRecord(recordDate, 70.5, 175.0, 75, 120, 80, 8, 10000, "测试备注");

        String result = record.toString();
        
        assertNotNull(result, "toString() 结果不应为 null");
        assertTrue(result.contains("日期: 2024-06-25"), "toString() 应包含正确格式化的日期");
        assertTrue(result.contains("身高: 175.0 cm"), "toString() 应包含身高信息");
        assertTrue(result.contains("体重: 70.5 kg"), "toString() 应包含体重信息");
        assertTrue(result.contains("心率: 75 次/分钟"), "toString() 应包含心率信息");
        assertTrue(result.contains("血压: 120/80 mmHg"), "toString() 应包含血压信息");
        assertTrue(result.contains("睡眠: 8 小时"), "toString() 应包含睡眠信息");
        assertTrue(result.contains("步数: 10000 步"), "toString() 应包含步数信息");
        assertTrue(result.contains("备注: 测试备注"), "toString() 应包含备注信息");
        assertTrue(result.startsWith("----------------------------------------"), "toString() 应以分隔线开始");
        assertTrue(result.endsWith("----------------------------------------"), "toString() 应以分隔线结束");
    }

    @Test
    void testToFileFormat() {
        LocalDate recordDate = LocalDate.of(2024, 6, 25);
        HealthRecord record = new HealthRecord(recordDate, 70.5, 175.0, 75, 120, 80, 8, 10000, "测试备注");

        String expectedFormat = "2024-06-25 | 70.5 | 175.0 | 75 | 120 | 80 | 8 | 10000 | 测试备注";
        assertEquals(expectedFormat, record.toFileFormat(), "toFileFormat() 输出格式应与预期一致");
    }

    @Test
    void testConstructorWithNullValues() {
        HealthRecord record = new HealthRecord(LocalDate.of(2024, 6, 25), null, null, null, null, null, null, null, null);

        assertNotNull(record.getRecordDate(), "记录日期不应为 null");
        assertNull(record.getWeight(), "体重应为 null");
        assertNull(record.getHeight(), "身高应为 null");
        assertNull(record.getHeartRate(), "心率应为 null");
        assertNull(record.getBloodPressureHigh(), "高压应为 null");
        assertNull(record.getBloodPressureLow(), "低压应为 null");
        assertNull(record.getSleepHours(), "睡眠时间应为 null");
        assertNull(record.getSteps(), "步数应为 null");
        assertNull(record.getNotes(), "备注应为 null");
    }

    @Test
    void testToStringWithNullValues() {
        LocalDate recordDate = LocalDate.of(2024, 6, 25);
        HealthRecord record = new HealthRecord(recordDate, null, null, null, null, null, null, null, null);

        String result = record.toString();
        
        assertNotNull(result, "toString() 结果不应为 null");
        assertTrue(result.contains("日期: 2024-06-25"), "toString() 应包含日期");
        assertTrue(result.contains("身高: 无"), "toString() 应显示身高为无");
        assertTrue(result.contains("体重: 无"), "toString() 应显示体重为无");
        assertTrue(result.contains("心率: 无"), "toString() 应显示心率为无");
        assertTrue(result.contains("血压: 无"), "toString() 应显示血压为无");
        assertTrue(result.contains("睡眠: 无"), "toString() 应显示睡眠为无");
        assertTrue(result.contains("步数: 无"), "toString() 应显示步数为无");
        assertTrue(result.contains("备注: 无"), "toString() 应显示备注为无");
    }

    @Test
    void testToFileFormatWithNullValues() {
        LocalDate recordDate = LocalDate.of(2024, 6, 25);
        HealthRecord record = new HealthRecord(recordDate, null, null, null, null, null, null, null, null);

        String expectedFormat = "2024-06-25 |  |  |  |  |  |  |  | ";
        assertEquals(expectedFormat, record.toFileFormat(), "toFileFormat() 应正确处理 null 值");
    }

    @Test
    void testToStringWithPartialNullValues() {
        LocalDate recordDate = LocalDate.of(2024, 6, 25);
        HealthRecord record = new HealthRecord(recordDate, 70.5, null, 75, null, 80, null, 10000, "测试");

        String result = record.toString();
        
        assertTrue(result.contains("体重: 70.5 kg"), "toString() 应显示体重");
        assertTrue(result.contains("身高: 无"), "toString() 应显示身高为无");
        assertTrue(result.contains("心率: 75 次/分钟"), "toString() 应显示心率");
        assertTrue(result.contains("血压: ?/80 mmHg"), "toString() 应显示部分血压信息");
        assertTrue(result.contains("睡眠: 无"), "toString() 应显示睡眠为无");
        assertTrue(result.contains("步数: 10000 步"), "toString() 应显示步数");
        assertTrue(result.contains("备注: 测试"), "toString() 应显示备注");
    }

    @Test
    void testToFileFormatWithPartialNullValues() {
        LocalDate recordDate = LocalDate.of(2024, 6, 25);
        HealthRecord record = new HealthRecord(recordDate, 70.5, null, 75, null, 80, null, 10000, "测试");

        String expectedFormat = "2024-06-25 | 70.5 |  | 75 |  | 80 |  | 10000 | 测试";
        assertEquals(expectedFormat, record.toFileFormat(), "toFileFormat() 应正确处理部分 null 值");
    }

    @Test
    void testSpecialCharactersInNotes() {
        LocalDate recordDate = LocalDate.of(2024, 6, 25);
        String specialNotes = "备注包含|分隔符\n换行符\t制表符";
        
        HealthRecord record = new HealthRecord(recordDate, 70.0, 175.0, 75, 120, 80, 8, 10000, specialNotes);

        assertEquals(specialNotes, record.getNotes(), "特殊字符备注应正确保存");
        assertTrue(record.toString().contains("备注: " + specialNotes), "toString() 应正确显示特殊字符");
        assertTrue(record.toFileFormat().endsWith(specialNotes), "toFileFormat() 应正确包含特殊字符");
    }

    @Test
    void testVeryLongNotes() {
        LocalDate recordDate = LocalDate.of(2024, 6, 25);
        String longNotes = "这是一个很长的备注".repeat(1000);
        
        HealthRecord record = new HealthRecord(recordDate, 70.0, 175.0, 75, 120, 80, 8, 10000, longNotes);

        assertEquals(longNotes, record.getNotes(), "极长备注应正确保存");
        assertTrue(record.toString().contains("备注: " + longNotes), "toString() 应正确显示极长备注");
        assertTrue(record.toFileFormat().endsWith(longNotes), "toFileFormat() 应正确包含极长备注");
    }

    @Test
    void testToStringStructure() {
        LocalDate recordDate = LocalDate.of(2024, 6, 25);
        HealthRecord record = new HealthRecord(recordDate, 70.0, 175.0, 75, 120, 80, 8, 10000, "测试");

        String result = record.toString();
        String[] lines = result.split("\n");

        assertEquals(10, lines.length, "toString() 应包含正确数量的行");
        assertEquals("----------------------------------------", lines[0], "第一行应为分隔线");
        assertTrue(lines[1].startsWith("日期: "), "第二行应包含日期");
        assertTrue(lines[2].startsWith("身高: "), "第三行应包含身高");
        assertTrue(lines[3].startsWith("体重: "), "第四行应包含体重");
        assertTrue(lines[4].startsWith("心率: "), "第五行应包含心率");
        assertTrue(lines[5].startsWith("血压: "), "第六行应包含血压");
        assertTrue(lines[6].startsWith("睡眠: "), "第七行应包含睡眠");
        assertTrue(lines[7].startsWith("步数: "), "第八行应包含步数");
        assertTrue(lines[8].startsWith("备注: "), "第九行应包含备注");
        assertEquals("----------------------------------------", lines[9], "最后一行应为分隔线");
    }

    @Test
    void testToFileFormatSeparatorCount() {
        LocalDate recordDate = LocalDate.of(2024, 6, 25);
        HealthRecord record = new HealthRecord(recordDate, 70.0, 175.0, 75, 120, 80, 8, 10000, "测试");

        String result = record.toFileFormat();
        long separatorCount = result.chars().filter(ch -> ch == '|').count();
        
        assertEquals(8, separatorCount, "toFileFormat() 应包含8个分隔符");
    }

    @Test
    void testWhitespaceStrings() {
        LocalDate recordDate = LocalDate.of(2024, 6, 25);
        String whitespaceNotes = "   \t\n  ";
        
        HealthRecord record = new HealthRecord(recordDate, 70.0, 175.0, 75, 120, 80, 8, 10000, whitespaceNotes);

        assertEquals(whitespaceNotes, record.getNotes(), "空白字符串备注应正确保存");
        assertTrue(record.toString().contains("备注: " + whitespaceNotes), "toString() 应正确显示空白字符串");
        assertTrue(record.toFileFormat().endsWith(whitespaceNotes), "toFileFormat() 应正确包含空白字符串");
    }

    @Test
    void testUnicodeCharacters() {
        LocalDate recordDate = LocalDate.of(2024, 6, 25);
        String unicodeNotes = "Unicode字符: 🏃‍♂️💪❤️";
        
        HealthRecord record = new HealthRecord(recordDate, 70.0, 175.0, 75, 120, 80, 8, 10000, unicodeNotes);

        assertEquals(unicodeNotes, record.getNotes(), "Unicode字符备注应正确保存");
        assertTrue(record.toString().contains("备注: " + unicodeNotes), "toString() 应正确显示Unicode字符");
        assertTrue(record.toFileFormat().endsWith(unicodeNotes), "toFileFormat() 应正确包含Unicode字符");
    }

    @Test
    void testDateNotOverridden() {
        LocalDate originalDate = LocalDate.of(2024, 6, 25);
        HealthRecord record = new HealthRecord(originalDate, 70.0, 175.0, 75, 120, 80, 8, 10000, "测试");

        // 设置一个新的日期
        LocalDate newDate = LocalDate.of(2024, 7, 1);
        record.setRecordDate(newDate);

        assertEquals(newDate, record.getRecordDate(), "日期应该被正确更新");
        assertNotEquals(originalDate, record.getRecordDate(), "原始日期应该被覆盖");
    }

    @Test
    void testIdenticalContentRecords() {
        LocalDate date1 = LocalDate.of(2024, 6, 25);
        LocalDate date2 = LocalDate.of(2024, 6, 25);
        
        HealthRecord record1 = new HealthRecord(date1, 70.0, 175.0, 75, 120, 80, 8, 10000, "测试");
        HealthRecord record2 = new HealthRecord(date2, 70.0, 175.0, 75, 120, 80, 8, 10000, "测试");

        assertEquals(record1.getRecordDate(), record2.getRecordDate(), "相同日期的记录应该相等");
        assertEquals(record1.getWeight(), record2.getWeight(), "相同体重的记录应该相等");
        assertEquals(record1.getHeight(), record2.getHeight(), "相同身高的记录应该相等");
        assertEquals(record1.getHeartRate(), record2.getHeartRate(), "相同心率的记录应该相等");
        assertEquals(record1.getBloodPressureHigh(), record2.getBloodPressureHigh(), "相同高压的记录应该相等");
        assertEquals(record1.getBloodPressureLow(), record2.getBloodPressureLow(), "相同低压的记录应该相等");
        assertEquals(record1.getSleepHours(), record2.getSleepHours(), "相同睡眠时间的记录应该相等");
        assertEquals(record1.getSteps(), record2.getSteps(), "相同步数的记录应该相等");
        assertEquals(record1.getNotes(), record2.getNotes(), "相同备注的记录应该相等");
    }

    @Test
    void testNumericStrings() {
        LocalDate recordDate = LocalDate.of(2024, 6, 25);
        String numericNotes = "123456789";
        
        HealthRecord record = new HealthRecord(recordDate, 70.0, 175.0, 75, 120, 80, 8, 10000, numericNotes);

        assertEquals(numericNotes, record.getNotes(), "数字字符串备注应正确保存");
        assertTrue(record.toString().contains("备注: " + numericNotes), "toString() 应正确显示数字字符串");
        assertTrue(record.toFileFormat().endsWith(numericNotes), "toFileFormat() 应正确包含数字字符串");
    }

    @Test
    void testDateBoundaryValues() {
        // 测试最小日期
        LocalDate minDate = LocalDate.of(1900, 1, 1);
        HealthRecord minRecord = new HealthRecord(minDate, 70.0, 175.0, 75, 120, 80, 8, 10000, "测试");
        assertEquals(minDate, minRecord.getRecordDate(), "最小日期应正确保存");

        // 测试最大日期（当前日期）
        LocalDate maxDate = LocalDate.now();
        HealthRecord maxRecord = new HealthRecord(maxDate, 70.0, 175.0, 75, 120, 80, 8, 10000, "测试");
        assertEquals(maxDate, maxRecord.getRecordDate(), "当前日期应正确保存");

        // 测试未来日期（应该被允许，因为构造函数不验证日期）
        LocalDate futureDate = LocalDate.now().plusDays(1);
        HealthRecord futureRecord = new HealthRecord(futureDate, 70.0, 175.0, 75, 120, 80, 8, 10000, "测试");
        assertEquals(futureDate, futureRecord.getRecordDate(), "未来日期应正确保存");
    }

    @Test
    void testSeparatorInjectionSecurity() {
        LocalDate recordDate = LocalDate.of(2024, 6, 25);
        String maliciousNotes = "恶意|分隔符|注入";
        
        HealthRecord record = new HealthRecord(recordDate, 70.0, 175.0, 75, 120, 80, 8, 10000, maliciousNotes);

        assertEquals(maliciousNotes, record.getNotes(), "包含分隔符的备注应正确保存");
        
        String fileFormat = record.toFileFormat();
        // 验证分隔符数量：8个字段分隔符 + 备注中的2个分隔符 = 10个
        long separatorCount = fileFormat.chars().filter(ch -> ch == '|').count();
        assertEquals(10, separatorCount, "toFileFormat() 应包含10个分隔符（8个字段分隔符 + 备注中的2个分隔符）");
        
        // 验证备注内容完整保存
        assertTrue(fileFormat.endsWith(maliciousNotes), "恶意备注应完整保存");
        
        // 验证字段数量仍然正确（9个字段）
        String[] parts = fileFormat.split(" \\| ");
        assertEquals(9, parts.length, "toFileFormat() 应包含9个字段");
    }

    @Test
    void testEmptyStringNotes() {
        LocalDate recordDate = LocalDate.of(2024, 6, 25);
        String emptyNotes = "";
        
        HealthRecord record = new HealthRecord(recordDate, 70.0, 175.0, 75, 120, 80, 8, 10000, emptyNotes);

        assertEquals(emptyNotes, record.getNotes(), "空字符串备注应正确保存");
        assertTrue(record.toString().contains("备注: "), "toString() 应正确显示空备注");
        assertTrue(record.toFileFormat().endsWith(""), "toFileFormat() 应正确包含空字符串");
    }

    @Test
    void testToStringContainsAllFields() {
        LocalDate recordDate = LocalDate.of(2024, 6, 25);
        HealthRecord record = new HealthRecord(recordDate, 70.0, 175.0, 75, 120, 80, 8, 10000, "完整测试");

        String result = record.toString();
        
        assertTrue(result.contains("日期:"), "toString() 应包含日期字段");
        assertTrue(result.contains("身高:"), "toString() 应包含身高字段");
        assertTrue(result.contains("体重:"), "toString() 应包含体重字段");
        assertTrue(result.contains("心率:"), "toString() 应包含心率字段");
        assertTrue(result.contains("血压:"), "toString() 应包含血压字段");
        assertTrue(result.contains("睡眠:"), "toString() 应包含睡眠字段");
        assertTrue(result.contains("步数:"), "toString() 应包含步数字段");
        assertTrue(result.contains("备注:"), "toString() 应包含备注字段");
    }

    @Test
    void testToFileFormatFieldOrder() {
        LocalDate recordDate = LocalDate.of(2024, 6, 25);
        HealthRecord record = new HealthRecord(recordDate, 70.0, 175.0, 75, 120, 80, 8, 10000, "测试");

        String result = record.toFileFormat();
        String[] parts = result.split(" \\| ");

        assertEquals(9, parts.length, "toFileFormat() 应包含9个字段");
        assertEquals("2024-06-25", parts[0], "第一个字段应为日期");
        assertEquals("70.0", parts[1], "第二个字段应为体重");
        assertEquals("175.0", parts[2], "第三个字段应为身高");
        assertEquals("75", parts[3], "第四个字段应为心率");
        assertEquals("120", parts[4], "第五个字段应为高压");
        assertEquals("80", parts[5], "第六个字段应为低压");
        assertEquals("8", parts[6], "第七个字段应为睡眠时间");
        assertEquals("10000", parts[7], "第八个字段应为步数");
        assertEquals("测试", parts[8], "第九个字段应为备注");
    }
} 