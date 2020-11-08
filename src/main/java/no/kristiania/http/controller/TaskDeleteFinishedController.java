package no.kristiania.http.controller;

import no.kristiania.db.Task;
import no.kristiania.db.TaskDao;
import no.kristiania.http.HttpMessage;
import no.kristiania.http.HttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;
import java.util.List;



public class TaskDeleteFinishedController extends AbstractController {
    private final TaskDao taskDao;
    public static final Logger logger = LoggerFactory.getLogger(HttpServer.class);

    public TaskDeleteFinishedController(DataSource dataSource) {
        this.taskDao = new TaskDao(dataSource);
    }

    @Override
    public void handle(HttpMessage request, Socket socket) throws IOException, SQLException {
        List<Task> taskList = taskDao.filterStatus("FINISHED");
        for (Task task: taskList) {
            taskDao.delete(task.getId());
            if(taskList.toArray().length == 1){
                logger.info("Deleted {} finished task from database", taskList.toArray().length);
            } else if (taskList.toArray().length > 1){
                logger.info("Deleted {} finished tasks from database", taskList.toArray().length);
            }
        }

        sendPostResponse(socket, "http://localhost:8080/index.html");
    }
}
