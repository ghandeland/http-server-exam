package no.kristiania.http.controller;

import no.kristiania.db.Task;
import no.kristiania.db.TaskDao;
import no.kristiania.http.HttpMessage;

import javax.sql.DataSource;
import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;
import java.util.Map;

public class TaskPostController extends AbstractController {
    private final TaskDao taskDao;

    public TaskPostController(DataSource dataSource) {
        this.taskDao = new TaskDao(dataSource);
    }

    @Override
    public void handle(HttpMessage request, Socket socket) throws IOException, SQLException {
        Map <String, String> memberQueryMap = handlePostRequest(request, socket);

        String taskName = memberQueryMap.get("name");
        String taskDescription = memberQueryMap.get("description");
        String taskStatus = memberQueryMap.get("status");

        Task task = new Task(taskName, taskDescription, taskStatus);

        taskDao.insert(task);

        sendPostResponse(socket);

    }
}
