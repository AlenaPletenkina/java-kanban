package yandex.practicum.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import yandex.practicum.exception.TaskValidationException;
import yandex.practicum.model.Epic;
import yandex.practicum.model.Subtask;
import yandex.practicum.model.Task;
import yandex.practicum.model.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static yandex.practicum.model.TaskStatus.NEW;

class InMemoryTaskManagerTest {
    InMemoryTaskManager manager = new InMemoryTaskManager();
    Task task1;

    Task task2;
    Task task3;
    Epic epic1;
    Subtask subtask1;
    Subtask subtaskStartTime;

    @BeforeEach
    public void beforeEach() {
        task1 = new Task("Сходить на тренировку", "Сегодня в 15.00", 1, TaskStatus.NEW,
                Duration.ofMinutes(10), LocalDateTime.of(2023, Month.JUNE, 25, 12, 0));
        task2 = new Task("Сделать уборку", "Прибраться в квартире после тренировки", 2, TaskStatus.NEW,
                Duration.ofMinutes(10), LocalDateTime.of(2022, Month.JUNE, 25, 12, 0));
        task3 = new Task("Сходить на тренировку", "Сегодня в 15.00", 1, NEW, Duration.ofMinutes(90),
                LocalDateTime.of(2021, Month.JUNE, 25, 12, 0));
        epic1 = new Epic("Сдать спринт", "Сдать спринт чтобы пройти дальше по программе", 3,
                TaskStatus.IN_PROGRESS, new ArrayList<>());
        manager.createEpic(epic1);
        List<Epic> allEpics = manager.getAllEpics();

        subtask1 = new Subtask("Задания в тренажере",
                "Сделать все задания в тренажере", 5, TaskStatus.NEW, allEpics.get(0).getId(),
                LocalDateTime.of(2020, Month.JUNE, 25, 12, 0), Duration.ofMinutes(30));
        subtaskStartTime = new Subtask("Задания в тренажере",
                "Сделать все задания в тренажере", 5, TaskStatus.NEW, allEpics.get(0).getId(),
                LocalDateTime.of(2019, Month.JUNE, 25, 12, 0), Duration.ofMinutes(20));
        manager.createTask(task1);
        manager.createTask(task2);

        //   manager.createSubtask(subtask1);
        manager.createSubtask(subtaskStartTime);
    }

    @Test
    public void createEpicAndCheckThatItIsNotEmpty() {
        Epic createdEpic1 = manager.createEpic(epic1);
        assertNotNull(createdEpic1);
    }

    @Test
    public void createSubtaskAndCheckThatItIsNotEmpty() {
        Epic epic = manager.epics.get(subtaskStartTime.getEpic());

        assertEquals(subtaskStartTime.getDuration(), epic.getDuration());

        assertEquals(subtaskStartTime.getStartTime(), epic.getStartTime());

        assertEquals(epic.getEndTime(), epic.getStartTime().plus(epic.getDuration()));

        assertNotNull(subtaskStartTime);
    }

    @Test
    public void canFindATaskByItsId() {
        Task foundTask = manager.getTaskById(task1.getId());
        assertEquals(task1, foundTask);
    }

    @Test
    public void canFindAEpicByItsId() {
        Epic foundEpic = manager.getEpicById(epic1.getId());
        assertEquals(epic1, foundEpic);
    }

    @Test
    public void canFindASubtaskByItsId() {
        Subtask foundSubtask = manager.getSubtaskById(subtaskStartTime.getId());
        assertEquals(subtaskStartTime, foundSubtask);
    }

    @Test
    public void givenAndGeneratedIdDoNotConflict() { //задачи с заданным id и сгенерированным id не конфликтуют
        Task task1 = new Task();
        task1.setId(3);
        Task task2 = new Task();
        manager.createTask(task1);
        manager.createTask(task2);
        assertEquals(4, manager.getAllTasks().size());
    }

    @Test
    public void taskUnchangedAfterAdding() { //проверяется неизменность задачи при добавлении задачи в менеджер
        Task taskFromManager = manager.getTaskById(task1.getId());

        assertNotNull(taskFromManager);
        assertEquals(task1.getName(), taskFromManager.getName());
        assertEquals(task1.getDescription(), taskFromManager.getDescription());
        assertEquals(task1.getStatus(), taskFromManager.getStatus());
    }

    @Test
    public void epicDeleteUnActualSubtask() {
        List<Epic> allEpics = manager.getAllEpics();
        Epic epic = allEpics.get(0);
        List<Integer> subtasks = epic.getSubtasks();
        assertEquals(1, subtasks.size());
        manager.removeSubtaskById(subtasks.get(0));
        subtasks = epic.getSubtasks();
        assertEquals(0, subtasks.size());

        assertEquals(epic.getDuration(), Duration.ZERO);

        assertNull(epic.getStartTime());
        assertNull(epic.getEndTime());
    }

    @Test
    public void epicStatusShouldBeNew() {

        Epic epic = new Epic("name", "Description", 0, TaskStatus.DONE, new ArrayList<>());
        manager.createEpic(epic);

        int id = epic.getId();

        Subtask subtask = new Subtask("name", "disc", 1, TaskStatus.NEW, id);
        Subtask subtask2 = new Subtask("name2", "disc2", 2, TaskStatus.NEW, id);

        manager.createSubtask(subtask);
        manager.createSubtask(subtask2);

        assertEquals(TaskStatus.NEW, epic.getStatus());
    }

    @Test
    public void epicStatusShouldBeDone() {

        Epic epic = new Epic("name", "Description", 0, TaskStatus.DONE, new ArrayList<>());
        manager.createEpic(epic);

        int id = epic.getId();

        Subtask subtask = new Subtask("name", "Description", 1, TaskStatus.DONE, id);
        Subtask subtask2 = new Subtask("name2", "Description2", 2, TaskStatus.DONE, id);

        manager.createSubtask(subtask);
        manager.createSubtask(subtask2);

        assertEquals(TaskStatus.DONE, epic.getStatus());
    }

    @Test
    public void epicStatusShouldBeInProgress() {
        Epic epic = new Epic("name", "Description", 0, TaskStatus.DONE, new ArrayList<>());
        manager.createEpic(epic);

        int id = epic.getId();

        Subtask subtask = new Subtask("name", "Description", 1, TaskStatus.DONE, id);
        Subtask subtask2 = new Subtask("name2", "Description2", 2, TaskStatus.NEW, id);

        manager.createSubtask(subtask);
        manager.createSubtask(subtask2);

        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus());
    }

    @Test
    public void failedValidationTaskTest() {
        task1 = new Task("Сходить на тренировку", "Сегодня в 15.00", 1, TaskStatus.NEW,
                Duration.ofMinutes(10), LocalDateTime.of(2018, Month.JUNE, 25, 12, 0));

        manager.createTask(task1);

        assertThrowsExactly(TaskValidationException.class, () -> {
            manager.updateTask(task1);
        });
    }
}







