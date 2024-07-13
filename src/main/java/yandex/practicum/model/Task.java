package yandex.practicum.model;

import com.google.gson.annotations.JsonAdapter;
import yandex.practicum.service.DurationTypeAdapter;
import yandex.practicum.service.LocalTimeTypeAdapter;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task implements Comparable {
    private String name;
    private String description;
    private Integer id;
    private TaskStatus status;
    private TaskType type;
    private Duration duration;
    private LocalDateTime startTime;


    public Task(String name, String description, int id, TaskStatus status) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.status = status;
        setType();


    }

    public Task(String name, String description, int id, TaskStatus status, Duration duration, LocalDateTime startTime) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.status = status;
        this.duration = duration;
        this.startTime = startTime;
        setType();
    }

    public Task() {
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Integer getId() {
        return id;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public TaskType getType() {
        return type;
    }

    private void setType() {
        for (TaskType type : TaskType.values()) {
            if (this.getClass().equals(type.getType())) {
                this.type = type;
            }
        }
    }

    @Override
    public String toString() {
        return id + "," + type.name() + "," + name + ","
                + status.name() + "," + description + "," + duration + "," + startTime + ",";

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    @Override
    public int compareTo(Object o) {
        Task task = (Task) o;
        return startTime.compareTo(task.startTime);

    }
}
