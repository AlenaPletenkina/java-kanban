package yandex.practicum.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
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
        return new Task("Test addNewTask", "Test addNewTask description", NEW);
    }

    public Task addTaskServer() throws IOException, InterruptedException {
        task.setId(1);
        return task;
    }

    public Epic addEpicServer() throws IOException, InterruptedException {
        epic.setId(2);
        return epic;
    }

    public Subtask addSubtaskServer() throws IOException, InterruptedException {
        int epicId = epic.getId();
        subtask.setId(3);
        subtask.setEpic(epicId);
        return subtask;
    }

    public HttpResponse<String> postTask(URI url, Task task, HttpClient client) throws IOException, InterruptedException {

        String json = gson.toJson(task);
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response;
    }

    public Subtask createSubtask() {
        return new Subtask("Test addNewSubtask", "Test addNewSubtask description", NEW);
    }

    public Epic createEpic() {
        return new Epic("Test addNewEpic", "Test addNewEpic description", NEW);
    }

    @BeforeAll
    static void setUp() throws IOException, InterruptedException {

        httpTS = new HttpTaskServer();
        httpTS.start();
        gson = new GsonBuilder()
                .registerTypeAdapter(Duration.class, new DurationTypeAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalTimeTypeAdapter())
                .create();
    }

    @AfterAll

    static void tearDown() {
        httpTS.stop();
    }

    @Test
    void addTasksToTaskServerEndUpdate() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        Task task1 = addTaskServer();
        URI url = URI.create(TASK_BASE_URL);
        assertEquals(201, postTask(url, task1, client).statusCode(), "Ошибка в добавлении новой задачи");

        String json = gson.toJson(task1);
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Обновление задачи не прошло");

        Epic epic1 = addEpicServer();
        url = URI.create(EPIC_BASE_URL);
        json = gson.toJson(epic1);
        assertEquals(201, postTask(url, epic1, client).statusCode(), "Ошибка в добавлении новой задачи");
        body = HttpRequest.BodyPublishers.ofString(json);
        request = HttpRequest.newBuilder().uri(url).POST(body).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Обновление задачи не прошло");

        Subtask subtask1 = addSubtaskServer();
        url = URI.create(SUBTASK_BASE_URL);
        json = gson.toJson(subtask1);
        assertEquals(201, postTask(url, subtask1, client).statusCode(), "Ошибка в добавлении новой задачи");
        body = HttpRequest.BodyPublishers.ofString(json);
        request = HttpRequest.newBuilder().uri(url).POST(body).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Обновление задачи не прошло");
    }

    @Test
    void getAllTasksAndTasks_byId() throws IOException, InterruptedException {

        HttpClient client = HttpClient.newHttpClient();

        postTask(URI.create(TASK_BASE_URL), addTaskServer(), client);
        URI url = URI.create(TASK_BASE_URL + "?id=1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        Task task = gson.fromJson(response.body(), Task.class);
        assertEquals("Test addNewTask", task.getName());

        postTask(URI.create(EPIC_BASE_URL), addEpicServer(), client);
        url = URI.create(EPIC_BASE_URL + "?id=2");
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        Epic epic = gson.fromJson(response.body(), Epic.class);
        assertEquals("Test addNewEpic", epic.getName());


        postTask(URI.create(SUBTASK_BASE_URL), addSubtaskServer(), client);
        url = URI.create(SUBTASK_BASE_URL + "?id=3");
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        Subtask subtask = gson.fromJson(response.body(), Subtask.class);
        assertEquals("Test addNewSubtask", subtask.getName());
    }

    @Test
    void deleteTaskAll_byId() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        System.out.println(postTask(URI.create(TASK_BASE_URL), addTaskServer(), client));
        System.out.println(postTask(URI.create(EPIC_BASE_URL), addEpicServer(), client));
        System.out.println(postTask(URI.create(SUBTASK_BASE_URL), addSubtaskServer(), client));

        URI url = URI.create(SUBTASK_BASE_URL + "?id=3");
        HttpRequest request1 = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
        assertEquals("Задача успешно удалена!", response1.body());

        url = URI.create(SUBTASK_BASE_URL);
        HttpRequest request2 = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        assertEquals("Задачи успешно удалены!", response2.body());

        url = URI.create(TASK_BASE_URL + "?id=1");
        request1 = HttpRequest.newBuilder().uri(url).DELETE().build();
        response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
        assertEquals("Задача успешно удалена!", response1.body());

        url = URI.create(TASK_BASE_URL);
        request2 = HttpRequest.newBuilder().uri(url).DELETE().build();
        response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        assertEquals("Задачи удалены!", response2.body());

        url = URI.create(EPIC_BASE_URL + "?id=2");
        HttpRequest request3 = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());
        assertEquals("Задача успешно удалена!", response3.body());

        url = URI.create(EPIC_BASE_URL);
        HttpRequest request4 = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response4 = client.send(request4, HttpResponse.BodyHandlers.ofString());
        assertEquals("Задачи удалены!", response4.body());
    }
}

