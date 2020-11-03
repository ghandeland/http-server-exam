package no.kristiania.db;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class TaskDao extends AbstractDao <Task> {

    public TaskDao(DataSource dataSource) {
        super(dataSource);
    }

    public void insert(Task task) throws SQLException {
        insert(task, "insert into task (name, description, status) values(?, ?, CAST(? AS task_status))");
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

    public List <Task> filterStatus(String value) throws SQLException {
        if(value.equals("*")){
            return list();
        }else{
            Task.TaskStatus.valueOf(value.trim());
            String enumStatus = String.valueOf(Task.TaskStatus.valueOf(value.trim()));
            return filter(enumStatus, "SELECT * FROM task WHERE status = CAST(? AS task_status)");
        }
    }

    public void alter(long taskId, String value) throws SQLException {
        alter(taskId, value, "UPDATE task SET status = CAST(? AS task_status) WHERE id = ?");
    }

    @Override
    protected void setDataOnStatement(PreparedStatement statement, Task task) throws SQLException {
        statement.setString(1, task.getName());
        statement.setString(2, task.getDescription());
        statement.setString(3, task.getStatus().toString());
    }

    @Override
    protected Task mapRow(ResultSet rs) throws SQLException {
        Task task = new Task();
        task.setId(rs.getLong("id"));
        task.setName(rs.getString("name"));
        task.setDescription(rs.getString("description"));
        task.setStatus(rs.getString("status"));
        return task;
    }
}
