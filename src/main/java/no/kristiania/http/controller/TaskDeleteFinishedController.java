package no.kristiania.http.controller;

import no.kristiania.db.Task;
import no.kristiania.db.TaskDao;
import no.kristiania.http.HttpMessage;

import javax.sql.DataSource;
import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class TaskDeleteFinishedController extends AbstractController {
    private final TaskDao taskDao;

    public TaskDeleteFinishedController(DataSource dataSource) {
        this.taskDao = new TaskDao(dataSource);
    }

    @Override
    public void handle(HttpMessage request, Socket socket) throws IOException, SQLException {
        List<Task> taskList = taskDao.filterStatus("FINISHED");
        for (Task task: taskList) {
            taskDao.delete(task.getId());
        }

        sendPostResponse(socket);
        
//        Map<String, String> bodyMap = handlePostRequest(request, socket);
//        long task = Long.parseLong(bodyMap.get("status-select"));
//        taskDao.delete(task);
//
//        sendPostResponse(socket);
    }
}
