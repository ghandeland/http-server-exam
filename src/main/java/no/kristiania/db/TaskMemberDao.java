package no.kristiania.db;

import no.kristiania.http.HttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashSet;
import java.util.List;


public class TaskMemberDao extends AbstractDao <TaskMember> {
    public static final Logger logger = LoggerFactory.getLogger(HttpServer.class);
    private final MemberDao memberDao = new MemberDao(dataSource);
    private final TaskDao taskDao = new TaskDao(dataSource);

    public TaskMemberDao(DataSource dataSource) {
        super(dataSource);
    }

    public void insert(long taskId, long memberId) throws SQLException {
        insert(new TaskMember(taskId, memberId));
    }

    public void insert(TaskMember taskMember) throws SQLException {
        insert(taskMember, "insert into task_member (task_id, member_id) values (?, ?);");
        logger.info("Task({}) assigned to member({}) and successfully inserted into database ",
                taskDao.retrieve(taskMember.getTaskId()).getName(),
                memberDao.retrieve(taskMember.getMemberId()).getFirstName() + " " + memberDao.retrieve(taskMember.getMemberId()).getLastName());
    }

    public LinkedHashSet <Long> retrieveMembersByTaskId(long taskId) throws SQLException {
        List <TaskMember> taskMemberList = retrieveMultiple(taskId, "select * from task_member where task_id = ?");
        LinkedHashSet <Long> memberIdSet = new LinkedHashSet <>();
        for(TaskMember taskMember : taskMemberList){
            memberIdSet.add(taskMember.getMemberId());
        }
        return memberIdSet;
    }

    public LinkedHashSet <Long> retrieveTasksByMemberId(long memberId) throws SQLException {
        List <TaskMember> taskMemberList = retrieveMultiple(memberId, "select * from task_member where member_id = ?");
        LinkedHashSet <Long> taskIdSet = new LinkedHashSet <>();
        for(TaskMember taskMember : taskMemberList){
            taskIdSet.add(taskMember.getTaskId());
        }
        return taskIdSet;
    }

    public void deleteFromTaskId(long id) throws SQLException {
        delete(id, "DELETE FROM task_member WHERE task_id = ?");
    }

    public void deleteFromMemberId(long id) throws SQLException {
        delete(id, "DELETE FROM task_member WHERE member_id = ?");
    }

    @Override
    protected void setDataOnStatement(PreparedStatement statement, TaskMember taskMember) throws SQLException {
        statement.setLong(1, taskMember.getTaskId());
        statement.setLong(2, taskMember.getMemberId());
    }

    @Override
    protected TaskMember mapRow(ResultSet rs) throws SQLException {
        TaskMember taskMember = new TaskMember(
                rs.getLong("task_id"),
                rs.getLong("member_id")
        );
        taskMember.setId(rs.getLong("id"));
        return taskMember;
    }


}
