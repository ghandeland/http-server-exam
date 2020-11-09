package no.kristiania.db;

public class TaskMember implements SetId {
    private final long taskId;
    private final long memberId;
    private long id;

    public TaskMember(long taskId, long memberId) {
        this.taskId = taskId;
        this.memberId = memberId;
    }

    @Override
    public void setId(long id) {
        this.id = id;
    }

    public long getTaskId() {
        return taskId;
    }

    public long getMemberId() {
        return memberId;
    }
}
