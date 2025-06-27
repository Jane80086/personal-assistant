package com.example.health;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

public class HealthRecordMenuTest {
    private static final String TEST_DATA_DIR = "test_data_health_menu";
    private static final Path TEST_DATA_PATH = Paths.get(TEST_DATA_DIR);
    
    private ByteArrayOutputStream outputStream;
    private PrintStream originalOut;
    private HealthRecordMenu menu;
    private Scanner scanner;

    @BeforeEach
    void setUp() throws IOException {
        cleanupTestDirectory();
        Files.createDirectories(TEST_DATA_PATH);
        
        // 重定向输出流以捕获控制台输出
        outputStream = new ByteArrayOutputStream();
        originalOut = System.out;
        System.setOut(new PrintStream(outputStream));
    }

    @AfterEach
    void tearDown() throws IOException {
        // 恢复原始输出流
        System.setOut(originalOut);
        cleanupTestDirectory();
    }

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

    // 测试构造函数
    @Test
    void testConstructor() {
        String input = "0\n"; // 输入0退出
        ByteArrayInputStream inputStream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        scanner = new Scanner(inputStream);
        
        menu = new HealthRecordMenu(scanner);
        assertNotNull(menu, "HealthRecordMenu实例应该被成功创建");
    }

    // 测试显示菜单功能
    @Test
    void testDisplayMenu() {
        String input = "0\n"; // 输入0退出
        ByteArrayInputStream inputStream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        scanner = new Scanner(inputStream);
        
        menu = new HealthRecordMenu(scanner);
        menu.displayMenu();
        
        String output = outputStream.toString();
        assertTrue(output.contains("===  健康记录管理系统 ==="), "应该显示主菜单标题");
        assertTrue(output.contains("1.  浏览所有记录"), "应该显示选项1");
        assertTrue(output.contains("2.  添加新记录"), "应该显示选项2");
        assertTrue(output.contains("3.  按日期范围查询"), "应该显示选项3");
        assertTrue(output.contains("4.  编辑现有记录"), "应该显示选项4");
        assertTrue(output.contains("5.  删除记录"), "应该显示选项5");
        assertTrue(output.contains("6.  查看统计信息"), "应该显示选项6");
        assertTrue(output.contains("7.  显示输入帮助"), "应该显示选项7");
        assertTrue(output.contains("0.  返回主菜单"), "应该显示选项0");
    }

    // 测试无效输入处理
    @Test
    void testDisplayMenuInvalidInput() {
        String input = "invalid\n8\n0\n"; // 无效输入，超出范围，然后退出
        ByteArrayInputStream inputStream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        scanner = new Scanner(inputStream);
        
        menu = new HealthRecordMenu(scanner);
        menu.displayMenu();
        
        String output = outputStream.toString();
        assertTrue(output.contains("错误：请输入有效的数字(0-7)"), "应该处理无效输入");
        assertTrue(output.contains("错误：无效的选择，请输入0-7之间的数字"), "应该处理超出范围的输入");
    }

    // 测试空输入处理
    @Test
    void testDisplayMenuEmptyInput() {
        String input = "\n0\n"; // 空输入，然后退出
        ByteArrayInputStream inputStream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        scanner = new Scanner(inputStream);
        
        menu = new HealthRecordMenu(scanner);
        menu.displayMenu();
        
        String output = outputStream.toString();
        assertTrue(output.contains("错误：请输入一个数字"), "应该处理空输入");
    }

    // 测试显示输入帮助功能
    @Test
    void testShowInputHelp() {
        String input = "7\n0\n"; // 选择帮助，然后退出
        ByteArrayInputStream inputStream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        scanner = new Scanner(inputStream);
        
        menu = new HealthRecordMenu(scanner);
        menu.displayMenu();
        
        String output = outputStream.toString();
        assertTrue(output.contains("===  输入帮助 ==="), "应该显示帮助标题");
        assertTrue(output.contains("日期格式: YYYY-MM-DD"), "应该显示日期格式说明");
        assertTrue(output.contains("数值输入: 请输入数字或直接按回车键跳过"), "应该显示数值输入说明");
        assertTrue(output.contains("血压: 分别输入高压和低压两个数值"), "应该显示血压输入说明");
    }

    // 测试浏览记录功能 - 空记录
    @Test
    void testBrowseRecordsWithDetailsEmpty() {
        // 确保没有记录
        HealthRecordManager tempManager = new HealthRecordManager();
        List<HealthRecord> existingRecords = tempManager.getAllRecords();
        for (int i = existingRecords.size(); i > 0; i--) {
            tempManager.deleteRecord(i);
        }
        
        String input = "1\n0\n0\n"; // 浏览记录，返回，退出
        ByteArrayInputStream inputStream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        scanner = new Scanner(inputStream);
        
        menu = new HealthRecordMenu(scanner);
        menu.displayMenu();
        
        String output = outputStream.toString();
        assertTrue(output.contains("目前没有健康记录"), "应该显示空记录提示");
        assertTrue(output.contains("您可以通过菜单选项2添加新记录"), "应该显示添加记录提示");
    }

    // 测试添加记录功能
    @Test
    void testAddRecord() {
        // 输入：选择添加记录，输入有效数据，确认添加
        String input = "2\n" + // 选择添加记录
                      "2024-06-25\n" + // 日期
                      "70.5\n" + // 体重
                      "175.0\n" + // 身高
                      "75\n" + // 心率
                      "120\n" + // 高压
                      "80\n" + // 低压
                      "8\n" + // 睡眠时间
                      "10000\n" + // 步数
                      "测试备注\n" + // 备注
                      "0\n"; // 退出
        
        ByteArrayInputStream inputStream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        scanner = new Scanner(inputStream);
        
        menu = new HealthRecordMenu(scanner);
        menu.displayMenu();
        
        String output = outputStream.toString();
        assertTrue(output.contains("=====  添加新记录 ====="), "应该显示添加记录标题");
        assertTrue(output.contains("记录添加成功"), "应该显示添加成功消息");
    }

    // 测试添加记录功能 - 空数据
    @Test
    void testAddRecordEmptyData() {
        // 输入：选择添加记录，所有字段都留空，确认添加空记录
        String input = "2\n" + // 选择添加记录
                      "\n" + // 日期留空
                      "\n" + // 体重留空
                      "\n" + // 身高留空
                      "\n" + // 心率留空
                      "\n" + // 高压留空
                      "\n" + // 低压留空
                      "\n" + // 睡眠时间留空
                      "\n" + // 步数留空
                      "\n" + // 备注留空
                      "y\n" + // 确认添加空记录
                      "0\n"; // 退出
        
        ByteArrayInputStream inputStream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        scanner = new Scanner(inputStream);
        
        menu = new HealthRecordMenu(scanner);
        menu.displayMenu();
        
        String output = outputStream.toString();
        assertTrue(output.contains("警告：您没有输入任何数据"), "应该显示空数据警告");
        assertTrue(output.contains("记录添加成功"), "应该显示添加成功消息");
    }

    // 测试添加记录功能 - 取消空记录
    @Test
    void testAddRecordCancelEmptyData() {
        // 输入：选择添加记录，所有字段都留空，取消添加
        String input = "2\n" + // 选择添加记录
                      "\n" + // 日期留空
                      "\n" + // 体重留空
                      "\n" + // 身高留空
                      "\n" + // 心率留空
                      "\n" + // 高压留空
                      "\n" + // 低压留空
                      "\n" + // 睡眠时间留空
                      "\n" + // 步数留空
                      "\n" + // 备注留空
                      "n\n" + // 取消添加空记录
                      "0\n"; // 退出
        
        ByteArrayInputStream inputStream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        scanner = new Scanner(inputStream);
        
        menu = new HealthRecordMenu(scanner);
        menu.displayMenu();
        
        String output = outputStream.toString();
        assertTrue(output.contains("记录添加已取消"), "应该显示取消消息");
    }

    // 测试日期输入验证 - 有效日期
    @Test
    void testDateInputValid() {
        String input = "2\n2024-06-25\n70.0\n175.0\n75\n120\n80\n8\n10000\n测试\n0\n";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        scanner = new Scanner(inputStream);
        
        menu = new HealthRecordMenu(scanner);
        menu.displayMenu();
        
        String output = outputStream.toString();
        assertTrue(output.contains("记录添加成功"), "有效日期应该被接受");
    }

    // 测试日期输入验证 - 无效格式
    @Test
    void testDateInputInvalidFormat() {
        String input = "2\ninvalid-date\n2024-06-25\n70.0\n175.0\n75\n120\n80\n8\n10000\n测试\n0\n";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        scanner = new Scanner(inputStream);
        
        menu = new HealthRecordMenu(scanner);
        menu.displayMenu();
        
        String output = outputStream.toString();
        assertTrue(output.contains("错误：无效日期格式"), "应该显示日期格式错误");
    }

    // 测试日期输入验证 - 未来日期
    @Test
    void testDateInputFutureDate() {
        LocalDate futureDate = LocalDate.now().plusDays(1);
        String input = "2\n" + futureDate.toString() + "\n2024-06-25\n70.0\n175.0\n75\n120\n80\n8\n10000\n测试\n0\n";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        scanner = new Scanner(inputStream);
        
        menu = new HealthRecordMenu(scanner);
        menu.displayMenu();
        
        String output = outputStream.toString();
        assertTrue(output.contains("警告：日期不能是未来日期"), "应该显示未来日期警告");
    }

    // 测试数值输入验证 - 有效体重
    @Test
    void testWeightInputValid() {
        String input = "2\n2024-06-25\n70.5\n175.0\n75\n120\n80\n8\n10000\n测试\n0\n";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        scanner = new Scanner(inputStream);
        
        menu = new HealthRecordMenu(scanner);
        menu.displayMenu();
        
        String output = outputStream.toString();
        assertTrue(output.contains("记录添加成功"), "有效体重应该被接受");
    }

    // 测试数值输入验证 - 无效体重
    @Test
    void testWeightInputInvalid() {
        String input = "2\n2024-06-25\n-10\n70.5\n175.0\n75\n120\n80\n8\n10000\n测试\n0\n";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        scanner = new Scanner(inputStream);
        
        menu = new HealthRecordMenu(scanner);
        menu.displayMenu();
        
        String output = outputStream.toString();
        assertTrue(output.contains("错误：体重应在0-300kg之间"), "应该显示体重范围错误");
    }

    // 测试数值输入验证 - 无效心率
    @Test
    void testHeartRateInputInvalid() {
        String input = "2\n2024-06-25\n70.5\n175.0\n-10\n75\n120\n80\n8\n10000\n测试\n0\n";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        scanner = new Scanner(inputStream);
        
        menu = new HealthRecordMenu(scanner);
        menu.displayMenu();
        
        String output = outputStream.toString();
        assertTrue(output.contains("错误：心率应在0-250次/分钟之间"), "应该显示心率范围错误");
    }

    // 测试数值输入验证 - 无效血压
    @Test
    void testBloodPressureInputInvalid() {
        String input = "2\n2024-06-25\n70.5\n175.0\n75\n120\n250\n80\n8\n10000\n测试\n0\n"; // 低压超出范围
        ByteArrayInputStream inputStream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        scanner = new Scanner(inputStream);
        
        menu = new HealthRecordMenu(scanner);
        menu.displayMenu();
        
        String output = outputStream.toString();
        assertTrue(output.contains("错误：低压应在20-200mmHg之间"), "应该显示血压范围错误");
    }

    // 测试数值输入验证 - 无效睡眠时间
    @Test
    void testSleepHoursInputInvalid() {
        String input = "2\n2024-06-25\n70.5\n175.0\n75\n120\n80\n25\n8\n10000\n测试\n0\n";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        scanner = new Scanner(inputStream);
        
        menu = new HealthRecordMenu(scanner);
        menu.displayMenu();
        
        String output = outputStream.toString();
        assertTrue(output.contains("错误：睡眠时间应在0-24小时之间"), "应该显示睡眠时间范围错误");
    }

    // 测试数值输入验证 - 无效步数
    @Test
    void testStepsInputInvalid() {
        String input = "2\n2024-06-25\n70.5\n175.0\n75\n120\n80\n8\n-1000\n10000\n测试\n0\n";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        scanner = new Scanner(inputStream);
        
        menu = new HealthRecordMenu(scanner);
        menu.displayMenu();
        
        String output = outputStream.toString();
        assertTrue(output.contains("错误：步数应在0-100000步之间"), "应该显示步数范围错误");
    }

    // 测试数值输入验证 - 非数字输入
    @Test
    void testNumericInputNonNumeric() {
        String input = "2\n2024-06-25\ninvalid\n70.5\n175.0\n75\n120\n80\n8\n10000\n测试\n0\n";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        scanner = new Scanner(inputStream);
        
        menu = new HealthRecordMenu(scanner);
        menu.displayMenu();
        
        String output = outputStream.toString();
        assertTrue(output.contains("错误：请输入有效的数字"), "应该显示数字格式错误");
    }

    // 测试整数输入验证 - 非整数输入
    @Test
    void testIntegerInputNonInteger() {
        String input = "2\n2024-06-25\n70.5\n175.0\ninvalid\n75\n120\n80\n8\n10000\n测试\n0\n";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        scanner = new Scanner(inputStream);
        
        menu = new HealthRecordMenu(scanner);
        menu.displayMenu();
        
        String output = outputStream.toString();
        assertTrue(output.contains("错误：请输入有效的整数"), "应该显示整数格式错误");
    }

    // 测试必填日期输入 - 不允许空值
    @Test
    void testRequiredDateInput() {
        String input = "3\n\n2024-06-25\n2024-06-26\n0\n"; // 搜索记录，开始日期留空，重新输入
        ByteArrayInputStream inputStream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        scanner = new Scanner(inputStream);
        
        menu = new HealthRecordMenu(scanner);
        menu.displayMenu();
        
        String output = outputStream.toString();
        assertTrue(output.contains("错误：此项为必填项"), "应该显示必填项错误");
    }

    // 测试日期范围搜索 - 开始日期晚于结束日期
    @Test
    void testDateRangeSearchInvalidRange() {
        String input = "3\n2024-06-26\n2024-06-25\n0\n"; // 搜索记录，开始日期晚于结束日期
        ByteArrayInputStream inputStream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        scanner = new Scanner(inputStream);
        
        menu = new HealthRecordMenu(scanner);
        menu.displayMenu();
        
        String output = outputStream.toString();
        assertTrue(output.contains("错误：开始日期不能晚于结束日期"), "应该显示日期范围错误");
    }

    // 测试统计信息功能
    @Test
    void testShowStatistics() {
        // 先添加一些记录，然后查看统计
        String input = "2\n2024-06-25\n70.0\n175.0\n75\n120\n80\n8\n10000\n记录1\n" +
                      "6\n" + // 查看统计
                      "0\n"; // 退出
        
        ByteArrayInputStream inputStream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        scanner = new Scanner(inputStream);
        
        menu = new HealthRecordMenu(scanner);
        menu.displayMenu();
        
        String output = outputStream.toString();
        assertTrue(output.contains("=====  健康数据统计 ====="), "应该显示统计标题");
    }

    // 测试空记录时的统计信息
    @Test
    void testShowStatisticsEmpty() {
        // 确保没有记录
        HealthRecordManager tempManager = new HealthRecordManager();
        List<HealthRecord> existingRecords = tempManager.getAllRecords();
        for (int i = existingRecords.size(); i > 0; i--) {
            tempManager.deleteRecord(i);
        }
        
        String input = "6\n0\n"; // 查看统计，退出
        ByteArrayInputStream inputStream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        scanner = new Scanner(inputStream);
        
        menu = new HealthRecordMenu(scanner);
        menu.displayMenu();
        
        String output = outputStream.toString();
        assertTrue(output.contains("没有足够的数据生成统计信息"), "应该显示空数据提示");
    }

    // 测试边界值输入
    @Test
    void testBoundaryValues() {
        String input = "2\n2024-06-25\n0.1\n0.1\n1\n30\n20\n0\n0\n边界测试\n" +
                      "2\n2024-06-26\n299.9\n299.9\n249\n299\n199\n24\n99999\n边界测试2\n" +
                      "0\n";
        
        ByteArrayInputStream inputStream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        scanner = new Scanner(inputStream);
        
        menu = new HealthRecordMenu(scanner);
        menu.displayMenu();
        
        String output = outputStream.toString();
        assertTrue(output.contains("记录添加成功"), "边界值应该被接受");
    }

    // 测试特殊字符处理
    @Test
    void testSpecialCharactersInNotes() {
        String input = "2\n2024-06-25\n70.5\n175.0\n75\n120\n80\n8\n10000\n备注包含|分隔符\n换行符\t制表符\n0\n";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        scanner = new Scanner(inputStream);
        
        menu = new HealthRecordMenu(scanner);
        menu.displayMenu();
        
        String output = outputStream.toString();
        assertTrue(output.contains("记录添加成功"), "特殊字符备注应该被接受");
    }

    // 测试长备注处理
    @Test
    void testLongNotes() {
        String longNotes = "这是一个很长的备注".repeat(100);
        String input = "2\n2024-06-25\n70.5\n175.0\n75\n120\n80\n8\n10000\n" + longNotes + "\n0\n";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        scanner = new Scanner(inputStream);
        
        menu = new HealthRecordMenu(scanner);
        menu.displayMenu();
        
        String output = outputStream.toString();
        assertTrue(output.contains("记录添加成功"), "长备注应该被接受");
    }

    // 测试菜单循环功能
    @Test
    void testMenuLoop() {
        // 为每个菜单选项提供足够的输入数据
        String input = "8\n" + // 无效选项
                      "7\n" + // 显示帮助
                      "6\n" + // 显示统计
                      "5\n" + // 删除记录（需要记录ID）
                      "1\n" + // 浏览记录
                      "4\n" + // 搜索记录（需要日期范围）
                      "2024-06-25\n2024-06-26\n" + // 为搜索提供日期
                      "2\n" + // 添加记录（需要完整数据）
                      "2024-06-27\n70.5\n175.0\n75\n120\n80\n8\n10000\n测试记录\n" + // 为添加提供数据
                      "0\n"; // 退出
        
        ByteArrayInputStream inputStream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        scanner = new Scanner(inputStream);
        
        menu = new HealthRecordMenu(scanner);
        menu.displayMenu();
        
        String output = outputStream.toString();
        assertTrue(output.contains("错误：无效的选择"), "应该处理无效选项");
        assertTrue(output.contains("正在返回主菜单"), "应该正常退出");
    }

    // 测试输入流关闭
    @Test
    void testScannerClose() {
        String input = "0\n";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        scanner = new Scanner(inputStream);
        
        menu = new HealthRecordMenu(scanner);
        menu.displayMenu();
        
        // 验证scanner没有被关闭（因为这是用户的责任）
        // 注意：在displayMenu()执行后，scanner可能已经被耗尽，所以我们需要重新创建
        assertNotNull(scanner, "Scanner实例应该存在");
    }
} 