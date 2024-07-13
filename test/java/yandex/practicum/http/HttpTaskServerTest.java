package yandex.practicum.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import yandex.practicum.HttpTaskServer;
import yandex.practicum.service.DurationTypeAdapter;
import yandex.practicum.service.LocalTimeTypeAdapter;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServerTest {
    public static HttpTaskServer taskServer;
    public static Gson gson = new GsonBuilder()
                .registerTypeAdapter(Duration .class,new DurationTypeAdapter())
            .registerTypeAdapter(LocalDateTime .class, new LocalTimeTypeAdapter())
            .create();

    @BeforeAll
    static void startServer() {
        try {
            taskServer = new HttpTaskServer();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @AfterAll
    static void stopServer() {

    }

}
