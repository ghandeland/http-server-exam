package no.kristiania.http.controller;

import no.kristiania.db.TaskMemberDao;
import no.kristiania.http.HttpMessage;
import no.kristiania.http.QueryString;

import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;
import java.util.Map;

public class MemberTaskPostController implements HttpController {
    private TaskMemberDao taskMemberDao;

    public MemberTaskPostController(TaskMemberDao taskMemberDao) {
        this.taskMemberDao = taskMemberDao;
    }

    @Override
    public void handle(HttpMessage request, Socket socket) throws IOException, SQLException {
        request.readAndSetHeaders(socket);

        int contentLength = Integer.parseInt(request.getHeader("Content-Length"));
        String body = request.readBody(socket, contentLength);

        request.setBody(body);


        Map<String, String> taskMemberMap = QueryString.queryStringToHashMap(body);

        long memberId = Long.valueOf(taskMemberMap.get("member"));
        long taskId = Long.valueOf(taskMemberMap.get("task"));

        taskMemberDao.insert(taskId, memberId);

        HttpMessage response = new HttpMessage();
        response.setCodeAndStartLine("204");
        response.setHeader("Connection", "close");
        response.setHeader("Content-length", "0");
        response.write(socket);
    }
}
