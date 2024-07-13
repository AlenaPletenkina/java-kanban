package yandex.practicum;

import com.sun.net.httpserver.HttpServer;
import yandex.practicum.http.*;
import yandex.practicum.service.Managers;
import yandex.practicum.service.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;


public class HttpTaskServer {
    public  HttpServer httpServer;

    private static final int PORT = 8080;
    private static final TaskManager taskManager = Managers.getDefault();

    public HttpTaskServer() throws IOException {
        httpServer = HttpServer.create();
        httpServer.bind(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks", new TaskHandler(taskManager));
        httpServer.createContext("/epics", new EpicHandler(taskManager));
        httpServer.createContext("/subtasks", new SubtaskHandler(taskManager));
        httpServer.createContext("/history", new HistoryHandler(taskManager));
        httpServer.createContext("/prioritized", new PrioritizedTasksHandler(taskManager));
    }

    public static void main(String[] args) throws IOException {

    }
    public void start() {
        httpServer.start();
    }

    public void stop() {
        httpServer.stop(0);
    }
}

