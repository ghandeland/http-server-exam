package no.kristiania.db;

import no.kristiania.http.HttpServer;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashSet;

// Still don't know if it needs to implement AbstractDao
public class TaskMemberDao {

    DataSource dataSource;

    public TaskMemberDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void insert(TaskMember taskMember) throws SQLException {
        insert(taskMember.getTaskId(), taskMember.getMemberId());
    }

    public void insert(long taskId, long memberId) throws SQLException {
        try(Connection connection = dataSource.getConnection()){
            try(PreparedStatement statement = connection.prepareStatement("insert into task_member (task_id, member_id) values (?, ?);")){
                statement.setLong(1, taskId);
                statement.setLong(2, memberId);

                statement.execute();
            }catch(SQLException e){
                HttpServer.logger.info("ENTRY ERROR: DUPLICATE PRIMARY KEY");
            }
        }
    }

    public LinkedHashSet <Long> retrieveMembersByTaskId(long taskId) throws SQLException {

        LinkedHashSet <Long> memberIdSet = new LinkedHashSet <>();

        try(Connection connection = dataSource.getConnection()){
            try(PreparedStatement statement = connection.prepareStatement("select * from task_member where task_id = ?")){
                statement.setLong(1, taskId);

                statement.executeQuery();

                try(ResultSet rs = statement.getResultSet()){
                    while(rs.next()){
                        memberIdSet.add(rs.getLong("member_id"));
                    }

                    return memberIdSet;
                }
            }
        }
    }

    public LinkedHashSet <Long> retrieveTasksByMemberId(long memberId) throws SQLException {

        LinkedHashSet <Long> taskIdSet = new LinkedHashSet <>();

        try(Connection connection = dataSource.getConnection()){
            try(PreparedStatement statement = connection.prepareStatement("select * from task_member where member_id = ?")){
                statement.setLong(1, memberId);

                statement.executeQuery();

                try(ResultSet rs = statement.getResultSet()){
                    while(rs.next()){
                        taskIdSet.add(rs.getLong("task_id"));
                    }

                    return taskIdSet;
                }
            }
        }
    }
}
