package com.example.todolist;
import com.example.TODOlist.TodoItem;
import com.example.TODOlist.TodoListMenu;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TodoListMenuComprehensiveTest {

    private TodoListMenu todoListMenu;
    private ByteArrayInputStream inputStream;
    private Scanner scanner;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    public void setUp() {
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    public void tearDown() {
        System.setOut(originalOut);
        // 关闭Scanner避免资源泄漏
        if (scanner != null) {
            scanner.close();
        }
        // 删除测试文件
        File file = new File("todo_list.txt");
        if (file.exists()) {
            file.delete();
        }
    }

    private void setupScanner(String input) {
        inputStream = new ByteArrayInputStream(input.getBytes());
        scanner = new Scanner(inputStream);
        todoListMenu = new TodoListMenu(scanner);
    }

    // 测试显示菜单并选择有效选项
    @Test
    public void testDisplayMenuValidChoice() {
        setupScanner("1\n0");
        todoListMenu.displayMenu();
        assertTrue(outContent.toString().contains("返回上一级菜单..."));
    }

    // 测试显示菜单并选择无效选项
    @Test
    public void testDisplayMenuInvalidChoice() {
        setupScanner("9\n0");
        todoListMenu.displayMenu();
        assertTrue(outContent.toString().contains("输入有误，请重新输入。"));
        assertTrue(outContent.toString().contains("返回上一级菜单..."));
    }

    // 测试显示菜单并输入非数字
    @Test
    public void testDisplayMenuNonNumericInput() {
        setupScanner("abc\n0");
        todoListMenu.displayMenu();
        assertTrue(outContent.toString().contains("输入有误，请输入数字。"));
        assertTrue(outContent.toString().contains("返回上一级菜单..."));
    }

    // 测试查看空待办列表
    @Test
    public void testViewTodoListEmpty() {
        setupScanner("0"); // 仅需输入0返回上一级
        todoListMenu.viewTodoList();
        assertTrue(outContent.toString().contains("待办列表为空。"));
    }

    @Test
    public void testViewTodoListMarkAsCompleted() {
        setupScanner("2\nTest Content\n1\n0\n1\n1\n0"); // 选择2新建待办，输入内容和优先级，0返回，选择1标记完成，0返回
        todoListMenu.displayMenu();
        assertTrue(outContent.toString().contains("待办事项已完成。"));
    }

    @Test
    public void testViewTodoListInvalidSubChoice() {
        setupScanner("2\nTest Content\n1\n0\n100000\n0"); // 选择2新建待办，输入内容和优先级，0返回，无效选择2，然后0返回
        todoListMenu.displayMenu();
        assertTrue(outContent.toString().contains("输入有误，请重新输入。"));
    }

    @Test
    public void testViewTodoListNonNumericSubInput() {
        setupScanner("2\nTest Content\n1\n0\nabc\n0"); // 选择2新建待办，输入内容和优先级，0返回，非数字输入，然后0返回
        todoListMenu.displayMenu();
        assertTrue(outContent.toString().contains("输入有误，请输入数字。"));
    }

    // 测试获取星级字符串
    @Test
    public void testGetStarString() {
        setupScanner(""); // 此测试不涉及用户输入
        int priority = 2;
        String expected = "**";
        assertEquals(expected, todoListMenu.getStarString(priority));
    }

    // 测试创建待办事项
    @Test
    public void testCreateTodoItem() {
        setupScanner("Test Content\n1\n0"); // 内容、优先级1、返回
        todoListMenu.createTodoItem();
        assertTrue(outContent.toString().contains("待办事项已添加。"));
    }

    // 测试创建待办事项并输入无效优先级
    @Test
    public void testCreateTodoItemInvalidPriority() {
        setupScanner("Test Content\n4\n1\n0"); // 无效优先级4，然后重新输入1，最后返回
        todoListMenu.createTodoItem();
        assertTrue(outContent.toString().contains("输入有误，请输入 1-3 之间的数字。"));
        assertTrue(outContent.toString().contains("待办事项已添加。"));
    }

    // 测试创建待办事项并输入非数字优先级
    @Test
    public void testCreateTodoItemNonNumericPriority() {
        setupScanner("Test Content\nabc\n1\n0"); // 非数字优先级，然后重新输入1，最后返回
        todoListMenu.createTodoItem();
        assertTrue(outContent.toString().contains("输入有误，请输入数字。"));
        assertTrue(outContent.toString().contains("待办事项已添加。"));
    }

    // 测试创建待办事项并输入无效子选择
    @Test
    public void testCreateTodoItemInvalidSubChoice() {
        setupScanner("Test Content\n1\n2\n0"); // 有效内容和优先级，无效子选择2，然后返回
        todoListMenu.createTodoItem();
        assertTrue(outContent.toString().contains("输入有误，请重新输入。"));
        assertTrue(outContent.toString().contains("待办事项已添加。"));
    }

    // 测试创建待办事项并输入非数字子选择
    @Test
    public void testCreateTodoItemNonNumericSubInput() {
        setupScanner("Test Content\n1\nabc\n0"); // 有效内容和优先级，非数字子选择，然后返回
        todoListMenu.createTodoItem();
        assertTrue(outContent.toString().contains("输入有误，请输入数字。"));
        assertTrue(outContent.toString().contains("待办事项已添加。"));
    }

    // 测试加载空文件
    @Test
    public void testLoadTodoListEmptyFile() {
        setupScanner(""); // 此测试不涉及用户输入
        List<TodoItem> list = todoListMenu.loadTodoList();
        assertTrue(list.isEmpty());
    }

    // 测试保存和加载待办列表
    @Test
    public void testSaveTodoListAndLoadTodoList() throws IOException {
        setupScanner(""); // 此测试不涉及用户输入
        List<TodoItem> list = new ArrayList<>();
        list.add(new TodoItem("Test Content", 1));
        todoListMenu.todoList = list;
        todoListMenu.saveTodoList();
        List<TodoItem> loadedList = todoListMenu.loadTodoList();
        assertEquals(list.size(), loadedList.size());
        assertEquals(list.get(0).getContent(), loadedList.get(0).getContent());
        assertEquals(list.get(0).getPriority(), loadedList.get(0).getPriority());
    }

    // 测试创建空内容待办事项
    @Test
    public void testCreateTodoItemEmptyContent() {
        setupScanner("\n1\n0"); // 空内容，优先级1，返回
        todoListMenu.createTodoItem();
        assertTrue(outContent.toString().contains("待办事项已添加。"));
    }

    // 测试创建最小优先级待办事项
    @Test
    public void testCreateTodoItemMinPriority() {
        setupScanner("Test Content\n1\n0"); // 内容，最小优先级1，返回
        todoListMenu.createTodoItem();
        assertTrue(outContent.toString().contains("待办事项已添加。"));
    }

    // 测试创建最大优先级待办事项
    @Test
    public void testCreateTodoItemMaxPriority() {
        setupScanner("Test Content\n3\n0"); // 内容，最大优先级3，返回
        todoListMenu.createTodoItem();
        assertTrue(outContent.toString().contains("待办事项已添加。"));
    }
}