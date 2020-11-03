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


public class TaskMemberDao extends AbstractDao <TaskMember> {

    public static final Logger logger = LoggerFactory.getLogger(HttpServer.class);
    public TaskMemberDao(DataSource dataSource) {
        super(dataSource);
    }
    public MemberDao mr = new MemberDao(dataSource);
    public TaskDao tr = new TaskDao(dataSource);



    public void insert(long taskId, long memberId) throws SQLException {
        insert(new TaskMember(taskId, memberId));
    }

    public void insert(TaskMember taskMember) throws SQLException {
        insert(taskMember, "insert into task_member (task_id, member_id) values (?, ?);");
        logger.info("Task({}) assigned to member({}) and successfully inserted into database ",
                tr.retrieve(taskMember.getTaskId()).getName(), mr.retrieve(taskMember.getMemberId()).getFirstName() + " " + mr.retrieve(taskMember.getMemberId()).getLastName());
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

    @Override
    protected void setDataOnStatement(PreparedStatement statement, TaskMember taskMember) throws SQLException {
        statement.setLong(1, taskMember.getTaskId());
        statement.setLong(2, taskMember.getMemberId());
    }

    @Override
    protected TaskMember mapRow(ResultSet rs) throws SQLException {
        TaskMember taskMember = new TaskMember();

        taskMember.setId(rs.getLong("id"));
        taskMember.setTaskId(rs.getLong("task_id"));
        taskMember.setMemberId(rs.getLong("member_id"));

        return taskMember;
    }


}
