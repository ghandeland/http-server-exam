package no.kristiania.http.controller;

import no.kristiania.db.Task;
import no.kristiania.db.TaskDao;
import no.kristiania.http.HttpMessage;
import no.kristiania.http.QueryString;

import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;
import java.util.Map;

public class TaskPostController implements HttpController {
    private TaskDao taskDao;

    public TaskPostController(TaskDao taskDao) {
        this.taskDao = taskDao;
    }

    @Override
    public void handle(HttpMessage request, Socket socket) throws IOException, SQLException {
        request.readAndSetHeaders(socket);
        int contentLength = Integer.parseInt(request.getHeader("Content-Length"));
        String body = request.readBody(socket, contentLength);

        request.setBody(body);

        Map <String, String> memberQueryMap = QueryString.queryStringToHashMap(body);

        String taskName = memberQueryMap.get("name");
        String taskDescription = memberQueryMap.get("description");
        String taskStatus = memberQueryMap.get("status");

        Task task = new Task(taskName, taskDescription, taskStatus);

        taskDao.insert(task);

        HttpMessage response = new HttpMessage();
        response.setCodeAndStartLine("204");
        response.setHeader("Connection", "close");
        response.setHeader("Content-length", "0");
        response.write(socket);

    }
}