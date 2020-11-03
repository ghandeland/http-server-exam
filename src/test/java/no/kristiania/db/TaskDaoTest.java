package no.kristiania.db;

import org.flywaydb.core.Flyway;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

public class TaskDaoTest {
    private TaskDao taskDao;

    public static Task sampleTask() {
        return new Task(sampleTaskName(), sampleTaskDescription(), sampleTaskStatus());
    }

    public static String sampleTaskName() {
        String[] options = {"Build final feature", "Clean up coode", "Market software", "Have meeting"};
        Random random = new Random();
        return options[random.nextInt(options.length)];
    }

    public static String sampleTaskDescription() {
        String[] options = {"Needs to be done by monday", "Very important", "Takes place in the meeting room", "Rapport to the CTO by monday"};
        Random random = new Random();
        return options[random.nextInt(options.length)];
    }

    public static String sampleTaskStatus() {
        String[] options = {"OPEN", "IN_PROGRESS", "FINISHED", "CANCELED"};
        Random random = new Random();
        return options[random.nextInt(options.length)];
    }

    @BeforeEach
    void setup() {
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setUrl("jdbc:h2:mem:testdatabase;DB_CLOSE_DELAY=-1");
        Flyway.configure().dataSource(dataSource).load().migrate();
        taskDao = new TaskDao(dataSource);
    }

    @Test
    void shouldRetrieveSavedTask() throws SQLException {

        Task taskSample = sampleTask();
        taskDao.insert(taskSample);

        Task retrievedSample = taskDao.retrieve(taskSample.getId());

        assertThat(taskSample).isNotEqualTo(retrievedSample);
        assertThat(taskSample.getName()).isEqualTo(retrievedSample.getName());
        assertThat(taskSample.getDescription()).isEqualTo(retrievedSample.getDescription());
    }

    @Test
    void shouldRetrieveTaskList() throws SQLException {
        Task sampleTask1 = sampleTask();
        Task sampleTask2 = sampleTask();
        Task sampleTask3 = sampleTask();

        List <Task> taskList = new ArrayList <>();
        taskList.add(sampleTask1);
        taskList.add(sampleTask2);
        taskList.add(sampleTask3);

        for(Task t : taskList){
            taskDao.insert(t);
        }

        for(int i = 0 ; i < 3 ; i++){
            assertThat(taskList.get(i)).hasNoNullFieldsOrProperties();
            assertThat(taskList.get(i))
                    .usingRecursiveComparison()
                    .isEqualTo(taskDao.retrieve(taskList.get(i).getId()));
        }
    }

    @Test
    @DisplayName("should filter on status")
    void shouldFilterOnStatus() throws SQLException {
        Task sampleTask1 = sampleTask();
        Task sampleTask2 = sampleTask();
        Task sampleTask3 = sampleTask();

        List <Task> taskList = new ArrayList <>();
        taskList.add(sampleTask1);
        taskList.add(sampleTask2);
        taskList.add(sampleTask3);

        for(Task t : taskList){
            taskDao.insert(t);
        }

        for(int i = 0 ; i < 3 ; i++){
            assertThat(taskList.get(i)).hasNoNullFieldsOrProperties();
            assertThat(taskDao.filterStatus(taskList.get(i).getStatus().toString()))
                    .extracting(Task::getId)
                    .contains(taskList.get(i).getId());
            assertThat(taskDao.filterStatus(taskList.get(i).getStatus().toString()))
                    .extracting(Task::getName)
                    .contains(taskList.get(i).getName());
            assertThat(taskDao.filterStatus(taskList.get(i).getStatus().toString()))
                    .extracting(Task::getDescription)
                    .contains(taskList.get(i).getDescription());
            assertThat(taskDao.filterStatus(taskList.get(i).getStatus().toString()))
                    .extracting(Task::getStatus)
                    .contains(taskList.get(i).getStatus());
        }
    }

}