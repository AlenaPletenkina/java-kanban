package yandex.practicum.manager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import yandex.practicum.service.Managers;


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