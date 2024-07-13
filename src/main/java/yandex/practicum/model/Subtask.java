package yandex.practicum.model;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {
    private Integer epic;

    public Subtask(String name, String description, int id, TaskStatus status, Integer epic) {
        super(name, description, id, status);
        this.epic = epic;
    }

    public Subtask(String name, String description, int id, TaskStatus status, Integer epic, LocalDateTime startTime,
                   Duration duration) {
        super(name, description, id, status,duration,startTime);
        this.epic = epic;
    }

    public Subtask(String name, String description, TaskStatus status) {
        super(name, description, status);
    }

    public Integer getEpic() {
        return epic;
    }

    public void setEpic(Integer epic) {
        this.epic = epic;
    }

    @Override
    public String toString() {
         return super.toString() + epic;
    }
}
