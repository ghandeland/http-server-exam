package no.kristiania.http.controller;

import no.kristiania.db.Member;
import no.kristiania.db.TaskStatus;
import no.kristiania.http.HttpMessage;

import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;

import static no.kristiania.db.Task.taskStatusToString;

public class TaskStatusSelectGetController extends AbstractController {

    @Override
    public void handle(HttpMessage request, Socket socket) throws IOException, SQLException {
        StringBuilder body = new StringBuilder();

        body.append("<option value='*'>Show all</option>");

        for(TaskStatus status : TaskStatus.values()) {
            body.append("<option ");

            if(status == TaskFilterPostController.getFilterStatus()) body.append("selected ");

            body.append("value=\"")
                    .append(status.toString())
                    .append("\">")
                    .append(taskStatusToString(status))
                    .append("</option>");
        }

        sendGetResponse(socket, body);
    }
}
