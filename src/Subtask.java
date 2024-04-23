public class Subtask extends Task {
    private Integer epic;

    public Subtask(String name, String description, int id, TaskStatus status, Integer epic) {
        super(name, description, id, status);
        this.epic = epic;
    }

    public Integer getEpic() {
        return epic;
    }

    public void setEpic(Integer epic) {
        this.epic = epic;
    }
}
