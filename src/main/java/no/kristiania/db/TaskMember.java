package no.kristiania.db;

public class TaskMember implements SetId {
    private long id;
    private long taskId;
    private long memberId;

    public TaskMember() {
    }

    public TaskMember(long taskId, long memberId) {
        this.taskId = taskId;
        this.memberId = memberId;
    }

    public long getId() { return id; }

    @Override
    public void setId(long id) { this.id = id; }

    public long getTaskId() {
        return taskId;
    }

    public void setTaskId(long taskId) {
        this.taskId = taskId;
    }

    public long getMemberId() {
        return memberId;
    }

    public void setMemberId(long memberId) {
        this.memberId = memberId;
    }
}
