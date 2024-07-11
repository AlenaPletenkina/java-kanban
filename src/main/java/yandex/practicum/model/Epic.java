package yandex.practicum.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class Epic extends Task {
    private List<Integer> subtasks;
    private LocalDateTime endTime;

    public Epic(String name, String description, int id, TaskStatus status, List<Integer> subtasks) {
        super(name, description, id, status);
        this.subtasks = subtasks;
    }

    public Epic(String name, String description, int id, TaskStatus status, List<Integer> subtasks, LocalDateTime startTime,
                Duration duration, LocalDateTime endTime) {
        super(name, description, id, status, duration, startTime);
        this.subtasks = subtasks;
        this.endTime = endTime;
    }

    public List<Integer> getSubtasks() {
        return subtasks;
    }

    public void setSubtasks(List<Integer> subtasks) {
        this.subtasks = subtasks;
    }


    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return super.toString()  + getId() + "," + endTime;
    }
}
