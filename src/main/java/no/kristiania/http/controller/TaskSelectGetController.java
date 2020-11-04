package no.kristiania.http.controller;

import no.kristiania.db.Task;
import no.kristiania.db.TaskDao;
import no.kristiania.http.HttpMessage;

import javax.sql.DataSource;
import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;

public class TaskSelectGetController implements HttpController {
    private final TaskDao taskDao;

    public TaskSelectGetController(DataSource dataSource) {
        this.taskDao = new TaskDao(dataSource);
    }

    @Override
    public void handle(HttpMessage request, Socket socket) throws IOException, SQLException {
        StringBuilder body = new StringBuilder();

        for(Task task : taskDao.list()){
            body.append("<option value=\"").append(task.getId()).append("\">")
                    .append(task.getName())
                    .append("</option>");
        }

        getResponse(socket, body);
    }
}
