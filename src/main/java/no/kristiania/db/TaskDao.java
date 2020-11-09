package no.kristiania.db;

import no.kristiania.http.HttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TaskDao extends AbstractDao <Task> {
    public static final Logger logger = LoggerFactory.getLogger(HttpServer.class);

    public TaskDao(DataSource dataSource) {
        super(dataSource);
    }

    public void insert(Task task) throws SQLException {
        boolean insertOk = insert(task, "insert into task (name, description, status) values(?, ?, CAST(? AS task_status))");
        if(insertOk) logger.info("New task ({}) successfully added to database", task.getName());
    }

    public List <Task> list() throws SQLException {
        return list("select * from task");
    }

    public Task retrieve(long id) throws SQLException {
        Task task = retrieve(id, "select * from task where id = ?");
        if(task == null)
            System.out.println("Task not found");
        return task;
    }

    public List <Task> filterStatus(String status) throws SQLException {
        if(status.equals("*")){
            return list();
        }else{
            TaskStatus.valueOf(status.trim());
            String enumStatus = String.valueOf(TaskStatus.valueOf(status.trim()));
            return filter(enumStatus, "SELECT * FROM task WHERE status = CAST(? AS task_status)");
        }
    }

    public List <Task> filterMember(String memberId) throws SQLException {
        if(memberId.equals("") || memberId.equals("*")){
            return list();
        }else{
            return filter(memberId, "SELECT * FROM task WHERE id = (SELECT task_id FROM task_member WHERE member_id = CAST(? AS integer))");
        }
    }

    public List <Task> filterTaskAndMember(String status, String memberId) throws SQLException {
        if(memberId.equals("*")){
            return filterStatus(status);
        }else if(status.equals("*")){
            return filterMember(memberId);
        }
        try(Connection connection = dataSource.getConnection()){
            try(PreparedStatement statement = connection.prepareStatement(
                    "select * from task where id in (select task_id from task_member where member_id = ?) and status = CAST(? AS task_status);"
            )){
                statement.setInt(1, Integer.parseInt(memberId));
                statement.setString(2, status);

                try(ResultSet rs = statement.executeQuery()){
                    List <Task> taskList = new ArrayList <>();
                    while(rs.next()){
                        taskList.add(mapRow(rs));
                    }
                    return taskList;
                }
            }
        }
    }

    public void alter(long id, String status) throws SQLException {
        alter(id, status, "UPDATE task SET status = CAST(? AS task_status) WHERE id = ?");
    }

    public void delete(long id) throws SQLException {
        delete(id, "DELETE FROM task_member WHERE task_id = ?");
        delete(id, "DELETE FROM task WHERE id = ?");
    }


    @Override
    protected void setDataOnStatement(PreparedStatement statement, Task task) throws SQLException {
        statement.setString(1, task.getName());
        statement.setString(2, task.getDescription());
        statement.setString(3, task.getStatus().toString());
    }

    @Override
    protected Task mapRow(ResultSet rs) throws SQLException {
        return new Task(rs.getLong("id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getString("status")
        );
    }
}
