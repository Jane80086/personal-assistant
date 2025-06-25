package com.example.life;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;

public class LifeRecordTest {
    @Test
    void testConstructorAndGetters(){
        String title = "我的第一篇日记";
        String content = "今天天气真好，去公园散步了。";
        String category = "日常";
        String mood = "😊 开心";

        LifeRecord record = new LifeRecord(title, content, category, mood);

        assertEquals(title, record.getTitle(), "标题应该匹配构造函数参数");
        assertEquals(content, record.getContent(), "内容应该匹配构造函数参数");
        assertEquals(category, record.getCategory(), "分类应该匹配构造函数参数");
        assertEquals(mood, record.getMood(), "心情应该匹配构造函数参数");
        assertNotNull(record.getTimestamp(), "时间戳不应为空，应在构造时自动生成");
    }

    @Test
    void testSetters(){
        LifeRecord record = new LifeRecord("旧标题", "旧内容", "旧分类", "旧心情");

        String newTitle = "新标题";
        String newContent = "新内容，记录了新的事件。";
        String newCategory = "工作";
        String newMood = "😤 生气";
        LocalDateTime newTimestamp = LocalDateTime.of(2023, 1, 1, 10, 30, 0);

        record.setTitle(newTitle);
        record.setContent(newContent);
        record.setCategory(newCategory);
        record.setMood(newMood);
        record.setTimestamp(newTimestamp);

        assertEquals(newTitle, record.getTitle(), "标题应该被更新");
        assertEquals(newContent, record.getContent(), "内容应该被更新");
        assertEquals(newCategory, record.getCategory(), "分类应该被更新");
        assertEquals(newMood, record.getMood(), "心情应该被更新");
        assertEquals(newTimestamp, record.getTimestamp(), "时间戳应该被更新");
    }

    @Test
    void testToStringFormat(){
        String title = "测试ToString";
        String content = "这是一段用于测试toString方法输出格式的内容。";
        String category = "学习";
        String mood = "🤔 思考";

        LifeRecord record = new LifeRecord(title, content, category, mood);

        // 为了测试 toString，设置一个固定的时间戳，以便预期输出确定
        LocalDateTime fixedTimestamp = LocalDateTime.of(2024, 6, 25, 14, 0, 0);
        record.setTimestamp(fixedTimestamp);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String expectedTimestampFormatted = fixedTimestamp.format(formatter);

        String expectedToString = "----------------------------------------\n" +
                "标题: " + title + "\n" +
                "分类: " + category + "\n" +
                "心情: " + mood + "\n" +
                "时间戳: " + expectedTimestampFormatted + "\n" +
                "内容:\n" + content + "\n" +
                "----------------------------------------";

        assertEquals(expectedToString, record.toString(), "toString() 输出格式应与预期一致");
    }

    @Test
    void testToFileFormat(){
        String title = "测试ToFile";
        String content = "这是一段用于测试toFileFormat方法输出格式的内容。";
        String category = "爱好";
        String mood = "😍 恋爱";

        LifeRecord record = new LifeRecord(title, content, category, mood);

        LocalDateTime fixedTimestamp = LocalDateTime.of(2025, 1, 1, 9, 0, 0);
        record.setTimestamp(fixedTimestamp);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String expectedTimestampFormatted = fixedTimestamp.format(formatter);

        String expectedToFileFormat = expectedTimestampFormatted + " | " +
                category + " | " +
                mood + " | " +
                title + " | " +
                content;

        assertEquals(expectedToFileFormat, record.toFileFormat(), "toFileFormat() 输出格式应与预期一致");
    }

    // 测试构造函数处理空字符串和null字符串
    @Test
    void testConstructorWithEmptyAndNullStrings(){
        // 测试空字符串
        LifeRecord emptyRecord = new LifeRecord("", "", "", "");
        assertEquals("", emptyRecord.getTitle(), "空标题应被正确设置");
        assertEquals("", emptyRecord.getContent(), "空内容应被正确设置");
        assertEquals("", emptyRecord.getCategory(), "空分类应被正确设置");
        assertEquals("", emptyRecord.getMood(), "空心情应被正确设置");
        assertNotNull(emptyRecord.getTimestamp(), "空字符串构造时时间戳不应为空");

        // 测试 null 字符串
        LifeRecord nullRecord = new LifeRecord(null, null, null, null);
        assertNull(nullRecord.getTitle(), "null 标题应被正确设置");
        assertNull(nullRecord.getContent(), "null 内容应被正确设置");
        assertNull(nullRecord.getCategory(), "null 分类应被正确设置");
        assertNull(nullRecord.getMood(), "null 心情应被正确设置");
        assertNotNull(nullRecord.getTimestamp(), "null 字符串构造时时间戳不应为空");
    }

    // 测试时间戳的自动生成
    @Test
    void testTimestampAutoGeneration() throws InterruptedException {
        LocalDateTime beforeCreation = LocalDateTime.now();
        Thread.sleep(10); // 确保有时间差
        LifeRecord record = new LifeRecord("标题", "内容", "分类", "心情");
        Thread.sleep(10);
        LocalDateTime afterCreation = LocalDateTime.now();

        assertTrue(record.getTimestamp().isAfter(beforeCreation), "时间戳应该在创建之后");
        assertTrue(record.getTimestamp().isBefore(afterCreation), "时间戳应该在当前时间之前");
    }

    // 测试toFileFormat方法处理null值
    @Test
    void testFormatMethodsWithNullValues() {
        LifeRecord record = new LifeRecord(null, null, null, null);
        LocalDateTime fixedTimestamp = LocalDateTime.of(2024, 1, 1, 12, 0, 0);
        record.setTimestamp(fixedTimestamp);

        // --- 测试 toString() ---
        String toStringResult = record.toString();
        assertNotNull(toStringResult, "toString() 结果不应为 null");
        assertTrue(toStringResult.contains("标题: null"), "toString() 应包含 '标题: null'");
        assertTrue(toStringResult.contains("分类: null"), "toString() 应包含 '分类: null'");
        assertTrue(toStringResult.contains("心情: null"), "toString() 应包含 '心情: null'");
        assertTrue(toStringResult.contains("内容:\nnull"), "toString() 应包含 '内容:\nnull'");
        assertTrue(toStringResult.contains(fixedTimestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))),
                "toString() 应包含正确格式化的时间戳");

        String toFileFormatResult = record.toFileFormat();
        assertNotNull(toFileFormatResult, "toFileFormat() 结果不应为 null");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String expectedFileFormat = fixedTimestamp.format(formatter) + " | " +
                "null" + " | " + // category
                "null" + " | " + // mood
                "null" + " | " + // title
                "null";          // content

        assertEquals(expectedFileFormat, toFileFormatResult,
                "toFileFormat() 应正确格式化包含 null 值的记录为 'null' 字符串");
    }

    // 测试包含特殊字符的字段
    @Test
    void testSpecialCharacters() {
        String title = "标题|包含|分隔符";
        String content = "内容包含换行符\n和制表符\t";
        String category = "分类 | 特殊";
        String mood = "😊|😢 复杂心情";

        LifeRecord record = new LifeRecord(title, content, category, mood);

        assertEquals(title, record.getTitle(), "特殊字符标题应正确保存");
        assertEquals(content, record.getContent(), "特殊字符内容应正确保存");
        assertEquals(category, record.getCategory(), "特殊字符分类应正确保存");
        assertEquals(mood, record.getMood(), "特殊字符心情应正确保存");
    }

    // 测试极长字符串
    @Test
    void testVeryLongStrings() {
        String longText = "这是一个很长的字符串".repeat(1000);

        LifeRecord record = new LifeRecord(longText, longText, longText, longText);

        assertEquals(longText, record.getTitle(), "极长标题应正确保存");
        assertEquals(longText, record.getContent(), "极长内容应正确保存");
        assertEquals(longText, record.getCategory(), "极长分类应正确保存");
        assertEquals(longText, record.getMood(), "极长心情应正确保存");
    }

    // 测试toString方法的完整性
    @Test
    void testToStringContainsAllFields() {
        String title = "完整测试";
        String content = "测试内容完整性";
        String category = "测试分类";
        String mood = "测试心情";

        LifeRecord record = new LifeRecord(title, content, category, mood);
        String result = record.toString();

        assertTrue(result.contains(title), "toString应包含标题");
        assertTrue(result.contains(content), "toString应包含内容");
        assertTrue(result.contains(category), "toString应包含分类");
        assertTrue(result.contains(mood), "toString应包含心情");
        assertTrue(result.contains("----------------------------------------"), "toString应包含分隔线");
    }

    // 验证toString方法的输出结构
    @Test
    void testToStringStructure() {
        LifeRecord record = new LifeRecord("测试标题", "测试内容", "测试分类", "测试心情");
        String result = record.toString();

        String[] lines = result.split("\n");
        assertEquals(8, lines.length, "toString输出应有8行");
        assertEquals("----------------------------------------", lines[0], "第一行应为分隔线");
        assertTrue(lines[1].startsWith("标题: "), "第二行应为标题");
        assertTrue(lines[2].startsWith("分类: "), "第三行应为分类");
        assertTrue(lines[3].startsWith("心情: "), "第四行应为心情");
        assertTrue(lines[4].startsWith("时间戳: "), "第五行应为时间戳");
        assertEquals("内容:", lines[5], "第六行应为内容标识");
        assertEquals("测试内容", lines[6], "第七行应为具体内容");
        assertEquals("----------------------------------------", lines[7], "第八行应为分隔线");
    }

    // 验证toFileFormat方法的分隔符数量
    @Test
    void testToFileFormatSeparatorCount() {
        LifeRecord record = new LifeRecord("标题", "内容", "分类", "心情");
        String result = record.toFileFormat();

        long separatorCount = result.chars().filter(ch -> ch == '|').count();
        assertEquals(4, separatorCount, "toFileFormat应包含4个分隔符");
    }

    // 验证空白字符串处理
    @Test
    void testWhitespaceStrings() {
        String whitespaceTitle = "   ";
        String tabContent = "\t\t\t";
        String newlineCategory = "\n\n";
        String mixedMood = " \t\n ";

        LifeRecord record = new LifeRecord(whitespaceTitle, tabContent, newlineCategory, mixedMood);

        assertEquals(whitespaceTitle, record.getTitle(), "空白字符标题应正确保存");
        assertEquals(tabContent, record.getContent(), "制表符内容应正确保存");
        assertEquals(newlineCategory, record.getCategory(), "换行符分类应正确保存");
        assertEquals(mixedMood, record.getMood(), "混合空白字符心情应正确保存");
    }

    // 验证Unicode字符处理
    @Test
    void testUnicodeCharacters() {
        String unicodeTitle = "测试 🌟 ∆ ∑ ∏ ∫ ∞";
        String unicodeContent = "包含各种符号：① ② ③ ④ ⑤ ⅰ ⅱ ⅲ ⅳ ⅴ";
        String unicodeCategory = "✓ ✗ ✈ ♠ ♣ ♥ ♦";
        String unicodeMood = "😀😃😄😁😆😅😂🤣";

        LifeRecord record = new LifeRecord(unicodeTitle, unicodeContent, unicodeCategory, unicodeMood);

        assertEquals(unicodeTitle, record.getTitle(), "Unicode标题应正确保存");
        assertEquals(unicodeContent, record.getContent(), "Unicode内容应正确保存");
        assertEquals(unicodeCategory, record.getCategory(), "Unicode分类应正确保存");
        assertEquals(unicodeMood, record.getMood(), "Unicode心情应正确保存");
    }

    // 验证时间戳不为null时不会被构造函数覆盖
    @Test
    void testTimestampNotOverridden() {
        LocalDateTime customTime = LocalDateTime.of(2020, 1, 1, 0, 0, 0);
        LifeRecord record = new LifeRecord("标题", "内容", "分类", "心情");
        record.setTimestamp(customTime);

        // 创建另一个记录不应影响第一个记录的时间戳
        LifeRecord anotherRecord = new LifeRecord("另一个标题", "另一个内容", "另一个分类", "另一个心情");

        assertEquals(customTime, record.getTimestamp(), "设置的时间戳不应被其他操作覆盖");
        assertNotEquals(customTime, anotherRecord.getTimestamp(), "新记录应有不同的时间戳");
    }

    // 验证相同内容的记录创建
    @Test
    void testIdenticalContentRecords() {
        String title = "相同标题";
        String content = "相同内容";
        String category = "相同分类";
        String mood = "相同心情";

        LifeRecord record1 = new LifeRecord(title, content, category, mood);
        LifeRecord record2 = new LifeRecord(title, content, category, mood);

        assertEquals(record1.getTitle(), record2.getTitle(), "相同内容的记录标题应相等");
        assertEquals(record1.getContent(), record2.getContent(), "相同内容的记录内容应相等");
        assertEquals(record1.getCategory(), record2.getCategory(), "相同内容的记录分类应相等");
        assertEquals(record1.getMood(), record2.getMood(), "相同内容的记录心情应相等");

        // 但时间戳应不同（除非在同一毫秒内创建）
        assertNotEquals(record1, record2, "即使内容相同，对象引用也应不同");
    }

    // 验证数字字符串处理
    @Test
    void testNumericStrings() {
        String numericTitle = "12345";
        String numericContent = "67890";
        String numericCategory = "0.123";
        String numericMood = "-456.789";

        LifeRecord record = new LifeRecord(numericTitle, numericContent, numericCategory, numericMood);

        assertEquals(numericTitle, record.getTitle(), "数字字符串标题应正确保存");
        assertEquals(numericContent, record.getContent(), "数字字符串内容应正确保存");
        assertEquals(numericCategory, record.getCategory(), "数字字符串分类应正确保存");
        assertEquals(numericMood, record.getMood(), "数字字符串心情应正确保存");
    }

    // 测试设置未来时间戳
    // 测试设置过去的极端时间戳（如1970年）
    @Test
    void testTimestampBoundaryValues() {
        LifeRecord record = new LifeRecord("时间戳测试", "测试时间戳的边界值", "测试", "测试心情");

        // 测试设置一个未来的时间戳 (例如，现在的一年以后)
        LocalDateTime futureTimestamp = LocalDateTime.now().plusYears(1);
        record.setTimestamp(futureTimestamp);
        assertEquals(futureTimestamp.withNano(0), record.getTimestamp().withNano(0),
                "未来时间戳应该被正确设置和获取 (忽略纳秒差异)");

        // 验证 toFileFormat() 和 toString() 对未来时间戳的格式化
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String expectedFutureFormatted = futureTimestamp.format(formatter);
        assertTrue(record.toFileFormat().startsWith(expectedFutureFormatted),
                "toFileFormat() 应正确格式化未来时间戳");
        assertTrue(record.toString().contains(expectedFutureFormatted),
                "toString() 应正确包含未来时间戳");


        // 测试设置一个过去的极端时间戳 (例如，Unix Epoch 开始时间 1970-01-01 00:00:00)
        LocalDateTime epochTimestamp = LocalDateTime.of(1970, 1, 1, 0, 0, 0);
        record.setTimestamp(epochTimestamp);
        assertEquals(epochTimestamp, record.getTimestamp(), "过去极端时间戳应该被正确设置和获取");

        // 验证 toFileFormat() 和 toString() 对过去时间戳的格式化
        String expectedEpochFormatted = epochTimestamp.format(formatter);
        assertTrue(record.toFileFormat().startsWith(expectedEpochFormatted),
                "toFileFormat() 应正确格式化过去时间戳");
        assertTrue(record.toString().contains(expectedEpochFormatted),
                "toString() 应正确包含过去时间戳");

        // 测试一个非常晚的时间戳（例如，公元9999年）
        LocalDateTime farFutureTimestamp = LocalDateTime.of(9999, 12, 31, 23, 59, 59);
        record.setTimestamp(farFutureTimestamp);
        assertEquals(farFutureTimestamp, record.getTimestamp(), "非常远未来的时间戳应该被正确设置和获取");
        String expectedFarFutureFormatted = farFutureTimestamp.format(formatter);
        assertTrue(record.toFileFormat().startsWith(expectedFarFutureFormatted),
                "toFileFormat() 应正确格式化非常远未来的时间戳");
        assertTrue(record.toString().contains(expectedFarFutureFormatted),
                "toString() 应正确包含非常远未来的时间戳");
    }

    // 测试包含 " | " 的内容是否会破坏文件格式
    @Test
    void testSeparatorInjectionSecurity() {
        // 定义一个包含分隔符的标题、内容、分类和心情
        String injectedTitle = "这是一个 | 带有 | 内部分隔符 | 的标题";
        String injectedContent = "这篇内容 | 包含了 | 多个 | 定界符 | 和一些 | 额外的 | 信息";
        String injectedCategory = "日常 | 工作 | 学习";
        String injectedMood = "😊 开心 | 😢 难过 | 🤔 思考";

        // 创建 LifeRecord 实例
        LifeRecord record = new LifeRecord(injectedTitle, injectedContent, injectedCategory, injectedMood);

        // 验证 Getter 方法是否返回了原始的、未被修改的字符串
        assertEquals(injectedTitle, record.getTitle(), "标题中的分隔符不应影响其存储和获取");
        assertEquals(injectedContent, record.getContent(), "内容中的分隔符不应影响其存储和获取");
        assertEquals(injectedCategory, record.getCategory(), "分类中的分隔符不应影响其存储和获取");
        assertEquals(injectedMood, record.getMood(), "心情中的分隔符不应影响其存储和获取");

        // 验证 toFileFormat() 方法的输出是否正确包含了这些分隔符，且没有引入额外的问题
        String fileFormat = record.toFileFormat();
        // 关键是验证原始字符串作为子串是否存在
        assertTrue(fileFormat.contains(injectedTitle), "toFileFormat() 应包含完整的标题，即使有分隔符");
        assertTrue(fileFormat.contains(injectedContent), "toFileFormat() 应包含完整的内容，即使有分隔符");
        assertTrue(fileFormat.contains(injectedCategory), "toFileFormat() 应包含完整的分类，即使有分隔符");
        assertTrue(fileFormat.contains(injectedMood), "toFileFormat() 应包含完整的心情，即使有分隔符");

        // 验证 toString() 方法的输出是否正确包含了这些分隔符
        String toStringOutput = record.toString();
        assertTrue(toStringOutput.contains(injectedTitle), "toString() 应包含完整的标题，即使有分隔符");
        assertTrue(toStringOutput.contains(injectedContent), "toString() 应包含完整的内容，即使有分隔符");
        assertTrue(toStringOutput.contains(injectedCategory), "toString() 应包含完整的分类，即使有分隔符");
        assertTrue(toStringOutput.contains(injectedMood), "toString() 应包含完整的心情，即使有分隔符");

        // 进一步验证 toFileFormat 的结构，确保它仍然符合预期的5个部分（时间戳，分类，心情，标题，内容）
        String[] parts = fileFormat.split(" \\| ", -1); // -1 ensures trailing empty strings are not discarded
        assertEquals(5, parts.length, "toFileFormat() 应分隔成5个部分");

        assertEquals(injectedCategory, parts[1], "文件格式的分类部分应匹配原始输入");
        assertEquals(injectedMood, parts[2], "文件格式的心情部分应匹配原始输入");
        assertEquals(injectedTitle, parts[3], "文件格式的标题部分应匹配原始输入");
        assertEquals(injectedContent, parts[4], "文件格式的内容部分应匹配原始输入");
    }
}
