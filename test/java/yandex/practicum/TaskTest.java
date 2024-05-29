package yandex.practicum;

import org.junit.jupiter.api.Test;
import yandex.practicum.model.Task;
import yandex.practicum.model.TaskStatus;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {
    @Test
    void taskAreEqualToEachOtherIfTheirIdIsEqual() { //экземпляры класса Task равны друг другу, если равен их id
        Task task1 = new Task("Сходить на тренировку", "Сегодня в 15.00", 1, TaskStatus.DONE);
        Task task2 = new Task("Сходить ", "Сегодня", 1, TaskStatus.DONE);
        assertEquals(task1, task2);
    }
}

