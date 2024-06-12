package yandex.practicum.service;

import yandex.practicum.model.Epic;
import yandex.practicum.model.Subtask;
import yandex.practicum.model.Task;
import yandex.practicum.model.TaskStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    protected final HashMap<Integer, Task> tasks;
    protected final HashMap<Integer, Epic> epics;
    protected final HashMap<Integer, Subtask> subtasks;
    protected final HistoryManager historyManager;

    public static int count = 0;

    public InMemoryTaskManager() {
        this.historyManager = new InMemoryHistoryManager();
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
    }

    @Override
    public List<Task> getHistory() {
        List<Task> history = historyManager.getHistory();
        return history;
    }

    @Override
    public void removeFromHistory(int id) {
        historyManager.remove(id);
    }

    private int generateId() {
        return count++;
    }

    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void removeAllTasks() {
            tasks.clear();

    }

    @Override
    public void removeAllEpic() {
        for (Epic epic : epics.values()) {
            epics.clear();
        }
        for (Subtask subtask : subtasks.values()) {
            subtasks.clear();
        }
    }

    @Override
    public void removeAllSubtasks() {
        for (Task subtask : subtasks.values()) {
            subtasks.clear();
        }
    }

    @Override
    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        historyManager.add(task);
        return task;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = epics.get(id);
        historyManager.add(epic);
        return epic;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        historyManager.add(subtask);
        return subtask;
    }

    @Override
    public Task createTask(Task task) {
        task.setId(generateId());
        tasks.put(task.getId(), task);
        return task;
    }

    @Override
    public Epic createEpic(Epic epic) {
        epic.setId(generateId());
        epics.put(epic.getId(), epic);
        return epic;
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        int id = generateId();
        subtask.setId(id);
        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(subtask.getEpic());
        if (epic != null) {
            List<Integer> subtasks1 = epic.getSubtasks();
            subtasks1.add(id);
        }
        return subtask;
    }

    @Override
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            List<Integer> subtasks1 = epic.getSubtasks();
            boolean hasError = false;
            for (Integer id : subtasks1) {
                if (epic.getId() == id) {
                    hasError = true;
                    break;
                }
            }
            if (!hasError) {
                TaskStatus status = epics.get(epic.getId()).getStatus();
                epic.setStatus(status);
                epics.put(epic.getId(), epic);
            }

        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId())) {
            Epic epic = epics.get(subtask.getEpic());
            if (epic.getId() == subtask.getId()) {
                throw new RuntimeException("Id эпика совпадает с Id подзадачи");
            }
            subtasks.put(subtask.getId(), subtask);
            TaskStatus status = getStatus(epic);
            epic.setStatus(status);
        }
    }

    @Override
    public void removeTaskById(int id) {
        tasks.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void removeEpicById(int id) {
        Epic epic = epics.get(id);
        List<Integer> subtasks1 = epic.getSubtasks();
        for (Integer subtask : subtasks1) {
            subtasks.remove(subtask);
            historyManager.remove(subtask);
        }
        epics.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void removeSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        Epic epic = epics.get(subtask.getEpic());
        List<Integer> subtasks1 = epic.getSubtasks();
        int index = -1;
        for (Integer subtaskId : subtasks1) {
            index++;
            if (subtaskId == subtask.getId()) {
                break;
            }
        }
        subtasks1.remove(index);
        TaskStatus status = getStatus(epic);
        epic.setStatus(status);
        subtasks.remove(id);
        historyManager.remove(id);
    }

    @Override
    public List<Subtask> getSubtaskList(Epic epic) {
        List<Integer> subtasks1 = epic.getSubtasks();
        List<Subtask> subtaskList = new ArrayList<>();
        for (Integer id : subtasks1) {
            Subtask subtask = subtasks.get(id);
            subtaskList.add(subtask);
        }
        return subtaskList;
    }

    public TaskStatus getStatus(Epic epic) {
        List<Subtask> subtasks1 = getSubtaskList(epic);
        int count = 0;
        int countNew = 0;
        for (Subtask subtask : subtasks1) {
            if (subtask.getStatus().equals(TaskStatus.IN_PROGRESS)) {
                return TaskStatus.IN_PROGRESS;
            }
            if (subtask.getStatus().equals(TaskStatus.DONE)) {
                count += 1;
                if (count == subtasks1.size()) {
                    return TaskStatus.DONE;
                }
            }
            if (subtask.getStatus().equals(TaskStatus.NEW)) {
                countNew += 1;
                if (countNew == subtasks1.size()) {
                    return TaskStatus.NEW;
                }
            }
        }
        return TaskStatus.IN_PROGRESS;
    }
}
