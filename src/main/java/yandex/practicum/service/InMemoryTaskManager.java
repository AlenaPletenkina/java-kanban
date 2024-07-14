package yandex.practicum.service;

import yandex.practicum.exception.TaskValidationException;
import yandex.practicum.model.Epic;
import yandex.practicum.model.Subtask;
import yandex.practicum.model.Task;
import yandex.practicum.model.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    protected final HashMap<Integer, Task> tasks;
    protected final HashMap<Integer, Epic> epics;
    protected final HashMap<Integer, Subtask> subtasks;
    protected final HistoryManager historyManager;
    protected final TreeSet<Task> sortedTasks;

    public static int count = 0;

    public InMemoryTaskManager() {
        this.historyManager = new InMemoryHistoryManager();
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
        sortedTasks = new TreeSet<>();
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
        updateSortedTask();
    }

    @Override
    public void removeAllEpic() {
        epics.clear();
        subtasks.clear();
        updateSortedTask();
    }

    @Override
    public void removeAllSubtasks() {
        subtasks.clear();
        List<Epic> allEpics = getAllEpics();
        allEpics.forEach(epic -> epic.getSubtasks().clear());
        updateSortedTask();
    }

    @Override
    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        if (task != null) {
            historyManager.add(task);
        }
        return task;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            historyManager.add(epic);
        }
        return epic;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            historyManager.add(subtask);
        }
        return subtask;
    }

    @Override
    public Task createTask(Task task) {
        validateTaskStartTime(task);
        task.setId(generateId());
        tasks.put(task.getId(), task);
        updateSortedTask();
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
        validateTaskStartTime(subtask);
        int id = generateId();
        subtask.setId(id);
        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(subtask.getEpic());

        if (epic != null) {
            List<Integer> subtasks1 = epic.getSubtasks();
            if (subtasks1 == null) {
                subtasks1 = new ArrayList<>();
            }
            subtasks1.add(id);
            System.out.println("");
            TaskStatus status = getStatus(epic);
            epic.setStatus(status);
            epic.setDuration(getDuration(epic));
            epic.setStartTime(getStartTime(epic));
            epic.setEndTime(getEndTime(epic));
            updateSortedTask();
        }
        return subtask;
    }

    @Override
    public void updateTask(Task task) {
        validateTaskStartTime(task);
        tasks.put(task.getId(), task);
        updateSortedTask();
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
                updateSortedTask();
            }
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        validateTaskStartTime(subtask);
        if (subtasks.containsKey(subtask.getId())) {
            Epic epic = epics.get(subtask.getEpic());
            if (epic.getId() == subtask.getId()) {
                throw new RuntimeException("Id эпика совпадает с Id подзадачи");
            }
            subtasks.put(subtask.getId(), subtask);
            TaskStatus status = getStatus(epic);
            epic.setDuration(getDuration(epic));
            epic.setStartTime(getStartTime(epic));
            epic.setEndTime(getEndTime(epic));
            epic.setStatus(status);
            updateSortedTask();
        }
    }

    @Override
    public void removeTaskById(int id) {
        tasks.remove(id);
        historyManager.remove(id);
        updateSortedTask();
    }

    @Override
    public void removeEpicById(int id) {
        Epic epic = epics.get(id);
        List<Integer> subtasks1 = epic.getSubtasks();

        if (subtasks1 != null) {
            subtasks1.forEach(subtask -> {
                subtasks.remove(subtask);
                historyManager.remove(subtask);
            });
        }
        epics.remove(id);
        historyManager.remove(id);
        updateSortedTask();
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
        epic.setDuration(getDuration(epic));
        epic.setStartTime(getStartTime(epic));
        epic.setEndTime(getEndTime(epic));
        subtasks.remove(id);
        historyManager.remove(id);
        updateSortedTask();
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

    public Duration getDuration(Epic epic) {
        List<Subtask> subtasks1 = getSubtaskList(epic);
        Duration totalDuration = Duration.ofMinutes(0);
        for (Subtask subtask : subtasks1) {
            if (subtask.getDuration() != null) {
                totalDuration = totalDuration.plus(subtask.getDuration());
            }
        }
        return totalDuration;
    }

    public LocalDateTime getStartTime(Epic epic) {
        List<Subtask> subtasks2 = getSubtaskList(epic);
        LocalDateTime startTime = null;
        for (Subtask subtask : subtasks2) {
            LocalDateTime currentStartTime = subtask.getStartTime();
            if (startTime == null || currentStartTime.isBefore(startTime)) {
                startTime = currentStartTime;
            }
        }
        return startTime;
    }

    public LocalDateTime getEndTime(Epic epic) {
        Duration duration = epic.getDuration();

        LocalDateTime startTime = epic.getStartTime();
        if (startTime == null) {
            return null;
        }
        LocalDateTime endTime = startTime.plus(duration);

        return endTime;
    }

    @Override
    public TreeSet<Task> getPrioritizedTasks() {
        return sortedTasks;
    }

    private void updateSortedTask() {
        List<Task> allTasks = getAllTasks();
        List<Subtask> allSubtasks = getAllSubtasks();
        allTasks.addAll(allSubtasks);
        List<Task> tasksWithStartTime = allTasks.stream()
                .filter(task -> task.getStartTime() != null)
                .toList();
        sortedTasks.clear();
        sortedTasks.addAll(tasksWithStartTime);
    }

    private void validateTaskStartTime(Task task) {
        LocalDateTime startTime = task.getStartTime();
        LocalDateTime endTime = startTime.plus(task.getDuration());
        for (Task otherTask : sortedTasks) {
            LocalDateTime otherStartTime = otherTask.getStartTime();
            LocalDateTime otherEndTime = otherStartTime.plus(otherTask.getDuration());
            boolean isBefore = endTime.isBefore(otherStartTime);
            // Проверяем, пересекаются ли интервалы
            if (!isBefore) {
                if (!otherEndTime.isBefore(startTime)) {
                    throw new TaskValidationException("Найдено пересечение времени задач.");
                }
            }
        }
    }
}
