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
        insert(task, "insert into task (name, description, status) values(?, ?, CAST(? AS task_status))");
        logger.info("New task ({}) successfully added to database", task.getName());
    }

    public List <Task> list() throws SQLException {
        return list("select * from task");
    }

    public Task retrieve(long id) throws SQLException {
        Task task = retrieve(id, "select * from task where id = ?");
        if(task == null){
            System.out.println("Task not found");
            return null;
        }
        return task;
    }

    public List <Task> filter(String filterValue) throws SQLException {
        if(filterValue.equals("*")){
            return list();
        }else{
            Task.TaskStatus.valueOf(filterValue.trim());
            String enumStatus = String.valueOf(Task.TaskStatus.valueOf(filterValue.trim()));

            try(Connection connection = dataSource.getConnection()){
                try(PreparedStatement statement = connection.prepareStatement("SELECT * FROM task WHERE status =" + "'" + enumStatus + "'")){
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
