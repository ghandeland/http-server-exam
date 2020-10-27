package no.kristiania.db;

import javax.sql.DataSource;
import java.sql.*;
import java.util.LinkedHashSet;

public class TaskMemberDao extends AbstractDao<TaskMember> {

    public TaskMemberDao(DataSource dataSource) { super(dataSource); }

    @Override
    protected void setDataOnStatement(PreparedStatement statement, TaskMember taskMember) throws SQLException {
        statement.setLong(1, taskMember.getTaskId());
        statement.setLong(2, taskMember.getMemberId());
    }


    public void insert(long taskId, long memberId) throws SQLException {
        try(Connection connection = dataSource.getConnection()){
            try(PreparedStatement statement = connection.prepareStatement("insert into task_member (task_id, member_id) values (?, ?);")){

                statement.setLong(1, taskId);
                statement.setLong(2, memberId);

                statement.executeUpdate();
            }
        }
    }

    public void insert(TaskMember taskMember) throws SQLException {
        insert(taskMember.getTaskId(), taskMember.getMemberId());
    }


    @Override
    protected TaskMember mapRow(ResultSet rs) throws SQLException {
        TaskMember taskMember = new TaskMember();

        taskMember.setId(rs.getLong("task_id"));
        taskMember.setMemberId(rs.getLong("member_id"));

        return taskMember;
    }

    public LinkedHashSet<Long> retrieveMembersByTaskId(long taskId) throws SQLException {

        LinkedHashSet<Long> memberIdSet = new LinkedHashSet<>();

        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("select * from task_member where task_id = ?")) {
                statement.setLong(1, taskId);

                statement.executeQuery();

                try (ResultSet rs = statement.getResultSet()) {
                    while(rs.next()) {
                        memberIdSet.add(rs.getLong("member_id"));
                    }

                    return memberIdSet;
                }
            }
        }
    }

    public LinkedHashSet<Long> retrieveTasksByMemberId(long memberId) throws SQLException {

        LinkedHashSet<Long> taskIdSet = new LinkedHashSet<>();

        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("select * from task_member where member_id = ?")) {
                statement.setLong(1, memberId);

                statement.executeQuery();

                try (ResultSet rs = statement.getResultSet()) {
                    while(rs.next()) {
                        taskIdSet.add(rs.getLong("task_id"));
                    }

                    return taskIdSet;
                }
            }
        }
    }
}
