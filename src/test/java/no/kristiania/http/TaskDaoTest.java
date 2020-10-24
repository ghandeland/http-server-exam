package no.kristiania.http;

import no.kristiania.http.Task;
import no.kristiania.http.TaskDao;
import org.flywaydb.core.Flyway;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

public class TaskDaoTest {
    @Test
    void shouldRetrieveSavedProject() throws SQLException {
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setUrl("jdbc:h2:mem:testdatabase;DB_CLOSE_DELAY=-1");
        Flyway.configure().dataSource(dataSource).load().migrate();

        TaskDao taskDao = new TaskDao(dataSource);

        Task taskSample = sampleTask();
        taskDao.insert(taskSample);

        Task retrievedSample = taskDao.retrieve(taskSample.getId());

        assertThat(taskSample).isNotEqualTo(retrievedSample);
        assertThat(taskSample.getName()).isEqualTo(retrievedSample.getName());
        assertThat(taskSample.getDescription()).isEqualTo(retrievedSample.getDescription());
    }

    @Test
    void ShouldRetrieveSingleProject() throws SQLException {
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setUrl("jdbc:h2:mem:testdatabase;DB_CLOSE_DELAY=-1");
        Flyway.configure().dataSource(dataSource).load().migrate();

        TaskDao taskDao = new TaskDao(dataSource);

        Task sampleTask1 = sampleTask();
        Task sampleTask2 = sampleTask();
        Task sampleTask3 = sampleTask();

        List<Task> taskList = new ArrayList<>();
        taskList.add(sampleTask1);
        taskList.add(sampleTask2);
        taskList.add(sampleTask3);

        for(Task t : taskList) {
            taskDao.insert(t);
        }

        List<Task> retrievedTaskList = taskDao.list();

        for (int i = 0; i < 3; i++) {
            assertThat(taskList.get(i)).isNotEqualTo(retrievedTaskList.get(i));
            assertThat(taskList.get(i).getName()).isEqualTo(retrievedTaskList.get(i).getName());
            assertThat(taskList.get(i).getDescription()).isEqualTo(retrievedTaskList.get(i).getDescription());
        }
    }

    private Task sampleTask() {
        return new Task(sampleTaskName(), sampleTaskDescription());

    }

    private String sampleTaskName() {
        String[] options = {"Build final feature", "Clean up coode", "Market software", "Have meeting"};
        Random random = new Random();
        return options[random.nextInt(options.length)];
    }

    private String sampleTaskDescription() {
        String[] options = {"Needs to be done by monday", "Very important", "Takes place in the meeting room", "Rapport to the CTO by monday"};
        Random random = new Random();
        return options[random.nextInt(options.length)];
    }
}