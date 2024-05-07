package test;

import main.model.Epic;
import main.model.Subtask;
import main.model.Task;
import main.model.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import main.service.InMemoryTaskManager;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class InMemoryTaskManagerTest {
    InMemoryTaskManager manager = new InMemoryTaskManager();
    Task task1;
    Task task2;
    Epic epic1;
    Subtask subtask1;

    @BeforeEach
    public void beforeEach() {
        task1 = new Task("Сходить на тренировку", "Сегодня в 15.00", 1, TaskStatus.NEW);
        task2 = new Task("Сделать уборку",
                "Прибраться в квартире после тренировки", 2,
                TaskStatus.NEW);
        epic1 = new Epic("Сдать спринт", "Сдать спринт,чтобы пройти дальше по программе", 3,
                TaskStatus.IN_PROGRESS, new ArrayList<>());
        subtask1 = new Subtask("Задания в тренажере",
                "Сделать все задания в тренажере", 5, TaskStatus.NEW, 0);
        manager.createTask(task1);
        manager.createTask(task2);
        manager.createEpic(epic1);
        manager.createSubtask(subtask1);
    }

    //InMemoryTaskManager добавляет задачи разного типа и может найти их по id
    @Test
    public void createTaskAndCheckThatItIsNotEmpty() {
        Task createdTask1 = manager.createTask(task1);
        assertNotNull(createdTask1);
    }

    @Test
    public void createEpicAndCheckThatItIsNotEmpty() {
        Epic createdEpic1 = manager.createEpic(epic1);
        assertNotNull(createdEpic1);
    }

    @Test
    public void createSubtaskAndCheckThatItIsNotEmpty() {
        Subtask createdSubtask1 = manager.createSubtask(subtask1);
        assertNotNull(createdSubtask1);
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
        Subtask foundSubtask = manager.getSubtaskById(subtask1.getId());
        assertEquals(subtask1, foundSubtask);
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
    public void taskUnchangedAfterAdding() {//проверяется неизменность задачи при добавлении задачи в менеджер
        Task taskFromManager = manager.getTaskById(task1.getId());

        assertNotNull(taskFromManager);
        assertEquals(task1.getName(), taskFromManager.getName());
        assertEquals(task1.getDescription(), taskFromManager.getDescription());
        assertEquals(task1.getStatus(), taskFromManager.getStatus());

    }


}







