package yandex.practicum;

import org.junit.jupiter.api.Test;
import yandex.practicum.model.Task;
import yandex.practicum.model.TaskStatus;
import yandex.practicum.service.InMemoryHistoryManager;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class InMemoryHistoryManagerTest { //задачи, добавляемые в HistoryManager, сохраняют предыдущую версию и её данные
    InMemoryHistoryManager manager = new InMemoryHistoryManager();

    @Test
    public void newTasksRetainThePreviousVersionOfTheTaskAndItsData() {
        Task task1 = new Task("Сходить на тренировку", "Сегодня в 15.00", 1, TaskStatus.DONE);

        manager.add(task1);

        task1.setName("Новое имя");
        task1.setDescription("Новое описание");
        task1.setStatus(TaskStatus.IN_PROGRESS);

        List<Task> history = manager.getHistory();

        assertFalse(history.isEmpty());
        assertEquals(task1.getName(), history.get(0).getName());
        assertEquals(task1.getDescription(), history.get(0).getDescription());
        assertEquals(task1.getStatus(), history.get(0).getStatus());
    }

    //проверить, что при добавлении в историю уже существующей таски - удаляется старая версия
    @Test
    public void oldTaskRemoveWhenAddNewVersion() {

        Task task1 = new Task("Сходить на тренировку", "Сегодня в 15.00", 1, TaskStatus.DONE);
        manager.add(task1);
        Task task2 = new Task("RRRRRR", "AAAAAAAAA", 1, TaskStatus.NEW);
        manager.add(task2);

        List<Task> history = manager.getHistory();
        assertEquals(1, history.size());
        assertEquals(task2.getName(), history.get(0).getName());
        assertEquals(task2.getDescription(), history.get(0).getDescription());
        assertEquals(task2.getStatus(), history.get(0).getStatus());
    }


    // проверяю,что при удалении из истории, все работает правильно
    @Test
    public void removeTaskTest() {

        Task task1 = new Task("Сходить на тренировку", "Сегодня в 15.00", 3, TaskStatus.DONE);
        manager.add(task1);
        Task task2 = new Task("RRRRRR", "AAAAAAAAA", 4, TaskStatus.NEW);
        manager.add(task2);

        List<Task> history = manager.getHistory();
        assertEquals(2, history.size());
        manager.remove(task1.getId());
        history = manager.getHistory();
        assertEquals(1, history.size());
    }
}
