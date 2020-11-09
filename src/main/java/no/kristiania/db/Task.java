package no.kristiania.db;

public class Task implements SetId {
    private final String name;
    private final String description;
    private final TaskStatus status;
    private long id;

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

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
