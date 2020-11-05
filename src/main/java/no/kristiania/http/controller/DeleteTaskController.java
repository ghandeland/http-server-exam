package no.kristiania.http.controller;

import no.kristiania.db.TaskDao;
import no.kristiania.http.HttpMessage;

import javax.sql.DataSource;
import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;
import java.util.Map;

public class DeleteTaskController extends AbstractController {
    private final TaskDao taskDao;

    public DeleteTaskController(DataSource dataSource) {
        this.taskDao = new TaskDao(dataSource);
    }

    @Override
    public void handle(HttpMessage request, Socket socket) throws IOException, SQLException {
        Map <String, String> bodyMap = handlePostRequest(request, socket);
        long task = Long.parseLong(bodyMap.get("task"));
        taskDao.delete(task);
        sendPostResponse(socket);
    }
}
