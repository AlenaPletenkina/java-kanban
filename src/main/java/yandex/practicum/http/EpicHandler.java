package yandex.practicum.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import yandex.practicum.model.Epic;
import yandex.practicum.model.Subtask;
import yandex.practicum.service.DurationTypeAdapter;
import yandex.practicum.service.LocalTimeTypeAdapter;
import yandex.practicum.service.TaskManager;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static java.util.Objects.isNull;

public class EpicHandler implements HttpHandler {
    TaskManager taskManager;
    private final Gson gson;
    String response;

    public EpicHandler(TaskManager newTaskManager) {
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
                getEpic(exchange);
                break;
            case "POST":
                addEpic(exchange);
                break;
            case "DELETE":
                deleteEpic(exchange);
                break;
            default:
                writeResponse(exchange, "Такого операции не существует", 404);
        }
    }

    private void getEpic(HttpExchange exchange) throws IOException {
        if (exchange.getRequestURI().getQuery() == null) {
            response = gson.toJson(taskManager.getAllEpics());
            writeResponse(exchange, response, 200);
            return;
        }
        if (exchange.getRequestURI().toString().contains("subtasks")) {
            Integer id = getTaskId(exchange).get();
            Epic epic = taskManager.getEpicById(id);
            List<Subtask> result = taskManager.getSubtaskList(epic);
            response = gson.toJson(result);
            writeResponse(exchange, response, 200);
            return;
        }
        if (getTaskId(exchange).isEmpty()) {
            writeResponse(exchange, "Некорректный идентификатор!", 400);
            return;
        }

        int id = getTaskId(exchange).get();
        Epic epicById = taskManager.getEpicById(id);
        if (isNull(epicById)) {
            writeResponse(exchange, "Задач с таким id не найдено!", 404);
            return;
        }
        response = gson.toJson(epicById);
        writeResponse(exchange, response, 200);
    }

    private void addEpic(HttpExchange exchange) throws IOException {
        try {
            InputStream json = exchange.getRequestBody();
            String jsonTask = new String(json.readAllBytes(), StandardCharsets.UTF_8);
            Epic epic = gson.fromJson(jsonTask, Epic.class);
            if (epic == null) {
                writeResponse(exchange, "Задача не должна быть пустой!", 400);
                return;
            }
            Epic epicById = taskManager.getEpicById(epic.getId());
            if (epicById == null) {
                taskManager.createEpic(epic);
                writeResponse(exchange, "Задача успешно добавлена!", 201);
                return;
            }
            taskManager.updateEpic(epic);
            writeResponse(exchange, "Эпик обновлен!", 200);

        } catch (JsonSyntaxException e) {
            writeResponse(exchange, "Получен некорректный JSON", 400);
        }
    }

    private void deleteEpic(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery();
        System.out.println("Получил запрос на удаление эпика "+query);
        if (query == null) {
            System.out.println("Запрос не содержит параметр ");
            writeResponse(exchange, "Не указан id эпика!", 404);
            return;
        }
        if (getTaskId(exchange).isEmpty()) {
            System.out.println("НЕ указан ид задачи ");
            writeResponse(exchange, "Не указан id эпика!", 404);
            return;
        }
        int id = getTaskId(exchange).get();
        if (taskManager.getEpicById(id) == null) {
            System.out.println("Задач с таким id не найдено");
            writeResponse(exchange, "Эпиков с таким id не найдено!", 404);
            return;
        }
        taskManager.removeEpicById(id);
        writeResponse(exchange, "Задача успешно удалена!", 200);
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
