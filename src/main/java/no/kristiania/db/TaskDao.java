package no.kristiania.db;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;

public class TaskDao extends AbstractDao <Task> {

    public TaskDao(DataSource dataSource) {
        super(dataSource);
    }

    public void insert(Task task) throws SQLException {
        try(Connection connection = dataSource.getConnection()){
            try(PreparedStatement statement = connection.prepareStatement(
                    "insert into task (name, description) values(?, ?)",
                    Statement.RETURN_GENERATED_KEYS)){

                statement.setString(1, task.getName());
                statement.setString(2, task.getDescription());

                statement.execute();

                try(ResultSet generatedKeys = statement.getGeneratedKeys()){
                    if(generatedKeys.next()){
                        task.setId(generatedKeys.getLong(1));
                    }
                }
            }
        }
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

    @Override
    protected Task mapRow(ResultSet rs) throws SQLException {
        Task task = new Task();

        task.setId(rs.getLong("id"));
        task.setName(rs.getString("name"));
        task.setDescription(rs.getString("description"));

        return task;
    }
}
