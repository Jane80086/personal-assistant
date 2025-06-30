package com.example.todolist;

import com.example.TODOlist.TodoItem;
import com.example.TODOlist.TodoListMenu;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TodoListTest {

    @Mock
    private Scanner scanner;

    private TodoListMenu todoListMenu;
    private List<TodoItem> todoList;

    private final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final PrintStream originalOut = System.out;

    @BeforeEach
    public void setUp() {
        todoList = new ArrayList<>();
        todoListMenu = new TodoListMenu(scanner);
        todoListMenu.todoList = todoList;
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

    // 测试 TodoItem 构造函数
    @Test
    public void testTodoItemConstructor() {
        String content = "Test Content";
        int priority = 2;
        TodoItem item = new TodoItem(content, priority);
        assertEquals(content, item.getContent());
        assertEquals(priority, item.getPriority());
        assertFalse(item.isCompleted());
        assertNotNull(item.getCreationTime());
    }

    @Test
    public void testTodoItemConstructorWithCreationTime() {
        String content = "Test Content";
        int priority = 2;
        boolean isCompleted = true;
        LocalDateTime creationTime = LocalDateTime.now();
        TodoItem item = new TodoItem(content, priority, isCompleted, creationTime);
        assertEquals(content, item.getContent());
        assertEquals(priority, item.getPriority());
        assertEquals(isCompleted, item.isCompleted());
        assertEquals(creationTime, item.getCreationTime());
    }

    // 测试 TodoItem 的 getter 和 setter 方法
    @Test
    public void testTodoItemGettersAndSetters() {
        TodoItem item = new TodoItem("Test", 1);
        item.setContent("New Test");
        item.setPriority(2);
        item.setCompleted(true);
        assertEquals("New Test", item.getContent());
        assertEquals(2, item.getPriority());
        assertTrue(item.isCompleted());
    }

    // 测试 TodoItem 的 compareTo 方法
    @Test
    public void testTodoItemCompareToPriority() {
        TodoItem item1 = new TodoItem("Test1", 1);
        TodoItem item2 = new TodoItem("Test2", 2);
        assertTrue(item2.compareTo(item1) < 0);
    }

    @Test
    public void testTodoItemCompareToCreationTime() {
        LocalDateTime time1 = LocalDateTime.now();
        LocalDateTime time2 = time1.plusMinutes(1);
        TodoItem item1 = new TodoItem("Test1", 1, false, time1);
        TodoItem item2 = new TodoItem("Test2", 1, false, time2);
        assertTrue(item1.compareTo(item2) < 0);
    }

    // 测试 TodoListMenu 的 displayMenu 方法
    @Test
    public void testDisplayMenuExit() {
        when(scanner.nextLine()).thenReturn("0");
        todoListMenu.displayMenu();
        verify(scanner, times(1)).nextLine();
    }

    @Test
    public void testDisplayMenuInvalidInput() {
        when(scanner.nextLine()).thenReturn("a", "0");
        todoListMenu.displayMenu();
        verify(scanner, times(2)).nextLine();
    }

    @Test
    public void testDisplayMenuViewTodoList() {
        when(scanner.nextLine()).thenReturn("1", "0");
        todoListMenu.displayMenu();
        verify(scanner, times(2)).nextLine();
    }

    @Test
    public void testDisplayMenuCreateTodoItem() {
        when(scanner.nextLine()).thenReturn("2", "Test Content", "2", "0", "0");
        todoListMenu.displayMenu();
        verify(scanner, times(5)).nextLine();
    }

    @Test
    public void testDisplayMenuEditTodoItem() {
        todoList.add(new TodoItem("Test", 1));
        when(scanner.nextLine()).thenReturn("3", "1", "New Test", "2", "0");
        todoListMenu.displayMenu();
        verify(scanner, times(5)).nextLine();
    }

    @Test
    public void testDisplayMenuDeleteTodoItem() {
        todoList.add(new TodoItem("Test", 1));
        when(scanner.nextLine()).thenReturn("4", "1", "0");
        todoListMenu.displayMenu();
        verify(scanner, times(3)).nextLine();
    }

    // 测试 TodoListMenu 的 viewTodoList 方法
    @Test
    public void testViewTodoListEmpty() {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        todoListMenu.viewTodoList();
        String expectedOutput = " 待办列表为空。";
        assertTrue(outContent.toString().contains(expectedOutput));
        System.setOut(System.out);
    }

    @Test
    public void testViewTodoListMarkCompleted() {
        todoList.add(new TodoItem("Test", 1));
        when(scanner.nextLine()).thenReturn("1");
        todoListMenu.viewTodoList();
        assertTrue(todoList.get(0).isCompleted());
        verify(scanner, times(1)).nextLine();
    }

    @Test
    public void testViewTodoListInvalidInput() {
        todoList.add(new TodoItem("Test", 1));
        when(scanner.nextLine()).thenReturn("a", "0");
        todoListMenu.viewTodoList();
        verify(scanner, times(2)).nextLine();
    }

    // 测试 TodoListMenu 的 view 方法
    @Test
    public void testViewEmptyList() {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        todoListMenu.view();
        String expectedOutput = " 待办列表为空。";
        assertTrue(outContent.toString().contains(expectedOutput));
        System.setOut(System.out);
    }

    @Test
    public void testViewNonEmptyList() {
        todoList.add(new TodoItem("Test", 1));
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        todoListMenu.view();
        String expectedOutput = "1. 内容: Test, 优先级: *, 创建时间: ";
        assertTrue(outContent.toString().contains(expectedOutput));
        System.setOut(System.out);
    }

    // 测试 TodoListMenu 的 getStarString 方法
    @Test
    public void testGetStarString() {
        assertEquals("***", todoListMenu.getStarString(3));
    }

    // 测试 TodoListMenu 的 createTodoItem 方法
    @Test
    public void testCreateTodoItem() {
        when(scanner.nextLine()).thenReturn("Test Content", "2", "0");
        todoListMenu.createTodoItem();
        assertEquals(1, todoList.size());
        assertEquals("Test Content", todoList.get(0).getContent());
        assertEquals(2, todoList.get(0).getPriority());
        verify(scanner, times(3)).nextLine();
    }

    @Test
    public void testCreateTodoItemInvalidPriority() {
        when(scanner.nextLine()).thenReturn("Test Content", "a", "2", "0");
        todoListMenu.createTodoItem();
        assertEquals(1, todoList.size());
        verify(scanner, times(4)).nextLine();
    }

    @Test
    public void testCreateTodoItemContinue() {
        when(scanner.nextLine()).thenReturn("Test Content", "2", "1", "Test Content 2", "3", "0");
        todoListMenu.createTodoItem();
        assertEquals(2, todoList.size());
        verify(scanner, times(6)).nextLine();
    }

    // 测试 TodoListMenu 的 loadTodoList 方法
    @Test
    public void testLoadTodoListEmptyFile() throws IOException {
        // 清理文件
        Path filePath = Paths.get("todo_list.txt");
        if (Files.exists(filePath)) {
            Files.delete(filePath);
        }

        List<TodoItem> loadedList = todoListMenu.loadTodoList();
        assertTrue(loadedList.isEmpty());
    }

    @Test
    public void testLoadTodoListSingleItem() throws IOException {
        // 创建一个待办事项并保存到文件
        TodoItem item = new TodoItem("Test", 1);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("todo_list.txt"))) {
            writer.write(item.getContent() + "," + item.getPriority() + "," + item.isCompleted() + "," +
                    item.getCreationTime().format(DATE_TIME_FORMATTER));
        }

        List<TodoItem> loadedList = todoListMenu.loadTodoList();
        assertEquals(1, loadedList.size());
        assertEquals(item.getContent(), loadedList.get(0).getContent());
        assertEquals(item.getPriority(), loadedList.get(0).getPriority());
        assertEquals(item.isCompleted(), loadedList.get(0).isCompleted());

    }

    // 测试 TodoListMenu 的 saveTodoList 方法
    @Test
    public void testSaveTodoList() {
        todoList.add(new TodoItem("Test", 1));
        todoListMenu.saveTodoList();
        // 这里可以添加更多的验证逻辑，例如检查文件内容
    }

    // 测试 TodoListMenu 的 editTodoItem 方法
    @Test
    public void testEditTodoItem() {
        todoList.add(new TodoItem("Test", 1));
        when(scanner.nextLine()).thenReturn("1", "New Test", "2");
        todoListMenu.editTodoItem();
        assertEquals("New Test", todoList.get(0).getContent());
        assertEquals(2, todoList.get(0).getPriority());
        verify(scanner, times(3)).nextLine();
    }

    @Test
    public void testEditTodoItemInvalidNumber() {
        todoList.add(new TodoItem("Test", 1));
        when(scanner.nextLine()).thenReturn("a", "0");
        todoListMenu.editTodoItem();
        verify(scanner, times(1)).nextLine();
    }

    // 测试 TodoListMenu 的 deleteTodoItem 方法
    @Test
    public void testDeleteTodoItem() {
        todoList.add(new TodoItem("Test", 1));
        when(scanner.nextLine()).thenReturn("1");
        todoListMenu.deleteTodoItem();
        assertTrue(todoList.isEmpty());
        verify(scanner, times(1)).nextLine();
    }

    @Test
    public void testDeleteTodoItemInvalidNumber() {
        todoList.add(new TodoItem("Test", 1));
        when(scanner.nextLine()).thenReturn("a", "0");
        todoListMenu.deleteTodoItem();
        verify(scanner, times(1)).nextLine();
    }

    // 边界条件测试
    @Test
    public void testCreateTodoItemMinPriority() {
        when(scanner.nextLine()).thenReturn("Test Content", "1", "0");
        todoListMenu.createTodoItem();
        assertEquals(1, todoList.size());
        assertEquals("Test Content", todoList.get(0).getContent());
        assertEquals(1, todoList.get(0).getPriority());
        verify(scanner, times(3)).nextLine();
    }

    @Test
    public void testCreateTodoItemMaxPriority() {
        when(scanner.nextLine()).thenReturn("Test Content", "3", "0");
        todoListMenu.createTodoItem();
        assertEquals(1, todoList.size());
        assertEquals("Test Content", todoList.get(0).getContent());
        assertEquals(3, todoList.get(0).getPriority());
        verify(scanner, times(3)).nextLine();
    }

    @Test
    public void testEditTodoItemMinPriority() {
        todoList.add(new TodoItem("Test", 1));
        when(scanner.nextLine()).thenReturn("1", "New Test", "1");
        todoListMenu.editTodoItem();
        assertEquals("New Test", todoList.get(0).getContent());
        assertEquals(1, todoList.get(0).getPriority());
        verify(scanner, times(3)).nextLine();
    }

    @Test
    public void testEditTodoItemMaxPriority() {
        todoList.add(new TodoItem("Test", 1));
        when(scanner.nextLine()).thenReturn("1", "New Test", "3");
        todoListMenu.editTodoItem();
        assertEquals("New Test", todoList.get(0).getContent());
        assertEquals(3, todoList.get(0).getPriority());
        verify(scanner, times(3)).nextLine();
    }

    // 异常处理测试
    @Test
    public void testLoadTodoListFileNotFound() {
        // 删除文件
        File file = new File("todo_list.txt");
        if (file.exists()) {
            file.delete();
        }

        List<TodoItem> loadedList = todoListMenu.loadTodoList();
        assertTrue(loadedList.isEmpty());
    }



    // 方法组合测试
    @Test
    public void testCreateEditDeleteTodoItem() {
        // 创建待办事项
        when(scanner.nextLine()).thenReturn( "Test Content", "2", "0");
        todoListMenu.createTodoItem();
        assertEquals(1, todoList.size());

        // 编辑待办事项
        when(scanner.nextLine()).thenReturn( "1", "New Test", "3");
        todoListMenu.editTodoItem();
        assertEquals("New Test", todoList.get(0).getContent());
        assertEquals(3, todoList.get(0).getPriority());

        // 删除待办事项
        when(scanner.nextLine()).thenReturn( "1");
        todoListMenu.deleteTodoItem();
        assertTrue(todoList.isEmpty());
    }

    @Test
    public void testCreateMultipleTodoItems() {
        when(scanner.nextLine()).thenReturn("2", "Test Content 1", "2", "1", "Test Content 2", "3", "0");
        todoListMenu.createTodoItem();
        assertEquals(2, todoList.size());
    }

    @Test
    public void testEditMultipleTodoItems() {
        todoList.add(new TodoItem("Test 1", 1));
        todoList.add(new TodoItem("Test 2", 2));

        when(scanner.nextLine()).thenReturn( "1", "New Test 1", "2", "2", "New Test 2", "3");
        todoListMenu.editTodoItem();
        todoListMenu.editTodoItem();

        assertEquals("New Test 1", todoList.get(1).getContent());
        assertEquals(2, todoList.get(1).getPriority());
        assertEquals("New Test 2", todoList.get(0).getContent());
        assertEquals(3, todoList.get(0).getPriority());
    }

    @Test
    public void testDeleteMultipleTodoItems() {
        todoList.add(new TodoItem("Test 1", 1));
        todoList.add(new TodoItem("Test 2", 2));

        when(scanner.nextLine()).thenReturn("1", "1");
        todoListMenu.deleteTodoItem();
        todoListMenu.deleteTodoItem();

        assertTrue(todoList.isEmpty());
    }

    // 文件操作测试
    @Test
    public void testLoadTodoListWithCorruptedFile() throws IOException {
        // 创建一个损坏的文件
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("todo_list.txt"))) {
            writer.write("Invalid line");
            List<TodoItem> loadedList = todoListMenu.loadTodoList();
            assertTrue(loadedList.isEmpty());
        }



    }

    @Test
    public void testSaveTodoListWithEmptyList() {
        todoListMenu.saveTodoList();
        // 验证文件是否为空
        File file = new File("todo_list.txt");
        if (file.exists()) {
            assertTrue(file.length() == 0);
        }
    }
}