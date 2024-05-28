package main.service;

import java.util.List;

import main.model.Task;

public interface HistoryManager {
    void add(Task task);

    void remove(int id);

    List<Task> getHistory();

}
