package yandex.practicum.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ManagersTest {
    @Test
    void utilityClassAlwaysReturnsReadyToUseInstances() {
        //утилитарный класс всегда возвращает проинициализированные и готовые к работе экземпляры менеджеров
        Assertions.assertNotNull(Managers.getDefault());
    }

    @Test
    void methodGetDefaultHistoryDontGetNull() {
        Assertions.assertNotNull(Managers.getDefaultHistory());
    }
}