package com.example.life;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class LifeRecordManagerTest {
    private static final String TEST_DATA_DIR = "test_data_records";
    private static final String TEST_FILE_NAME = "test_life_records.txt";
    private static final Path TEST_FILE_PATH = Paths.get(TEST_DATA_DIR, TEST_FILE_NAME);
    private static final Path TEST_DATA_PATH = Paths.get(TEST_DATA_DIR);

    private LifeRecordManager manager;

    // 初始化测试数据
    @BeforeEach
    void setUp() throws IOException {
        cleanupTestDirectory();
        Files.createDirectories(TEST_DATA_PATH);
        manager = new LifeRecordManager(TEST_DATA_DIR, TEST_FILE_NAME);
    }

    // 清理测试数据
    @AfterEach
    void tearDown() throws IOException {
        cleanupTestDirectory();
    }

    // 清理测试数据
    private void cleanupTestDirectory() throws IOException {
        if (Files.exists(TEST_DATA_PATH)) {
            try (var paths = Files.walk(TEST_DATA_PATH)) {
                paths.sorted(Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(file -> {
                            if (!file.delete()) {
                                System.err.println("无法删除文件: " + file.getAbsolutePath());
                            }
                        });
            }
        }
    }

    // 测试数据目录创建
    @Test
    void testCreateDataDirectory_initialCreation() {
        assertTrue(Files.exists(TEST_DATA_PATH), "数据目录应该被创建");
        assertTrue(Files.isDirectory(TEST_DATA_PATH), "测试路径应该是一个目录");
    }

    // 测试添加单条记录并从文件重新加载的功能。
    @Test
    void testAddRecordAndLoadFromFile_singleRecord()  {
        manager.addRecord("我的第一条记录", "这是今天的心情。", "日常", "😊 开心");

        LifeRecordManager newManager = new LifeRecordManager(TEST_DATA_DIR, TEST_FILE_NAME);
        List<LifeRecord> records = newManager.getAllRecords();

        assertNotNull(records, "记录列表不应为空");
        assertEquals(1, records.size(), "应加载一条记录");

        LifeRecord record = records.get(0);
        assertEquals("我的第一条记录", record.getTitle(), "记录标题应匹配");
        assertEquals("这是今天的心情。", record.getContent(), "记录内容应匹配");
        assertEquals("日常", record.getCategory(), "记录分类应匹配");
        assertEquals("😊 开心", record.getMood(), "记录心情应匹配");
        assertNotNull(record.getTimestamp(), "时间戳不应为空");
    }

    // 测试添加多条记录并从文件重新加载的功能。
    @Test
    void testAddRecordAndLoadFromFile_multipleRecords() {
        manager.addRecord("记录1", "内容1", "日常", "开心");
        manager.addRecord("记录2", "内容2", "学习", "思考");
        manager.addRecord("记录3", "内容3", "旅行", "激动");

        LifeRecordManager newManager = new LifeRecordManager(TEST_DATA_DIR, TEST_FILE_NAME);
        List<LifeRecord> records = newManager.getAllRecords();

        assertEquals(3, records.size(), "应加载三条记录");
        assertEquals("记录1", records.get(0).getTitle(), "第一条记录标题应匹配");
        assertEquals("记录2", records.get(1).getTitle(), "第二条记录标题应匹配");
        assertEquals("记录3", records.get(2).getTitle(), "第三条记录标题应匹配");
    }

    // 测试从空数据文件加载记录
    @Test
    void testLoadRecordsFromFile_emptyFile() {
        assertEquals(0, manager.getAllRecords().size(), "空文件加载后记录列表应为空");
    }

    //测试从包含格式错误行的文件中加载记录。
    @Test
    void testLoadRecordsFromFile_malformedLine() throws IOException {
        Files.writeString(TEST_FILE_PATH, "2024-06-25 10:00:00 | 日常 | 😊 开心 | 只有标题\n", StandardCharsets.UTF_8);
        Files.writeString(TEST_FILE_PATH, "2024-06-25 10:01:00 | 日常 | 😊 开心 | 正确标题 | 正确内容\n",
                StandardCharsets.UTF_8, StandardOpenOption.APPEND);

        LifeRecordManager newManager = new LifeRecordManager(TEST_DATA_DIR, TEST_FILE_NAME);
        List<LifeRecord> records = newManager.getAllRecords();

        assertEquals(1, records.size(), "只有一条正确的记录应该被加载");
        assertEquals("正确标题", records.get(0).getTitle(), "正确记录的标题应匹配");
    }

    // 测试按标题搜索记录
    @Test
    void testSearchRecords_byTitle() {
        manager.addRecord("Java编程学习", "今天学习了Java并发。", "学习", "😊 开心");
        manager.addRecord("健身日常", "去健身房锻炼了。", "健康", "😴 疲惫");
        List<LifeRecord> results = manager.searchRecords("Java", null, null, null);
        assertEquals(1, results.size(), "应找到一条匹配标题的记录");
        assertEquals("Java编程学习", results.get(0).getTitle(), "匹配标题的记录标题应正确");
    }

    // 测试按内容搜索记录
    @Test
    void testSearchRecords_byContent() {
        manager.addRecord("Java编程学习", "今天学习了Java并发。", "学习", "😊 开心");
        manager.addRecord("健身日常", "去健身房锻炼了。", "健康", "😴 疲惫");
        List<LifeRecord> results = manager.searchRecords(null, "健身房锻炼", null, null);
        assertEquals(1, results.size(), "应找到一条匹配内容的记录");
        assertEquals("健身日常", results.get(0).getTitle(), "匹配内容的记录标题应正确");
    }

    // 测试按分类搜索记录
    @Test
    void testSearchRecords_byCategory() {
        manager.addRecord("会议记录", "参加了项目例会。", "工作", "😌 冷静");
        manager.addRecord("食谱整理", "整理了周末做菜的食谱。", "日常", "😊 开心");
        List<LifeRecord> results = manager.searchRecords(null, null, "工作", null);
        assertEquals(1, results.size(), "应找到一条匹配分类的记录");
        assertEquals("会议记录", results.get(0).getTitle(), "匹配分类的记录标题应正确");
    }

    // 测试按心情搜索记录
    @Test
    void testSearchRecords_byMood() {
        manager.addRecord("心情低落", "今天感到有点难过。", "日常", "😢 难过");
        manager.addRecord("新项目启动", "新项目启动，感到很激动。", "工作", "😎 激动");
        List<LifeRecord> results = manager.searchRecords(null, null, null, "难过");
        assertEquals(1, results.size(), "应找到一条匹配心情的记录");
        assertEquals("心情低落", results.get(0).getTitle(), "匹配心情的记录标题应正确");
    }

    // 测试多关键词搜索，所有提供的关键词都匹配一条记录的情况。
    @Test
    void testSearchRecords_multipleKeywords_allMatch() {
        manager.addRecord("项目总结", "完成了本周的项目总结报告。", "工作", "😌 冷静");
        List<LifeRecord> results = manager.searchRecords("项目", "报告", "工作", "冷静");
        assertEquals(1, results.size(), "所有关键词都匹配时应找到一条记录");
        assertEquals("项目总结", results.get(0).getTitle(), "所有关键词匹配的记录标题应正确");
    }

    // 测试多关键词搜索，所有提供的关键词都匹配一条记录的情况。
    @Test
    void testSearchRecords_multipleKeywords_partialMatch() {
        manager.addRecord("项目总结", "完成了本周的项目总结报告。", "工作", "😌 冷静");
        manager.addRecord("周末计划", "计划去登山。", "旅行", "😊 开心");
        List<LifeRecord> results = manager.searchRecords("项目", "登山", null, null);
        assertTrue(results.isEmpty(), "部分关键词不匹配时，应返回空列表");
    }

    //测试多关键词搜索，所有提供的关键词都匹配一条记录的情况。
    @Test
    void testSearchRecords_noKeywordsProvided() {
        manager.addRecord("记录1", "内容1", "日常", "开心");
        List<LifeRecord> results = manager.searchRecords(null, null, null, null);
        assertTrue(results.isEmpty(), "未提供任何关键词时应返回空列表");
    }

    //测试在提供空字符串或空白字符串作为关键词的情况下搜索功能。
    @Test
    void testSearchRecords_emptyKeywordsProvided() {
        manager.addRecord("记录1", "内容1", "日常", "开心");
        List<LifeRecord> results = manager.searchRecords("", " ", " \t", null);
        assertTrue(results.isEmpty(), "提供空关键词时应返回空列表");
    }

    //测试在没有记录与提供关键词匹配的情况下搜索功能。
    @Test
    void testSearchRecords_noMatch() {
        manager.addRecord("苹果", "水果", "食物", "开心");
        List<LifeRecord> results = manager.searchRecords("香蕉", null, null, null);
        assertTrue(results.isEmpty(), "没有匹配项时应返回空列表");
    }

    //测试记录编辑功能，验证在有效索引下所有字段的更新。
    @Test
    void testEditRecord_validIndex_allFieldsUpdated() throws  InterruptedException {
        manager.addRecord("原始标题", "原始内容", "日常", "😊 开心");
        Thread.sleep(100); // 确保时间戳有差异

        boolean result = manager.editRecord(1, "新标题", "新内容", "工作", "😤 生气");
        assertTrue(result, "编辑应该成功");

        LifeRecord editedRecord = manager.getRecord(1);
        assertNotNull(editedRecord, "编辑后的记录不应为空");
        assertEquals("新标题", editedRecord.getTitle(), "标题应该被更新");
        assertEquals("新内容", editedRecord.getContent(), "内容应该被更新");
        assertEquals("工作", editedRecord.getCategory(), "分类应该被更新");
        assertEquals("😤 生气", editedRecord.getMood(), "心情应该被更新");
        assertTrue(editedRecord.getTimestamp().isAfter(LocalDateTime.now().minusSeconds(5)), "时间戳应该被更新");

        LifeRecordManager newManager = new LifeRecordManager(TEST_DATA_DIR, TEST_FILE_NAME);
        LifeRecord loadedRecord = newManager.getRecord(1);
        assertEquals("新标题", loadedRecord.getTitle(), "从文件加载的标题应匹配新标题");
        assertEquals("新内容", loadedRecord.getContent(), "从文件加载的内容应匹配新内容");
        assertEquals("工作", loadedRecord.getCategory(), "从文件加载的分类应匹配新分类");
        assertEquals("😤 生气", loadedRecord.getMood(), "从文件加载的心情应匹配新心情");
    }

    //测试记录编辑功能，验证在有效索引下，即使内容字段没有显式更改
    @Test
    void testEditRecord_validIndex_noContentChanges() throws InterruptedException {
        manager.addRecord("原始标题", "原始内容", "日常", "😊 开心");
        LifeRecord originalRecord = manager.getRecord(1);
        LocalDateTime originalTimestamp = originalRecord.getTimestamp();

        Thread.sleep(10); // 10毫秒延迟

        boolean result = manager.editRecord(1, originalRecord.getTitle(), originalRecord.getContent(),
                originalRecord.getCategory(), originalRecord.getMood());
        assertTrue(result, "即使值相同，编辑也应该成功");

        LifeRecord editedRecord = manager.getRecord(1);
        assertEquals("原始标题", editedRecord.getTitle(), "标题应保持不变");
        assertTrue(editedRecord.getTimestamp().isAfter(originalTimestamp), "时间戳应该被更新，即使内容没有改变");

        LifeRecordManager newManager = new LifeRecordManager(TEST_DATA_DIR, TEST_FILE_NAME);
        LifeRecord loadedRecord = newManager.getRecord(1);
        assertEquals("原始标题", loadedRecord.getTitle(), "从文件加载的标题应与原始标题相同");
        assertTrue(loadedRecord.getTimestamp().isAfter(originalTimestamp), "从文件加载的记录时间戳应该被更新");
    }

    //测试使用无效索引（零或负数）编辑记录。
    @Test
    void testEditRecord_invalidIndex_belowZero() {
        manager.addRecord("记录", "内容", "分类", "心情");
        boolean result = manager.editRecord(0, "新", "新", "新", "新");
        assertFalse(result, "非法索引 (0) 应该导致编辑失败");
        assertEquals(1, manager.getAllRecords().size(), "记录数量不应改变");
    }

    //测试使用无效索引（超出列表大小）编辑记录。
    @Test
    void testEditRecord_invalidIndex_aboveSize() {
        manager.addRecord("记录", "内容", "分类", "心情");
        boolean result = manager.editRecord(2, "新", "新", "新", "新");
        assertFalse(result, "非法索引 (超出范围) 应该导致编辑失败");
        assertEquals(1, manager.getAllRecords().size(), "记录数量不应改变");
    }

    //测试使用空列表进行编辑
    @Test
    void testEditRecord_emptyList() {
        boolean result = manager.editRecord(1, "新", "新", "新", "新");
        assertFalse(result, "空列表时编辑任何索引都应失败");
    }

    //测试记录删除功能，验证删除中间一条记录的行为。
    @Test
    void testDeleteRecord_validIndex_middle()  {
        manager.addRecord("记录1", "内容1", "日常", "开心");
        manager.addRecord("记录2", "内容2", "学习", "思考");
        manager.addRecord("记录3", "内容3", "旅行", "激动");

        manager.deleteRecord(2);

        LifeRecordManager newManager = new LifeRecordManager(TEST_DATA_DIR, TEST_FILE_NAME);
        List<LifeRecord> records = newManager.getAllRecords();

        assertEquals(2, records.size(), "删除后应该只剩两条记录");
        assertEquals("记录1", records.get(0).getTitle(), "第一条记录应不变");
        assertEquals("记录3", records.get(1).getTitle(), "第三条记录应变为第二条");
    }

    //测试记录删除功能，验证删除第一条记录的行为。
    @Test
    void testDeleteRecord_validIndex_first()  {
        manager.addRecord("记录1", "内容1", "日常", "开心");
        manager.addRecord("记录2", "内容2", "学习", "思考");

        manager.deleteRecord(1);

        LifeRecordManager newManager = new LifeRecordManager(TEST_DATA_DIR, TEST_FILE_NAME);
        List<LifeRecord> records = newManager.getAllRecords();

        assertEquals(1, records.size(), "删除第一条记录后应剩一条");
        assertEquals("记录2", records.get(0).getTitle(), "第二条记录应变为第一条");
    }

    //测试记录删除功能，验证删除最后一条记录的行为。
    @Test
    void testDeleteRecord_validIndex_last()  {
        manager.addRecord("记录1", "内容1", "日常", "开心");
        manager.addRecord("记录2", "内容2", "学习", "思考");

        manager.deleteRecord(2);

        LifeRecordManager newManager = new LifeRecordManager(TEST_DATA_DIR, TEST_FILE_NAME);
        List<LifeRecord> records = newManager.getAllRecords();

        assertEquals(1, records.size(), "删除最后一条记录后应剩一条");
        assertEquals("记录1", records.get(0).getTitle(), "第一条记录应不变");
    }

    //测试使用无效索引（零或负数）删除记录。
    @Test
    void testDeleteRecord_invalidIndex_belowZero() {
        manager.addRecord("记录", "内容", "分类", "心情");
        manager.deleteRecord(0);
        assertEquals(1, manager.getAllRecords().size(), "记录数量不应改变");
    }

    //测试使用无效索引（超出列表大小）删除记录。
    @Test
    void testDeleteRecord_invalidIndex_aboveSize() {
        manager.addRecord("记录", "内容", "分类", "心情");
        manager.deleteRecord(2); // 只有1条记录
        assertEquals(1, manager.getAllRecords().size(), "记录数量不应改变");
    }

    //测试使用空列表进行删除
    @Test
    void testDeleteRecord_emptyList() {
        manager.deleteRecord(1);
        assertEquals(0, manager.getAllRecords().size(), "空列表时删除任何索引都不应改变数量");
    }

    //测试重写文件功能，验证删除所有记录后文件是否为空。
    @Test
    void testRewriteFile_noRecords() throws IOException {
        manager.rewriteFile();
        List<String> lines = Files.readAllLines(TEST_FILE_PATH, StandardCharsets.UTF_8);
        assertTrue(lines.isEmpty(), "没有记录时重写文件，文件应为空");
    }

    //测试重写文件功能，验证添加和删除记录后文件是否正确
    @Test
    void testRewriteFile_withRecords() throws IOException {
        manager.addRecord("记录A", "内容A", "日常", "开心");
        manager.addRecord("记录B", "内容B", "学习", "思考");

        List<String> linesAfterAdd = Files.readAllLines(TEST_FILE_PATH, StandardCharsets.UTF_8);
        assertFalse(linesAfterAdd.isEmpty(), "添加记录后文件不应为空");
        assertEquals(2, linesAfterAdd.size(), "添加两条记录后文件应有两行");

        assertEquals(2, manager.getAllRecords().size(), "管理器内存中应该有2条记录");

        assertFalse(Files.readAllLines(TEST_FILE_PATH).isEmpty(), "文件在添加记录后不应为空");

        manager.deleteRecord(1);

        List<String> lines = Files.readAllLines(TEST_FILE_PATH, StandardCharsets.UTF_8);

        assertEquals(1, lines.size(), "删除一条记录后，文件应该只剩一条记录");
        assertTrue(lines.get(0).contains("记录B"), "文件中应该包含记录B");

        manager.deleteRecord(1);
        lines = Files.readAllLines(TEST_FILE_PATH, StandardCharsets.UTF_8);
        assertTrue(lines.isEmpty(), "清空所有记录后，文件应该为空");

        manager.addRecord("新记录1", "新内容1", "健康", "平静");
        manager.addRecord("新记录2", "新内容2", "朋友", "激动");

        LifeRecordManager newManager = new LifeRecordManager(TEST_DATA_DIR, TEST_FILE_NAME);
        List<LifeRecord> loadedRecords = newManager.getAllRecords();
        assertEquals(2, loadedRecords.size(), "重写后应有两条记录");
        assertEquals("新记录1", loadedRecords.get(0).getTitle(), "重写后的第一条记录标题应正确");
        assertEquals("新记录2", loadedRecords.get(1).getTitle(), "重写后的第二条记录标题应正确");
    }

    //测试获取记录功能，验证获取指定索引的记录。
    @Test
    void testGetRecord_validIndex() {
        manager.addRecord("记录1", "内容1", "日常", "开心");
        manager.addRecord("记录2", "内容2", "学习", "思考");
        LifeRecord record = manager.getRecord(1);
        assertNotNull(record, "获取的记录不应为空");
        assertEquals("记录1", record.getTitle(), "获取的记录标题应匹配");
    }

    //测试获取记录功能，验证使用无效索引获取记录。
    @Test
    void testGetRecord_invalidIndex() {
        manager.addRecord("记录1", "内容1", "日常", "开心");
        assertNull(manager.getRecord(0), "索引0是无效的，应返回null");
        assertNull(manager.getRecord(2), "索引超出范围，应返回null");
    }

    //测试当管理器为空时检索所有记录。
    @Test
    void testGetAllRecords_empty() {
        assertTrue(manager.getAllRecords().isEmpty(), "空管理器应返回空记录列表");
    }

    //测试当管理器包含记录时检索所有记录。
    @Test
    void testGetAllRecords_withRecords() {
        manager.addRecord("记录1", "内容1", "日常", "开心");
        manager.addRecord("记录2", "内容2", "学习", "思考");
        List<LifeRecord> records = manager.getAllRecords();
        assertEquals(2, records.size(), "应返回两条记录");
    }

    //测试获取分类功能
    @Test
    void testGetCategories() {
        List<String> categories = manager.getCategories();
        assertFalse(categories.isEmpty(), "分类列表不应为空");
        assertTrue(categories.contains("日常"), "分类列表应包含'日常'");
        assertTrue(categories.contains("工作"), "分类列表应包含'工作'");
        assertEquals(10, categories.size(), "验证默认分类数量应为10");
    }

    //测试获取心情功能
    @Test
    void testGetMoods() {
        List<String> moods = manager.getMoods();
        assertFalse(moods.isEmpty(), "心情列表不应为空");
        assertTrue(moods.contains("😊 开心"), "心情列表应包含'😊 开心'");
        assertTrue(moods.contains("😢 难过"), "心情列表应包含'😢 难过'");
        assertEquals(10, moods.size(), "验证默认心情数量应为10");
    }

    // 验证默认构造函数
    @Test
    void testDefaultConstructor() {
        LifeRecordManager defaultManager = new LifeRecordManager();

        // 不检查是否为空，因为可能从现有文件加载了记录
        assertNotNull(defaultManager.getAllRecords(), "记录列表不应为null");

        List<String> categories = defaultManager.getCategories();
        List<String> moods = defaultManager.getMoods();

        assertNotNull(categories, "分类列表不应为null");
        assertNotNull(moods, "心情列表不应为null");
        assertEquals(10, categories.size(), "默认分类数量应为10");
        assertEquals(10, moods.size(), "默认心情数量应为10");
    }

    // 验证自定义目录和文件名构造函数
    @Test
    void testCustomConstructor() throws IOException {
        String customDir = "custom_test_dir";
        String customFile = "custom_test_file.txt";

        try {
            LifeRecordManager customManager = new LifeRecordManager(customDir, customFile);
            assertTrue(Files.exists(Paths.get(customDir)), "自定义目录应该被创建");

            customManager.addRecord("测试记录", "测试内容", "测试分类", "测试心情");
            assertEquals(1, customManager.getAllRecords().size(), "自定义管理器应能正常添加记录");

            Path customFilePath = Paths.get(customDir, customFile);
            assertTrue(Files.exists(customFilePath), "自定义文件应该被创建");

            List<String> lines = Files.readAllLines(customFilePath, StandardCharsets.UTF_8);
            assertEquals(1, lines.size(), "自定义文件应包含一条记录");

        } finally {
            // 清理自定义测试目录
            Path customPath = Paths.get(customDir);
            if (Files.exists(customPath)) {
                try (var paths = Files.walk(customPath)) {
                    paths.sorted(Comparator.reverseOrder())
                            .map(Path::toFile)
                            .forEach(file -> {
                                if (!file.delete()) {
                                    System.err.println("无法删除文件: " + file.getAbsolutePath());
                                }
                            });
                }
            }
        }
    }

    // 验证文件内容包含特殊字符时的处理
    @Test
    void testLoadRecordsFromFile_withSpecialCharacters() throws IOException {
        String specialLine = "2024-06-25 10:00:00 | 测试分类|特殊 | 😊|😢 复杂心情 | 标题|含|分隔符 | 内容包含换行符\\n和制表符\\t";
        Files.writeString(TEST_FILE_PATH, specialLine + "\n", StandardCharsets.UTF_8);

        LifeRecordManager newManager = new LifeRecordManager(TEST_DATA_DIR, TEST_FILE_NAME);
        List<LifeRecord> records = newManager.getAllRecords();

        assertEquals(1, records.size(), "特殊字符记录应被正确加载");
        LifeRecord record = records.get(0);
        assertEquals("标题|含|分隔符", record.getTitle(), "包含分隔符的标题应正确处理");
        assertEquals("内容包含换行符\\n和制表符\\t", record.getContent(), "包含特殊字符的内容应正确处理");
    }

    // 验证文件包含空行时的处理
    @Test
    void testLoadRecordsFromFile_withEmptyLines() throws IOException {
        String content = "2024-06-25 10:00:00 | 日常 | 😊 开心 | 标题1 | 内容1\n" +
                "\n" +  // 空行
                "   \n" +  // 空白行
                "2024-06-25 10:01:00 | 学习 | 🤔 思考 | 标题2 | 内容2\n" +
                "\n";  // 末尾空行

        Files.writeString(TEST_FILE_PATH, content, StandardCharsets.UTF_8);

        LifeRecordManager newManager = new LifeRecordManager(TEST_DATA_DIR, TEST_FILE_NAME);
        List<LifeRecord> records = newManager.getAllRecords();

        assertEquals(2, records.size(), "空行应被忽略，只加载有效记录");
        assertEquals("标题1", records.get(0).getTitle(), "第一条记录应正确");
        assertEquals("标题2", records.get(1).getTitle(), "第二条记录应正确");
    }

    // 验证搜索功能的大小写不敏感性
    @Test
    void testSearchRecords_caseInsensitive() {
        manager.addRecord("Java编程", "学习JAVA语言", "STUDY", "HAPPY");

        List<LifeRecord> results1 = manager.searchRecords("java", null, null, null);
        List<LifeRecord> results2 = manager.searchRecords(null, "java", null, null);
        List<LifeRecord> results3 = manager.searchRecords(null, null, "study", null);
        List<LifeRecord> results4 = manager.searchRecords(null, null, null, "happy");

        assertEquals(1, results1.size(), "标题搜索应不区分大小写");
        assertEquals(1, results2.size(), "内容搜索应不区分大小写");
        assertEquals(1, results3.size(), "分类搜索应不区分大小写");
        assertEquals(1, results4.size(), "心情搜索应不区分大小写");
    }

    // 验证搜索功能的部分匹配
    @Test
    void testSearchRecords_partialMatch() {
        manager.addRecord("Java编程学习笔记", "今天学习了Java并发编程", "编程学习", "😊 开心学习");

        List<LifeRecord> results1 = manager.searchRecords("Java编程", null, null, null);
        List<LifeRecord> results2 = manager.searchRecords(null, "并发", null, null);
        List<LifeRecord> results3 = manager.searchRecords(null, null, "编程", null);
        List<LifeRecord> results4 = manager.searchRecords(null, null, null, "开心");

        assertEquals(1, results1.size(), "标题部分匹配应成功");
        assertEquals(1, results2.size(), "内容部分匹配应成功");
        assertEquals(1, results3.size(), "分类部分匹配应成功");
        assertEquals(1, results4.size(), "心情部分匹配应成功");
    }

    // 验证编辑记录时null值处理
    @Test
    void testEditRecord_withNullValues() {
        manager.addRecord("原始标题", "原始内容", "原始分类", "原始心情");

        boolean result = manager.editRecord(1, null, null, null, null);
        assertTrue(result, "编辑为null值应该成功");

        LifeRecord editedRecord = manager.getRecord(1);
        assertNull(editedRecord.getTitle(), "标题应被设置为null");
        assertNull(editedRecord.getContent(), "内容应被设置为null");
        assertNull(editedRecord.getCategory(), "分类应被设置为null");
        assertNull(editedRecord.getMood(), "心情应被设置为null");
    }

    // 验证编辑记录时空字符串处理
    @Test
    void testEditRecord_withEmptyStrings() {
        manager.addRecord("原始标题", "原始内容", "原始分类", "原始心情");

        boolean result = manager.editRecord(1, "", "", "", "");
        assertTrue(result, "编辑为空字符串应该成功");

        LifeRecord editedRecord = manager.getRecord(1);
        assertEquals("", editedRecord.getTitle(), "标题应被设置为空字符串");
        assertEquals("", editedRecord.getContent(), "内容应被设置为空字符串");
        assertEquals("", editedRecord.getCategory(), "分类应被设置为空字符串");
        assertEquals("", editedRecord.getMood(), "心情应被设置为空字符串");
    }

    // 验证负数索引的处理
    @Test
    void testNegativeIndexHandling() {
        manager.addRecord("记录", "内容", "分类", "心情");

        assertNull(manager.getRecord(-1), "负索引获取记录应返回null");
        assertFalse(manager.editRecord(-1, "新", "新", "新", "新"), "负索引编辑应失败");

        int originalSize = manager.getAllRecords().size();
        manager.deleteRecord(-1);
        assertEquals(originalSize, manager.getAllRecords().size(), "负索引删除应不影响记录数量");
    }

    // 验证大量记录的处理
    @Test
    void testLargeNumberOfRecords() {
        int recordCount = 1000;

        // 添加大量记录
        for (int i = 1; i <= recordCount; i++) {
            manager.addRecord("记录" + i, "内容" + i, "分类" + (i % 5), "心情" + (i % 3));
        }

        assertEquals(recordCount, manager.getAllRecords().size(), "应正确添加大量记录");

        // 测试搜索性能
        List<LifeRecord> results = manager.searchRecords("记录500", null, null, null);
        assertEquals(1, results.size(), "大量记录中搜索应正确");
        assertEquals("记录500", results.get(0).getTitle(), "搜索结果应正确");

        // 测试编辑中间记录
        assertTrue(manager.editRecord(500, "编辑后的记录500", "编辑后的内容", "编辑后的分类", "编辑后的心情"),
                "大量记录中编辑应成功");

        // 测试删除中间记录
        manager.deleteRecord(500);
        assertEquals(recordCount - 1, manager.getAllRecords().size(), "删除后记录数应正确");
    }

    // 验证getMoods和getCategories返回的列表是否可修改
    @Test
    void testImmutabilityOfReturnedLists() {
        List<String> categories = manager.getCategories();
        int originalCategoriesSize = categories.size();

        try {
            categories.add("新分类");
            fail("getCategories() 返回的列表不应允许外部修改");
        } catch (UnsupportedOperationException e) {
            System.out.println("成功捕获到 getCategories() 列表的修改尝试，列表是不可修改的。");
        } catch (Exception e) {
            fail("尝试修改 getCategories() 返回的列表时发生了意外异常: " + e.getMessage());
        }

        List<String> newCategories = manager.getCategories();
        assertEquals(originalCategoriesSize, newCategories.size(), "原始分类列表不应被外部修改影响");
        assertFalse(newCategories.contains("新分类"), "新添加的分类不应出现在原始列表中");

        List<String> moods = manager.getMoods();
        int originalMoodsSize = moods.size();

        try {
            moods.add("😈 新心情");
            fail("getMoods() 返回的列表不应允许外部修改");
        } catch (UnsupportedOperationException e) {
            System.out.println("成功捕获到 getMoods() 列表的修改尝试，列表是不可修改的。");
        } catch (Exception e) {
            fail("尝试修改 getMoods() 返回的列表时发生了意外异常: " + e.getMessage());
        }

        List<String> newMoods = manager.getMoods();
        assertEquals(originalMoodsSize, newMoods.size(), "原始心情列表不应被外部修改影响");
        assertFalse(newMoods.contains("😈 新心情"), "新添加的心情不应出现在原始列表中");

        manager.addRecord("测试记录", "测试内容", "测试分类", "测试心情"); // Add a record to test getAllRecords

        List<LifeRecord> records = manager.getAllRecords();
        int originalRecordsSize = records.size();

        try {
            records.clear(); // This will throw UnsupportedOperationException if it's an unmodifiable list
            fail("getAllRecords() 返回的列表不应允许外部修改");
        } catch (UnsupportedOperationException e) {
            // Expected
            System.out.println("成功捕获到 getAllRecords() 列表的修改尝试，列表是不可修改的。");
        } catch (Exception e) {
            fail("尝试修改 getAllRecords() 返回的列表时发生了意外异常: " + e.getMessage());
        }

        List<LifeRecord> newRecords = manager.getAllRecords();
        assertEquals(originalRecordsSize, newRecords.size(), "原始记录列表不应被外部修改影响");
    }

    // 验证getAllRecords返回的列表是否可修改
    @Test
    void testGetAllRecordsImmutability() {
        manager.addRecord("测试记录", "测试内容", "测试分类", "测试心情");

        List<LifeRecord> records = manager.getAllRecords();
        int originalSize = records.size(); // 获取原始大小，用于稍后验证

        // 尝试修改返回的不可修改列表，预期会抛出异常
        assertThrows(UnsupportedOperationException.class, records::clear, "getAllRecords() 返回的列表不应允许外部修改，尝试 clear() 应该抛出 UnsupportedOperationException");

        List<LifeRecord> newRecords = manager.getAllRecords();
        assertEquals(originalSize, newRecords.size(), "原始记录列表在外部修改尝试后不应受到影响");
    }

    // 验证记录添加后的文件内容格式
    @Test
    void testAddRecordFileFormat() throws IOException {
        manager.addRecord("测试标题", "测试内容", "测试分类", "测试心情");

        List<String> lines = Files.readAllLines(TEST_FILE_PATH, StandardCharsets.UTF_8);
        assertEquals(1, lines.size(), "应有一行记录");

        String line = lines.get(0);
        String[] parts = line.split(" \\| ");
        assertEquals(5, parts.length, "记录应包含5个部分");

        // 验证各部分内容
        assertTrue(parts[0].matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}"), "时间戳格式应正确");
        assertEquals("测试分类", parts[1], "分类应匹配");
        assertEquals("测试心情", parts[2], "心情应匹配");
        assertEquals("测试标题", parts[3], "标题应匹配");
        assertEquals("测试内容", parts[4], "内容应匹配");
    }
}