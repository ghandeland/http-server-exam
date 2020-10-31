package no.kristiania.db;

public class TaskMember implements SetId {
    private long taskId;
    private long memberId;

    public TaskMember() {
    }

    public TaskMember(long taskId, long memberId) {
        this.taskId = taskId;
        this.memberId = memberId;
    }

    public long getTaskId() {
        return taskId;
    }

    @Override
    public void setId(long taskId) {
        this.taskId = taskId;
    }

    public long getMemberId() {
        return memberId;
    }

    public void setMemberId(long memberId) {
        this.memberId = memberId;
    }
}
