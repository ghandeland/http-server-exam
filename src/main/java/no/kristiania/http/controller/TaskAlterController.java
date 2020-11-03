package no.kristiania.http.controller;

import no.kristiania.db.TaskDao;
import no.kristiania.http.HttpMessage;
import no.kristiania.http.QueryString;

import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;
import java.util.Map;

public class TaskAlterController implements HttpController {
    private final TaskDao taskDao;

    public TaskAlterController(TaskDao taskDao) {
        this.taskDao = taskDao;
    }

    @Override
    public void handle(HttpMessage request, Socket socket) throws IOException, SQLException {
        request.readAndSetHeaders(socket);
        String body = request.readBody(socket, Integer.parseInt(request.getHeader("Content-Length")));
        request.setBody(body);

        Map <String, String> stringMap = QueryString.queryStringToHashMap(body);
        String taskStatus = stringMap.get("status");
        long taskId = Long.parseLong(stringMap.get("task"));

        taskDao.alter(taskId, taskStatus);

        HttpMessage response = new HttpMessage();
        response.setCodeAndStartLine("301");
        response.setHeader("Location", "/index.html");
        response.write(socket);
    }
}
