package main.service;

import main.model.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {

    private Node head; //указатель на первый элемент списка
    private Node tail; // указатель на последний элемент списка
    Map<Integer, Node> customHashMap = new HashMap<>();


    @Override
    public void add(Task task) { // добавляет задачу в связный список и в customHashMap
        int taskId = task.getId();
        if (customHashMap.containsKey(taskId)) {
            Node node = customHashMap.remove(taskId);
            removeNode(node);
        }
        linkLast(task);
        customHashMap.put(taskId, tail);
    }

    @Override
    public void remove(int id) { //удаляет задачу из связного списка и customHashMap
        if (customHashMap.containsKey(id)) {
            Node node = customHashMap.get(id);
            customHashMap.remove(id);
            removeNode(node);
        }
    }

    @Override
    public List<Task> getHistory() { //предоставляет доступ к списку задач

        return getTasks();
    }

    public void linkLast(Task task) { // добавляет задачу в конец этого списка
        Node oldTail = tail;
        Node newNode = new Node(task, null, oldTail);
        tail = newNode;
        if (oldTail == null) {
            head = newNode;
        } else {
            oldTail.setNext(newNode);
        }
        int id = task.getId();
        customHashMap.put(id, newNode);
    }

    public List<Task> getTasks() { // собирать все задачи в обычный ArrayList
        List<Task> listTasks = new ArrayList<>();
        Node node = head;
        while (node != null) {
            listTasks.add(node.getTask());
            node = node.getNext();
        }
        return listTasks;
    }

    public void removeNode(Node node) { //удаляет узел из связного списка, сохраняя связность и корректность списка
        Node next = node.getNext();
        Node prev = node.getPrev();
        if (prev == null) {
            head = next;
        } else {
            prev.setNext(next);
            node.setPrev(null);
        }
        if (next == null) {
            tail = prev;
        } else {
            next.setPrev(prev);
            node.setNext(null);
        }
        node.setTask(null);
    }
}


