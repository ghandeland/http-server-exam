package no.kristiania.db;

public class Task implements SetId {
    private long id;
    private String name;
    private String description;
    private TaskStatus status;

    public Task(String name, String description, String status) {
        this.name = name;
        this.description = description;
        this.status = TaskStatus.valueOf(status);
    }

    public Task(long id, String name, String description, String status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = TaskStatus.valueOf(status);
    }

    public static String taskStatusToString(TaskStatus taskStatus) {
        String taskStatusString = taskStatus.toString();
        taskStatusString = taskStatusString.substring(0, 1).toUpperCase() + taskStatusString.substring(1).toLowerCase();
        taskStatusString = taskStatusString.replace('_', ' ');
        return taskStatusString;
    }

    public long getId() {
        return id;
    }

    @Override
    public void setId(long id) {
        this.id = id;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = TaskStatus.valueOf(status);
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
