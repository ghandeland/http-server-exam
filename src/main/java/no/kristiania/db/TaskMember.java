package no.kristiania.db;

public class TaskMember {
    private long taskId;
    private long memberId;

    public TaskMember() {
        this.taskId = taskId;
        this.memberId = memberId;
    }

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
