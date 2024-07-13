package yandex.practicum.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import yandex.practicum.exception.TaskValidationException;
import yandex.practicum.model.Subtask;
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

public class SubtaskHandler implements HttpHandler {
    TaskManager taskManager;
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private final Gson gson;
    String response;

    public SubtaskHandler(TaskManager newTaskManager) {
        this.taskManager = newTaskManager;
        this.gson = new GsonBuilder()
                .registerTypeAdapter(Duration.class,new DurationTypeAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalTimeTypeAdapter())
                .create();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        switch (method) {
            case "GET":
                getSubTask(exchange);
                break;
            case "POST":
                addSubTask(exchange);
                break;
            case "DELETE":
                deleteSubTask(exchange);
                break;
            default:
                writeResponse(exchange, "Такого операции не существует", 404);
        }
    }

    private void getSubTask(HttpExchange exchange) throws IOException {
        if (exchange.getRequestURI().getQuery() == null) {
            response = gson.toJson(taskManager.getAllSubtasks());
            writeResponse(exchange, response, 200);
            return;
        }
        if (getTaskId(exchange).isEmpty()) {
            writeResponse(exchange, "Некорректный идентификатор!", 400);
            return;
        }
        int id = getTaskId(exchange).get();
        Subtask subtaskById = taskManager.getSubtaskById(id);
        if (isNull(subtaskById)) {
            writeResponse(exchange, "Задач с таким id не найдено!", 404);
            return;
        }
        response = gson.toJson(subtaskById);
        writeResponse(exchange, response, 200);
    }

    private void addSubTask(HttpExchange exchange) throws IOException {
        try {
            InputStream json = exchange.getRequestBody();
            String jsonTask = new String(json.readAllBytes(), DEFAULT_CHARSET);
            Subtask subTask = gson.fromJson(jsonTask, Subtask.class);
            if (subTask == null) {
                writeResponse(exchange, "Задача не должна быть пустой!", 400);
                return;
            }
            Subtask subtaskById = taskManager.getSubtaskById(subTask.getId());
            if (subtaskById == null) {
                taskManager.createSubtask(subTask);
                writeResponse(exchange, "Задача успешно добавлена!", 201);
                return;
            }
            taskManager.updateSubtask(subTask);
            writeResponse(exchange, "Задача обновлена!", 200);

        } catch (JsonSyntaxException e) {
            writeResponse(exchange, "Получен некорректный JSON", 400);
        } catch(TaskValidationException exp) {
            writeResponse(exchange, "Пересечение по времени ", 406);
        }
    }

    private void deleteSubTask(HttpExchange exchange) throws IOException {
        if (exchange.getRequestURI().getQuery() == null) {
            writeResponse(exchange, "Не указан id подзадачи!", 404);
            return;
        }
        if (getTaskId(exchange).isEmpty()) {
            writeResponse(exchange, "Некорректный id подзадачи!", 404);
            return;
        }
        int id = getTaskId(exchange).get();
        if (taskManager.getSubtaskById(id) == null) {
            writeResponse(exchange, "Подзадачи с таким id не найдено!", 404);
            return;
        }
    taskManager.removeSubtaskById(id);
    writeResponse(exchange, "Подзадача успешно удалена!",200);
}

    private Optional<Integer> getTaskId(HttpExchange exchange) {
        String[] pathParts = exchange.getRequestURI().getQuery().split("=");
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
