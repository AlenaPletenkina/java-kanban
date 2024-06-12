package yandex.practicum.service;

import yandex.practicum.exception.ManagerSaveException;
import yandex.practicum.model.Epic;
import yandex.practicum.model.Subtask;
import yandex.practicum.model.Task;
import yandex.practicum.model.TaskStatus;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;

import static java.util.Objects.isNull;
import static yandex.practicum.model.TaskType.*;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private Path file;

    public FileBackedTaskManager(Path file) {
        super();
        this.file = file;
    }

    public FileBackedTaskManager() {
        super();
    }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        save();
    }

    @Override
    public void removeAllEpic() {
        super.removeAllEpic();
        save();
    }

    @Override
    public void removeAllSubtasks() {
        super.removeAllSubtasks();
        save();
    }

    @Override
    public Task createTask(Task task) {
        Task task1 = super.createTask(task);
        save();
        return task1;
    }

    @Override
    public Epic createEpic(Epic epic) {
        Epic epic1 = super.createEpic(epic);
        save();
        return epic1;
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        Subtask subtask1 = super.createSubtask(subtask);
        save();
        return subtask1;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void removeTaskById(int id) {
        super.removeTaskById(id);
        save();
    }

    @Override
    public void removeEpicById(int id) {
        super.removeEpicById(id);
        save();
    }

    @Override
    public void removeSubtaskById(int id) {
        super.removeSubtaskById(id);
        save();
    }

    public void save() {
        try (FileWriter writer = new FileWriter(file.toFile(), false)) {
            writer.write("id,type,name,status,description,epic\n");
            for (Integer key : tasks.keySet()) {
                writer.write(tasks.get(key).toString() + "\n");
            }
            for (Integer key : epics.keySet()) {
                writer.write(epics.get(key).toString() + "\n");
            }
            for (Integer key : subtasks.keySet()) {
                writer.write(subtasks.get(key).toString() + "\n");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static Task fromString(String value) { //  создание задачи из строки
        String[] linesTask = value.split(",", 6);
        int id = Integer.parseInt(linesTask[0]);
        String name = linesTask[2];
        String type = linesTask[1];
        TaskStatus status = TaskStatus.valueOf(linesTask[3]);
        String description = linesTask[4];
        if (type.equals(EPIC.name())) {
            return new Epic(name, description, id, status, new ArrayList<>());
        } else if (type.equals((SUBTASK.name()))) {
            int epic = Integer.parseInt(linesTask[5]);
            return new Subtask(name, description, id, status, epic);
        } else {
            return new Task(name, description, id, status);
        }

    }

    public void loadFromFile(File file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file.toString()))) {
            reader.readLine();
            while (true) {
                String line = reader.readLine();
                if (isNull(line) || line.isEmpty()) {
                    break;
                }
                Task task = fromString(line);
                if (task.getType().equals(TASK)) {
                    tasks.put(task.getId(), task);
                } else if (task.getType().equals(EPIC)) {
                    epics.put(task.getId(), (Epic) task);
                } else {
                    Subtask subtask = (Subtask) task;
                    subtasks.put(task.getId(), subtask);
                    Epic epic = epics.get(subtask.getEpic());
                    epic.getSubtasks().add(subtask.getId());
                }
                count += 1;
            }
        } catch (FileNotFoundException e) {
            throw new ManagerSaveException("Файл не найден.");
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка чтения файла.");
        }
    }
}

