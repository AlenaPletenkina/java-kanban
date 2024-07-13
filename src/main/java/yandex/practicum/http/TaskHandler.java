package yandex.practicum.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import yandex.practicum.exception.TaskValidationException;
import yandex.practicum.model.Task;
import yandex.practicum.service.DurationTypeAdapter;
import yandex.practicum.service.LocalTimeTypeAdapter;
import yandex.practicum.service.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

import static java.util.Objects.isNull;

public class TaskHandler implements HttpHandler {
    TaskManager taskManager;
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private final Gson gson;
    String response;

    public TaskHandler(TaskManager newTaskManager) {
        this.taskManager = newTaskManager;
        this.gson = new GsonBuilder()
                .registerTypeAdapter(Duration.class,new DurationTypeAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalTimeTypeAdapter())
                .create();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = String.valueOf(exchange.getRequestURI());

        //InputStream inputStream = exchange.getRequestBody();
        //String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);

        System.out.println("Обрабатывается запрос " + path + " с методом " + method);
        switch (method) {
            case "GET":
                getTask(exchange);
                break;
            case "POST":
                addTask(exchange);
                break;
            case "DELETE":
                deleteTask(exchange);
                break;
            default:
                writeResponse(exchange, "Такой операции не существует", 404);
        }
    }

    private void getTask(HttpExchange exchange) throws IOException {
        if (exchange.getRequestURI().getQuery() == null) {
            response = gson.toJson(taskManager.getAllTasks());
            writeResponse(exchange, response, 200);
            return;
        }

        if (getTaskId(exchange).isEmpty()) {
            writeResponse(exchange, "Некорректный идентификатор!", 400);
            return;
        }

        int id = getTaskId(exchange).get();
        Task taskById = taskManager.getTaskById(id);
        if (isNull(taskById)) {
            System.out.println("Задачи с таким айди не найдено");
            writeResponse(exchange, "Задач с таким id не найдено!", 404);
            return;
        }
        response = gson.toJson(taskById);
        writeResponse(exchange, response, 200);
    }

    private void addTask(HttpExchange exchange) throws IOException {
        try {
            InputStream json = exchange.getRequestBody();
            String jsonTask = new String(json.readAllBytes(), DEFAULT_CHARSET);
            System.out.println("Получил Жсон на создание таск"+jsonTask);
            Task task = gson.fromJson(jsonTask, Task.class);
            System.out.println("Получил запрос на создание таски -"+task);
            if (task == null) {
                writeResponse(exchange, "Задача не должна быть пустой!", 400);
                return;
            }
            Task taskById = taskManager.getTaskById(task.getId());
            if (taskById == null) {
                System.out.println("Не нашел таску с айди"+task.getId());
                taskManager.createTask(task);
                writeResponse(exchange, "Задача успешно добавлена!", 201);
                return;
            }
            taskManager.updateTask(task);
            writeResponse(exchange, "Задача обновлена", 200);

        } catch (JsonSyntaxException e) {
            writeResponse(exchange, "Получен некорректный JSON", 400);
        }catch (TaskValidationException exp){
            writeResponse(exchange, "Найдено пересечение по времени!", 406);
        }
    }

    private void deleteTask(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery();
        System.out.println("Получил запрос на удаление таски "+query);
        if (query == null) {
            System.out.println("Запрос не содержит параметр ");
            writeResponse(exchange, "Не указан id задачи ", 404);
            return;
        }
        if (getTaskId(exchange).isEmpty()) {
            System.out.println("НЕ указан ид задачи ");
            writeResponse(exchange, "Не указан id задачи ", 404);
            return;
        }
        int id = getTaskId(exchange).get();
        if (taskManager.getTaskById(id) == null) {
            System.out.println("Задач с таким id не найдено");
            writeResponse(exchange, "Задач с таким id не найдено!", 404);
            return;
        }
        taskManager.removeTaskById(id);
        writeResponse(exchange, "Задача успешно удалена!", 200);
    }

    private Optional<Integer> getTaskId(HttpExchange exchange) {
        String[] pathParts = exchange.getRequestURI().getQuery().split("=");
        System.out.println("Получил запрос с айди равное "+ pathParts[1]);
        try {
            return Optional.of(Integer.parseInt(pathParts[1]));
        } catch (NumberFormatException exception) {
            return Optional.empty();
        }
    }

    public static void writeResponse(HttpExchange exchange,
                                     String responseString,
                                     int responseCode) throws IOException {
        if (responseString.isBlank()) {
            exchange.sendResponseHeaders(responseCode, 0);
        } else {
            byte[] bytes = responseString.getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(responseCode, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        }
        exchange.close();
    }
}
