package no.kristiania.http.controller;

import no.kristiania.db.Task;
import no.kristiania.db.TaskDao;
import no.kristiania.http.HttpMessage;

import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;

public class TaskSelectGetController implements HttpController {
    private TaskDao taskDao;

    public TaskSelectGetController(TaskDao taskDao) {
        this.taskDao = taskDao;
    }

    @Override
    public void handle(HttpMessage request, Socket socket) throws IOException, SQLException {
        StringBuilder body = new StringBuilder();

        for(Task task : taskDao.list()){
            body.append("<option value=\"").append(task.getId()).append("\">")
                    .append(task.getName())
                    .append("</option>");
        }

        HttpMessage response = new HttpMessage();
        response.setBody(body.toString());
        response.setCodeAndStartLine("200");
        response.setHeader("Content-Length", String.valueOf(response.getBody().length()));
        response.setHeader("Content-Type", "text/plain");
        response.setHeader("Connection", "close");
        response.write(socket);
    }
}
