package no.kristiania.http.controller;

import no.kristiania.db.TaskMemberDao;
import no.kristiania.http.HttpMessage;
import no.kristiania.http.QueryString;

import javax.sql.DataSource;
import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;
import java.util.Map;

public class MemberTaskPostController extends AbstractController {
    private final TaskMemberDao taskMemberDao;

    public MemberTaskPostController(DataSource dataSource) {
        this.taskMemberDao = new TaskMemberDao(dataSource);
    }

    @Override
    public void handle(HttpMessage request, Socket socket) throws IOException, SQLException {
        request.readAndSetHeaders(socket);

        int contentLength = Integer.parseInt(request.getHeader("Content-Length"));
        String body = request.readBody(socket, contentLength);

        request.setBody(body);

        Map <String, String> taskMemberMap = QueryString.queryStringToHashMap(body);

        long memberId = Long.parseLong(taskMemberMap.get("member"));
        long taskId = Long.parseLong(taskMemberMap.get("task"));

        taskMemberDao.insert(taskId, memberId);

        postResponse(socket);
    }
}
