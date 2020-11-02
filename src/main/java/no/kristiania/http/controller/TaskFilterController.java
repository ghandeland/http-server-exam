package no.kristiania.http.controller;

import no.kristiania.db.Task;
import no.kristiania.db.TaskDao;
import no.kristiania.http.HttpMessage;
import no.kristiania.http.QueryString;

import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class TaskFilterController implements HttpController {
    private static List <Task> filterList;
    private final TaskDao taskDao;


    public TaskFilterController(TaskDao taskDao) {
        this.taskDao = taskDao;
    }

    @Override
    public void handle(HttpMessage request, Socket socket) throws IOException, SQLException {
        request.readAndSetHeaders(socket);
        int contentLength = Integer.parseInt(request.getHeader("Content-Length"));
        String body = request.readBody(socket, contentLength);
        request.setBody(body);
        Map <String, String> taskQueryMap = QueryString.queryStringToHashMap(body);
        String taskStatus = taskQueryMap.get("taskStatus");

        filterList = taskDao.filter(taskStatus);

        HttpMessage response = new HttpMessage();
        response.setCodeAndStartLine("204");
        response.setHeader("Connection", "close");
        response.setHeader("Content-length", "0");
        response.write(socket);
    }

    public static List <Task> getFilterList() {
        return filterList;
    }
}
