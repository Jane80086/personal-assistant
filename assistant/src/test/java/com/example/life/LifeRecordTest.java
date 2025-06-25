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

    // 测试构造函数处理空字符串
    @Test
    void testConstructorWithEmptyStrings(){
        LifeRecord record = new LifeRecord("", "", "", "");
        assertEquals("", record.getTitle());
        assertEquals("", record.getContent());
        assertEquals("", record.getCategory());
        assertEquals("", record.getMood());
    }

    // 测试构造函数处理 null 字符串
    @Test
    void testConstructorWithNullStrings(){
        LifeRecord record = new LifeRecord(null, null, null, null);
        assertNull(record.getTitle());
        assertNull(record.getContent());
        assertNull(record.getCategory());
        assertNull(record.getMood());
    }
}
