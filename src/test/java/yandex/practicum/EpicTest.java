package yandex.practicum;

import org.junit.jupiter.api.Test;
import yandex.practicum.model.Epic;
import yandex.practicum.model.TaskStatus;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EpicTest {

    @Test
    void epicAreEqualToEachOtherIfTheirIdIsEqual() { //экземпляры класса Epic равны друг другу, если равен их id
        Epic epic1 = new Epic("Сдать спринт", "Сдать спринт,чтобы пройти дальше по программе", 3,
                TaskStatus.IN_PROGRESS, new ArrayList<>());
        Epic epic2 = new Epic("Сдать ", "Сдать ", 3,
                TaskStatus.IN_PROGRESS, new ArrayList<>());
        assertEquals(epic1, epic2);
    }
}
