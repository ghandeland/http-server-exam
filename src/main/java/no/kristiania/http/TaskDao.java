package no.kristiania.http;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TaskDao {
    private DataSource dataSource;
    private List<Task> projects = new ArrayList<>();
    public TaskDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void insert(Task task) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("insert into task (name, description) values(?, ?)", Statement.RETURN_GENERATED_KEYS)) {

                statement.setString(1, task.getName());
                statement.setString(2, task.getDescription());

                statement.execute();

                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if(generatedKeys.next()) {
                        task.setId(generatedKeys.getInt(1));
                    }
                }
            }
        }
    }

    public List<Task> list() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("select * from task")) {
                try (ResultSet resultSet = statement.executeQuery()) {
                    while(resultSet.next()) {


                        List<Task> projects = new ArrayList<>();

                        while(resultSet.next()) {

                            String name = resultSet.getString("name");
                            String description = resultSet.getString("description");

                            projects.add(new Task(name, description));
                        }

                        return projects;
                    }
                }
            }
        }
        return null;
    }

    public Task retrieve(int id) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("select * from task where id = ?")) {

                statement.setInt(1, id);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if(resultSet.next()) {

                        String name = resultSet.getString("name");
                        String department = resultSet.getString("description");

                        return new Task(id, name, department);
                    }
                }
            }
        }
        System.out.println("Task not found");
        return null;
    }
}
