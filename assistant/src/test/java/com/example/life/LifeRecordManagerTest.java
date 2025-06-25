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

    // 测试数据目录创建
    @Test
    void testCreateDataDirectory_alreadyExists() {
        manager = new LifeRecordManager(TEST_DATA_DIR, TEST_FILE_NAME); // 重新创建，模拟目录已存在
        assertTrue(Files.exists(TEST_DATA_PATH), "数据目录仍然应该存在");
        assertTrue(Files.isDirectory(TEST_DATA_PATH), "测试路径仍然应该是一个目录");
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
}