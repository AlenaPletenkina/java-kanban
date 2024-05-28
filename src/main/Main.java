package main;

import main.model.Epic;
import main.model.Subtask;
import main.model.Task;
import main.model.TaskStatus;
import main.service.InMemoryTaskManager;
import main.service.TaskManager;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        System.out.println("Поехали!");

        TaskManager taskManager = new InMemoryTaskManager();
        //Создаю две задачи, а также эпик с двумя подзадачами и эпик с одной подзадачей.
        Task task1 = new Task("Сходить на тренировку", "Сегодня в 15.00", 1, TaskStatus.DONE);
        Task task2 = new Task("Сделать уборку",
                "Прибраться в квартире после тренировки", 2,
                TaskStatus.NEW);
        Epic epic1 = new Epic("Сдать спринт", "Сдать спринт,чтобы пройти дальше по программе", 3,
                TaskStatus.IN_PROGRESS, new ArrayList<>());
        Epic epic2 = new Epic("Подготовка к отпуску",
                "Выполнить приготовления к перелету", 4, TaskStatus.DONE, new ArrayList<>());
        Subtask subtask1 = new Subtask("Задания в тренажере",
                "Сделать все задания в тренажере", 5, TaskStatus.DONE, 0);
        Subtask subtask2 = new Subtask("Сдать контрольную",
                "Выполнить контрольную и пройти ревью", 6, TaskStatus.NEW, 0);
        Subtask subtask3 = new Subtask("Собрать чемодан",
                "Сложить вещи и документы ", 7,
                TaskStatus.DONE, 4);

        Task createdTask1 = taskManager.createTask(task1);
        Task createdTask2 = taskManager.createTask(task2);

        Epic createdEpic1 = taskManager.createEpic(epic1);
        Epic createdEpic2 = taskManager.createEpic(epic2);
        subtask1.setEpic(createdEpic1.getId());
        subtask2.setEpic(createdEpic1.getId());
        subtask3.setEpic(createdEpic2.getId());

        Subtask createdSubtask1 = taskManager.createSubtask(subtask1);
        Subtask createdSubtask2 = taskManager.createSubtask(subtask2);
        Subtask createdSubtask3 = taskManager.createSubtask(subtask3);

        createdEpic1.setSubtasks(new ArrayList<>(List.of(createdSubtask1.getId(), createdSubtask2.getId())));
        createdEpic2.setSubtasks(new ArrayList<>(List.of(createdSubtask3.getId())));

        taskManager.updateEpic(createdEpic1);
        taskManager.updateEpic(createdEpic2);

        System.out.println("ВЫВОЖУ СОЗДАННЫЕ ЗАДАЧИ");
        System.out.println(createdTask1);
        System.out.println(createdTask2);
        System.out.println("ВЫВОЖУ СОЗДАННЫЕ ЭПИКИ");
        System.out.println(createdEpic1);
        System.out.println(createdEpic2);
        System.out.println("ВЫВОЖУ СОЗДАННЫЕ ПОДЗАДАЧИ");
        System.out.println(createdSubtask1);
        System.out.println(createdSubtask2);
        System.out.println(createdSubtask3);
        System.out.println("ПРОВЕРЯЮ СТАТУСЫ");
        System.out.println("Статус задачи1 = " + createdTask1.getStatus());
        System.out.println("Статус эпика1 = " + createdEpic1.getStatus());
        System.out.println("Статус подзадачи = " + createdSubtask1.getStatus());
        System.out.println("Статус подзадачи2 = " + createdSubtask2.getStatus());
        System.out.println("ИЗМЕНЯЮ СТАТУСЫ");
        createdSubtask2.setStatus(TaskStatus.DONE);
        taskManager.updateSubtask(createdSubtask2);
        createdTask1.setStatus(TaskStatus.NEW);
        taskManager.updateTask(createdTask1);
        System.out.println("Статус задачи1  = " + createdTask1.getStatus());
        System.out.println("Статус эпика1 = " + createdEpic1.getStatus());
        System.out.println("Статус подзадачи2 = " + createdSubtask2.getStatus());

        System.out.println("Количество эпиков = " + taskManager.getAllEpics().size());
        System.out.println("Количество подзадач = " + taskManager.getAllSubtasks().size());
        System.out.println("Количество задач = " + taskManager.getAllTasks().size());

        System.out.println("УДАЛЯЮ ОДНУ ЗАДАЧУ И ЭПИК");
        taskManager.removeTaskById(createdTask1.getId());
        taskManager.removeEpicById(createdEpic1.getId());

        System.out.println("Количество эпиков = " + taskManager.getAllEpics().size());
        System.out.println("Количество подзадач = " + taskManager.getAllSubtasks().size());
        System.out.println("Количество задач = " + taskManager.getAllTasks().size());

        System.out.println("ИСТОРИЯ");
        List<Task> history = taskManager.getHistory();
        System.out.println("В истории сохранено " + history.size() + " просмотров");
        taskManager.getEpicById(createdEpic2.getId());
        taskManager.getSubtaskById(createdEpic2.getSubtasks().get(0));

        history = taskManager.getHistory();
        System.out.println("В истории сохранено " + history.size() + " просмотров");
        System.out.println(history);


        //Создаю две задачи, эпик с тремя подзадачами и эпик без подзадач для спринта 6

        Task task5 = new Task("Сходить на тренировку в зал", "Сегодня в 15.00", 1, TaskStatus.DONE);
        Task task6 = new Task("Сделать уборку",
                "Прибраться в квартире после тренировки", 2,
                TaskStatus.NEW);

        Epic epic7 = new Epic("Сдать спринт", "Сдать спринт,чтобы пройти дальше по программе", 3,
                TaskStatus.IN_PROGRESS, new ArrayList<>());
        Epic epic8 = new Epic("Подготовка к отпуску",
                "Выполнить приготовления к перелету", 4, TaskStatus.DONE, new ArrayList<>());

        Subtask subtask9 = new Subtask("Задания в тренажере",
                "Сделать все задания в тренажере", 5, TaskStatus.DONE, 7);
        Subtask subtask10 = new Subtask("Сдать контрольную",
                "Выполнить контрольную и пройти ревью", 6, TaskStatus.NEW, 7);
        Subtask subtask11 = new Subtask("Пройти ревью",
                "Выполнить задание согласно ТЗ ", 7,
                TaskStatus.DONE, 7);

        Task createdTask5 = taskManager.createTask(task5);
        Task createdTask6 = taskManager.createTask(task6);

        Epic createdEpic7 = taskManager.createEpic(epic7);
        Epic createdEpic8 = taskManager.createEpic(epic8);

        subtask9.setEpic(createdEpic7.getId());
        subtask10.setEpic(createdEpic7.getId());
        subtask11.setEpic(createdEpic7.getId());

        Subtask createdSubtask9 = taskManager.createSubtask(subtask1);
        Subtask createdSubtask10 = taskManager.createSubtask(subtask2);
        Subtask createdSubtask11 = taskManager.createSubtask(subtask3);

        //Запрашиваю созданные задачи несколько раз в разном порядке.
        //После каждого запроса вывожу историю и убеждаюсь, что в ней нет повторов.

        System.out.println(taskManager.getHistory().size());
        taskManager.getTaskById(createdTask5.getId());
        taskManager.getTaskById(createdTask6.getId());
        taskManager.getEpicById(createdEpic7.getId());
        taskManager.getSubtaskById(createdSubtask9.getId());
        taskManager.getTaskById(createdTask5.getId());
        System.out.println(taskManager.getHistory().size());

        //Удаляю задачу, которая есть в истории, и проверяю, что при печати она не будет выводиться.

        taskManager.removeFromHistory(createdTask6.getId());
        System.out.println(taskManager.getHistory());

        //Удаляю эпик с тремя подзадачами и убеждаюсь, что из истории удалился как сам эпик, так и все его подзадачи
        taskManager.removeEpicById(createdEpic7.getId());
        List<Task> updatedHistory = taskManager.getHistory();
        System.out.println(updatedHistory);

        // TODO чтобы геттеры и сеттеры не влияли на таскМенеджер и его контекст,
        //  необходимо в методах получения задачи возвращать не сам объект, а его копию
    }
}


