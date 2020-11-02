package no.kristiania.http.controller;

import no.kristiania.db.Task;
import no.kristiania.db.TaskDao;
import no.kristiania.http.HttpMessage;

import java.io.IOException;
import java.net.Socket;

public class TaskGetFilterController implements HttpController {

    private TaskDao taskDao;

    public TaskGetFilterController(TaskDao taskDao) {
        this.taskDao = taskDao;
    }

    @Override
    public void handle(HttpMessage request, Socket socket) throws IOException {

        StringBuilder body = new StringBuilder("<ul>");
        if(TaskFilterController.getFilterList() != null)
            for(Task task : TaskFilterController.getFilterList()){
                body.append("<li>" + "taskid: ").append(task.getId())
                        .append("<br>").append("Task name: ").append(task.getName())
                        .append("<br>").append("Task description: ").append(task.getDescription())
                        .append("<br>").append("Task status: ").append(task.getStatus())
                        .append("</li>");
            }
        body.append("</ul>");

        HttpMessage response = new HttpMessage();
        response.setBody(body.toString());
        response.setCodeAndStartLine("200");
        response.setHeader("Content-Length", String.valueOf(response.getBody().length()));
        response.setHeader("Content-Type", "text/html");
        response.setHeader("Connection", "close");
        response.write(socket);
    }

}