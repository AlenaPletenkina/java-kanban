package test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import main.service.Managers;


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