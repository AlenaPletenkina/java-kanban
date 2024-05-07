package main.service;
import java.util.List;
import main.model.Task;

public interface HistoryManager {
    void add(Task task);

    List<Task> getHistory();

}
