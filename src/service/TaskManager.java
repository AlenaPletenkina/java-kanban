package service;

import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class TaskManager {
    private final HashMap<Integer, Task> tasks;
    private final HashMap<Integer, Epic> epics;
    private final HashMap<Integer, Subtask> subtasks;


    public static int count = 0;

    public TaskManager() {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
    }

    private int generateId() {
        return count++;
    }

    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    public void removeAllTasks() {
        for (Task task : tasks.values()) {
            tasks.clear();
        }
    }

    public void removeAllEpic() {
        for (Epic epic : epics.values()) {
            epics.clear();
        }
        for (Subtask subtask : subtasks.values()) {
            subtasks.clear();
        }
    }

    public void removeAllSubtasks() {
        for (Task subtask : subtasks.values()) {
            subtasks.clear();
        }
    }

    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        return task;
    }

    public Epic getEpicById(int id) {
        Epic epic = epics.get(id);
        return epic;
    }

    public Subtask getSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        return subtask;
    }

    public Task createTask(Task task) {
        task.setId(generateId());
        tasks.put(task.getId(), task);
        return task;
    }

    public Epic createEpic(Epic epic) {
        epic.setId(generateId());
        epics.put(epic.getId(), epic);
        return epic;
    }

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

    public void updateTask(Task task) { //обновление Таска
        tasks.put(task.getId(), task);
    }

    public void updateEpic(Epic epic) { //обновление Эпика
        if (epics.containsKey(epic.getId())) {
            TaskStatus status = epics.get(epic.getId()).getStatus();
            epic.setStatus(status);
            epics.put(epic.getId(), epic);
        }
    }

    public void updateSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId())) {
            subtasks.put(subtask.getId(), subtask);
            Epic epic = epics.get(subtask.getEpic());
            TaskStatus status = getStatus(epic);
            epic.setStatus(status);
        }
    }

    public void removeTaskById(int id) { // Удаление по идентификатору объекта model.Task
        tasks.remove(id);
    }

    public void removeEpicById(int id) {
        Epic epic = epics.get(id);
        List<Integer> subtasks1 = epic.getSubtasks();
        for (Integer subtask : subtasks1) {
            subtasks.remove(subtask);
        }
        epics.remove(id);
    }

    public void removeSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        Epic epic = epics.get(subtask.getEpic());
        List<Integer> subtasks1 = epic.getSubtasks();
        subtasks1.remove(subtask);
        TaskStatus status = getStatus(epic);
        epic.setStatus(status);
        subtasks.remove(id);
    }

    public List<Subtask> getSubtaskList(Epic epic) {
        List<Integer> subtasks1 = epic.getSubtasks();
        List<Subtask> subtaskList = new ArrayList<>();
        for (Integer id : subtasks1) {
            Subtask subtask = subtasks.get(id);
            subtaskList.add(subtask);
        }
        return subtaskList;
    }

    private TaskStatus getStatus(Epic epic) {
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

