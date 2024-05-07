package test;

import main.model.Task;
import main.model.TaskStatus;
import org.junit.jupiter.api.Test;
import main.service.InMemoryHistoryManager;

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


}