package no.kristiania.http.controller;

import no.kristiania.db.TaskDao;
import no.kristiania.http.HttpMessage;

import javax.sql.DataSource;
import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;
import java.util.Map;

public class TaskAlterController extends AbstractController {
    private final TaskDao taskDao;

    public TaskAlterController(DataSource dataSource) {
        this.taskDao = new TaskDao(dataSource);
    }

    @Override
    public void handle(HttpMessage request, Socket socket) throws IOException, SQLException {
        Map <String, String> stringMap = handlePostRequest(request, socket);
        String taskStatus = stringMap.get("status");
        long taskId = Long.parseLong(stringMap.get("task"));

        taskDao.alter(taskId, taskStatus);


        sendPostResponse(socket, "http://localhost:8080/alterProjectTask.html");
    }
}
