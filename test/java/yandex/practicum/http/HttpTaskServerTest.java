package yandex.practicum.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.*;
import yandex.practicum.HttpTaskServer;
import yandex.practicum.model.Epic;
import yandex.practicum.model.Subtask;
import yandex.practicum.model.Task;
import yandex.practicum.service.DurationTypeAdapter;
import yandex.practicum.service.LocalTimeTypeAdapter;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static yandex.practicum.model.TaskStatus.NEW;

public class HttpTaskServerTest {
    private static final String TASK_BASE_URL = "http://localhost:8080/tasks";
    private static final String EPIC_BASE_URL = "http://localhost:8080/epics";
    private static final String SUBTASK_BASE_URL = "http://localhost:8080/subtasks";

    private static HttpTaskServer httpTS;
    private static Gson gson;

    protected Task task = createTask();
    protected Epic epic = createEpic();
    protected Subtask subtask = createSubtask();

    public Task createTask() {
        return new Task("Test addNewTask", "Test addNewTask description", 1000, NEW, Duration.ofMinutes(100),
                LocalDateTime.parse(("12.12.24 23:10"), DateTimeFormatter.ofPattern("dd.MM.yy HH:mm")));
    }

    public Subtask createSubtask() {
        return new Subtask("Test addNewSubtask", "Test addNewSubtask description", 1000, NEW, 1,
                LocalDateTime.parse(("10.10.23 23:10"), DateTimeFormatter.ofPattern("dd.MM.yy HH:mm")), Duration.ofMinutes(10));
    }

    public Epic createEpic() {
        return new Epic("Test addNewEpic", "Test addNewEpic description", 1000, NEW, new ArrayList<>());
    }

    public Subtask addSubtaskServer() throws IOException, InterruptedException {

        subtask.setEpic(1);
        return subtask;
    }

    public HttpResponse<String> postTask(URI url, Task task, HttpClient client) throws IOException, InterruptedException {
        String json = gson.toJson(task);
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response;
    }

    @BeforeEach
    void setUp() throws IOException, InterruptedException {
        httpTS = new HttpTaskServer();
        httpTS.start();
        gson = new GsonBuilder()
                .registerTypeAdapter(Duration.class, new DurationTypeAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalTimeTypeAdapter())
                .create();
    }

    @AfterEach
    void tearDown() {
        httpTS.stop();
    }

    @Test
    void addTasksToTaskServerEndUpdate() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        Task task1 = task;
        URI url = URI.create(TASK_BASE_URL);

        assertEquals(201, postTask(url, task1, client).statusCode(), "Задача успешно добавлена");
        assertEquals(406, postTask(url, task1, client).statusCode(), "Найдено пересечение по времени");

        url = URI.create(EPIC_BASE_URL);

        assertEquals(201, postTask(url, epic, client).statusCode(), "Задача успешно добавлена");
        assertEquals(201, postTask(url, epic, client).statusCode(), "Задача успешно добавлена");

        url = URI.create(SUBTASK_BASE_URL);

        assertEquals(201, postTask(url, subtask, client).statusCode(), "Задача успешно добавлена");
    }

    @Test
    void getAllTasksAndTasks_byId() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        postTask(URI.create(TASK_BASE_URL), task, client);
        URI url = URI.create(TASK_BASE_URL + "?id=0");

        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        Task task = gson.fromJson(response.body(), Task.class);
        assertEquals("Test addNewTask", task.getName());

        postTask(URI.create(EPIC_BASE_URL), epic, client);
        url = URI.create(EPIC_BASE_URL + "?id=1");

        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        Epic epic = gson.fromJson(response.body(), Epic.class);
        assertEquals("Test addNewEpic", epic.getName());

        postTask(URI.create(SUBTASK_BASE_URL), addSubtaskServer(), client);
        url = URI.create(SUBTASK_BASE_URL + "?id=2");

        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        Subtask subtask = gson.fromJson(response.body(), Subtask.class);
        assertEquals("Test addNewSubtask", subtask.getName());
    }

    @Test
    void deleteTaskAll_byId() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        System.out.println(postTask(URI.create(TASK_BASE_URL), task, client));
        System.out.println(postTask(URI.create(EPIC_BASE_URL), epic, client));
        System.out.println(postTask(URI.create(SUBTASK_BASE_URL), addSubtaskServer(), client));

        URI url = URI.create(SUBTASK_BASE_URL + "?id=2");

        HttpRequest request1 = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
        assertEquals("Подзадача успешно удалена!", response1.body());

        url = URI.create(TASK_BASE_URL + "?id=0");

        request1 = HttpRequest.newBuilder().uri(url).DELETE().build();
        response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
        assertEquals("Задача успешно удалена!", response1.body());

        url = URI.create(EPIC_BASE_URL + "?id=1");

        HttpRequest request3 = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());
        assertEquals("Задача успешно удалена!", response3.body());
    }
}

