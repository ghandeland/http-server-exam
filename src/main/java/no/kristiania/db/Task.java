package no.kristiania.db;

public class Task implements SetId {
    public enum TaskStatus {OPEN, IN_PROGRESS, FINISHED, CANCELED};

    long id;
    String name;
    String description;
    TaskStatus status;

    public Task() {}

    public Task(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        this.status = TaskStatus.OPEN;
    }

    public Task(String taskName, String taskDescription, String taskStatus) {

        this.name = taskName;
        this.description = taskDescription;
        this.status = status.valueOf(taskStatus);
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = TaskStatus.valueOf(status);
    }

    public long getId() {
        return id;
    }

    @Override
    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
