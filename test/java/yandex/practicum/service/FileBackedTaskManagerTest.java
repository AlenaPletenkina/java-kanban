package yandex.practicum.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import yandex.practicum.exception.LoadFromFileException;
import yandex.practicum.exception.ManagerSaveException;
import yandex.practicum.model.Epic;
import yandex.practicum.model.Subtask;
import yandex.practicum.model.Task;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;
import static org.junit.jupiter.api.Assertions.*;
import static yandex.practicum.model.TaskStatus.IN_PROGRESS;
import static yandex.practicum.model.TaskStatus.NEW;

class FileBackedTaskManagerTest {
    File tempFile;
    FileBackedTaskManager fileManager;

    @BeforeEach
    public void init() {
        try {
            tempFile = File.createTempFile("test", "csv");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        fileManager = new FileBackedTaskManager(tempFile.toPath());
    }

    @Test
    public void saveAndLoadOfEmptyFilesTest() {
        assertNotNull(tempFile);
        fileManager.save();
        fileManager.loadFromFile(tempFile);
    }

    @Test
    public void shouldSaveFewTasks() {
        createTasks();
        assertEquals(1, fileManager.getAllEpics().size());
        assertEquals(2, fileManager.getAllTasks().size());
        assertEquals(1, fileManager.getAllSubtasks().size());
        int count = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(tempFile.toString()))) {
            reader.readLine();
            while (true) {
                String line = reader.readLine();
                if (isNull(line) || line.isEmpty()) {
                    break;
                }
                count += 1;
            }
        } catch (FileNotFoundException e) {
            throw new ManagerSaveException("Файл не найден.");
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка чтения файла.");
        }
        assertEquals(4, count);
    }

    @Test
    public void shouldLoadFewTasks() {
        createTasks();
        assertEquals(1, fileManager.getAllEpics().size());
        assertEquals(2, fileManager.getAllTasks().size());
        assertEquals(1, fileManager.getAllSubtasks().size());

        File copyFile = createCopyFile(tempFile);
        fileManager.removeAllTasks();
        fileManager.removeAllEpic();
        fileManager.removeAllSubtasks();
        assertEquals(0, fileManager.getAllEpics().size());
        assertEquals(0, fileManager.getAllTasks().size());
        assertEquals(0, fileManager.getAllSubtasks().size());

        fileManager.loadFromFile(copyFile);
        assertEquals(1, fileManager.getAllEpics().size());
        assertEquals(2, fileManager.getAllTasks().size());
        assertEquals(1, fileManager.getAllSubtasks().size());
    }

    private void createTasks() {
        Task task1 = new Task("Сходить на тренировку", "Сегодня в 15.00", 1, NEW, Duration.ofMinutes(90),
                LocalDateTime.now());
        Task task2 = new Task("Сделать уборку",
                "Прибраться в квартире после тренировки", 2,
                NEW, Duration.ofMinutes(40), LocalDateTime.of(2024, Month.JUNE, 25, 12, 0));

        Epic epic1 = new Epic("Сдать спринт", "Сдать спринт чтобы пройти дальше по программе", 3,
                IN_PROGRESS, new ArrayList<>());
        fileManager.createEpic(epic1);
        List<Epic> allEpics = fileManager.getAllEpics();
        Subtask subtask1 = new Subtask("Задания в тренажере",
                "Сделать все задания в тренажере", 5, NEW, allEpics.get(0).getId(),
                LocalDateTime.now(), Duration.ofMinutes(50));

        fileManager.createTask(task1);
        fileManager.createTask(task2);
        fileManager.createSubtask(subtask1);
    }

    private File createCopyFile(File tempFile) {
        File copied;
        try {
            copied = File.createTempFile("file2", "csv");
            Files.copy(tempFile.toPath(), copied.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return copied;
    }

    @Test
    public void shouldSaveOfEmptyThrowException() {
        Path path = Path.of(("src/resource/historyTasksManager.csv"));
        File file = new File(String.valueOf(path));
        final LoadFromFileException exception = assertThrows(
                LoadFromFileException.class,
                () -> {
                    fileManager.loadFromFile(file);
                });
        assertEquals("Файл не найден.", exception.getMessage());
    }


}